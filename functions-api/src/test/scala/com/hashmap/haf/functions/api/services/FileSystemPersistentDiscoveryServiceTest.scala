package com.hashmap.haf.functions.api.services;

import java.io.{File, FileNotFoundException}
import java.net.URI
import java.nio.file.Path

import com.hashmap.haf.functions.gateways.FunctionsInputGateway
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito.{mock, _}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.{ActiveProfiles, TestPropertySource}

@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[FileSystemPersistentDiscoveryService]))
@TestPropertySource(Array("classpath:application-test.yml"))
@ActiveProfiles(Array("test"))
class FileSystemPersistentDiscoveryServiceTest {

    @Value("${functions.input.location}")
    private var functionInputLocation: String = _

    @MockBean
    var inputGateway: FunctionsInputGateway = _

    @MockBean
    var igniteFunctionService: IgniteFunctionTypeService = _

    @Autowired
    @InjectMocks
    var fileSystemPersistentDiscoveryService: FileSystemPersistentDiscoveryService = _

    /*@Test
    def testIfIgniteConfigPropertyIsMapped = assertEquals("cache.xml", igniteConfig)*/

    @Test(expected = classOf[FileNotFoundException])
    def shouldThrowExceptionForInvalidFileLocation = {
        val invalidPathToDirContainingFunctionJars = "file:///home/invalid/dir"
        fileSystemPersistentDiscoveryService.discoverFunctions(new URI(invalidPathToDirContainingFunctionJars))
    }

    @Test(expected = classOf[IllegalArgumentException])
    def shouldThrowExceptionForIncorrectURIPath = {
        val invalidPathToDirContainingFunctionJars = "/home/invalid/dir"
        fileSystemPersistentDiscoveryService.discoverFunctions(new URI(invalidPathToDirContainingFunctionJars))

    }

    @Test
    def shouldDiscoverFunctions = {

        when(inputGateway.listFilesFrom(new URI(functionInputLocation)))
          .thenReturn(List(mock(classOf[Path])))


        when(inputGateway.readFileFrom(new URI(functionInputLocation)))
          .thenReturn(Some(mock(classOf[File])))

        fileSystemPersistentDiscoveryService.discoverFunctions(new URI(functionInputLocation))
    }
}