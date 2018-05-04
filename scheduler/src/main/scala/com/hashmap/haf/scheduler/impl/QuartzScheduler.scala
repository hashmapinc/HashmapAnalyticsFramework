package com.hashmap.haf.scheduler.impl


import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.haf.scheduler.api.Scheduler
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.util.{Failure, Success, Try}

@Service
case class QuartzScheduler @Autowired()(scheduler: QuartzSchedulerExtension) extends Scheduler {

  val logger = LoggerFactory.getLogger(classOf[QuartzScheduler])

  override def createJob(_name: String, _cronExpression: String) =
    logErrorIfAny(Try(scheduler.createSchedule(name = _name, cronExpression = _cronExpression)))

  override def submitJob(_name: String, _subscriberActor: ActorRef, msg: AnyRef) =
    logErrorIfAny(Try(scheduler.schedule(_name, _subscriberActor, msg)))

  override def updateJob(_name: String, _subscriberActor: ActorRef, _cronExpression: String, msg: AnyRef) =
    logErrorIfAny(Try(scheduler.rescheduleJob(_name, _subscriberActor, msg,  cronExpression = _cronExpression)))

  override def suspendJob(_name: String) = logErrorIfAny(Try(scheduler.suspendJob(_name)))

  override def resumeJob(_name: String) = logErrorIfAny(Try(scheduler.resumeJob(_name)))

  override def SuspendAll = logErrorIfAny(Try(scheduler.suspendAll()))

  override def cancelJob(_name: String) = logErrorIfAny(Try(scheduler.cancelJob(_name)))


  private def logErrorIfAny[T](tryObj: Try[T]): Boolean = {
    tryObj match {
      case Success(_) => true
      case Failure(e) =>
        logger.error(e.getMessage)
        logger.debug(e.getStackTrace.toString)
        false
    }
  }
}
