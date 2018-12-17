package com.hashmap.dataquality.controller

import com.hashmap.dataquality.metadata.{DeviceMetaData, MetadataFetchService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/api"))
class DataQualityController {

  @Autowired
  val metaDataFetchService: MetadataFetchService = null

  @PostMapping(value = Array("/mandatory-tags"))
  @ResponseStatus(value = HttpStatus.OK)
  def saveMandatoryTags(@RequestBody deviceMetaData: DeviceMetaData): Unit = {
    metaDataFetchService.saveMetaDataForDevice(deviceMetaData)
  }
}