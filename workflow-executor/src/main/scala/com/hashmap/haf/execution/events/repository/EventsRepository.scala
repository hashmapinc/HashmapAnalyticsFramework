package com.hashmap.haf.execution.events.repository

import java.util.UUID

import com.hashmap.haf.execution.events.entity.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait EventsRepository extends JpaRepository[EventEntity, UUID]{

}
