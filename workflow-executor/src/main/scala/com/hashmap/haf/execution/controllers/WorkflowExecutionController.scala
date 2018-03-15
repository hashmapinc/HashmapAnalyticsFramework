package com.hashmap.haf.execution.controllers

import java.sql.{Date, Timestamp}
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dexecutor.core.task.ExecutionResults
import com.github.dexecutor.core.{DefaultDexecutor, DexecutorConfig, Duration, ExecutionConfig}
import com.hashmap.haf.datastore.{DataframeIgniteCache, Datastore}
import com.hashmap.haf.execution.clients.{FunctionsServiceClient, WorkflowServiceClient}
import com.hashmap.haf.execution.executor.IgniteDexecutorState
import com.hashmap.haf.execution.models.Responses.WorkflowExecutionResult
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.processors.VelocitySourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.workflow.constants.XmlConstants.{LIVY_TASK, SPARK_TASK, _}
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.factory.Factory.{TaskFactory, WorkflowTask}
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import com.hashmap.haf.workflow.task.{DefaultTaskProvider, SparkIgniteTask}
import org.apache.ignite.internal.IgnitionEx
import org.apache.ignite.{Ignite, Ignition}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.web.bind.annotation._

import scala.xml.{Node, NodeSeq}

@RestController
@RequestMapping(Array("/api"))
class WorkflowExecutionController @Autowired()(functionsServiceClient: FunctionsServiceClient,
                                               workflowServiceClient: WorkflowServiceClient,
                                               sourceGenerator: VelocitySourceGenerator,
                                               functionCompiler: FunctionCompiler) {
  private val logger = LoggerFactory.getLogger(classOf[WorkflowExecutionController])

  @Value("${functions.ignite.config}")
  var igniteConfigPath: String = _

  var ignite: Ignite = _

  @PostConstruct
  def init(): Unit = {
    logger.trace("Initializing Ignite in client mode.")
    val igConfig = Thread.currentThread().getContextClassLoader.getResource(igniteConfigPath)
    val configuration = IgnitionEx.loadConfiguration(igConfig).get1()
    configuration.setClientMode(true)
    ignite = Ignition.start(configuration)
    logger.trace("Ignite started in client mode.")
  }

  @RequestMapping(value = Array("/workflow/{workflowId}/cache/{cacheId}/{count}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getCacheData(@PathVariable("workflowId") workflowId: String, @PathVariable("cacheId") cacheId: String, @PathVariable("count") count: Int): Array[Array[String]]  = {
    val spark = SparkSession
      .builder()
      .appName("someApp")
      .master("local")
      .getOrCreate()

    val cache: Datastore = DataframeIgniteCache.create()
    val (schema, igniteRDD) = cache.get(spark.sparkContext, workflowId + "_"+ cacheId)
    val rdd1: RDD[Row] = igniteRDD.map(_._2)
    val df: DataFrame = spark.sqlContext.createDataFrame(rdd1, schema)

    lazy val timeZone = DateTimeUtils.getTimeZone(spark.sessionState.conf.sessionLocalTimeZone)
    val rows: Array[Array[String]] = schema.fieldNames +: df.take(count).map { row =>
      row.toSeq.toArray.map { cell =>
        val str = cell match {
          case null => "null"
          case binary: Array[Byte] => binary.map("%02X".format(_)).mkString("[", " ", "]")
          case array: Array[_] => array.mkString("[", ", ", "]")
          case seq: Seq[_] => seq.mkString("[", ", ", "]")
          case d: Date =>
            DateTimeUtils.dateToString(DateTimeUtils.fromJavaDate(d))
          case ts: Timestamp =>
            DateTimeUtils.timestampToString(DateTimeUtils.fromJavaTimestamp(ts), timeZone)
          case _ => cell.toString
        }
        str
      }: Array[String]
    }
    spark.close()
    rows
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
    val results: ExecutionResults[UUID, String] =
			executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
		WorkflowExecutionResult(workflowId, results)
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
