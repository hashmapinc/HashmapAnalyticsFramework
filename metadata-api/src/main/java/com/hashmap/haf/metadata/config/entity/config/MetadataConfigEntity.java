package com.hashmap.haf.metadata.config.entity.config;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.entity.BaseSqlEntity;
import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import com.hashmap.haf.metadata.config.entity.data.resource.DataResourceEntity;
import com.hashmap.haf.metadata.config.entity.data.resource.jdbc.JdbcResourceEntity;
import com.hashmap.haf.metadata.config.model.data.resource.jdbc.JdbcResource;
import com.hashmap.haf.metadata.config.model.data.resource.jdbc.JdbcResourceId;
import com.hashmap.haf.metadata.config.entity.data.resource.rest.RestResourceEntity;
import com.hashmap.haf.metadata.config.model.data.resource.rest.RestResource;
import com.hashmap.haf.metadata.config.model.data.resource.rest.RestResourceId;
import com.hashmap.haf.metadata.config.trigger.TriggerType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Slf4j
@Table(name = ModelConstants.METADATA_CONFIG_TABLE_NAME)
public class MetadataConfigEntity extends BaseSqlEntity<MetadataConfig> {

    @Column(name = ModelConstants.METADATA_CONFIG_NAME)
    private String name;

    @Column(name = ModelConstants.METADATA_CONFIG_OWNER_ID)
    private String ownerId;

    @OneToOne(cascade = CascadeType.ALL)
    private DataResourceEntity source;

    @OneToOne(cascade = CascadeType.ALL)
    private DataResourceEntity sink;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.METADATA_CONFIG_TRIGGER_TYPE)
    private TriggerType triggerType;

    @Column(name = ModelConstants.METADATA_CONFIG_TRIGGER_SCHEDULE)
    private String triggerSchedule;

    public MetadataConfigEntity() {
        super();
    }

    public MetadataConfigEntity(MetadataConfig metadataConfig) {
        if (metadataConfig.getId() != null) {
            this.setId(metadataConfig.getId().getId());
        } else {
            this.setId(Generators.timeBasedGenerator().generate());
        }

        this.name = metadataConfig.getName();
        this.ownerId = metadataConfig.getOwnerId();
        this.source = getDataResourceEntity(metadataConfig.getSource());
        this.sink = getDataResourceEntity(metadataConfig.getSink());
        this.triggerType = metadataConfig.getTriggerType();
        this.triggerSchedule = metadataConfig.getTriggerSchedule();
    }

    @Override
    public MetadataConfig toData() {
        MetadataConfig metadataConfig = new MetadataConfig(new MetadataConfigId(getId()));
        metadataConfig.setCreatedTime(UUIDs.unixTimestamp(getId()));
        metadataConfig.setName(name);
        metadataConfig.setOwnerId(ownerId);
        metadataConfig.setSource(getDataResource(source));
        metadataConfig.setSink(getDataResource(sink));
        metadataConfig.setTriggerType(triggerType);
        metadataConfig.setTriggerSchedule(triggerSchedule);
        return metadataConfig;
    }

    private DataResourceEntity getDataResourceEntity(DataResource dataResource) {
        if (dataResource instanceof JdbcResource) {
            return new JdbcResourceEntity((JdbcResource) dataResource);
        }
        if (dataResource instanceof RestResource) {
            return new RestResourceEntity((RestResource) dataResource);
        }
        return null;
    }

    private DataResource getDataResource(DataResourceEntity dataResourceEntity) {
        if (dataResourceEntity instanceof JdbcResourceEntity) {
            JdbcResource jdbcResource = new JdbcResource();
            jdbcResource.setId(new JdbcResourceId(dataResourceEntity.getId()));
            jdbcResource.setCreatedTime(UUIDs.unixTimestamp(getId()));
            jdbcResource.setDbUrl(((JdbcResourceEntity) dataResourceEntity).getDbUrl());
            jdbcResource.setUsername(((JdbcResourceEntity) dataResourceEntity).getUsername());
            jdbcResource.setPassword(((JdbcResourceEntity) dataResourceEntity).getPassword());
            return jdbcResource;
        }
        if (dataResourceEntity instanceof RestResourceEntity) {
            RestResource restResource = new RestResource();
            restResource.setId(new RestResourceId(dataResourceEntity.getId()));
            restResource.setCreatedTime(UUIDs.unixTimestamp(getId()));
            restResource.setUrl(((RestResourceEntity) dataResourceEntity).getUrl());
            restResource.setUsername(((RestResourceEntity) dataResourceEntity).getUsername());
            restResource.setPassword(((RestResourceEntity) dataResourceEntity).getPassword());
            return restResource;
        }
        return null;
    }
}
