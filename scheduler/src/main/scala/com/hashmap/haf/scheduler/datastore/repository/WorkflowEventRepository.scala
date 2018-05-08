package com.hashmap.haf.scheduler.datastore.repository

import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait WorkflowEventRepository extends CrudRepository[WorkflowEvent, String] {

}
