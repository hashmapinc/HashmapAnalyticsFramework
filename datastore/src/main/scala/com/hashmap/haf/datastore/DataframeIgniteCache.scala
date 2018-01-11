package com.hashmap.haf.datastore

import org.apache.ignite.spark.{IgniteContext, IgniteRDD}
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}

object DataframeIgniteCache {

  def create():Datastore = {
    //todo replace with system property on node
    //System.setProperty("IGNITE_HOME", "/Users/jetinder/Downloads/apache-ignite-fabric-2.3.0-bin/")
    IgniteCacheInstance
  }

  private object IgniteCacheInstance extends Datastore {

    private val SCHEMA_STORE = "schemas" // All schemas for given data frame will be store here
    private val SUFFIX_FOR_SCHEMA = "_schema" // schema for given df and key will be store with key as key+_schema

    def set(sc: SparkContext, df: DataFrame, KEY: String){
      //val ic = new IgniteContext(sc, CONFIG, false)
      val ic = new IgniteContext(sc)
      val sharedRDD = ic.fromCache[String, Row](KEY)
      val rddSchemaCache = ic.ignite.getOrCreateCache[String, StructType](SCHEMA_STORE)
      rddSchemaCache.put(KEY+SUFFIX_FOR_SCHEMA, df.schema)
      sharedRDD.saveValues(df.rdd)
      //Future(ic.close())
      //ic.close()
    }

    def get(sc: SparkContext, KEY: String): (StructType, IgniteRDD[String, Row]) = {
      //val ic = new IgniteContext(sc, CONFIG, true)
      val ic = new IgniteContext(sc)
      val rddSchemaCache = ic.ignite.getOrCreateCache[String, StructType](SCHEMA_STORE)
      val key = ic.fromCache[String, Row](KEY)
      val schema = rddSchemaCache.get(KEY+SUFFIX_FOR_SCHEMA)
      val tup = (schema , key)
      //ic.close()
      tup
    }
  }


}

