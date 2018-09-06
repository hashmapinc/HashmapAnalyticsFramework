package com.hashmap.haf.metadata.config.model.config;

import com.hashmap.haf.metadata.config.model.SearchTextBased;
import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MetadataConfig extends SearchTextBased<MetadataConfigId> {

    private static final long serialVersionUID = 8793760996366783238L;

    private String ownerId;
    private String name;
    private DataResource source;
    private DataResource sink;

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
    }

    @Override
    public String getSearchText() {
        return getName();
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

    public void update(MetadataConfig metadataConfig) {
        this.setOwnerId(metadataConfig.getOwnerId());
        this.setName(metadataConfig.getName());

        DataResource updatedSink = metadataConfig.getSink();
        if (this.sink != null) {
            updatedSink.setId(this.sink.getId());
        }
        this.setSink(updatedSink);

        DataResource updatedSource = metadataConfig.getSource();
        if (this.source != null) {
            updatedSource.setId(this.source.getId());
        }
        this.setSource(updatedSource);
    }

    @Override
    public String toString() {
        return "MetadataConfig{" +
                "ownerId = " + ownerId +
                ", name=" + name +
                ", source=" + source +
                ", sink=" + sink +
                '}';
    }
}
