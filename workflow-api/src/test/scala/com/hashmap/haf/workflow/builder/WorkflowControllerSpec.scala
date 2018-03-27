package com.hashmap.haf.workflow.builder

import java.nio.charset.Charset
import java.util
import java.util.{Arrays, UUID}

import com.fasterxml.jackson.databind.ObjectMapper
import com.hashmap.haf.workflow.controllers.WorkflowController
import com.hashmap.haf.workflow.service.WorkflowService
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.hashmap.haf.workflow.model.SavedWorkflow
import com.hashmap.haf.workflow.models.DefaultWorkflow
import com.hashmap.haf.workflow.util.UUIDConverter
import org.mockito.Mockito._
import org.springframework.test.web.servlet.{MockMvc, ResultActions}
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._
import org.junit.{Assert, Test}
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.http.MockHttpInputMessage

@RunWith(classOf[SpringRunner])
@WebMvcTest(value = Array(classOf[WorkflowController]), secure = false)
class WorkflowControllerSpec {

  @MockBean
  private var workflowService: WorkflowService = _

  @Autowired
  private var mockMvc: MockMvc = _



  private val workflowXML: String = Resources.toString(Resources.getResource("test-spark-workflow.xml"), Charsets.UTF_8)


  @Test
  def testSaveWorkflow() {
    when(workflowService.saveOrUpdate(workflowXML)).thenReturn(DefaultWorkflow(workflowXML))

    val putRequest = put(s"/api/workflows")
    putRequest.contentType(MediaType.TEXT_XML).content(workflowXML)
    mockMvc.perform(putRequest)
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.id").isNotEmpty)
      .andExpect(jsonPath("$.name").value("Sample Workflow"))
  }

  @Test
  def testFindById(): Unit = {
    val workflowId = "bea90c838d84f4e9a66ea41df42cb9a"
    when(workflowService.findById(UUIDConverter.fromString(workflowId))).thenReturn(DefaultWorkflow(workflowXML))
    val getRequest = get(s"/api/workflows/$workflowId")
    mockMvc.perform(getRequest)
      .andExpect(status().isOk)
  }

  @Test
  def testFindAll(): Unit = {
    when(workflowService.findAll).thenReturn(List(DefaultWorkflow(workflowXML)))
    val getRequest = get("/api/workflows")
    mockMvc.perform(getRequest).andExpect(status().isOk)
  }

  @Test
  def testDelete(): Unit = {
    val workflowId = "bea90c838d84f4e9a66ea41df42cb9a"
    val deleteRequest = delete(s"/api/workflows/$workflowId")
    mockMvc.perform(deleteRequest).andExpect(status().isOk)
  }



}
