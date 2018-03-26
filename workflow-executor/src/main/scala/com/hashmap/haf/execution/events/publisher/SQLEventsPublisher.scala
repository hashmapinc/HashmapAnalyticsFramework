package com.hashmap.haf.execution.events.publisher

import com.hashmap.haf.execution.events.Event
import com.hashmap.haf.execution.events.dao.EventsDao
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import scala.util._

class SQLEventsPublisher @Autowired()(dao: EventsDao) extends EventsPublisher{

  private val logger = LoggerFactory.getLogger(classOf[SQLEventsPublisher])

  override def publish(event: Event): Unit = {
    Try(dao.save(event)) match {
      case Success(_) => logger.debug(s"Event for ${event.target} is published.")
      case Failure(e) => logger.error(s"Error ${e.getMessage} occurred while publishing event $event")
    }
  }
}
