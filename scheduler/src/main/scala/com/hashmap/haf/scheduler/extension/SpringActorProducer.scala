package com.hashmap.haf.scheduler.extension

import akka.actor.Actor
import akka.actor.IndirectActorProducer
import org.springframework.context.ApplicationContext

class SpringActorProducer(val applicationContext: ApplicationContext,
                          val actorBeanName: String, val args: Seq[AnyRef]) extends IndirectActorProducer {
  def this(applicationContext: ApplicationContext, actorBeanName: String) = this(applicationContext, actorBeanName, Seq.empty[AnyRef])
  override def produce: Actor =
    args.length match {
      case 0 => applicationContext.getBean(actorBeanName).asInstanceOf[Actor]
      case _ => applicationContext.getBean(actorBeanName, args).asInstanceOf[Actor]
    }

  override def actorClass: Class[_ <: Actor] =
    applicationContext.getType(actorBeanName).asInstanceOf[Class[_ <: Actor]]
}