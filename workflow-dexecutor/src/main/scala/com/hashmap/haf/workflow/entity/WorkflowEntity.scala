package com.hashmap.haf.workflow.entity

import java.util.UUID
import javax.persistence.{Column, Entity, Table}
import com.hashmap.haf.workflow.models.DefaultWorkflow
import com.hashmap.haf.workflow.util.UUIDConverter

@Entity
@Table(name = "workflow")
class WorkflowEntity(
                      id: UUID,
                      name: String,
                      tasks: List[SparkIgniteTaskEntity]
                    ) extends BaseSqlEntity[DefaultWorkflow](UUIDConverter.fromTimeUUID(id)){

  override def toData(): DefaultWorkflow = {
    DefaultWorkflow()
  }
}

object WorkflowEntity {
  def apply(defaultWorkflow: DefaultWorkflow): WorkflowEntity = {
    val tasks: List[SparkIgniteTaskEntity] = defaultWorkflow.getTasks.map(SparkIgniteTaskEntity(_))


    val workflowEntity = new WorkflowEntity(defaultWorkflow.getId, defaultWorkflow.getName, defaultWorkflow.getTasks)
    workflowEntity
  }
}

