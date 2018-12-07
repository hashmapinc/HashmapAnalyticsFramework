package com.hashmap.dataquality.metadata

import com.hashmap.dataquality.util.JsonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scalaj.http.Http

@Service
class MetadataFetchService {

  @Autowired
  private var metadataDao: MetadataDao = _

  def getMetadataForDevice(deviceId: String): Either[String, Metadata] = metadataDao.fetch(deviceId) match {
    case Some(metadataString) => Right(JsonUtil.fromJson[Metadata](metadataString))
    case None => getMetadataFromRemote(deviceId) match {
      case Right(metadataString) => metadataDao.persist(deviceId, metadataString); Right(JsonUtil.fromJson[Metadata](metadataString))
      case Left(errorMsg) => Left(errorMsg)
    }
  }

  private def getMetadataFromRemote(deviceId: String): Either[String, String] = {
    val url = ""
    val response = Http(url).postData("").asString
    response.code match {
      case 200 => Right(response.body) //Some(JsonUtil.fromJson[Metadata](response.body))
      case _ => Left(s"Error fetching metadata from $url. Error code - ${response.code}")
    }
  }

}