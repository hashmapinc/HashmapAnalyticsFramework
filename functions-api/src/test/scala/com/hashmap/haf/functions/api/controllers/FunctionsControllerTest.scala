package com.hashmap.haf.functions.api.controllers

import com.hashmap.haf.functions.api.services.FunctionsBootstrapService
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.{ActiveProfiles, TestPropertySource}
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._

import scala.collection.JavaConverters._

@RunWith(classOf[SpringRunner])
@WebMvcTest(value = Array(classOf[FunctionsController]), secure = false)
@TestPropertySource(Array("classpath:application-test.yml"))
@ActiveProfiles(Array("test"))
class FunctionsControllerTest {

    @MockBean
    private var igniteFunctionService: IgniteFunctionTypeService = _

    @Autowired
    private var mockMvc: MockMvc = _

    @Test
    def shouldReturnAllFunctions(): Unit = {
        when(igniteFunctionService.findByClazz("jdbcReader"))
          .thenReturn(mock(classOf[IgniteFunctionType]))

        when(igniteFunctionService.findAll())
          .thenReturn(List(new IgniteFunctionType("serviceName", Array(), "functionClazzName", "packageName")).asJava)

        mockMvc
          .perform(get("/api/functions"))
          .andExpect(status().isOk)
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
          .andExpect(jsonPath("$[0].functionClazz").value("functionClazzName"))
          .andExpect(jsonPath("$[0].service").value("serviceName"))
          .andExpect(jsonPath("$[0].packageName").value("packageName"))
    }

    @Test
    def shouldReturnFunctionByName(): Unit = {
        val clazzName = "jdbcReader"

        when(igniteFunctionService.findByClazz("jdbcReader"))
          .thenReturn(new IgniteFunctionType("serviceName", Array(), "functionClazzName", "packageName"))

        mockMvc
          .perform(get(s"/api/functions/$clazzName"))
          .andExpect(status().isOk)
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
          .andExpect(jsonPath("$.functionClazz").value("functionClazzName"))
          .andExpect(jsonPath("$.service").value("serviceName"))
          .andExpect(jsonPath("$.packageName").value("packageName"))
    }
}


