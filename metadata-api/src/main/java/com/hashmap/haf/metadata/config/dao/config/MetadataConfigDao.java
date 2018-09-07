package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.page.TextPageLink;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetadataConfigDao {

    MetadataConfig save(MetadataConfig metadataConfig);

    Optional<MetadataConfig> findById(UUID id);

    List<MetadataConfig> findByOwnerId(String ownerId, TextPageLink textPageLink);

    boolean removeById(UUID id);
}
