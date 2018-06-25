package com.hashmap.haf.metadata.config.entity;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;
import com.hashmap.haf.metadata.core.common.constants.ModelConstants;
import com.hashmap.haf.metadata.core.common.entity.BaseSqlEntity;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Slf4j
@Table(name = ModelConstants.METADATA_QUERY_TABLE_NAME)
public class MetadataQueryEntity extends BaseSqlEntity<MetadataQuery> {

    @Column(name = ModelConstants.METADATA_QUERY)
    String queryStmt;

    @OneToOne(cascade = CascadeType.REMOVE)
    MetadataConfigId metadataConfigId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.METADATA_QUERY_TRIGGER_TYPE)
    private TriggerType triggerType;

    @Column(name = ModelConstants.METADATA_QUERY_TRIGGER_SCHEDULE)
    private String triggerSchedule;

    public MetadataQueryEntity() {
        super();
    }


    public MetadataQueryEntity(MetadataQuery metadataQuery) {
        if (metadataQuery.getId() != null) {
            this.setId(metadataQuery.getId().getId());
        } else {
            this.setId(Generators.timeBasedGenerator().generate());
        }

        this.metadataConfigId = metadataQuery.getMetadataConfigId();
        this.queryStmt = metadataQuery.getQueryStmt();
        this.triggerType = metadataQuery.getTriggerType();
        this.triggerSchedule = metadataQuery.getTriggerSchedule();
    }

    @Override
    public MetadataQuery toData() {
        MetadataQuery metadataQuery = new MetadataQuery(new MetadataQueryId(getId()));
        metadataQuery.setCreatedTime(UUIDs.unixTimestamp(getId()));
        metadataQuery.setMetadataConfigId(this.metadataConfigId);
        metadataQuery.setQueryStmt(this.queryStmt);
        metadataQuery.setTriggerType(this.triggerType);
        metadataQuery.setTriggerSchedule(this.triggerSchedule);
        return metadataQuery;
    }
}
