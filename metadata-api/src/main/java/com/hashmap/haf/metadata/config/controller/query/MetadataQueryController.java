package com.hashmap.haf.metadata.config.controller.query;

import com.hashmap.haf.metadata.config.controller.BaseController;
import com.hashmap.haf.metadata.config.exceptions.MetadataException;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import com.hashmap.haf.metadata.config.page.TextPageData;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.service.query.MetadataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class MetadataQueryController extends BaseController {

    @Autowired
    private MetadataQueryService metadataQueryService;

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/metaquery", method = RequestMethod.POST)
    public ResponseEntity saveMetadataQuery(@RequestBody MetadataQuery metadataQuery) {
        if (metadataQuery.getId() == null
                && metadataQueryService.findMetadataQueryByQueryStmtAndMetadataConfigId(
                        metadataQuery.getQueryStmt(), metadataQuery.getMetadataConfigId()) == null) {
            MetadataQuery savedMetadataQuery = checkNotNull(metadataQueryService.saveMetadataQuery(metadataQuery));
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMetadataQuery);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("MetadataQuery already present with this query statement.");
        }
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
    @RequestMapping(value = "/metaquery/metaconfig/{id}", params = {"limit"}, method = RequestMethod.GET)
    public ResponseEntity getAllMetadataQueryByMetadataId(@PathVariable String id,
                                                          @RequestParam int limit,
                                                          @RequestParam(required = false) String idOffset) {
        try {
            MetadataConfigId metadataConfigId =  new MetadataConfigId(UUID.fromString(id));
            TextPageLink pageLink = createPageLink(limit, null, idOffset, null);
            TextPageData<MetadataQuery> metadataQueries = checkNotNull(metadataQueryService.findAllMetadataQueryByMetadataId(metadataConfigId, pageLink));
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

    private <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new MetadataException("{\"error\":\"Requested item wasn't found!\"}");
        }
        return reference;
    }
}
