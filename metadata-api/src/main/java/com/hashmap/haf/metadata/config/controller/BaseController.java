package com.hashmap.haf.metadata.config.controller;

import com.hashmap.haf.metadata.config.page.TextPageLink;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import java.util.UUID;

public abstract class BaseController {

    protected TextPageLink createPageLink(int limit, String textSearch, String idOffset, String textOffset) {
        UUID idOffsetUuid = null;
        if (StringUtils.isNotEmpty(idOffset)) {
            idOffsetUuid = UUID.fromString(idOffset);
        }
        return new TextPageLink(limit, textSearch, idOffsetUuid, textOffset);
    }
}