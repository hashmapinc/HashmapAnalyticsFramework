package com.hashmap.haf.execution.controllers

import com.hashmap.haf.execution.clients.{FunctionsServiceClient, WorkflowServiceClient}
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.processors.VelocitySourceGenerator
import org.apache.ignite.Ignite
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Mockito._
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(classOf[MockitoJUnitRunner])
class WorkflowExecutionControllerTest {

	private var ignite: Ignite = _

	private var functionsServiceClient: FunctionsServiceClient = _

	private var workflowServiceClient: WorkflowServiceClient = _

	private var sourceGenerator: VelocitySourceGenerator = _

	private var mockMvc: MockMvc = _

	@Before
	def setUp(): Unit = {
		functionsServiceClient = mock(classOf[FunctionsServiceClient])
		workflowServiceClient = mock(classOf[WorkflowServiceClient])
		sourceGenerator = mock(classOf[VelocitySourceGenerator])
		ignite = mock(classOf[Ignite])
		val controller = new WorkflowExecutionController(functionsServiceClient, workflowServiceClient,
			sourceGenerator, FunctionCompiler(), ignite)
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
	}

	@Test
	def returnErrorIfWorkflowIdIsInvalid(): Unit ={
		when(workflowServiceClient.getFunction("invalid")).thenReturn(null)
		mockMvc.perform(get("/api/workflow/execute/invalid"))
			.andExpect(status().isNotFound)
	}

}
