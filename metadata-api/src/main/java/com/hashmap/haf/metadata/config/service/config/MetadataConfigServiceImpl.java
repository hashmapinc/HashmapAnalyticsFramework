package com.hashmap.haf.metadata.config.service.config;

import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.dao.config.MetadataConfigDao;
import com.hashmap.haf.metadata.config.exceptions.DataValidationException;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.page.TextPageData;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.service.query.MetadataQueryService;
import com.hashmap.haf.metadata.config.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MetadataConfigServiceImpl implements MetadataConfigService {

    private static final String INCORRECT_METADATACONFIG_ID = "Incorrect metaDataConfigId ";
    private static final String INCORRECT_OWNER_ID = "Incorrect ownerId ";

    @Autowired
    private MetadataConfigDao metadataConfigDao;

    @Autowired
    private MetadataQueryService metadataQueryService;

    @Autowired
    private ManagerActorService managerActorService;

    @Override
    public MetadataConfig saveMetadataConfig(MetadataConfig metadataConfig) {
        if (metadataConfig == null) {
            throw new DataValidationException("Metadata-Config Object cannot be null");
        }
        log.trace("Executing saveMetadataConfig [{}]", metadataConfig);
        MetadataConfig savedMetadataConfig = metadataConfigDao.save(metadataConfig);
        managerActorService.process(new MetadataMessage(savedMetadataConfig, MessageType.CREATE));
        return savedMetadataConfig;
    }

    @Override
    public MetadataConfig findMetadataConfigById(MetadataConfigId metadataConfigId) {
        log.trace("Executing findMetaDataConfigById [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        Optional<MetadataConfig> metadataConfig = metadataConfigDao.findById(metadataConfigId.getId());
        return metadataConfig.orElse(null);
    }

    @Override
    public TextPageData<MetadataConfig> findAllMetadataConfigByOwnerId(String ownerId, TextPageLink pageLink) {
        log.trace("Executing findAllMetadataConfigByOwnerId [{}]", ownerId);
        Validator.validateString(ownerId, INCORRECT_OWNER_ID + ownerId);
        return new TextPageData<>(metadataConfigDao.findByOwnerId(ownerId, pageLink), pageLink);
    }

    @Override
    public MetadataConfig updateMetadataConfig(MetadataConfig metadataConfig) {
        if (metadataConfig == null) {
            throw new DataValidationException("Can't update non-existent metadata-config");
        }
        log.trace("Executing updateMetadataConfigById [{}]", metadataConfig.getId());
        Validator.validateId(metadataConfig.getId(), INCORRECT_METADATACONFIG_ID + metadataConfig.getId());
        Optional<MetadataConfig> savedMetadataConfig = metadataConfigDao.findById(metadataConfig.getId().getId());

        if (savedMetadataConfig.isPresent()){
            savedMetadataConfig.get().update(metadataConfig);
            MetadataConfig updatedMetadataConfig = metadataConfigDao.save(savedMetadataConfig.get());
            managerActorService.process(new MetadataMessage(updatedMetadataConfig, MessageType.UPDATE));
            return updatedMetadataConfig;
        } else {
            throw new DataValidationException("Can't update for non-existent metaDataConfig!");
        }
    }

    @Override
    public void deleteMetadataConfig(MetadataConfigId metadataConfigId) {
        log.trace("Executing deleteMetadataConfig [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        MetadataConfig metadataConfig = findMetadataConfigById(metadataConfigId);
        if (metadataConfig != null) {
            metadataQueryService.deleteMetadataQueryByMetadataConfigId(metadataConfigId);
            metadataConfigDao.removeById(metadataConfigId.getId());
            managerActorService.process(new MetadataMessage(metadataConfig, MessageType.DELETE));
        }
    }

    @Override
    public MetadataConfig runIngestion(MetadataConfigId metadataConfigId) {
        log.trace("Executing runIngestion [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        MetadataConfig metadataConfig = findMetadataConfigById(metadataConfigId);
        if (metadataConfig != null) {
            managerActorService.process(new RunIngestionMsg(metadataConfig));
        }
        return metadataConfig;
    }

    @Override
    public boolean testConnection(MetadataConfigId metadataConfigId) {
        log.trace("Executing runIngestion [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        MetadataConfig metadataConfig = findMetadataConfigById(metadataConfigId);
        if (metadataConfig != null) {
            try {
                return metadataConfig.getSource().testConnection();
            } catch (Exception e) {
                log.warn("Exception : {}", e.getMessage());
            }
        }
        return false;
    }
}
