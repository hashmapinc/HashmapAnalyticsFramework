package com.hashmap.dataquality.metadata

import org.rocksdb.util.SizeUnit
import org.rocksdb.{CompactionStyle, CompressionType, Options, RocksDB}
import org.springframework.stereotype.Service

@Service
class MetadataDao {

  private val UTF8: String = "UTF-8"
  private val dbFilePath: String = "/tmp/db"

  RocksDB.loadLibrary()

  private val options: Options = new Options().setCreateIfMissing(true)
    .setCreateIfMissing(true)
    .setWriteBufferSize(200 * SizeUnit.MB)
    .setMaxWriteBufferNumber(3)
    .setMaxBackgroundCompactions(10)
    .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
    .setCompactionStyle(CompactionStyle.UNIVERSAL)

  private val db: RocksDB = RocksDB.open(dbFilePath)

  //TODO: Handle faults/exceptions

  def persist(id: String, value: String): Unit = {
    db.put(id.getBytes(UTF8), value.getBytes(UTF8))
  }

  def fetch(id: String): Option[String] = {
    val v = db.get(id.getBytes(UTF8))
    v match {
      case null => None
      case _ => Some(new String(v, UTF8))
    }
  }

}
