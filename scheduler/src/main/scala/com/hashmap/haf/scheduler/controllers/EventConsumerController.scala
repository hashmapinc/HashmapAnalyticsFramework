package com.hashmap.haf.scheduler.controllers

import java.util

import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.haf.scheduler.actors.WorkflowEventListenerActor.{AddJob, DropJob, ResumeJob, StopJob}
import com.hashmap.haf.scheduler.datastore.actors.DatastoreActor.{GetAll, GetEvents}
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, MediaType}
import org.springframework.web.bind.annotation._
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

@RestController
@RequestMapping(Array("/api"))
class EventConsumerController @Autowired()(system: ActorSystem, springExtension: SpringExtension) {

  private val workflowEventListenerActor: ActorRef =
    system.actorOf(springExtension.props("workflowEventListenerActor"))

  val datastoreActor = system.actorOf(springExtension.props("datastoreActor"))

  //To do: Convert this to PUT
  @RequestMapping(value = Array("/workflow"), method = Array(RequestMethod.PUT))
  @ResponseStatus(value = HttpStatus.OK)
  def saveOrUpdate(@RequestBody workflowEvent: WorkflowEvent) =
    workflowEventListenerActor ! AddJob(workflowEvent)

//  {
//    "id": "80cd7f0a878a646a2de00be4109246b",
//    "cronExpression": "0/5 0/1 * 1/1 * ? *",
//    "isRunning": true
//  }

  @RequestMapping(value = Array("/workflow/{workflowId}"), method = Array(RequestMethod.DELETE))
  @ResponseStatus(value = HttpStatus.OK)
  def delete(@PathVariable("workflowId") workflowId: String) =
    workflowEventListenerActor ! DropJob(workflowId)

  @RequestMapping(value = Array("/workflow/pause/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseStatus(value = HttpStatus.OK)
  def pause(@PathVariable("workflowId") workflowId: String) =
    workflowEventListenerActor ! StopJob(workflowId)


  @RequestMapping(value = Array("/workflow/resume/{workflowId}"), method = Array(RequestMethod.GET))
  @ResponseStatus(value = HttpStatus.OK)
  def resume(@PathVariable("workflowId") workflowId: String) =
    workflowEventListenerActor ! ResumeJob(workflowId)


  @RequestMapping(value = Array("/workflows"), method = Array(RequestMethod.POST),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseStatus(value = HttpStatus.OK)
  def get(@RequestBody workflowIds: util.List[String]) = {
    implicit val timeout: Timeout = Timeout(20 second)
    val futureWorkflows = (datastoreActor ? GetEvents(workflowIds.asScala.toList)).mapTo[List[WorkflowEvent]]
    Await.result(futureWorkflows, Duration.Inf).map(we => we.id -> we).toMap.asJava
  }

  @RequestMapping(value = Array("/workflows"), method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseStatus(value = HttpStatus.OK)
  def getAll = {
    implicit val timeout: Timeout = Timeout(30 second)
    val futureWorkflows = (datastoreActor ? GetAll).mapTo[List[WorkflowEvent]]
    Await.result(futureWorkflows, Duration.Inf).map(we => we.id -> we).toMap.asJava
  }
}
