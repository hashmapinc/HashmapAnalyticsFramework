package com.hashmap.haf.workflow.dao

import com.hashmap.haf.workflow.entity.WorkflowEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

trait WorkflowRepository extends CrudRepository[WorkflowEntity, String] with JpaRepository[WorkflowEntity, String]{


}
