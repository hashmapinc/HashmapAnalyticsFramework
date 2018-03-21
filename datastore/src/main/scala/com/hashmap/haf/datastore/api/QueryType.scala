package com.hashmap.haf.datastore.api

sealed trait QueryType
case object SELECT extends QueryType
case object INSERT extends QueryType
