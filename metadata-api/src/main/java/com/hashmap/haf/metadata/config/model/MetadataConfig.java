package com.hashmap.haf.metadata.config.model;

import com.hashmap.haf.metadata.core.common.data.BaseData;
import com.hashmap.haf.metadata.core.data.resource.DataResource;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MetadataConfig extends BaseData<MetadataConfigId> {
    private String ownerId;
    private String name;
    private DataResource source;
    private DataResource sink;
    private TriggerType triggerType;
    private String triggerSchedule;

    public MetadataConfig() {
        super();
    }

    public MetadataConfig(MetadataConfigId id) {
        super(id);
    }

    public MetadataConfig(MetadataConfig metadataConfig) {
        super(metadataConfig);
        this.ownerId = metadataConfig.ownerId;
        this.name = metadataConfig.name;
        this.source = metadataConfig.source;
        this.sink = metadataConfig.sink;
        this.triggerType = metadataConfig.triggerType;
        this.triggerSchedule = metadataConfig.triggerSchedule;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataResource getSource() {
        return source;
    }

    public void setSource(DataResource source) {
        this.source = source;
    }

    public DataResource getSink() {
        return sink;
    }

    public void setSink(DataResource sink) {
        this.sink = sink;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerSchedule() {
        return triggerSchedule;
    }

    public void setTriggerSchedule(String triggerSchedule) {
        this.triggerSchedule = triggerSchedule;
    }

    @Override
    public String toString() {
        return "MetadataConfig{" +
                "ownerId" + ownerId +
                "name=" + name +
                ", source=" + source +
                ", sink=" + sink +
                ", triggerType=" + triggerType +
                ", triggerSchedule=" + triggerSchedule +
                '}';
    }
}
