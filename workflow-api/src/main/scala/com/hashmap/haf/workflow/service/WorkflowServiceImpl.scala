package com.hashmap.haf.workflow.service

import java.util.UUID
import com.hashmap.haf.workflow.dao.WorkflowDao
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WorkflowServiceImpl @Autowired()(private val workflowDao: WorkflowDao) extends WorkflowService {

  override def  saveOrUpdate(workflowXml: String): Workflow[UUID, String] = {
    val workflow: Workflow[UUID, String] = DefaultWorkflow(workflowXml)
    workflowDao.saveOrUpdate(workflow)
  }

  override def findById(id: UUID): Option[Workflow[UUID, String]] = {
      workflowDao.findById(id)
  }

  override def findAll: List[Workflow[UUID, String]] = {
    workflowDao.findAll
  }

  override def delete(id: UUID): Unit = {
    workflowDao.deleteById(id)
  }
}
