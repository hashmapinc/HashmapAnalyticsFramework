package com.hashmap.haf.metadata.config.service.config;

import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;

import java.util.List;

public interface MetadataConfigService  {

    MetadataConfig findMetadataConfigById(MetadataConfigId metadataConfigId);

    MetadataConfig saveMetadataConfig(MetadataConfig metadataConfig);

    void deleteMetadataConfig(MetadataConfigId metadataConfigId);

    List<MetadataConfig> findAllMetadataConfigByOwnerId(String ownerId);

    List<MetadataConfig> findAllMetadataConfig();

    MetadataConfig updateMetadataConfig(MetadataConfig metadataConfig);

    MetadataConfig runIngestion(MetadataConfigId metadataConfigId);

    boolean testConnection(MetadataConfigId metadataConfigId);

}
