package com.hashmap.haf.scheduler.controllers

import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.haf.scheduler.actors.WorkflowEventListenerActor.{AddJob, DropJob}
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class EventConsumerController @Autowired()(system: ActorSystem, springExtension: SpringExtension) {

  private val workflowEventListenerActor: ActorRef =
    system.actorOf(springExtension.props("workflowEventListenerActor"))

  @RequestMapping(value = Array("/workflow"))
  @ResponseStatus(value = HttpStatus.OK)
  def saveOrUpdate(@RequestBody workflowEvent: WorkflowEvent) =
    workflowEventListenerActor ! AddJob(workflowEvent)

  @RequestMapping(value = Array("/workflow/{workflowId}"), method = Array(RequestMethod.DELETE))
  @ResponseStatus(value = HttpStatus.OK)
  def delete(@PathVariable("workflowId") workflowId: String) =
    workflowEventListenerActor ! DropJob(workflowId)
}
