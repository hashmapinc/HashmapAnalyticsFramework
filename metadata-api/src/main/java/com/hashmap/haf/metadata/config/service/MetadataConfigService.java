package com.hashmap.haf.metadata.config.service;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;

public interface MetadataConfigService  {

    MetadataConfig findMetadataConfigById(MetadataConfigId metadataConfigId);

    MetadataConfig saveMetadataConfig(MetadataConfig metadataConfig);

    void deleteMetadataConfig(MetadataConfigId metadataConfigId);

}
