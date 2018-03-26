package com.hashmap.haf.execution.events.entity

import java.io.StringWriter
import java.sql.Timestamp

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.hashmap.haf.execution.events.Event
import javax.persistence._
import org.hibernate.annotations.{Type, TypeDef}

import scala.beans.BeanProperty

@Entity
@Table(name = "EVENTS")
//@TypeDef(name = "json", typeClass = classOf[JsonStringType])
class EventEntity extends BaseSqlEntity[Event]{

  @BeanProperty
  @Column(name = "target_id")
  var target: String = _

  @BeanProperty
  @Column
  var eventTimestamp: Timestamp = _

  @BeanProperty
  //@Type(`type` = "json")
  @Column(name = "event_body")
  var body: String = _

  /**
    * This method convert domain model object to data transfer object.
    *
    * @return the dto object
    */
  override def toData(): Event = Event(getId, target, eventTimestamp, EventEntity.mapper.readTree(body))
}

object EventEntity{

  val mapper = new ObjectMapper()

  def apply(event: Event): EventEntity = {
    val entity = new EventEntity()
    entity.setId(event.id)
    entity.setTarget(event.target)
    //TODO: DO UTC conversion
    entity.setEventTimestamp(event.eventTimestamp)
    val writer = new StringWriter()
    mapper.writeValue(writer, event.eventInfo)
    entity.setBody(writer.toString)
    entity
  }
}
