package com.hashmap.haf.metadata.config.service.config;

import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.page.TextPageData;
import com.hashmap.haf.metadata.config.page.TextPageLink;

public interface MetadataConfigService  {

    MetadataConfig findMetadataConfigById(MetadataConfigId metadataConfigId);

    MetadataConfig saveMetadataConfig(MetadataConfig metadataConfig);

    void deleteMetadataConfig(MetadataConfigId metadataConfigId);

    TextPageData<MetadataConfig> findAllMetadataConfigByOwnerId(String ownerId, TextPageLink pageLink);

    MetadataConfig updateMetadataConfig(MetadataConfig metadataConfig);

    MetadataConfig runIngestion(MetadataConfigId metadataConfigId);

    boolean testConnection(MetadataConfigId metadataConfigId);

}
