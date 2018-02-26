package com.hashmap.haf.datastore.impl

import com.hashmap.haf.datastore.api.Datastore
import com.typesafe.config.ConfigFactory
import org.apache.ignite.spark.IgniteDataFrameSettings._
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions.monotonically_increasing_id


@SerialVersionUID(124L)
object IgniteSparkDFStore extends Datastore[DataFrame, SparkDFOptions] {
  private [this] val configFactory = ConfigFactory.load()
  private[this] val configPath:String = configFactory.getString("ignite.configPath")
  private[this] val primaryKeyFieldName = configFactory.getString("ignite.primaryKeyField")

  override def set(data: DataFrame, options: SparkDFOptions): Unit = {
    val df =
      if(data.columns.contains(primaryKeyFieldName))
        data
      else
        data.withColumn(primaryKeyFieldName,monotonically_increasing_id())

    //val df =
    df.write
      .format(FORMAT_IGNITE)
      .option(OPTION_CONFIG_FILE, configPath)
      .option(OPTION_TABLE, options.tableName)
      .option(OPTION_CREATE_TABLE_PRIMARY_KEY_FIELDS, primaryKeyFieldName)
      .option(OPTION_CREATE_TABLE_PARAMETERS, options.tableParameters)
      .mode(SaveMode.Overwrite)
      .save()
  }

  override def get(options: SparkDFOptions): DataFrame = {
    options.sparkSession.read
      .format(FORMAT_IGNITE)
      .option(OPTION_CONFIG_FILE, configPath)
      .option(OPTION_TABLE, options.tableName)
      .load()
  }
}
