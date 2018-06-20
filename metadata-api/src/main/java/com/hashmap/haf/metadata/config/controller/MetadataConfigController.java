package com.hashmap.haf.metadata.config.controller;

import com.hashmap.haf.metadata.config.exceptions.MetadataException;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.service.MetadataConfigService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity saveMetadataConfig(@RequestBody MetadataConfig metadataConfig) {
        MetadataConfig savedMetadataConfig = checkNotNull(metadataConfigService.saveMetadataConfig(metadataConfig));
        return ResponseEntity.status(HttpStatus.OK).body(savedMetadataConfig);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig", method = RequestMethod.PUT)
    public ResponseEntity updateMetadataConfig(@RequestBody MetadataConfig metadataConfig) {
        MetadataConfig savedMetadataConfig = checkNotNull(metadataConfigService.updateMetadataConfig(metadataConfig));
        return ResponseEntity.status(HttpStatus.OK).body(savedMetadataConfig);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.GET)
    public ResponseEntity getMetadataConfig(@PathVariable String id) {
        MetadataConfigId metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        try {
            MetadataConfig metadataConfig = checkNotNull(metadataConfigService.findMetadataConfigById(metadataConfigId));
            return  ResponseEntity.status(HttpStatus.FOUND)
                    .body(metadataConfig);
        } catch (MetadataException exp) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exp.getMessage());
        }
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig", method = RequestMethod.GET)
    public ResponseEntity getMetadataConfigs() {
        try {
            List<MetadataConfig> metadataConfigs = checkNotNull(metadataConfigService.findAllMetadataConfig());
            return ResponseEntity.status(HttpStatus.FOUND)
            .body(metadataConfigs);
        }catch (MetadataException exp){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(exp.getMessage());
        }
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/owner/{ownerId}", method = RequestMethod.GET)
    public ResponseEntity getMetadataConfigsByOwnerId(@PathVariable String ownerId) {
        try {
            List<MetadataConfig> metadataConfigs = checkNotNull(metadataConfigService.findAllMetadataConfigByOwnerId(ownerId));
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(metadataConfigs);
        }catch (MetadataException exp) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exp.getMessage());
        }
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteMetadataConfig(@PathVariable String id) {
        MetadataConfigId  metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaconfig/{id}/query", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void createQuery(@PathVariable String id, @RequestBody String query) {
        MetadataConfigId  metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
        MetadataConfig foundMetadataConfig = metadataConfigService.findMetadataConfigById(metadataConfigId);
        metadataConfigService.createQueryMsg(query, foundMetadataConfig);
    }


    private <T> T checkNotNull(T reference) throws MetadataException {
        if (reference == null) {
            throw new MetadataException("{\"error\":\"Requested item wasn't found!\"}");
        }
        return reference;
    }
}
