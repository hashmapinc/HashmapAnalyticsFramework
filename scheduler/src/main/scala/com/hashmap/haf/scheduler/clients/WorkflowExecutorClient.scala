package com.hashmap.haf.scheduler.clients

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.scheduling.annotation.{Async, EnableAsync}
import org.springframework.web.bind.annotation.{GetMapping, PathVariable}

@EnableAsync
@FeignClient(name = "workflow-executor")
trait WorkflowExecutorClient {
  @Async
  @GetMapping(path = Array("/api/workflow/execute/{workflowId}"))
  def getFunction(@PathVariable("workflowId") workflowId: String): String
}
