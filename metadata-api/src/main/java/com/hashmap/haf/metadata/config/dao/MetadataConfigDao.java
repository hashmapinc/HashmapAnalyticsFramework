package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.model.MetadataConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetadataConfigDao {

    MetadataConfig save(MetadataConfig metadataConfig);

    Optional<MetadataConfig> findById(UUID id);

    List<MetadataConfig> findByOwnerId(String ownerId);

    List<MetadataConfig> findAll();

    boolean removeById(UUID id);
}
