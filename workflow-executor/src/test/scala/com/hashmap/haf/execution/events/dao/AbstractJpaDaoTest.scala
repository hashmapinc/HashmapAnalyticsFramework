package com.hashmap.haf.execution.events.dao

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.hashmap.haf.execution.configs.JpaConfig
import com.hashmap.haf.execution.events.dao.config.JpaTestConfig
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.{DependencyInjectionTestExecutionListener, DirtiesContextTestExecutionListener}
import org.springframework.test.context.{ContextConfiguration, TestExecutionListeners, TestPropertySource}

@RunWith(classOf[SpringRunner])
@TestPropertySource(Array("classpath:sql-test.yml"))
@ContextConfiguration(classes = Array(classOf[JpaConfig], classOf[JpaTestConfig]))
@TestExecutionListeners(Array(classOf[DependencyInjectionTestExecutionListener],
  classOf[DirtiesContextTestExecutionListener],
  classOf[DbUnitTestExecutionListener]))
@DbUnitConfiguration(databaseConnection = Array("dbUnitDatabaseConnection"))
abstract class AbstractJpaDaoTest{

}
