package com.hashmap.haf.workflow.entity

import javax.persistence._

import com.hashmap.haf.workflow.task.LivyTask

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Entity
class LivyTaskEntity (
                       @BeanProperty val jar: String,
                       @BeanProperty val className: String,
                       @BeanProperty val inputCache: String,
                       @BeanProperty val outputCache: String
                     ) extends BaseTaskEntity[String] {

  @Transient
  private val serialVersionUID = 7969830667450807686L

  private def this() = this(null, null, null, null)

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="LIVY_TASK_ARGUMENTS")
  var functionArguments: java.util.Map[String, String] = _

  @BeanProperty
  @ElementCollection
  @MapKeyColumn(name="PARAM")
  @Column(name="VALUE")
  @CollectionTable(name="LIVY_TASK_CONFIGURATIONS")
  var configurations: java.util.Map[String, String] = _

  override def toData(): LivyTask = {
    new LivyTask(name = name,
      id = getId,
      jar = jar,
      className = className,
      inputCache = inputCache,
      outputCache = outputCache,
      functionArguments = functionArguments.asScala.toMap,
      configurations = configurations.asScala.toMap,
      to = getTo.asScala.toList
    )
  }
}

object LivyTaskEntity {
  def apply(livyTask: LivyTask): LivyTaskEntity = {
    val livyTaskEntity = new LivyTaskEntity(
      jar = livyTask.jar,
      className = livyTask.className,
      inputCache = livyTask.inputCache,
      outputCache = livyTask.outputCache
    )
    livyTaskEntity.setId(livyTask.id)
    livyTaskEntity.setName(livyTask.name)
    livyTaskEntity.setTo(livyTask.to.asJava)
    livyTaskEntity.setFunctionArguments(livyTask.functionArguments.asJava)
    livyTaskEntity.setConfigurations(livyTask.configurations.asJava)
    livyTaskEntity
  }

}
