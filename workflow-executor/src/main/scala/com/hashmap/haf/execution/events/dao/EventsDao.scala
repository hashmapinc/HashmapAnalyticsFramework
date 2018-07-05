package com.hashmap.haf.execution.events.dao

import com.hashmap.haf.execution.events.Event

trait EventsDao {

  def save(event: Event): Event
}
