package com.hashmap.haf.workflow.entity

import javax.persistence._

import com.hashmap.haf.workflow.task.SparkIgniteTask

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Entity
class SparkIgniteTaskEntity(
                             @BeanProperty val className: String,
                             @BeanProperty val inputCache: String,
                             @BeanProperty val outputCache: String
                           ) extends BaseTaskEntity[String] {

  private def this() = this(null, null, null)

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="SPARK_IGNITE_TASK_ARGUMENTS")
  var functionArguments: java.util.Map[String, String] = _

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="SPARK_IGNITE_TASK_CONFIGURATIONS")
  var configurations: java.util.Map[String, String] = _

  override def toData(): SparkIgniteTask = {
    new SparkIgniteTask(name = name,
      id = getId,
      className = className,
      inputCache = inputCache,
      outputCache = outputCache,
      functionArguments = functionArguments.asScala.toMap,
      configurations = configurations.asScala.toMap,
      to = getTo.asScala.toList
    )
  }
}

object SparkIgniteTaskEntity {
  def apply(sparkIgniteTask: SparkIgniteTask): SparkIgniteTaskEntity = {
    val sparkIgniteTaskEntity = new SparkIgniteTaskEntity(
      className = sparkIgniteTask.className,
      inputCache = sparkIgniteTask.inputCache,
      outputCache = sparkIgniteTask.outputCache
    )
    sparkIgniteTaskEntity.setId(sparkIgniteTask.id)
    sparkIgniteTaskEntity.setName(sparkIgniteTask.name)
    sparkIgniteTaskEntity.setTo(sparkIgniteTask.to.asJava)
    sparkIgniteTaskEntity.setFunctionArguments(sparkIgniteTask.functionArguments.asJava)
    sparkIgniteTaskEntity.setConfigurations(sparkIgniteTask.configurations.asJava)
    sparkIgniteTaskEntity
  }

}


