package com.hashmap.haf.datastore.api


trait Datastore[T, O <: DatastoreOptions] {
  def set(data: T, options: O)
  def get(options: O): T
}
