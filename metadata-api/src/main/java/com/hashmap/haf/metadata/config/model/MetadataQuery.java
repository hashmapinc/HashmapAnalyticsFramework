package com.hashmap.haf.metadata.config.model;

import com.hashmap.haf.metadata.core.common.data.BaseData;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MetadataQuery extends BaseData<MetadataQueryId> {
    private MetadataConfigId metadataConfigId;
    private String queryStmt;

    public MetadataQuery() {
    }

    public MetadataQuery(MetadataQueryId id) {
        super(id);
    }

    public MetadataQuery(MetadataQuery metadataQuery) {
        super(metadataQuery);
        this.metadataConfigId = metadataQuery.metadataConfigId;
        this.queryStmt = metadataQuery.queryStmt;
    }

    public MetadataConfigId getMetadataConfigId() {
        return metadataConfigId;
    }

    public void setMetadataConfigId(MetadataConfigId metadataConfigId) {
        this.metadataConfigId = metadataConfigId;
    }

    public String getQueryStmt() {
        return queryStmt;
    }

    public void setQueryStmt(String queryStmt) {
        this.queryStmt = queryStmt;
    }

    @Override
    public String toString() {
        return "MetadataQuery{" +
                "metadataConfigId = " + metadataConfigId +
                "queryStmt=" + queryStmt +
                '}';
    }

    public void update(MetadataQuery metadataQuery) {
        if(metadataQuery.getQueryStmt() != null) {
            this.setQueryStmt(metadataQuery.getQueryStmt());
        }
    }
}
