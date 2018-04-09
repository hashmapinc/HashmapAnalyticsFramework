package com.hashmap.haf.workflow.install

import java.nio.file.{Files, Paths}

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class WorkflowInstallationService @Autowired()(private val databaseSchemaService: DatabaseSchemaService) {

  @Value("${install.data_dir}")
  var dataDir: String = _

  def performInstall(): Unit = {
    println("************ Install Dir **************")
    println(dataDir)
    println("************ Install Dir **************")

    if (this.dataDir == null) throw new RuntimeException("'install.data_dir' property should specified!")
    if (!Files.isDirectory(Paths.get(this.dataDir))) throw new RuntimeException("'install.data_dir' property value is not a valid directory!")

    databaseSchemaService.createDatabaseSchema()
  }
}
