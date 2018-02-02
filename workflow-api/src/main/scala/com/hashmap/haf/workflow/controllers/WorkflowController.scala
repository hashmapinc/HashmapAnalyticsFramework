package com.hashmap.haf.workflow.controllers
import java.util

import com.hashmap.haf.workflow.model.{SavedWorkflow, SavedWorkflowWithXML}
import com.hashmap.haf.workflow.service.WorkflowService
import com.hashmap.haf.workflow.util.UUIDConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, MediaType}
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._

@RestController
@RequestMapping(Array("/api"))
class WorkflowController @Autowired()(private val workflowService: WorkflowService) {

  @RequestMapping(value = Array("/workflows/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def findById(@PathVariable("workflowId") workflowId: String): String = {
     workflowService.findById(UUIDConverter.fromString(workflowId)).toXml.toString
  }

  @RequestMapping(value = Array("/workflows"), method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseBody
  def findAll: util.Map[String, SavedWorkflowWithXML] = {
    workflowService.findAll.map(workflow => {
      val id = (workflow.toXml \ "@id").toString()
      id -> SavedWorkflowWithXML(id, (workflow.toXml \ "@name").toString(), workflow.toXml.toString())
    }).toMap.asJava
  }

  @RequestMapping(value = Array("/workflows"), method = Array(RequestMethod.PUT),
    consumes = Array("text/xml"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseBody
  def saveOrUpdate(@RequestBody workflowXml: String): SavedWorkflow = {
    val workflowSaved = workflowService.saveOrUpdate(workflowXml).toXml

    SavedWorkflow((workflowSaved \ "@id").toString(),
      (workflowSaved \ "@name").toString())
  }

  @RequestMapping(value = Array("/workflows/{workflowId}"), method = Array(RequestMethod.DELETE))
  @ResponseStatus(value = HttpStatus.OK)
  def delete(@PathVariable("workflowId") workflowId: String): Unit = {
    workflowService.delete(UUIDConverter.fromString(workflowId))
  }
}
