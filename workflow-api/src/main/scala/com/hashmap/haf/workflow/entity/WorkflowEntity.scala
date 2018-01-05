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
  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @JoinColumn(name = "WORKFLOW_ID")
  var sparkIgniteTaskEntities : java.util.List[BaseTaskEntity[String]] = _


  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="WORKFLOW_CONFIGURATIONS")
  var configurations: java.util.Map[String, String] = _

  override def toData(): DefaultWorkflow = {
    DefaultWorkflow(tasks = sparkIgniteTaskEntities.asScala.map(_.toData()).toList,
      name = name,
      configurations = configurations.asScala.toMap,
      id = getId)
  }
}

object WorkflowEntity {
  def apply(defaultWorkflow: DefaultWorkflow): WorkflowEntity = {
    val igniteTaskEntities: List[BaseTaskEntity[String]] = defaultWorkflow.tasks.map(a => SparkIgniteTaskEntity(a.asInstanceOf[SparkIgniteTask]))
    val workflowEntity = new WorkflowEntity(defaultWorkflow.getName)
    workflowEntity.setSparkIgniteTaskEntities(igniteTaskEntities.asJava)
    workflowEntity.setConfigurations(defaultWorkflow.configurations.asJava)
    workflowEntity.setId(defaultWorkflow.id)
    workflowEntity
  }
}

