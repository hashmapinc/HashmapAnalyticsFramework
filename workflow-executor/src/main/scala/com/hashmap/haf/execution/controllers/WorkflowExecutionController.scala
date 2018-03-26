package com.hashmap.haf.execution.controllers

import java.sql.{Date, Timestamp}

import com.hashmap.haf.datastore.{DataframeIgniteCache, Datastore}
import com.hashmap.haf.execution.clients.WorkflowServiceClient
import com.hashmap.haf.execution.exceptions.Exceptions.WorkflowNotFoundException
import com.hashmap.haf.execution.models.Responses.WorkflowExecutionResult
import com.hashmap.haf.execution.services.WorkflowExecutionService
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class WorkflowExecutionController @Autowired()(workflowServiceClient: WorkflowServiceClient,
																							 workflowExecutionService: WorkflowExecutionService) {
  private val logger = LoggerFactory.getLogger(classOf[WorkflowExecutionController])

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
  def executeById(@PathVariable("workflowId") workflowId: String): WorkflowExecutionResult = {

		logger.trace(s"Workflow execution request received for id $workflowId")

    val workflowXml: String = workflowServiceClient.getFunction(workflowId)
		logger.debug(s"Workflow XML for id $workflowId found is $workflowXml")

    if(workflowXml == null || workflowXml.isEmpty) {
			val errorMessage = s"Workflow not found for id $workflowId"
			logger.error(errorMessage)
			throw new WorkflowNotFoundException(errorMessage)
		}
		workflowExecutionService.executeWorkflow(workflowId, workflowXml)
  }
}
