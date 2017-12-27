package com.hashmap.haf.scheduler.consumer.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.hashmap.haf.scheduler.actors.WorkflowEventListenerActor
import com.hashmap.haf.scheduler.actors.WorkflowEventListenerActor.{AddJob, DropJob}
import org.springframework.stereotype.Component
import spray.json.DefaultJsonProtocol

import scala.io.StdIn

final case class WorkflowEvent(id: Long, cronExpression: String)

object WorkflowEventImplicits {
  implicit def workflowEventToMap(workflowEvent: WorkflowEvent): Map[String, String] = {
    Map("id" -> workflowEvent.id.toString, "cronExpression" -> workflowEvent.cronExpression)
  }
  implicit def mapToWorkflowEvent(storedMap: Map[String, String]): WorkflowEvent  = {
    WorkflowEvent(storedMap("id").toLong, storedMap("cronExpression"))
  }
}

  // collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val workflowEventFormat = jsonFormat2(WorkflowEvent)
}

@Component
class EventConsumer {
  def start() =  EventConsumer._start
}

object EventConsumer extends JsonSupport {
  def _start = {
    //WebServer.startServer("localhost", 8181)
    implicit val system = ActorSystem("SchedulerApp")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val workflowEventListenerActor = system.actorOf(WorkflowEventListenerActor.props)

    val route =
      path("workflow") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Please submit workflow</h1>"))
        } ~
          post {
            entity(as[WorkflowEvent]) { workflowEvent => // will unmarshal JSON to WorkflowEvent
              workflowEventListenerActor ! AddJob(workflowEvent.id, workflowEvent.cronExpression)
              complete(s"Submitted workflow with id ${workflowEvent.id} and expressions ${workflowEvent.cronExpression}")
            }
          } ~
          delete {
            entity(as[WorkflowEvent]) { workflowEvent => // will unmarshal JSON to WorkflowEvent
              workflowEventListenerActor ! DropJob(workflowEvent.id)
              complete(s"Submitted workflow with id ${workflowEvent.id} and expressions ${workflowEvent.cronExpression}")
            }
          }
      }
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

    println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}


//
//object WebServer extends HttpApp with JsonSupport{
//  val _system = this.systemReference.get()
//  //val schedulerRouter = _system.actorOf(RoundRobinPool(5).props(SchedulerActor.props(new QuartzScheduler(_system))), "SchedulerRouter")
//  val schedulerActor = _system.actorOf(SchedulerActor.props(new QuartzScheduler(_system)))
//  val executorActor = _system.actorOf(ExecutorActor.props(new WorkflowExecutor()))
//  val workflowEventListenerActor = _system.actorOf(WorkflowEventListenerActor.props(schedulerActor, executorActor))
//
//  import WorkflowEvent._
//
//  override def routes: Route =
//    path("workflow") {
//      get {
//        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Please submit workflow</h1>"))
//      } ~
//      post {
//        entity(as[WorkflowEvent]) { workflowEvent => // will unmarshal JSON to WorkflowEvent
//          workflowEventListenerActor ! AddJob(workflowEvent.id, workflowEvent.cronExpression)
//          complete(s"Submitted workflow with id ${workflowEvent.id} and expressions ${workflowEvent.cronExpression}")
//        }
//      }
//    }
//}





