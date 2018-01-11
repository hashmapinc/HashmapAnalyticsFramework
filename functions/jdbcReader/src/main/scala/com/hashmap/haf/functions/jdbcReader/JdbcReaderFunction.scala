package com.hashmap.haf.functions.jdbcReader

import java.util.Properties

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.DataframeIgniteCache
import com.hashmap.haf.functions.constants.TaskConfigurationConstants._
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import org.apache.spark.sql.{DataFrame, SparkSession}

@IgniteFunction(functionClazz = "JdbcReaderSparkTask", service = "jdbcReaderService",
  configs = Array())
class JdbcReaderService extends ServiceFunction{

  var appName = ""
  @IgniteInstanceResource
  var ignite: Ignite = _
  override def run(inputKey: String, outputKey: String, functionArguments: Map[String, String], configurations: Map[String, String]): String = {
    println("Executing JDBC Reader Job.....")
    val spark = SparkSession
      .builder()
      .appName(configurations.getOrElse(SPARK_APP_NAME, throw new IllegalArgumentException(SPARK_APP_NAME + " not provided in configurations")))
      .master(configurations.getOrElse(SPARK_MASTER, throw new IllegalArgumentException(SPARK_MASTER + " not provided in configurations")))
      .getOrCreate()

    val cache = DataframeIgniteCache.create()
    val jdbcUrl = functionArguments.getOrElse(JDBC_URL, throw new IllegalArgumentException(JDBC_URL + " not provided in task arguments"))
    val dbTable = functionArguments.getOrElse(JDBC_DB_TABLE, throw new IllegalArgumentException(JDBC_DB_TABLE + " not provided in task arguments"))
    val connectionProperties = new Properties()
    connectionProperties.put("user", functionArguments.getOrElse(JDBC_USER, throw new IllegalArgumentException(JDBC_USER + " not provided in task arguments")))
    connectionProperties.put("password", functionArguments.getOrElse(JDBC_PASSWORD, throw new IllegalArgumentException(JDBC_PASSWORD + " not provided in task arguments")))

    val newDs: DataFrame = spark.read.jdbc(jdbcUrl, dbTable, connectionProperties)

    cache.set(spark.sparkContext, newDs, outputKey)
    println("Setting to output cache .......showing only 10 rows of it")
    newDs.show(10)

    spark.close()
    println("Executed JDBC Reader Service")
    "Successful"
  }

  override def cancel(ctx: ServiceContext) = println("Cancelled")

  override def init(ctx: ServiceContext) = {
    appName = ctx.name()
  }

  override def execute(ctx: ServiceContext) = println("Executing")
}

