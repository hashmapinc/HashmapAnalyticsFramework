package com.hashmap.haf.metadata.config.service;

import com.hashmap.haf.metadata.config.model.*;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;

import java.util.List;

public interface MetadataQueryService {

    MetadataQuery findMetadataQueryById(MetadataQueryId metadataQueryId);

    MetadataQuery saveMetadataQuery(MetadataQuery metadataQuery);

    void deleteMetadataQuery(MetadataQueryId metadataQueryId);

    List<MetadataQuery> findAllMetadataQueryByMetadataId(MetadataConfigId metadataConfigId);

    List<MetadataQuery> findAllMetadataQuery();

    MetadataQuery updateMetadataQuery(MetadataQuery metadataQuery);

    int deleteMetadataQueryByMetadataConfigId(MetadataConfigId metadataConfigId);

}
