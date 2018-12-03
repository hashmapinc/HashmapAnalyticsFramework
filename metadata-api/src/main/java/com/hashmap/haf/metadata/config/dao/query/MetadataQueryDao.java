package com.hashmap.haf.metadata.config.dao.query;

import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.page.TextPageLink;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetadataQueryDao {

    MetadataQuery save(MetadataQuery metadataQuery);

    Optional<MetadataQuery> findById(UUID id);

    List<MetadataQuery> findAll();

    List<MetadataQuery> findByMetadataConfigId(UUID metadataId, TextPageLink pageLink);

    Optional<MetadataQuery> findByQueryStmtAndMetadataConfigId(String queryStmt, UUID metadataId);

    boolean removeById(UUID id);

    int removeByMetadataConfigId(UUID metadataId);
}
