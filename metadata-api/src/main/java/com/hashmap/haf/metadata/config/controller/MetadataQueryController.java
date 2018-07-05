package com.hashmap.haf.metadata.config.controller;

import com.hashmap.haf.metadata.config.exceptions.MetadataException;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;
import com.hashmap.haf.metadata.config.service.MetadataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class MetadataQueryController {

    @Autowired
    private MetadataQueryService metadataQueryService;

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery", method = RequestMethod.POST)
    public ResponseEntity saveMetadataQuery(@RequestBody MetadataQuery metadataQuery) {
        MetadataQuery savedMetadataQuery = checkNotNull(metadataQueryService.saveMetadataQuery(metadataQuery));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMetadataQuery);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery", method = RequestMethod.PUT)
    public ResponseEntity updateMetadataQuery(@RequestBody MetadataQuery metadataQuery) {
        MetadataQuery savedMetadataQuery = checkNotNull(metadataQueryService.updateMetadataQuery(metadataQuery));
        return ResponseEntity.status(HttpStatus.OK).body(savedMetadataQuery);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery/{id}", method = RequestMethod.GET)
    public ResponseEntity getMetadataQuery(@PathVariable String id) {
        MetadataQueryId metadataQueryId =  new MetadataQueryId(UUID.fromString(id));
        try {
            MetadataQuery metadataQuery = checkNotNull(metadataQueryService.findMetadataQueryById(metadataQueryId));
            return  ResponseEntity.status(HttpStatus.OK)
                    .body(metadataQuery);
        } catch (MetadataException exp) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exp.getMessage());
        }
    }


    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery/metaconfig/{id}", method = RequestMethod.GET)
    public ResponseEntity getAllMetadataQueryByMetadataId(@PathVariable String id) {
        try {
            MetadataConfigId metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
            List<MetadataQuery> metadataQueries = checkNotNull(metadataQueryService.findAllMetadataQueryByMetadataId(metadataConfigId));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(metadataQueries);
        }catch (MetadataException exp) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exp.getMessage());
        }
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteMetadataQuery(@PathVariable String id) {
        MetadataQueryId  metadataQueryId =  new MetadataQueryId(UUID.fromString(id));
        metadataQueryService.deleteMetadataQuery(metadataQueryId);
    }

    private <T> T checkNotNull(T reference) throws MetadataException {
        if (reference == null) {
            throw new MetadataException("{\"error\":\"Requested item wasn't found!\"}");
        }
        return reference;
    }
}
