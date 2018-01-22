package com.hashmap.haf.scheduler.extension

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider, Props}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

object SpringExtension extends ExtensionId[SpringExtension] with ExtensionIdProvider {

  override def lookup(): ExtensionId[_ <: Extension] = SpringExtension

  override def createExtension(system: ExtendedActorSystem): SpringExtension =
    new SpringExtension

}

@Component
class SpringExtension extends Extension {
  var applicationContext: ApplicationContext = null

  def initialize(implicit applicationContext: ApplicationContext) = {
    this.applicationContext = applicationContext
    this
  }

  def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    this.applicationContext = applicationContext
  }

  def props(actorBeanName: String, args: AnyRef*): Props =
    args.length match {
      case 0 => Props.create(classOf[SpringActorProducer], applicationContext, actorBeanName)
      case _ => Props.create(classOf[SpringActorProducer], applicationContext, actorBeanName, args)
    }
}
