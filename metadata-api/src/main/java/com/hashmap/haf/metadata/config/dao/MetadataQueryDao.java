package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.model.MetadataQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetadataQueryDao {

    MetadataQuery save(MetadataQuery metadataQuery);

    Optional<MetadataQuery> findById(UUID id);

    List<MetadataQuery> findAll();

    List<MetadataQuery> findByMetadataId(UUID metadataId);

    boolean removeById(UUID id);

}
