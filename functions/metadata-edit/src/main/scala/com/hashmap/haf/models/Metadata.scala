package com.hashmap.haf.models

import play.api.libs.json.{Json, OFormat}

case class ColumnType(categorical: Boolean = false,
                      feature: Boolean = true,
                      label: Boolean  = false,
                      score: Boolean = false)
object ColumnType{
	implicit lazy val format: OFormat[ColumnType] = Json.format[ColumnType]
}

case class Conversion(name: Option[String] = None, dataType: Option[String] = None)
object Conversion{
	implicit lazy val format: OFormat[Conversion] = Json.format[Conversion]
}

case class ColumnMetadata(flags: ColumnType,
                          weight: Int,
                          conversion: Option[Conversion] = None)
object ColumnMetadata{
	implicit lazy val format: OFormat[ColumnMetadata] = Json.format[ColumnMetadata]
}

//TODO: dataType should be an enum from available source data types
case class Column(name: String,
                  key: Boolean = false,
                  dataType: String,
                  metadata: ColumnMetadata){
	def toName: Option[String] = metadata.conversion.flatMap(_.name)
}
object Column{
	implicit lazy val format: OFormat[Column] = Json.format[Column]
}

case class DataSet(name: String,
                   table: String,
                   columns: List[Column]){

	def originalColumnsMap: Map[String, Column] = columns.map(c => c.name -> c).toMap

	def keyColumns: List[String] = {
		val keyColumnList = columns.filter(_.key)
		if(keyColumnList.isEmpty){
			throw new IllegalStateException("At least one column should be primary key")
		}
		columnNames(keyColumnList)
	}

	private def columnNames(columns: List[Column]): List[String] ={
		columns.map(c => c.toName.getOrElse(c.name))
	}

	def renamedColumns: List[String] = {
		columnNames(columns)
	}
}
object DataSet{
	implicit lazy val format: OFormat[DataSet] = Json.format[DataSet]
}

//TODO: source is database type, needs to be an enum. Currently assumes only JDBC
case class DataSource(source: String,
                      host: String,
                      port: Int,
                      database: String,
                      datasets: List[DataSet]){
	def connectionUrl: String = s"jdbc:$source://$host:$port/$database"

	def datasetFor(table: String): Option[DataSet] = datasets.find(_.table.equalsIgnoreCase(table))

	def datasetsMapping: Map[String, DataSet] = datasets.map(d => d.table -> d ).toMap
}

object DataSource{
	implicit lazy val format: OFormat[DataSource] = Json.format[DataSource]
}
