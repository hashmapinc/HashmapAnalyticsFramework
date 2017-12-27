package com.hashmap.haf.scheduler.executor.api

trait Executor{
  type Status = Int
  def execute(id: String):Status
  def status(id: Int): Status
}