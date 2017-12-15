package com.hashmap.haf.workflow.dao

import java.util.UUID

import com.hashmap.haf.workflow.entity.WorkflowEntity
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WorkflowDaoImpl @Autowired()(private val workflowRepository: WorkflowRepository)extends WorkflowDao {

  @Transactional
  override def saveWorkflow(workflow: Workflow[UUID, String]): Workflow[UUID, String] = {
    val workflowEntity = WorkflowEntity(workflow.asInstanceOf[DefaultWorkflow])
    println("---- saving workflow entity -----"+workflowEntity)
    workflowRepository.save(workflowEntity).toData()
  }
}
