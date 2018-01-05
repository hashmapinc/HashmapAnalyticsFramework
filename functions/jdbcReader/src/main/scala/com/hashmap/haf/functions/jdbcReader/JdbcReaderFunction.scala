package com.hashmap.haf.functions.jdbcReader

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.{DataframeIgniteCache, Datastore}
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.Ignite
import org.apache.ignite.services.ServiceContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}

@IgniteFunction(functionClazz = "JdbcReaderSparkTask", service = "jdbcReaderService", configs = Array())
class JdbcReaderService extends ServiceFunction{

  var appName = ""
  var cache: Datastore = _
  //val CONFIG = getClass.getResource("/examples/cache.xml").toURI.toURL.toString
  override def run(inputKey: String, outputKey: String, config: Any): String = {
    val spark = SparkSession
      .builder()
      .appName("Spark JDBC Reader Service")
      .master("local")
      .getOrCreate()

    //val cache = DataframeIgniteCache.create(CONFIG)

    //val opts: Map[String, String] = config.asInstanceOf[Map[String, String]]
    val opts: Map[String, String] = Map(
      "url" -> "jdbc:postgresql:mydb",
      "dbtable" -> "cities",
      "password" -> "admin",
      "user" -> "postgres"
    )

    val newDs = spark.read.format("jdbc").options(opts).load

    cache.set(spark.sparkContext, newDs, outputKey)

    println("DF with following schema has been saved: ")
    newDs.schema.foreach(field => println(s"${field.name}: metadata=${field.metadata}"))

    spark.close()
    ""
  }

  override def cancel(ctx: ServiceContext) = println("Cancelled")

  override def init(ctx: ServiceContext) = {
    appName = ctx.name()
  }

  override def execute(ctx: ServiceContext) = println("Executing")
}

