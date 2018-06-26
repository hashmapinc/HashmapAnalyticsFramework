package com.hashmapinc.haf.page;

import com.hashmapinc.haf.constants.ModelConstants;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class PaginatedRequest {

    private Map<String, Object> criteria;
    private UUID tenantId;
    private UUID customerId;
    private TextPageLink pageLink;

    public boolean hasCriteria(){
        return criteria != null && !criteria.isEmpty();
    }

    public String idOffset(){
        return pageLink.getIdOffset() != null ? pageLink.getIdOffset().toString() : ModelConstants.NULL_UUID.toString();
    }
}
