package com.hashmap.haf.workflow.dao

import java.util.UUID

import com.hashmap.haf.workflow.entity.WorkflowEntity
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import com.hashmap.haf.workflow.util.UUIDConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

@Component
class WorkflowDaoImpl @Autowired()(private val workflowRepository: WorkflowRepository) extends WorkflowDao {

  @Transactional
  override def saveOrUpdate(workflow: Workflow[UUID, String]): Workflow[UUID, String] = {
    val workflowEntity = WorkflowEntity(workflow.asInstanceOf[DefaultWorkflow])
    workflowRepository.save(workflowEntity).toData()
  }

  @Transactional
  override def findById(id: UUID): Option[Workflow[UUID, String]] = workflowRepository.findOne(UUIDConverter.fromTimeUUID(id)) match {
      case null => None
      case we => Some(we.toData())
  }


  @Transactional
  override def findAll: List[Workflow[UUID, String]] = {
    workflowRepository.findAll().toList.map(_.toData())
  }

  @Transactional
  override def deleteById(id: UUID): Unit = {
    workflowRepository.delete(UUIDConverter.fromTimeUUID(id))
  }
}
