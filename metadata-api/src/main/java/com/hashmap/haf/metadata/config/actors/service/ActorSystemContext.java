package com.hashmap.haf.metadata.config.actors.service;

import com.hashmap.haf.metadata.config.service.config.MetadataConfigService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ActorSystemContext {

    private static final String AKKA_CONF_FILE_NAME = "actor-system.conf";

    @Getter @Setter private ManagerActorService actorService;

    @Autowired
    @Getter private MetadataConfigService metadataConfigService;

    @Autowired
    @Qualifier("oauth2RestTemplate")
    @Getter
    private RestTemplate oauth2RestTemplate;

    @Getter private final Config config;

    public ActorSystemContext() {
        Config toResolve = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load());
        config = toResolve.resolve();
    }
}
