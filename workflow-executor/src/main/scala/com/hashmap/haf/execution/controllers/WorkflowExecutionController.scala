package com.hashmap.haf.execution.controllers

import java.sql.{Date, Timestamp}
import java.util
import java.util.{Collections, UUID}
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dexecutor.core.{DefaultDexecutor, DexecutorConfig, Duration, ExecutionConfig}
import com.hashmap.haf.datastore.api.{Datastore, SELECT}
import com.hashmap.haf.datastore.impl.IgniteQueryableDataStore
import com.hashmap.haf.execution.clients.{FunctionsServiceClient, WorkflowServiceClient}
import com.hashmap.haf.execution.executor.IgniteDexecutorState
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.processors.VelocitySourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.workflow.constants.XmlConstants.{LIVY_TASK, SPARK_TASK, _}
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.factory.Factory.{TaskFactory, WorkflowTask}
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import com.hashmap.haf.workflow.task.{DefaultTaskProvider, SparkIgniteTask}
import org.apache.ignite.cache.query.SqlFieldsQuery
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.internal.IgnitionEx
import org.apache.ignite.{Ignite, Ignition}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.web.bind.annotation._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.xml.{Node, NodeSeq}

@RestController
@RequestMapping(Array("/api"))
class WorkflowExecutionController @Autowired()(functionsServiceClient: FunctionsServiceClient,
                                               workflowServiceClient: WorkflowServiceClient,
                                               sourceGenerator: VelocitySourceGenerator,
                                               functionCompiler: FunctionCompiler) {
  @Value("${functions.ignite.config}")
  var igniteConfigPath: String = _

  var ignite: Ignite = _

  @PostConstruct
  def init(): Unit = {
    val igConfig = Thread.currentThread().getContextClassLoader.getResource(igniteConfigPath)
    val configuration = IgnitionEx.loadConfiguration(igConfig).get1()
    configuration.setClientMode(true)
    ignite = Ignition.start(configuration)
  }

  @RequestMapping(value = Array("/workflow/{workflowId}/cache/{cacheId}/{count}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getCacheData(@PathVariable("workflowId") workflowId: String, @PathVariable("cacheId") cacheId: String, @PathVariable("count") count: Int): util.List[util.List[String]]  = {
    //val CACHE_NAME = "temp_" + workflowId
    val tableName = cacheId + "_" + workflowId

    val datastore = IgniteQueryableDataStore(ignite)

    datastore.query(s"SELECT * FROM $tableName limit $count", SELECT)

    /*val ccfg = new CacheConfiguration[Any, Any](CACHE_NAME).setSqlSchema("PUBLIC")
    val tempCache = ignite.getOrCreateCache(ccfg)

    try{
      val cursor = tempCache.query(new SqlFieldsQuery(s"SELECT * FROM $tableName limit $count"))
      val clCnt = cursor.getColumnsCount()
      val fNames:util.List[String] = (0 until clCnt).map(cursor.getFieldName(_)).toList
      val _data = cursor.getAll
      // Converting collection of Any types to collection of Strings
      val data: util.List[util.List[String]] = _data.map(lst => lst.toList.filter(_ != null).map(_.toString).asJava).asJava
      //As given collection is not modifiable, we need to convert it into modifiable collection
      val newList = new util.ArrayList(data)
      newList.add(0, fNames)
      newList


    }finally {
      tempCache.destroy()
    }*/
  }


  @RequestMapping(value = Array("/workflow/execute/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def executeById(@PathVariable("workflowId") workflowId: String): String = {

    object CustomTaskFactory extends TaskFactory[UUID, String] {
      def create(xml: Node): WorkflowTask[UUID, String] = {
        (xml \ "_").headOption.map(_.label) match {
          case Some(SPARK_TASK) | Some(LIVY_TASK) => {
            val functionClassName = (xml \ CLASSNAME_ATTRIBUTE).text
            functionCompiler.loadClazz(functionClassName) match {
              case Some(c) => {
                c.getConstructor(classOf[NodeSeq], classOf[Ignite], classOf[String]).newInstance(xml, ignite, workflowId).asInstanceOf[SparkIgniteTask]
              }
              case _ => {
                generateSourceAndCompile(functionClassName).get.getConstructor(classOf[NodeSeq], classOf[Ignite], classOf[String]).newInstance(xml, ignite, workflowId).asInstanceOf[SparkIgniteTask]
              }
            }
          }
          case None => throw new IllegalStateException("At least one tak should be defined in a workflow")
          case _ => throw new IllegalArgumentException("No factory method found for given task")
        }
      }
    }

    val workflowXml: String = workflowServiceClient.getFunction(workflowId)
    val workflow = DefaultWorkflow(workflowXml, CustomTaskFactory)
    val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(workflow)
    workflow.buildTaskGraph(executor)
    executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
    "Done"
  }

  private def generateSourceAndCompile(functionClassName: String) = {
    val functionTypeString = functionsServiceClient.getFunction(functionClassName.substring(functionClassName.lastIndexOf(".")+1))
    val mapper = new ObjectMapper()
    val functionType = mapper.readValue(functionTypeString, classOf[IgniteFunctionType])
    sourceGenerator.generateSource(functionType) match {
      case Right(source) => {
        functionCompiler.compile(functionClassName, source)
        functionCompiler.loadClazz(functionClassName)
      }
      case Left(_) => None
    }
  }

  private def newTaskExecutor(workflow: Workflow[UUID, String]) = {
    val executorState = new IgniteDexecutorState[UUID, String](workflow.getId.toString, ignite)
    val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](executorState), DefaultTaskProvider(workflow))
    config.setDexecutorState(executorState)
    new DefaultDexecutor[UUID, String](config)
  }
}
