package com.hashmap.haf.workflow.controllers

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow
import com.hashmap.haf.workflow.service.WorkflowService
import com.hashmap.haf.workflow.util.UUIDConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

import scala.xml.Elem

@RestController
@RequestMapping(Array("/api"))
class WorkflowController @Autowired()(private val workflowService: WorkflowService) {

  @RequestMapping(value = Array("/workflows/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def findById(@PathVariable("workflowId") workflowId: String): String = {
     workflowService.findById(UUIDConverter.fromString(workflowId)).toXml.toString
  }

  @RequestMapping(value = Array("/workflows"), method = Array(RequestMethod.PUT), consumes = Array("text/xml"))
  @ResponseBody
  def saveOrUpdate(@RequestBody workflowXml: String): String = {
     workflowService.saveOrUpdate(workflowXml).toXml.toString
  }

  @RequestMapping(value = Array("/workflows/{workflowId}"), method = Array(RequestMethod.DELETE))
  @ResponseStatus(value = HttpStatus.OK)
  def delete(@PathVariable("workflowId") workflowId: String): Unit = {
    workflowService.delete(UUIDConverter.fromString(workflowId))
  }
}
