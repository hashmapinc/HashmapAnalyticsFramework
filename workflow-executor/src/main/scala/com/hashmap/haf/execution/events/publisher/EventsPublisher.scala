package com.hashmap.haf.execution.events.publisher

import com.hashmap.haf.execution.events.Event

trait EventsPublisher {

  def publish(event: Event)

}
