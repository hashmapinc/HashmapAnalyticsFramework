package com.hashmap.haf.workflow.entity

import java.util
import javax.persistence._

import com.hashmap.haf.workflow.task.BaseTask

import scala.beans.BeanProperty

@Entity
@Table(name = "WORKFLOW_TASKS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class BaseTaskEntity[R] extends BaseSqlEntity[BaseTask[R]]{

  @BeanProperty
  protected var name: String = _

  @BeanProperty
  @ElementCollection
  @CollectionTable(name="TASKS_TO_TASKS_MAPPING")
  @Column(name="TO_BASE_TASK_ENTITY_ID")
  var to: java.util.List[String] = new util.ArrayList[String]()
}
