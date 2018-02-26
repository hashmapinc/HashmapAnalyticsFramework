package com.hashmap.haf.datastore.api

trait QueryableDataStore[T] {
  def query(queryStr:String, queryType: QueryType): T
}


