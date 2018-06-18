package com.hashmap.haf.metadata.config.actors;

import com.hashmap.haf.metadata.config.service.MetadataConfigService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActorSystemContext {

    @Getter @Setter private ManagerActorService actorService;

    @Autowired
    @Getter private MetadataConfigService metadataConfigService;
}
