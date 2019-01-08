package com.hashmap.dataquality.metadata

import com.hashmap.dataquality.util.JsonUtil
import org.springframework.beans.factory.annotation.{Autowired, Qualifier, Value}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MetadataFetchService @Autowired()(metadataDao: MetadataDao,
                                        @Qualifier("oauth2RestTemplate") oauth2RestTemplate: RestTemplate,
                                        @Value("${tempus.uri}") private val URI: String) {

  private val URL_FORMAT = "%s/api/%s/attribute/mandatory-tags"

  def saveMetaDataForDevice(deviceMetaData: DeviceMetaData): Unit = {
    metadataDao.persist(deviceMetaData.deviceId, JsonUtil.toJson(deviceMetaData.getMandatoryTags))
  }

  def getMetadataForDevice(deviceId: String): Either[String, List[TagMetaData]] = metadataDao.fetch(deviceId) match {
    case Right(metadata: Option[String]) => metadata match {
      case Some(metadataString: String) => Right(JsonUtil.fromJson[List[TagMetaData]](metadataString))
      case None => getMetadataFromRemote(deviceId) match {
        case Right(metadataStringFromRemote) => metadataDao.persist(deviceId, metadataStringFromRemote); Right(JsonUtil.fromJson[List[TagMetaData]](metadataStringFromRemote))
        case Left(errorMsg) => Left(errorMsg)
      }
    }
    case Left(daoErrorMsg: String) => Left(daoErrorMsg)
  }

  private def getMetadataFromRemote(deviceId: String): Either[String, String] = {
    val url = String.format(URL_FORMAT, URI, deviceId)
    val response = oauth2RestTemplate.getForObject(url
      , classOf[String])
    response match {
      case null => Left(s"Error fetching metadata from $url")
      case _ => Right(response)
    }

  }

}