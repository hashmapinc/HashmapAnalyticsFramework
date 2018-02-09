package com.hashmap.haf.workflow.install

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.sql.DriverManager

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class SqlDatabaseSchemaService extends DatabaseSchemaService {

  private val SQL_DIR = "sql"
  private val SCHEMA_SQL = "workflow-schema.sql"

  @Value("${install.data_dir}")
  var dataDir: String = _

  @Value("${spring.datasource.url}")
  var dbUrl: String = _

  @Value("${spring.datasource.username}")
  var dbUserName: String = _

  @Value("${spring.datasource.password}")
  var dbPassword: String = _

  override def createDatabaseSchema(): Unit = {
    val schemaFile = Paths.get(this.dataDir, SQL_DIR, SCHEMA_SQL)
    val conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword)
    try {
      val sql = new String(Files.readAllBytes(schemaFile), Charset.forName("UTF-8"))
      conn.createStatement.execute(sql)
    } finally if (conn != null) conn.close()
  }
}
