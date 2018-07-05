package com.hashmap.haf.execution.events.dao

import java.sql.Timestamp
import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.springtestdbunit.annotation.{DatabaseSetup, DbUnitConfiguration}
import com.hashmap.haf.execution.events.Event
import org.junit.{Before, Test}
import org.springframework.beans.factory.annotation.Autowired

class EventDaoSqlTest extends AbstractJpaDaoTest{

  @Autowired
  private var eventsDao: EventsDao = _

  private val mapper = new ObjectMapper()

  @Before
  def setup(): Unit ={
    mapper.registerModule(DefaultScalaModule)
  }

  @Test
  @DatabaseSetup(Array("classpath:dbunit/empty_dataset.xml"))
  def test_save(): Unit ={
    val workflowId = UUID.randomUUID().toString
    val event = Event(UUID.randomUUID(), workflowId, new Timestamp(System.currentTimeMillis()), mapper.readTree("{}"))

    val saved = eventsDao.save(event)

    assert(saved != null)
  }

}
