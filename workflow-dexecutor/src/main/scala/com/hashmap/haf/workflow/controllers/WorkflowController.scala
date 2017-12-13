package com.hashmap.haf.workflow.controllers

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow
import com.hashmap.haf.workflow.service.WorkflowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class WorkflowController @Autowired()(private val workflowService: WorkflowService) {

  @RequestMapping(value = Array("/workflow/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getWorkflowById(@PathVariable("workflowId") applicationId: String): String = {
    println("getting Workflow by id")
    applicationId
  }

  @RequestMapping(value = Array("/workflow"), method = Array(RequestMethod.POST), consumes = Array("text/xml"))
  @ResponseBody
  def saveWorkflow(@RequestBody workflowXml: String): Workflow[UUID, String] = {
    val workflow = workflowService.saveWorkflow(workflowXml)
    println(workflow)
    workflow
  }
}
