package com.hashmap.haf.functions.api.services;

import com.hashmap.haf.functions.services.FunctionsDiscoveryService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.{ActiveProfiles, TestPropertySource}


@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[FunctionsBootstrapService]))
@TestPropertySource(Array("classpath:application-test.yml"))
@ActiveProfiles(Array("test"))
class FunctionsBootstrapServiceTest {

  @MockBean
  private var discoveryService: FunctionsDiscoveryService = _


  @Autowired
  @InjectMocks
  private var functionsBootstrapService: FunctionsBootstrapService = _

  @Test
  def testInit = functionsBootstrapService.init()
}

