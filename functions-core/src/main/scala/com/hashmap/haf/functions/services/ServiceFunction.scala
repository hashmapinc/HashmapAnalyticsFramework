package com.hashmap.haf.functions.services

import org.apache.ignite.services.Service

trait ServiceFunction extends Service{
  def run(inputKey: String, outputKey: String,functionArguments: Map[String, String], configurations: Map[String, String]): String
}
