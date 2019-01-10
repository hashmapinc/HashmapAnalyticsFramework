package com.hashmap.dataquality.metadata

import com.hashmap.dataquality.util.JsonUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Qualifier, Value}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MetadataService @Autowired()(metadataDao: MetadataDao,
                                   @Qualifier("oauth2RestTemplate") oauth2RestTemplate: RestTemplate,
                                   @Value("${tempus.uri}") private val URI: String) {

  private val log = LoggerFactory.getLogger(classOf[MetadataDao])

  private val URL_FORMAT = "%s/api/device/%s/data-quality-meta-data"

  def saveMetaDataForDevice(deviceId: String, mandatoryTags: String): Unit = {
    metadataDao.persist(deviceId, mandatoryTags)
  }

  def getMetadataForDevice(deviceId: String): Either[String, List[TagMetaData]] = metadataDao.fetch(deviceId) match {
    case Right(metadata: Option[String]) => metadata match {
      case Some(metadataString: String) => Right(JsonUtil.fromJson[List[TagMetaData]](metadataString))
      case None => log.info(s"No mandatory tags present for device Id $deviceId")
        Left(s"No mandatory tags")
    }
    case Left(daoErrorMsg: String) => Left(daoErrorMsg)
  }

  def getMetadataFromRemote(deviceId: String): Either[String, DataQualityMetaData] = {
    val url = String.format(URL_FORMAT, URI, deviceId)
    val response = oauth2RestTemplate.getForObject(url
      , classOf[DataQualityMetaData])
    response match {
      case null => Left(s"Error fetching metadata from $url")
      case _ => Right(response)
    }

  }


}