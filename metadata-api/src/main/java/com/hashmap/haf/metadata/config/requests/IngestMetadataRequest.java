package com.hashmap.haf.metadata.config.requests;

import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class IngestMetadataRequest {
    private MetadataConfigId configId;
    private String ownerId;
    private String configName;
    private Map<String, Object> data;
}
