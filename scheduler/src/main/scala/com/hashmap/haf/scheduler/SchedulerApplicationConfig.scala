package com.hashmap.haf.scheduler

import akka.actor.ActorSystem
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration, Scope}
import feign.Request
import org.springframework.core.env.ConfigurableEnvironment
import redis.RedisClient

@Configuration
class SchedulerApplicationConfig {

  private val defaultRibbonReadTimeout = 70000
  private val defaultRibbonConnectTimeout = 60000

  @Bean
  @Scope("prototype")
  def actorSystem(context: ApplicationContext, springExtension: SpringExtension): ActorSystem = {
    val system = ActorSystem("AkkaTaskProcessing")
    springExtension.initialize(context)
    system
  }

  @Bean
  @Scope("prototype")
  def redisClientFactory(context: ApplicationContext, @Value("${spring.redis.host}") host: String): RedisClient = {
    implicit val system = context.getBean(classOf[ActorSystem])
    RedisClient(host)
  }

  @Bean
  @Scope("prototype")
  def quartzExtensionFactory(system: ActorSystem): QuartzSchedulerExtension =
    QuartzSchedulerExtension(system)



  /**
    * Method to create a bean to increase the timeout value,
    * It is used to overcome the Retryable exception while invoking the feign client.
    *
    * @param env ,
    *            An { @link ConfigurableEnvironment}
    * @return A { @link Request}
    */
  @Bean
  def requestOptions(env: ConfigurableEnvironment): Request.Options = {
    val ribbonReadTimeout = env.getProperty("ribbon.ReadTimeout", classOf[Int], defaultRibbonReadTimeout)
    val ribbonConnectionTimeout = env.getProperty("ribbon.ConnectTimeout", classOf[Int], defaultRibbonConnectTimeout)
    new Request.Options(ribbonConnectionTimeout, ribbonReadTimeout)
  }

}
