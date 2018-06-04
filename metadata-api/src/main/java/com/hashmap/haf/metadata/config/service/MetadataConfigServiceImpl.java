package com.hashmap.haf.metadata.config.service;

import com.hashmap.haf.metadata.config.dao.MetadataConfigDao;
import com.hashmap.haf.metadata.config.exceptions.DataValidationException;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.core.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MetadataConfigServiceImpl implements MetadataConfigService {

    private static final String INCORRECT_METADATACONFIG_ID = "Incorrect metaDataConfigId ";
    private static final String INCORRECT_OWNER_ID = "Incorrect ownerId ";

    @Autowired
    private MetadataConfigDao metadataConfigDao;

    @Override
    public MetadataConfig saveMetadataConfig(MetadataConfig metadataConfig) {
        log.trace("Executing saveMetadataConfig [{}]", metadataConfig);
        return metadataConfigDao.save(metadataConfig);
    }

    @Override
    public MetadataConfig findMetadataConfigById(MetadataConfigId metadataConfigId) {
        log.trace("Executing findMetaDataConfigById [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        Optional<MetadataConfig> metadataConfig = metadataConfigDao.findById(metadataConfigId.getId());
        return metadataConfig.orElse(null);
    }

    @Override
    public List<MetadataConfig> findAllMetadataConfigByOwnerId(String ownerId) {
        log.trace("Executing findAllMetadataConfigByOwnerId [{}]", ownerId);
        Validator.validateString(ownerId, INCORRECT_OWNER_ID + ownerId);
        return metadataConfigDao.findByOwnerId(ownerId);
    }

    @Override
    public MetadataConfig updateMetadataConfig(MetadataConfig metadataConfig) {
        log.trace("Executing updateMetadataConfigById [{}]", metadataConfig.getId());
        Validator.validateId(metadataConfig.getId(), INCORRECT_METADATACONFIG_ID + metadataConfig.getId());
        Optional<MetadataConfig> savedMetadataConfig = metadataConfigDao.findById(metadataConfig.getId().getId());

        if(savedMetadataConfig.isPresent()){
            savedMetadataConfig.get().update(metadataConfig);
            return  metadataConfigDao.save(savedMetadataConfig.get());
        }else {
            throw new DataValidationException("Can't update for non-existent metaDataConfig!");
        }
    }

    @Override
    public List<MetadataConfig> findAllMetadataConfig() {
        log.trace("Executing findAllMetadataConfig [{}]");
        return metadataConfigDao.findAll();
    }

    @Override
    public void deleteMetadataConfig(MetadataConfigId metadataConfigId) {
        log.trace("Executing deleteMetadataConfig [{}]", metadataConfigId);
        Validator.validateId(metadataConfigId, INCORRECT_METADATACONFIG_ID + metadataConfigId);
        metadataConfigDao.removeById(metadataConfigId.getId());
    }

}
