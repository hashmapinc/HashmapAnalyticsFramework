package com.hashmap.haf.workflow.service

import java.util.UUID

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.hashmap.haf.workflow.models.Workflow
import org.junit.{Assert, Test}
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.{ContextConfiguration, TestPropertySource}
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.web.servlet.config.annotation.EnableWebMvc

import scala.collection.immutable

@RunWith(classOf[SpringRunner])
@EnableWebMvc
@SpringBootTest
@TestPropertySource(locations = Array({"classpath:workflow-api-test.properties"}))
class WorkflowServiceSpec {

  @Autowired
  var workflowService: WorkflowService = _


  @Test
  def testSave(): Unit = {
    val workflowXML: String = Resources.toString(Resources.getResource("test-spark-workflow.xml"), Charsets.UTF_8)
    val savedWorkflow = workflowService.saveOrUpdate(workflowXML)

    Assert.assertNotNull(savedWorkflow.getId)
    Assert.assertEquals("Sample Workflow", savedWorkflow.getName)
  }

  @Test
  def testFindById(): Unit = {
    val workflowXML: String = Resources.toString(Resources.getResource("test-spark-workflow.xml"), Charsets.UTF_8)
    val savedWorkflow = workflowService.saveOrUpdate(workflowXML)

    val retrievedWorkflow = workflowService.findById(savedWorkflow.getId)
    Assert.assertEquals(savedWorkflow.getName, retrievedWorkflow.get.getName)
  }

  @Test
  def testFindAll(): Unit = {
    val workflowXML1: String = Resources.toString(Resources.getResource("test-spark-workflow.xml"), Charsets.UTF_8)
    val workflowXML2: String = Resources.toString(Resources.getResource("test-spark-workflow-1.xml"), Charsets.UTF_8)

    workflowService.saveOrUpdate(workflowXML1)
    workflowService.saveOrUpdate(workflowXML2)

    val retrievedWorkflows: immutable.Seq[Workflow[UUID, String]] = workflowService.findAll

    Assert.assertTrue(retrievedWorkflows.lengthCompare(2) >= 0)
    val sorted = retrievedWorkflows.sortBy(_.getName)
    Assert.assertEquals("Sample Workflow",sorted.head.getName)
    Assert.assertEquals("Sample Workflow 1",sorted(1).getName)
  }

  @Test
  def testDelete(): Unit = {
    val workflowXML: String = Resources.toString(Resources.getResource("test-spark-workflow.xml"), Charsets.UTF_8)
    val savedWorkflow = workflowService.saveOrUpdate(workflowXML)

    val retrievedWorkflow = workflowService.findById(savedWorkflow.getId)
    Assert.assertNotNull(retrievedWorkflow)

    workflowService.delete(retrievedWorkflow.get.getId)
    val deletedWorkflow = workflowService.findById(savedWorkflow.getId)
    Assert.assertEquals(None, deletedWorkflow)
  }

}
