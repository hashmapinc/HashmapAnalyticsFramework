package com.hashmap.haf.metadata.config.entity;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;
import com.hashmap.haf.metadata.core.common.constants.ModelConstants;
import com.hashmap.haf.metadata.core.common.entity.BaseSqlEntity;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import com.hashmap.haf.metadata.core.util.UUIDConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Slf4j
@Table(name = ModelConstants.METADATA_QUERY_TABLE_NAME)
public class MetadataQueryEntity extends BaseSqlEntity<MetadataQuery> {

    @Column(name = ModelConstants.METADATA_QUERY)
    private String queryStmt;

//    @OneToOne(cascade = CascadeType.REMOVE)
    @Column(name = "metadata_id")
    private String metadataConfigId;

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

        if (metadataQuery.getMetadataConfigId() != null) {
            this.metadataConfigId = metadataQuery.getMetadataConfigId().getId().toString();
        }
        this.queryStmt = metadataQuery.getQueryStmt();
        this.triggerType = metadataQuery.getTriggerType();
        this.triggerSchedule = metadataQuery.getTriggerSchedule();
    }

    @Override
    public MetadataQuery toData() {
        MetadataQuery metadataQuery = new MetadataQuery(new MetadataQueryId(getId()));
        metadataQuery.setCreatedTime(UUIDs.unixTimestamp(getId()));
        if (metadataConfigId != null) {
            metadataQuery.setMetadataConfigId(new MetadataConfigId(UUID.fromString(this.metadataConfigId)));
        }
        metadataQuery.setQueryStmt(this.queryStmt);
        metadataQuery.setTriggerType(this.triggerType);
        metadataQuery.setTriggerSchedule(this.triggerSchedule);
        return metadataQuery;
    }
}
