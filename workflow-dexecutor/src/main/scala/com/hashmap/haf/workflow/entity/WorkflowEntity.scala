package com.hashmap.haf.workflow.entity

import javax.persistence._
import collection.JavaConverters._
import com.hashmap.haf.workflow.models.DefaultWorkflow
import com.hashmap.haf.workflow.task.{BaseTask, SparkIgniteTask}

import beans.BeanProperty


@Entity
@Table(name = "WORKFLOW")
class WorkflowEntity(@BeanProperty val name: String) extends BaseSqlEntity[DefaultWorkflow]{

  private def this() = this(null)

  @BeanProperty
  @OneToMany(cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "WORKFLOW_ID")
  var sparkIgniteTaskEntities : java.util.List[BaseTaskEntity[String]] = _

  override def toData(): DefaultWorkflow = {
    DefaultWorkflow(sparkIgniteTaskEntities.asScala.map(_.toData()).toList, name, getId)
  }
}

object WorkflowEntity {
  def apply(defaultWorkflow: DefaultWorkflow): WorkflowEntity = {
    val igniteTaskEntities: List[BaseTaskEntity[String]] = defaultWorkflow.tasks.map(a => SparkIgniteTaskEntity(a.asInstanceOf[SparkIgniteTask]))
    val workflowEntity = new WorkflowEntity(defaultWorkflow.getName)
    workflowEntity.setSparkIgniteTaskEntities(igniteTaskEntities.asJava)
    workflowEntity.setId(defaultWorkflow.id)
    workflowEntity
  }
}

