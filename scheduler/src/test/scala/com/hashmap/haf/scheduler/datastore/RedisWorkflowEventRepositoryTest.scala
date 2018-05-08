package com.hashmap.haf.scheduler.datastore

import com.hashmap.haf.scheduler.SchedulerApplicationConfig
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.junit.{After, Test}
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, TestPropertySource}
import redis.RedisClient

/*
@RunWith(classOf[SpringRunner])
@SpringBootTest(classOf[RedisWorkflowEventRepository])
@ContextConfiguration(classes = Array(classOf[SchedulerApplicationConfig]), loader = classOf[AnnotationConfigContextLoader])
@TestPropertySource(Array("classpath:application-test.yml"))
@ActiveProfiles(Array("test"))
class RedisWorkflowEventRepositoryTest {
  @Autowired
  private var redis: RedisClient = _
  @Autowired
  private var redisWorkflowEventRepository: RedisWorkflowEventRepository = _

  private val workflowEvent: WorkflowEvent = WorkflowEvent("worflowId-1", "0/5 0/1 * 1/1 * ? *", false)

  @After
  def after = redis.flushall()

  @Test
  def shouldAddWorkflowEvent = {
    redisWorkflowEventRepository.addOrUpdate(workflowEvent)
  }

}*/
