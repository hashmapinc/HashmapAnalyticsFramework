package com.hashmap.haf.workflow.dao

import com.hashmap.haf.workflow.entity.WorkflowEntity
import org.springframework.data.repository.CrudRepository
trait WorkflowRepository extends CrudRepository[WorkflowEntity, String]{


}
