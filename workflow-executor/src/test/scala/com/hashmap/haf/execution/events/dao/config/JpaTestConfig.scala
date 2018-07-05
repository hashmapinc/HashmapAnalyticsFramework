package com.hashmap.haf.execution.events.dao.config

import java.io.IOException
import java.sql.SQLException

import com.github.springtestdbunit.bean.{DatabaseConfigBean, DatabaseDataSourceConnectionFactoryBean}
import javax.sql.DataSource
import org.dbunit.DatabaseUnitException
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class JpaTestConfig {

  @Autowired
  private var dataSource: DataSource = _

  @Bean
  def databaseConfigBean: DatabaseConfigBean = {
    val databaseConfigBean = new DatabaseConfigBean
    databaseConfigBean.setDatatypeFactory(new HsqldbDataTypeFactory)
    databaseConfigBean
  }

  @Bean(name = Array("dbUnitDatabaseConnection"))
  @throws[SQLException]
  @throws[DatabaseUnitException]
  @throws[IOException]
  def dbUnitDatabaseConnection: DatabaseDataSourceConnectionFactoryBean = {
    val databaseDataSourceConnectionFactoryBean = new DatabaseDataSourceConnectionFactoryBean
    databaseDataSourceConnectionFactoryBean.setDatabaseConfig(databaseConfigBean)
    databaseDataSourceConnectionFactoryBean.setDataSource(dataSource)
    databaseDataSourceConnectionFactoryBean
  }

}
