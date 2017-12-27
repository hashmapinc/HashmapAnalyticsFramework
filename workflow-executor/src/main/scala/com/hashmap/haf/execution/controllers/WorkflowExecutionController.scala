package com.hashmap.haf.execution.controllers

import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class WorkflowExecutionController {

  @RequestMapping(value = Array("/workflow/execute/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def executebyId(@PathVariable("workflowId") workflowId: String): String = {
    println("executing !!!!")
    "executing"
  }
}
