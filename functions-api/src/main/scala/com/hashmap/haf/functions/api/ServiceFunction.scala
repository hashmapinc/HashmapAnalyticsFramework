package com.hashmap.haf.functions.api

trait ServiceFunction {
  def run(inputKey: String, outputKey:String, config: Any): Unit
}
