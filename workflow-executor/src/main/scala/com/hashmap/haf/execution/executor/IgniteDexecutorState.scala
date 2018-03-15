package com.hashmap.haf.execution.executor

import java.util
import org.apache.ignite.configuration.CollectionConfiguration
import com.github.dexecutor.core.{DexecutorState, Phase}
import com.github.dexecutor.core.graph._
import com.github.dexecutor.core.task.{ExecutionResult, ExecutionResults}
import org.apache.ignite.{Ignite, IgniteAtomicLong, IgniteCache}
import org.apache.ignite.cache.CacheAtomicityMode._
import org.apache.ignite.cache.CacheMode.PARTITIONED
import com.github.dexecutor.core.graph.Dag


class IgniteDexecutorState[T <: Comparable[T], R] (cacheName: String, ignite: Ignite) extends DexecutorState[T, R]{

  val CACHE_ID_PHASE: String = cacheName + "-phase"
  val CACHE_ID_GRAPH: String = cacheName + "-graph"
  val CACHE_ID_NODES_COUNT: String = cacheName + "-nodes-count"
  val CACHE_ID_PROCESSED_NODES: String = cacheName + "-processed-nodes"
  val CACHE_ID_DISCONTINUED_NODES: String = cacheName + "-discontinued-nodes"
  val CACHE_ID_ERRORED_NODES: String = cacheName + "-errored-nodes"

  val distributedCache: IgniteCache[String, Object] =  ignite.getOrCreateCache(cacheName + "distribute")
  distributedCache.put(CACHE_ID_PHASE, Phase.BUILDING)
  distributedCache.put(CACHE_ID_GRAPH, new DefaultDag())

  val nodesCount: IgniteAtomicLong = ignite.atomicLong(CACHE_ID_NODES_COUNT, 0, true)


  val setCfg = new CollectionConfiguration
  setCfg.setAtomicityMode(TRANSACTIONAL)
  setCfg.setCacheMode(PARTITIONED)

  val processedNodes: util.Collection[Node[T, R]] = ignite.set(CACHE_ID_PROCESSED_NODES, setCfg)
  val discontinuedNodes: util.Collection[Node[T, R]] = ignite.set(CACHE_ID_DISCONTINUED_NODES, setCfg)
  val erroredNodes: util.Collection[ExecutionResult[T, R]] = ignite.set(CACHE_ID_ERRORED_NODES, setCfg)


  @SuppressWarnings(Array("unchecked"))
  private def getDag: Dag[T, R] = {
    distributedCache.get(CACHE_ID_GRAPH).asInstanceOf[Dag[T, R]]
  }

  override def addIndependent(nodeValue: T): Unit = {
    val graph: Dag[T, R] = getDag
    graph.addIndependent(nodeValue)
    distributedCache.put(CACHE_ID_GRAPH, graph)
  }

  override def addDependency(evalFirstValue: T, evalAfterValue: T): Unit = {
    val graph: Dag[T, R] = getDag
    graph.addDependency(evalFirstValue, evalAfterValue)
    distributedCache.put(CACHE_ID_GRAPH, graph)
  }


  override def addAsDependentOnAllLeafNodes(nodeValue: T): Unit = {
    val graph: Dag[T, R] = getDag
    graph.addAsDependentOnAllLeafNodes(nodeValue)
    distributedCache.put(CACHE_ID_GRAPH, graph)
  }

  override def addAsDependencyToAllInitialNodes(nodeValue: T): Unit = {
    val graph: Dag[T, R] = getDag
    graph.addAsDependencyToAllInitialNodes(nodeValue)
    distributedCache.put(CACHE_ID_GRAPH, graph)
  }

  override def graphSize(): Int = getDag.size()

  override def getInitialNodes: util.Set[Node[T, R]] = getDag.getInitialNodes

  override def getGraphNode(id: T): Node[T, R] = getDag.get(id)

  override def getNonProcessedRootNodes: util.Set[Node[T, R]] = getDag.getNonProcessedRootNodes

  override def validate(validator: Validator[T, R]): Unit = validator.validate(getDag)

  override def setCurrentPhase(currentPhase: Phase): Unit = distributedCache.put(CACHE_ID_PHASE, currentPhase)

  override def getCurrentPhase: Phase = distributedCache.get(CACHE_ID_PHASE).asInstanceOf[Phase]

  override def getUnProcessedNodesCount: Int = nodesCount.get().toInt

  override def incrementUnProcessedNodesCount(): Unit = nodesCount.incrementAndGet()

  override def decrementUnProcessedNodesCount(): Unit = nodesCount.decrementAndGet()

  override def shouldProcess(node: Node[T, R]): Boolean = !isAlreadyProcessed(node) && allIncomingNodesProcessed(node)

  private def isAlreadyProcessed(node: Node[T, R]) = processedNodes.contains(node)

  private def allIncomingNodesProcessed(node: Node[T, R]) = node.getInComingNodes.isEmpty || areAlreadyProcessed(node.getInComingNodes)

  private def areAlreadyProcessed(nodes: java.util.Set[Node[T, R]]) = processedNodes.containsAll(nodes)

  override def markProcessingDone(node: Node[T, R]): Unit = processedNodes.add(node)

  override def getProcessedNodes: util.Collection[Node[T, R]] = new util.ArrayList[Node[T, R]](processedNodes)

  override def isDiscontinuedNodesNotEmpty: Boolean = !this.discontinuedNodes.isEmpty

  override def getDiscontinuedNodes: util.Collection[Node[T, R]] = new util.ArrayList[Node[T, R]](discontinuedNodes)

  override def markDiscontinuedNodesProcessed(): Unit = discontinuedNodes.clear()

  override def processAfterNoError(nodes: util.Collection[Node[T, R]]): Unit = discontinuedNodes.addAll(nodes)

  override def print(traversar: Traversar[T, R], action: TraversarAction[T, R]): Unit = traversar.traverse(getDag, action)

  override def addErrored(task: ExecutionResult[T, R]): Unit = erroredNodes.add(task)

  override def removeErrored(task: ExecutionResult[T, R]): Unit = erroredNodes.remove(task)


  override def getErrored: ExecutionResults[T, R] = {
    val result: ExecutionResults[T, R]  = new ExecutionResults()
    import scala.collection.JavaConversions._
    for (r <- erroredNodes) {
      result.add(r)
    }
    result
  }

  override def erroredCount(): Int = erroredNodes.size()

  override def forcedStop(): Unit = {
    // TODO Auto-generated method stub
  }

}
