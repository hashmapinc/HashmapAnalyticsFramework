package com.hashmap.haf.functions.jdbcReader

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.DataframeIgniteCache
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import org.apache.spark.sql.SparkSession

@IgniteFunction(functionClazz = "JdbcReaderSparkTask", service = "jdbcReaderService",
  configs = Array())
class JdbcReaderService extends ServiceFunction{

  var appName = ""
  @IgniteInstanceResource
  var ignite: Ignite = _
  val CONFIG = getClass.getResource("/cache.xml").toURI.toURL.toString
  override def run(inputKey: String, outputKey: String, config: Any): String = {
    val spark = SparkSession
      .builder()
      .appName("Spark JDBC Reader Service")
      .master("local")
      .getOrCreate()

    val cache = DataframeIgniteCache.create(CONFIG)

    //val opts: Map[String, String] = config.asInstanceOf[Map[String, String]]
    val opts: Map[String, String] = Map(
      "url" -> "jdbc:postgresql://192.168.1.67:5432/thingsboard",
      "dbtable" -> "ts_kv",
      "password" -> "postgres",
      "user" -> "postgres"
    )

    val newDs = spark.read.format("jdbc").options(opts).load

    cache.set(spark.sparkContext, newDs, outputKey)

    spark.close()
    ""
  }

  override def cancel(ctx: ServiceContext) = println("Cancelled")

  override def init(ctx: ServiceContext) = {
    appName = ctx.name()
  }

  override def execute(ctx: ServiceContext) = println("Executing")
}

