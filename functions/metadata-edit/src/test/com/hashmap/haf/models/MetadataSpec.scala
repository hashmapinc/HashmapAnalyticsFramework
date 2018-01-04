package com.hashmap.haf.models

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import play.api.libs.json.{JsValue, Json}

class MetadataSpec extends Specification with BeforeAfterAll{

	val columns = List(
		Column("entity_type", key = false, "string", ColumnMetadata(ColumnType(), -1)),
		Column("entity_id", key = true, "string", ColumnMetadata(ColumnType(), -1, Some(Conversion(dataType = Some("uuid"))))),
		Column("key", key = false, "string", ColumnMetadata(ColumnType(), -1)),
		Column("ts", key = true, "long", ColumnMetadata(ColumnType(), -1, Some(Conversion(Some("date_time"), Some("timestamp"))))),
		Column("str_v", key = false, "string", ColumnMetadata(ColumnType(), -1, Some(Conversion(dataType = Some("double"))))),
		Column("long_v", key = false, "long", ColumnMetadata(ColumnType(), -1)),
		Column("bool_v", key = false, "boolean", ColumnMetadata(ColumnType(), -1, Some(Conversion(dataType = Some("smallint"))))),
		Column("dbl_v", key = false, "double", ColumnMetadata(ColumnType(), -1))
	)
	val dataset = DataSet("timeseries", "ts_kv", columns)
	val source = DataSource("postgresql", "localhost", 5432, "thingsboard", List(dataset))
	var expected: JsValue = _

	"Data Source" should {
		"format a json from instance" in {
			val result = Json.toJson[DataSource](source)

			result shouldEqual expected
		}

	}

	override def beforeAll(): Unit = {
		expected = Json.parse(this.getClass.getResourceAsStream("/thingsboard-source-test.json"))
	}

	override def afterAll(): Unit = {
		//close input stream, is it necessary ?
	}
}
