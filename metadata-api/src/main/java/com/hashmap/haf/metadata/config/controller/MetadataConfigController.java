package com.hashmap.haf.metadata.config.controller;

import com.hashmap.haf.metadata.config.exceptions.MetadataException;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.service.MetadataConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//TODO :  testSource, runIngestion
@RestController
@RequestMapping("/api")
@Slf4j
public class MetadataConfigController {

    @Autowired
    private MetadataConfigService metadataConfigService;

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig", method = RequestMethod.POST)
    public MetadataConfig saveMetadataConfig(@RequestBody MetadataConfig metadataConfig) {
        MetadataConfig savedMetadataConfig = checkNotNull(metadataConfigService.saveMetadataConfig(metadataConfig));
        return savedMetadataConfig;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig", method = RequestMethod.PUT)
    public MetadataConfig updateMetadataConfig(@RequestBody MetadataConfig metadataConfig) {
        MetadataConfig savedMetadataConfig = checkNotNull(metadataConfigService.updateMetadataConfig(metadataConfig));
        return savedMetadataConfig;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.GET)
    public MetadataConfig getMetadataConfig(@PathVariable String id){
        MetadataConfigId metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        MetadataConfig metadataConfig = checkNotNull(metadataConfigService.findMetadataConfigById(metadataConfigId));
        return metadataConfig;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig", method = RequestMethod.GET)
    public List<MetadataConfig> getMetadataConfigs() {
        List<MetadataConfig> metadataConfigs = checkNotNull(metadataConfigService.findAllMetadataConfig());
        return metadataConfigs;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/owner/{ownerId}", method = RequestMethod.GET)
    public List<MetadataConfig> getMetadataConfigsByOwnerId(@PathVariable String ownerId) {
        List<MetadataConfig> metadataConfigs = checkNotNull(metadataConfigService.findAllMetadataConfigByOwnerId(ownerId));
        return metadataConfigs;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteMetadataConfig(@PathVariable String id) {
        MetadataConfigId  metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }

    private <T> T checkNotNull(T reference) throws MetadataException {
        if (reference == null) {
            throw new MetadataException("Requested item wasn't found!");
        }
        return reference;
    }
}
