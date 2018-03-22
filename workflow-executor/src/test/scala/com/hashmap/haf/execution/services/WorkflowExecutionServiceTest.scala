package com.hashmap.haf.execution.services

import java.util
import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.dexecutor.core.task.ExecutionStatus
import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.hashmap.haf.execution.clients.FunctionsServiceClient
import com.hashmap.haf.execution.controllers.{SampleFailureWorkflowTask, SampleWorkflowTask}
import com.hashmap.haf.execution.exceptions.Exceptions.{FunctionNotFoundException, SourceCompilationException, SourceGenerationException}
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.processors.VelocitySourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import org.apache.ignite.{Ignite, Ignition}
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.velocity.exception.ParseErrorException
import org.junit.{After, Before, Test}
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.invocation.InvocationOnMock
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import org.junit.Assert._

@RunWith(classOf[MockitoJUnitRunner])
class WorkflowExecutionServiceTest {

	private val mapper: ObjectMapper = new ObjectMapper()

	private var functionsServiceClient: FunctionsServiceClient = _

	private var sourceGenerator: VelocitySourceGenerator = _

	private var functionCompiler: FunctionCompiler = _

	private var ignite: Ignite = _

	private var service: WorkflowExecutionService = _

	private val successWorkflow: String = Resources.toString(Resources.getResource("test-success-workflow.xml"), Charsets.UTF_8)
	private val failureWorkflow: String = Resources.toString(Resources.getResource("test-error-workflow.xml"), Charsets.UTF_8)

	@Before
	def setup(): Unit ={
		mapper.registerModule(DefaultScalaModule)
		ignite = igniteServer()
		functionsServiceClient = mock(classOf[FunctionsServiceClient])
		sourceGenerator = mock(classOf[VelocitySourceGenerator])
		functionCompiler = mock(classOf[FunctionCompiler])
		when(sourceGenerator.generateSource(any[IgniteFunctionType]())).thenReturn(Right(""))
		doNothing().when(functionCompiler).compile(anyString(), anyString())
		service = new WorkflowExecutionService(functionsServiceClient, sourceGenerator, functionCompiler, ignite)
	}

	@Test
	def executeWorkflowWithNoPrecompiledTasksAndReturnSuccessfulResult(): Unit ={
		val functionClazzName = classOf[SampleWorkflowTask].getCanonicalName
		val functionResult = new IgniteFunctionType("sample_service", null, "SampleWorkflowTask", classOf[SampleWorkflowTask].getPackage.getName)
		val workflowId = UUID.randomUUID().toString

		when(functionsServiceClient.getFunction("SampleWorkflowTask")).thenReturn(mapper.writeValueAsString(functionResult))
		when(functionCompiler.loadClazz(functionClazzName)).thenAnswer(new Answer[Option[Class[_]]](){
			override def answer(invocation: InvocationOnMock): Option[Class[_]] = None
		}).thenAnswer(new Answer[Option[Class[_]]](){
			override def answer(invocation: InvocationOnMock): Option[Class[_]] = Some(classOf[SampleWorkflowTask])
		})

		val result = service.executeWorkflow(workflowId, successWorkflow)

		verify(functionCompiler, times(3)).loadClazz(functionClazzName)
		verify(functionCompiler, times(1)).compile(anyString(), anyString())
		verify(functionsServiceClient, times(1)).getFunction("SampleWorkflowTask")
		verify(sourceGenerator, times(1)).generateSource(functionResult)

		assertNotNull(result)
		assertEquals(result.id, workflowId)
		assertEquals(result.status, ExecutionStatus.SUCCESS)
		assertFalse(result.errors.isDefined)
		assertFalse(result.skippedTasks.isDefined)
	}

	@Test
	def executeWorkflowWithPrecompiledTasks(): Unit ={
		val functionClazzName = classOf[SampleWorkflowTask].getCanonicalName
		val functionResult = new IgniteFunctionType("sample_service", null, "SampleWorkflowTask", classOf[SampleWorkflowTask].getPackage.getName)
		val workflowId = UUID.randomUUID().toString

		when(functionsServiceClient.getFunction("SampleWorkflowTask")).thenReturn(mapper.writeValueAsString(functionResult))
		when(functionCompiler.loadClazz(functionClazzName)).thenReturn(Some(classOf[SampleWorkflowTask]))

		val result = service.executeWorkflow(workflowId, successWorkflow)

		verify(functionCompiler, times(2)).loadClazz(functionClazzName)
		verifyNoMoreInteractions(functionsServiceClient, sourceGenerator, functionCompiler)

		assertNotNull(result)
		assertEquals(result.id, workflowId)
		assertEquals(result.status, ExecutionStatus.SUCCESS)
		assertFalse(result.errors.isDefined)
		assertFalse(result.skippedTasks.isDefined)
	}

	@Test
	def returnWorkflowExecutionResultWithErrorsOccurredInTask(): Unit ={
		val functionClazzName = classOf[SampleFailureWorkflowTask].getCanonicalName
		val functionResult = new IgniteFunctionType("failure_service", null, "SampleFailureWorkflowTask", classOf[SampleFailureWorkflowTask].getPackage.getName)
		val workflowId = UUID.randomUUID().toString

		when(functionsServiceClient.getFunction("SampleFailureWorkflowTask")).thenReturn(mapper.writeValueAsString(functionResult))
		when(functionCompiler.loadClazz(functionClazzName)).thenReturn(Some(classOf[SampleFailureWorkflowTask]))

		val result = service.executeWorkflow(workflowId, failureWorkflow)

		assertNotNull(result)
		assertEquals(result.id, workflowId)
		assertEquals(result.status, ExecutionStatus.ERRORED)
		assertTrue(result.errors.isDefined)
		assertEquals(result.errors.get.taskErrors.size, 1)
	}

	@Test(expected = classOf[FunctionNotFoundException])
	def throwFunctionNotFoundExceptionIfNoFunctionFound(): Unit ={
		val workflowId = UUID.randomUUID().toString

		when(functionCompiler.loadClazz(anyString())).thenReturn(None)
		when(functionsServiceClient.getFunction(anyString())).thenReturn(null)

		service.executeWorkflow(workflowId, successWorkflow)
	}

	@Test(expected = classOf[SourceGenerationException])
	def throwSourceGenerationExceptionIfErrorWhileGeneratingSource(): Unit ={
		val workflowId = UUID.randomUUID().toString
		val functionResult = new IgniteFunctionType("sample_service", null, "SampleWorkflowTask", classOf[SampleWorkflowTask].getPackage.getName)

		when(functionCompiler.loadClazz(anyString())).thenReturn(None)
		when(functionsServiceClient.getFunction(functionResult.getFunctionClazz)).thenReturn(mapper.writeValueAsString(functionResult))
		when(sourceGenerator.generateSource(functionResult)).thenReturn(Left("Error while loading template", new ParseErrorException("parse error")))

		service.executeWorkflow(workflowId, successWorkflow)
	}

	@After
	def tearDown(): Unit = {
		if(ignite != null)
			ignite.close()
	}


	private def igniteServer(): Ignite ={
		val configs = new IgniteConfiguration()
		configs.setClientMode(false)
		val spi = new TcpDiscoverySpi()
		val finder = new TcpDiscoveryMulticastIpFinder()
		finder.setAddresses(util.Arrays.asList("127.0.0.1:47500..47509"))
		spi.setIpFinder(finder)
		configs.setDiscoverySpi(spi)
		Ignition.start(configs)
	}

}
