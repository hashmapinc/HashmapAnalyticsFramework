package com.hashmap.dataquality.metadata

import java.io.File

import javax.annotation.{PostConstruct, PreDestroy}
import org.rocksdb.util.SizeUnit
import org.rocksdb.{CompactionStyle, CompressionType, Options, RocksDB}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MetadataDao {

  private val log = LoggerFactory.getLogger(classOf[MetadataDao])

  private val UTF8: String = "UTF-8"

  @Value("${db.file-path}") private val dbFilePath: String = "tmp/db"

  RocksDB.loadLibrary()
  private var db: RocksDB = _

  val options: Options = new Options().setCreateIfMissing(true)
    .setCreateIfMissing(true)
    .setWriteBufferSize(200 * SizeUnit.MB)
    .setMaxWriteBufferNumber(3)
    .setMaxBackgroundCompactions(10)
    .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
    .setCompactionStyle(CompactionStyle.UNIVERSAL)

  @PostConstruct
  def init(): Unit = {
    val dbFile: File = new File(dbFilePath)
    dbFile.mkdirs()

    this.db = RocksDB.open(dbFilePath)
  }

  def persist(id: String, value: String): Unit = {
    try {
      db.put(id.getBytes(UTF8), value.getBytes(UTF8))
    } catch {
      case e: Exception => log.error("Exception in persisting data in rocksDb", e)
    }
  }

  def fetch(id: String): Either[String, Option[String]] = {
    var value: String = null
    try {
      value = new String(db.get(id.getBytes(UTF8)), UTF8)
    } catch {
      case e: Exception => log.error("Exception in Fetching metadata from rocksDb", e); return Left("Error in fetching metadata")
    }
    value match {
      case null => Right(None)
      case _ => Right(Some(value))
    }
  }

  @PreDestroy
  def close(): Unit = {
    db.close()
  }

}
