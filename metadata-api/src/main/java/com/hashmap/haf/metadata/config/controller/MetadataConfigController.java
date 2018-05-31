package com.hashmap.haf.metadata.config.controller;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.service.MetadataConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//TODO: Add exception handling
//TODO: Add Logging
//TODO : Add Owner Id based API
//TODO : findAll, update, findByOwnerId, testSource
@RestController
@RequestMapping("/api")
public class MetadataConfigController {

    @Autowired
    private MetadataConfigService metadataConfigService;

    @RequestMapping(value = "/metaconfig", method = RequestMethod.POST)
    public MetadataConfig saveMetadataConfig(@RequestBody MetadataConfig metadataConfig) {
        return metadataConfigService.saveMetadataConfig(metadataConfig);
    }

    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.GET)
    public MetadataConfig getMetadataConfig(@PathVariable String id){
        MetadataConfigId metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        return metadataConfigService.findMetadataConfigById(metadataConfigId);
    }

    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteMetadataConfig(@PathVariable String id) {
        MetadataConfigId  metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }
}
