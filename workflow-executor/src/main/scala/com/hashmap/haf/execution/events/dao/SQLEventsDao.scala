package com.hashmap.haf.execution.events.dao

import com.hashmap.haf.execution.events.Event
import com.hashmap.haf.execution.events.entity.EventEntity
import com.hashmap.haf.execution.events.repository.EventsRepository
import org.springframework.beans.factory.annotation.Autowired

class SQLEventsDao @Autowired()(repository: EventsRepository) extends EventsDao {

  override def save(event: Event): Event = {
    val entity: EventEntity = EventEntity(event)
    repository.save[EventEntity](entity).toData()
  }
}
