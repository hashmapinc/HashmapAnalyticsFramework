package com.hashmap.haf.metadata.config.service;

import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.dao.MetadataQueryDao;
import com.hashmap.haf.metadata.config.exceptions.DataValidationException;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;
import com.hashmap.haf.metadata.core.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MetadataQueryServiceImpl  implements  MetadataQueryService{
    private static final String INCORRECT_METADATAQUERY_ID = "Incorrect metadataQueryId ";
    private static final String INCORRECT_METADATACONFIG_ID = "Incorrect  metadataConfigId ";

    @Autowired
    private MetadataQueryDao metadataQueryDao;

    @Autowired
    private ManagerActorService managerActorService;

    @Autowired
    private MetadataConfigService metadataConfigService;

    @Override
    public MetadataQuery saveMetadataQuery(MetadataQuery metadataQuery) {
        if (metadataQuery == null) {
            throw new DataValidationException("Metadata-Query Object cannot be null");
        }
        log.trace("Executing saveMetadataQuery [{}]", metadataQuery);
        MetadataConfig metadataConfig = metadataConfigService.findMetadataConfigById(metadataQuery.getMetadataConfigId());
        if (metadataConfig == null) {
            throw new DataValidationException("Metadata-Config Object with ID: " + metadataQuery.getMetadataConfigId() + " not found");
        }
        MetadataQuery savedMetadataQuery = metadataQueryDao.save(metadataQuery);
        managerActorService.process(new QueryMessage(savedMetadataQuery, metadataConfig, MessageType.CREATE));
        return savedMetadataQuery;
    }

    @Override
    public MetadataQuery findMetadataQueryById(MetadataQueryId metadataQueryId) {
        log.trace("Executing findMetaDataQueryById [{}]", metadataQueryId);
        Validator.validateId(metadataQueryId, INCORRECT_METADATAQUERY_ID+ metadataQueryId);
        Optional<MetadataQuery> metadataQuery = metadataQueryDao.findById(metadataQueryId.getId());
        return metadataQuery.orElse(null);
    }


    @Override
    public List<MetadataQuery> findAllMetadataQueryByMetadataId(MetadataConfigId metadataConfigId) {
        log.trace("Executing findAllMetadataQueryByMetadataId [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        return metadataQueryDao.findByMetadataConfigId(metadataConfigId.getId());
    }

    @Override
    public int deleteMetadataQueryByMetadataConfigId(MetadataConfigId metadataConfigId) {
        log.trace("Executing deleteMetadataQueryByMetadataConfigId [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        return metadataQueryDao.removeByMetadataConfigId(metadataConfigId.getId());
    }

    @Override
    public MetadataQuery updateMetadataQuery(MetadataQuery metadataQuery) {
        if (metadataQuery == null) {
            throw new DataValidationException("Can't update non-existent metadata-query");
        }
        log.trace("Executing updateMetadataQuery [{}]", metadataQuery);
        Validator.validateId(metadataQuery.getId(), INCORRECT_METADATAQUERY_ID+ metadataQuery.getId());
        MetadataConfig metadataConfig = metadataConfigService.findMetadataConfigById(metadataQuery.getMetadataConfigId());
        if (metadataConfig == null) {
            throw new DataValidationException("Metadata-Config Object with ID: " + metadataQuery.getMetadataConfigId() + " not found");
        }

        Optional<MetadataQuery> savedMetadataQuery = metadataQueryDao.findById(metadataQuery.getId().getId());
        if (savedMetadataQuery.isPresent()){
            savedMetadataQuery.get().update(metadataQuery);
            MetadataQuery updatedMetadataQuery = metadataQueryDao.save(savedMetadataQuery.get());
            managerActorService.process(new QueryMessage(updatedMetadataQuery, metadataConfig, MessageType.UPDATE));
            return updatedMetadataQuery;
        } else {
            throw new DataValidationException("Can't update for non-existent metadataQuery!");
        }
    }

    @Override
    public List<MetadataQuery> findAllMetadataQuery() {
        log.trace("Executing findAllMetadataQuery [{}]");
        return metadataQueryDao.findAll();
    }

    @Override
    public void deleteMetadataQuery(MetadataQueryId metadataQueryId) {
        log.trace("Executing deleteMetadataQuery [{}]", metadataQueryId);
        Validator.validateId(metadataQueryId, INCORRECT_METADATAQUERY_ID + metadataQueryId);
        MetadataQuery metadataQuery = findMetadataQueryById(metadataQueryId);
        if (metadataQuery != null) {
            metadataQueryDao.removeById(metadataQueryId.getId());
            MetadataConfig metadataConfig = metadataConfigService.findMetadataConfigById(metadataQuery.getMetadataConfigId());
            managerActorService.process(new QueryMessage(metadataQuery, metadataConfig, MessageType.DELETE));
        }
    }

}
