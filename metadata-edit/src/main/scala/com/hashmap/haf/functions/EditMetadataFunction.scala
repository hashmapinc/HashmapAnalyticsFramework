package com.hashmap.haf.functions

import java.util.{Properties, UUID}

import com.hashmap.haf.models.{Column, DataSet, DataSource}
import com.hashmap.haf.utils.SparkFunctionContext
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.apache.ignite.cache.affinity.Affinity
import org.apache.ignite.spark.IgniteRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.Metadata
import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

object EditMetadataFunction extends App with SparkFunctionContext{

	assert(args.length == 1, println("Application accepts one parameter with JSON"))

	private val value: RDD[(String, String)] = spark.sparkContext.wholeTextFiles("resources/a")


	parseArgument(args(0)) match {
		case Success(source: DataSource) =>
			val connectionProperties = new Properties()
			connectionProperties.put("user", "postgres")
			connectionProperties.put("password", "postgres")

			import java.sql.DriverManager
			val connection = DriverManager.getConnection(source.connectionUrl, "postgres", "postgres")
			println("Postgres connection isClosed: ", connection.isClosed)

			source.datasetsMapping.foreach{case (t, d) =>

				val tableStream: DataFrame = spark.read.jdbc(source.connectionUrl, t, connectionProperties)
				tableStream.printSchema()
				tableStream.columns.foreach(println)
				tableStream.show(5)
				println(tableStream.schema.json)
				val frame = tableStream.selectExpr(buildExpression(d.columns): _*)
				frame.printSchema()
				frame.columns.foreach(println)
				frame.show(5)

				writeStream(frame, d)

			}

		case Failure(e) => println("Exception occurred while parsing json parameter ", e)
	}

	def parseArgument(metadata: String): Try[DataSource] = Try {
		Json.parse(metadata).as[DataSource]
	}

	def buildExpression(columns: List[Column]): List[String] ={
		columns.map{ column =>
			column.metadata.conversion.map{ c =>
				(c.name, c.dataType) match {
					case (Some(n), Some(t)) if !t.equals(column.dataType) && !n.equals(column.name) =>
						s"${conversionSQL(column.dataType, t, column.name)} as $n"
					case (_, Some(t)) if !t.equals(column.dataType) =>
						s"${conversionSQL(column.dataType, t, column.name)} ${column.name}"
					case (Some(n), _) if !n.equals(column.name) => s"${column.name} as $n"
					case (_, _) => s"${column.name}"
				}
			}.getOrElse(s"${column.name}")
		}
	}

	def conversionSQL(from: String, to: String, columnName: String): String ={
		registry.findFunctionFor(from.toLowerCase, to.toLowerCase) match {
			case Some(f) => s"$f($columnName)"
			case None => s"cast($columnName as $to)"
		}
	}

	def writeStream(stream: DataFrame, dataSet: DataSet): Unit ={
		val columns = dataSet.renamedColumns
		println("Columns: ", columns)
		import spark.sqlContext.implicits._
		/* IF we have read data using stream instead of batch we can use below code
				stream
					.select(
						keyFor(dataSet),
						to_json(struct(keyColumns.head, keyColumns.tail: _*)).alias("value"))
					.writeStream
					.format("kafka")
					.option("kafka.bootstrap.servers", "localhost:9092")
					.option("topic", "metadata-topic")
					.option("checkpointLocation", "/path/to/HDFS/dir")
					.outputMode("complete")
					.start()*/
		val savedRdd: IgniteRDD[Affinity[UUID], String] = igniteContext.fromCache[Affinity[UUID], String]("metadataEdit")
		savedRdd.foreach(p => println("Before: ", p))
		/*val s = stream
			.select(keyFor(dataSet),
				to_json(struct(columns.head, columns.tail: _*)).alias("value"))
			.map(r => (r.getAs[String]("key"), r.getAs[String]("value")))*/
		//val rddPair = s.toJavaRDD
		val rdd: JavaRDD[String] = stream
			.select(to_json(struct(columns.head, columns.tail: _*)).alias("value"))
			.map(_.getAs[String]("value"))
			.toJavaRDD
		savedRdd.saveValues(rdd)
		//savedRdd.savePairs(rddPair, overwrite = true)
		savedRdd.foreach(p => println("After: ", p))
		/*stream
			.select(keyFor(dataSet),
				to_json(struct(columns.head, columns.tail: _*)).alias("value"))
			  	.foreachPartition(p => {
					  val producer = new KafkaProducer[String, String](
						  KafkaConfiguration.kafkaProducerProperties("localhost:9092", "metadata-producer"))
					  p.foreach(r => {
						  val dataRecord = new ProducerRecord[String, String]("metadata-topic", r.getAs[String]("key"), r.getAs[String]("value"))
						  //println(dataRecord)
						  producer.send(dataRecord)
					  })
				  })*/
	}

	//Use In case of composite key
	private def keyFor(dataSet: DataSet): sql.Column ={
		if(dataSet.keyColumns.length > 1) to_json(struct(dataSet.keyColumns.head, dataSet.keyColumns.tail: _*)).alias("key")
		else col(dataSet.keyColumns.head).cast("string").alias("key")
	}

	ssc.awaitTermination()

}

/*import org.apache.spark.sql.functions._
			val castDouble = (i: String) => i.toDouble
			val toInt    = udf[Int, String]( _.toInt)
			val toDouble = udf[Double, String](castDouble)
			val toHour   = udf((t: String) => "%04d".format(t.toInt).take(2).toInt )
			val days_since_nearest_holidays = udf(
				(year:String, month:String, dayOfMonth:String) => year.toInt + 27 + month.toInt-12
			)
			spark.sqlContext.udf.register("toDouble", castDouble)*/
