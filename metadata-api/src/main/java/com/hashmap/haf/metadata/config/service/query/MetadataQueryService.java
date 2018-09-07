package com.hashmap.haf.metadata.config.service.query;

import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import com.hashmap.haf.metadata.config.page.TextPageData;
import com.hashmap.haf.metadata.config.page.TextPageLink;

import java.util.List;

public interface MetadataQueryService {

    MetadataQuery findMetadataQueryById(MetadataQueryId metadataQueryId);

    MetadataQuery saveMetadataQuery(MetadataQuery metadataQuery);

    void deleteMetadataQuery(MetadataQueryId metadataQueryId);

    TextPageData<MetadataQuery> findAllMetadataQueryByMetadataId(MetadataConfigId metadataConfigId, TextPageLink pageLink);

    MetadataQuery updateMetadataQuery(MetadataQuery metadataQuery);

    int deleteMetadataQueryByMetadataConfigId(MetadataConfigId metadataConfigId);

    List<MetadataQuery> scheduleAllQueries();
}