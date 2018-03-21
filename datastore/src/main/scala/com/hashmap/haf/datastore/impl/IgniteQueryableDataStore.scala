package com.hashmap.haf.datastore.impl

import java.time.Instant
import java.util

import com.hashmap.haf.datastore.api.{INSERT, QueryType, QueryableDataStore, SELECT}
import org.apache.ignite.{Ignite, IgniteCache}
import org.apache.ignite.cache.query.{FieldsQueryCursor, SqlFieldsQuery}
import org.apache.ignite.configuration.CacheConfiguration

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class IgniteQueryableDataStore(ignite: Ignite) extends QueryableDataStore[util.List[util.List[String]]] {
  val CACHE_NAME = "temp_" + Instant.now.getEpochSecond
  val ccfg = new CacheConfiguration[Any, Any](CACHE_NAME).setSqlSchema("PUBLIC")

  override def query(queryStr: String, queryType: QueryType): util.List[util.List[String]] = {
    val tempCache = ignite.getOrCreateCache(ccfg)
    try {
      queryType match {
        case SELECT => selectQuery(queryStr, tempCache)
        case INSERT => ???
      }
    } finally {
      tempCache.destroy()
    }
  }

  private def selectQuery(queryStr: String, tempCache: IgniteCache[Any, Any]) = {
    val cursor = tempCache.query(new SqlFieldsQuery(queryStr))
    val (columnNames: util.List[String], tableData: util.List[util.List[String]]) = getTableDataAndColumns(cursor)
    consolidatedData(columnNames, tableData)
  }

  private def consolidatedData(columnNames: util.List[String], tableData: util.List[util.List[String]]) = {
    //As given collection is not modifiable, we need to convert it into modifiable collection
    val newList = new util.ArrayList(tableData)
    newList.add(0, columnNames)
    newList
  }

  // Converting collection of Any types to collection of Strings
  implicit private def toCollectionOfString(tableData : util.List[util.List[_]]): util.List[util.List[String]] =
    tableData.map(lst => lst.toList.filter(_ != null).map(_.toString).asJava).asJava

  private def getTableDataAndColumns(cursor: FieldsQueryCursor[util.List[_]]) = {
    val clCnt = cursor.getColumnsCount()
    val fNames: util.List[String] = (0 until clCnt).map(cursor.getFieldName(_)).toList
    val _data: util.List[util.List[String]] = cursor.getAll // Implicit conversion
    (fNames, _data)
  }
}

object IgniteQueryableDataStore{
  def apply(ignite: Ignite): IgniteQueryableDataStore = new IgniteQueryableDataStore(ignite)
}