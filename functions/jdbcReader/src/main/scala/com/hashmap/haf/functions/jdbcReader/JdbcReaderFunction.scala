package com.hashmap.haf.functions.jdbcReader

import java.util.Properties

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.impl.{IgniteSparkDFStore, SparkDFOptions}
import com.hashmap.haf.functions.constants.TaskConfigurationConstants._
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.util.Utils

import scala.concurrent.Future
import scala.util.{Failure, Success}

@IgniteFunction(functionClazz = "JdbcReaderSparkTask", service = "jdbcReaderService",
  configs = Array())
class JdbcReaderService extends ServiceFunction{

  var appName = ""
  @IgniteInstanceResource
  var ignite: Ignite = _
  override def run(inputKey: String, outputKey: String, functionArguments: Map[String, String], configurations: Map[String, String]): String = {
    println("Executing JDBC Reader Job.....")
    val (sparkAppName: String, sparkMaster: String, tableParameters: String) = getConfigurations(configurations)
    val (jdbcUrl: String, dbTable: String, jdbcUser: String, jdbcPassword: String) = getFunctionArguments(functionArguments)

    val spark = SparkSession
      .builder()
      .appName(sparkAppName)
      .master(sparkMaster)
      .getOrCreate()

    Logger.getRootLogger.setLevel(Level.DEBUG)
    Logger.getLogger("org.apache.ignite").setLevel(Level.DEBUG)

    val newDs: DataFrame = getDataframe(jdbcUrl, dbTable, jdbcUser, jdbcPassword, spark)
    println("Saving DF")
    IgniteSparkDFStore.set(newDs, SparkDFOptions(spark, outputKey, tableParameters))
    println("Displaying 10 rows of saved df")
    newDs.show(10)
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = Future { spark.close() }

    f onComplete {
      case Success(x) => println("JDBC: Successfully closed spark context")
      case Failure(e) => e.printStackTrace()
    }
    /*try{
      println("Closing spark context")
      spark.close()
    }catch {
      case e => e.printStackTrace()
    }*/
    println("Executed JDBC Reader Service")
    "Successful"
  }

  private def getDataframe(jdbcUrl: String, dbTable: String, jdbcUser: String, jdbcPassword: String, spark: SparkSession) = {
    val connectionProperties = new Properties()
    connectionProperties.put("user", jdbcUser)
    connectionProperties.put("password", jdbcPassword)
    spark.read.jdbc(jdbcUrl, dbTable, connectionProperties)
  }

  private def getConfigurations(configurations: Map[String, String]) = {
    val sparkAppName = configurations.getOrElse(SPARK_APP_NAME, throw new IllegalArgumentException(SPARK_APP_NAME + " not provided in configurations"))
    val sparkMaster = configurations.getOrElse(SPARK_MASTER, throw new IllegalArgumentException(SPARK_MASTER + " not provided in configurations"))
    val tableParameters = configurations.getOrElse(TABLE_PARAMETERS, "template=partitioned")
    (sparkAppName, sparkMaster, tableParameters)
  }

  private def getFunctionArguments(functionArguments: Map[String, String]) = {
    val jdbcUrl = functionArguments.getOrElse(JDBC_URL, throw new IllegalArgumentException(JDBC_URL + " not provided in task arguments"))
    val dbTable = functionArguments.getOrElse(JDBC_DB_TABLE, throw new IllegalArgumentException(JDBC_DB_TABLE + " not provided in task arguments"))
    val jdbcUser = functionArguments.getOrElse(JDBC_USER, throw new IllegalArgumentException(JDBC_USER + " not provided in task arguments"))
    val jdbcPassword = functionArguments.getOrElse(JDBC_PASSWORD, throw new IllegalArgumentException(JDBC_PASSWORD + " not provided in task arguments"))
    (jdbcUrl, dbTable, jdbcUser, jdbcPassword)
  }

  override def cancel(ctx: ServiceContext) = println("Cancelled JDBC")

  override def init(ctx: ServiceContext) = {
    appName = ctx.name()
  }

  override def execute(ctx: ServiceContext) = println("Executing")
}

