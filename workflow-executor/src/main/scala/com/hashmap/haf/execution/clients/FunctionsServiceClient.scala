package com.hashmap.haf.execution.clients

import com.hashmap.haf.models.IgniteFunctionType
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.{GetMapping, PathVariable}

@FeignClient(name = "functions-service")
trait FunctionsServiceClient {

  @GetMapping(path = Array("/api/functions/{clazz}"))
  def getFunction(@PathVariable("clazz") functionClazz: String): String

}

@FeignClient(name = "workflow-service")
trait WorkflowServiceClient {

  @GetMapping(path = Array("/api/workflows/{workflowId}"))
  def getFunction(@PathVariable("workflowId") workflowId: String): String

}
