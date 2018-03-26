package com.hashmap.haf.workflow.entity

import javax.persistence._

import com.hashmap.haf.workflow.models.DefaultWorkflow
import com.hashmap.haf.workflow.task.{LivyTask, SparkIgniteTask}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


@Entity
@Table(name = "WORKFLOW")
class WorkflowEntity(@BeanProperty val name: String) extends BaseSqlEntity[DefaultWorkflow]{

  private def this() = this(null)

  @BeanProperty
  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @JoinColumn(name = "WORKFLOW_ID")
  var taskEntities : java.util.List[BaseTaskEntity[String]] = _


  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="WORKFLOW_CONFIGURATIONS")
  var configurations: java.util.Map[String, String] = _

  override def toData(): DefaultWorkflow = {
    DefaultWorkflow(tasks = taskEntities.asScala.map(_.toData()).toList,
      name = name,
      configurations = configurations.asScala.toMap,
      id = getId)
  }
}

object WorkflowEntity {
  def apply(defaultWorkflow: DefaultWorkflow): WorkflowEntity = {
    val taskEntities: List[BaseTaskEntity[String]] = defaultWorkflow.tasks.map {
      case task: SparkIgniteTask => SparkIgniteTaskEntity(task)
      case task: LivyTask => LivyTaskEntity(task)
      case _ => throw new IllegalArgumentException("Task is not of any specified type")
    }
    val workflowEntity = new WorkflowEntity(defaultWorkflow.getName)
    workflowEntity.setTaskEntities(taskEntities.asJava)
    workflowEntity.setConfigurations(defaultWorkflow.configurations.asJava)
    workflowEntity.setId(defaultWorkflow.id)
    workflowEntity
  }
}

