package com.hashmap.haf.datastore.impl

import com.hashmap.haf.datastore.api.DatastoreOptions
import org.apache.spark.sql.SparkSession

@SerialVersionUID(114L)
case class SparkDFOptions(sparkSession: SparkSession, tableName: String,
                          tableParameters: String = "template=partitioned") extends DatastoreOptions