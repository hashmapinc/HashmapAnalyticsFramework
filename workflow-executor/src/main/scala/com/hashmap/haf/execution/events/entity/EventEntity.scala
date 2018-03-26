package com.hashmap.haf.execution.events.entity

import java.sql.Timestamp
import com.fasterxml.jackson.databind.JsonNode
import com.hashmap.haf.execution.events.Event
import javax.persistence._
import org.hibernate.annotations.Type
import scala.beans.BeanProperty

@Entity
@Table(name = "EVENTS")
class EventEntity extends BaseSqlEntity[Event]{

  @BeanProperty
  @Column(name = "target_id")
  var target: String = _

  @BeanProperty
  @Column
  var eventTimestamp: Timestamp = _

  @BeanProperty
  @Type(`type` = "json")
  @Column(name = "event_body")
  var body: JsonNode = _

  /**
    * This method convert domain model object to data transfer object.
    *
    * @return the dto object
    */
  override def toData(): Event = Event(getId, target, eventTimestamp, body)
}

object EventEntity{

  def apply(event: Event): EventEntity = {
    val entity = new EventEntity()
    entity.setId(event.id)
    entity.setTarget(event.target)
    //TODO: DO UTC conversion
    entity.setEventTimestamp(event.eventTimestamp)
    entity.setBody(event.eventInfo)
    entity
  }
}
