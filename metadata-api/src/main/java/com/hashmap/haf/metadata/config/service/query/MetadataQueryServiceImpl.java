package com.hashmap.haf.metadata.config.service.query;

import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.dao.query.MetadataQueryDao;
import com.hashmap.haf.metadata.config.exceptions.DataValidationException;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import com.hashmap.haf.metadata.config.page.TextPageData;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.service.config.MetadataConfigService;
import com.hashmap.haf.metadata.config.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MetadataQueryServiceImpl  implements MetadataQueryService {
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
        MetadataQuery savedMetadataQuery = metadataQueryDao.save(metadataQuery);
        return scheduleQuery(savedMetadataQuery);
    }

    @Override
    public MetadataQuery findMetadataQueryById(MetadataQueryId metadataQueryId) {
        log.trace("Executing findMetaDataQueryById [{}]", metadataQueryId);
        Validator.validateId(metadataQueryId, INCORRECT_METADATAQUERY_ID+ metadataQueryId);
        Optional<MetadataQuery> metadataQuery = metadataQueryDao.findById(metadataQueryId.getId());
        return metadataQuery.orElse(null);
    }

    @Override
    public MetadataQuery findMetadataQueryByQueryStmtAndMetadataConfigId(String queryStmt , MetadataConfigId metadataConfigId) {
        log.trace("Executing findMetadataQueryByQueryStmtAndMetadataConfigId [{}], [{}]", queryStmt, metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATAQUERY_ID+ metadataConfigId);
        Optional<MetadataQuery> foundMetadataQuery = metadataQueryDao.findByQueryStmtAndMetadataConfigId(queryStmt, metadataConfigId.getId());
        return foundMetadataQuery.orElse(null);
    }

    @Override
    public TextPageData<MetadataQuery> findAllMetadataQueryByMetadataId(MetadataConfigId metadataConfigId, TextPageLink pageLink) {
        log.trace("Executing findAllMetadataQueryByMetadataId [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        return new TextPageData<>(metadataQueryDao.findByMetadataConfigId(metadataConfigId.getId(), pageLink), pageLink);
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

    @Override
    public List<MetadataQuery> scheduleAllQueries() {
        return metadataQueryDao.findAll().stream().map(this::scheduleQuery).collect(Collectors.toList());
    }

    private MetadataQuery scheduleQuery(MetadataQuery query) {
        MetadataConfig metadataConfig = metadataConfigService.findMetadataConfigById(query.getMetadataConfigId());
        log.info("Scheduling metadata query [{}]", query);
        managerActorService.process(new QueryMessage(query, metadataConfig, MessageType.CREATE));
        return query;
    }
}
