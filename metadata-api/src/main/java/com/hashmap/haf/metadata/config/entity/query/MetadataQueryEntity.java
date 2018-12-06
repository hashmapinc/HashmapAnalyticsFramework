package com.hashmap.haf.metadata.config.entity.query;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.entity.BaseSqlEntity;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import com.hashmap.haf.metadata.config.trigger.TriggerType;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
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
    private String queryStmt;

    @Column(name = ModelConstants.METADATA_QUERY_CONFIG_ID)
    private String metadataConfigId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.METADATA_QUERY_TRIGGER_TYPE)
    private TriggerType triggerType;

    @Column(name = ModelConstants.METADATA_QUERY_TRIGGER_SCHEDULE)
    private String triggerSchedule;

    @Column(name = ModelConstants.METADATA_QUERY_ATTRIBUTE)
    private String attribute;

    public MetadataQueryEntity() {
        super();
    }


    public MetadataQueryEntity(MetadataQuery metadataQuery) {
        if (metadataQuery.getId() != null) {
            this.setId(metadataQuery.getId().getId());
        } else {
            this.setId(Generators.timeBasedGenerator().generate());
        }

        if (metadataQuery.getMetadataConfigId() != null) {
            this.metadataConfigId = UUIDConverter.fromTimeUUID(metadataQuery.getMetadataConfigId().getId());
        }
        this.queryStmt = metadataQuery.getQueryStmt();
        this.triggerType = metadataQuery.getTriggerType();
        this.triggerSchedule = metadataQuery.getTriggerSchedule();
        this.attribute = metadataQuery.getAttribute();
    }

    @Override
    public MetadataQuery toData() {
        MetadataQuery metadataQuery = new MetadataQuery(new MetadataQueryId(getId()));
        metadataQuery.setCreatedTime(UUIDs.unixTimestamp(getId()));
        if (metadataConfigId != null) {
            metadataQuery.setMetadataConfigId(new MetadataConfigId(UUIDConverter.fromString(this.metadataConfigId)));
        }
        metadataQuery.setQueryStmt(this.queryStmt);
        metadataQuery.setTriggerType(this.triggerType);
        metadataQuery.setTriggerSchedule(this.triggerSchedule);
        metadataQuery.setAttribute(this.attribute);
        return metadataQuery;
    }
}
