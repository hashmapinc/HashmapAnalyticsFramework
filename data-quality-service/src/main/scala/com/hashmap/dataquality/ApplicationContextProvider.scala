package com.hashmap.dataquality

import org.springframework.context
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component

object ApplicationContextProvider {
  private var context:  ApplicationContext = _

  def getApplicationContext: ApplicationContext = context
}

@Component
class ApplicationContextProvider extends ApplicationContextAware {

  override def setApplicationContext(applicationContext: context.ApplicationContext): Unit = {
    ApplicationContextProvider.context = applicationContext
  }
}
