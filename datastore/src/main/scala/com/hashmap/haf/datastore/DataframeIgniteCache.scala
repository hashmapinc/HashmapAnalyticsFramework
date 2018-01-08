package com.hashmap.haf.datastore

import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.spark.{IgniteContext, IgniteRDD}
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}

object DataframeIgniteCache {

  private var CONFIG:String = _ // Configuration to create ignite cache

  def create(configUrl: String):Datastore = {
    //todo replace with system property on node
    System.setProperty("IGNITE_HOME", "/Users/jetinder/Downloads/apache-ignite-fabric-2.3.0-bin/")
    CONFIG = configUrl
    IgniteCacheInstance
  }

  private object IgniteCacheInstance extends Datastore{

    private val SCHEMA_STORE = "schemas" // All schemas for given data frame will be store here
    private val SUFFIX_FOR_SCHEMA = "_schema" // schema for given df and key will be store with key as key+_schema

    private val schemaCacheConfig = makeSchemaCacheConfig(SCHEMA_STORE) // Creating dynamic cache configuration for schema store

    def set(sc: SparkContext, df: DataFrame, KEY: String){
      //val ic = new IgniteContext(sc, CONFIG, false)
      val ic = new IgniteContext(sc)
      val sharedRDD = ic.fromCache[String, Row](KEY)
      val rddSchemaCache = ic.ignite.getOrCreateCache[String, StructType]("schemas")
      rddSchemaCache.put(KEY+SUFFIX_FOR_SCHEMA, df.schema)
      sharedRDD.saveValues(df.rdd)
      //Future(ic.close())
      //ic.close()
    }

    def get(sc: SparkContext, KEY: String): (StructType, IgniteRDD[String, Row]) = {
      //val ic = new IgniteContext(sc, CONFIG, true)
      val ic = new IgniteContext(sc)
      val rddSchemaCache = ic.ignite.getOrCreateCache[String, StructType]("schemas")
      val key = ic.fromCache[String, Row](KEY)
      val schema = rddSchemaCache.get(KEY+"_schema")
      val tup = (schema , key)
      //ic.close()
      tup
    }

    private def makeSchemaCacheConfig(name: String) =
      new CacheConfiguration[String, StructType](name)
        .setAtomicityMode(CacheAtomicityMode.ATOMIC)
        .setBackups(1)
        .setAffinity(new RendezvousAffinityFunction(false, 1))
  }


}

