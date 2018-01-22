package com.hashmap.haf.scheduler

import akka.actor.ActorSystem
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration, Scope}
import redis.RedisClient

@Configuration
class SchedulerApplicationConfig {
  @Bean
  @Scope("prototype")
  def actorSystem(context: ApplicationContext, springExtension: SpringExtension): ActorSystem = {
    val system = ActorSystem("AkkaTaskProcessing")
    springExtension.initialize(context)
    system
  }

  @Bean
  @Scope("prototype")
  def redisClientFactory(context: ApplicationContext, @Value("${db.redis.host}") host: String): RedisClient = {
    implicit val system = context.getBean(classOf[ActorSystem])
    RedisClient(host)
  }

  @Bean
  @Scope("prototype")
  def quartzExtensionFactory(system: ActorSystem): QuartzSchedulerExtension =
    QuartzSchedulerExtension(system)

  import feign.Request
  import org.springframework.context.annotation.Bean
  import org.springframework.core.env.ConfigurableEnvironment

  import org.springframework.core.env.ConfigurableEnvironment

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
    val ribbonReadTimeout = env.getProperty("ribbon.ReadTimeout", classOf[Int], 70000)
    val ribbonConnectionTimeout = env.getProperty("ribbon.ConnectTimeout", classOf[Int], 60000)
    new Request.Options(ribbonConnectionTimeout, ribbonReadTimeout)
  }

}
