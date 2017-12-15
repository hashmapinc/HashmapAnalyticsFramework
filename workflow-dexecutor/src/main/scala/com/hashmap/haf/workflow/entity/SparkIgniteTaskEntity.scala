package com.hashmap.haf.workflow.entity

import java.util
import javax.persistence._
import collection.JavaConverters._
import com.hashmap.haf.workflow.task.{BaseTask, SparkIgniteTask}

import beans.BeanProperty

@Entity
class SparkIgniteTaskEntity(
                             @BeanProperty val inputCache: String,
                             @BeanProperty val outputCache: String
                           ) extends BaseTaskEntity[String] {

  private def this() = this(null, null)

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="SPARK_IGNITE_TASKS_ARGUMENTS")
  var functionArguments: java.util.Map[String, String] = _

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="SPARK_IGNITE_TASKS_CONFIGURATIONS")
  var configurations: java.util.Map[String, String] = _

  override def toData(): SparkIgniteTask = {
    new SparkIgniteTask(name,
      getId,
      inputCache,
      outputCache,
      functionArguments.asScala.toMap,
      configurations.asScala.toMap,
      getTo.asScala.toList
    )
  }
}

object SparkIgniteTaskEntity {
  def apply(sparkIgniteTask: SparkIgniteTask): SparkIgniteTaskEntity = {
    val sparkIgniteTaskEntity = new SparkIgniteTaskEntity(
      sparkIgniteTask.inputCache,
      sparkIgniteTask.outputCache
    )
    sparkIgniteTaskEntity.setId(sparkIgniteTask.id)
    sparkIgniteTaskEntity.setName(sparkIgniteTask.name)
    sparkIgniteTaskEntity.setTo(sparkIgniteTask.to.asJava)
    sparkIgniteTaskEntity.setFunctionArguments(sparkIgniteTask.functionArguments.asJava)
    sparkIgniteTaskEntity.setConfigurations(sparkIgniteTask.configurations.asJava)
    sparkIgniteTaskEntity
  }

}

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
