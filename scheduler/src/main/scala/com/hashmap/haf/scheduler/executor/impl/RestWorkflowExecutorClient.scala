package com.hashmap.haf.scheduler.executor.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.hashmap.haf.scheduler.clients.WorkflowExecutorClient
import com.hashmap.haf.scheduler.executor.api.Executor
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.concurrent.Future
import scala.util.{Failure, Success}

@Service
class RestWorkflowExecutorClient @Autowired()(system: ActorSystem,
                                              workflowExecutorClient: WorkflowExecutorClient) extends Executor  {

  override def execute(id: String):Status = {
    println(s"Executing workflow for id: $id")
    val response: String = workflowExecutorClient.getFunction(id)
    println(response)
    200
  }

  override def status(id: Int) = ???

}


/*
// URL Based service
@Service
class RestWorkflowExecutorClient @Autowired()(system: ActorSystem,
                                                   @Value("${workflow.executor.url}") url: String) extends Executor  {

  override def execute(id: String):Status = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val _system = system

    println(s"Executing workflow with url: $url id: $id") //0/30 0/1 * 1/1 * ? *
    val responseFuture: Future[HttpResponse] =Http().singleRequest(HttpRequest(uri = url + id))
    responseFuture
      .onComplete {
        case Success(res) => println(res); 200
        case Failure(_)   => println("something wrong"); 500
      }

    200
  }

  override def status(id: Int) = ???

}

*/
