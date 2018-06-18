package com.hashmap.haf.metadata.config.actors;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class SupervisionStrategy {

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(3, scala.concurrent.duration.Duration.create(3, TimeUnit.SECONDS),
                    DeciderBuilder
                            .match(Exception.class, e -> {
                                log.info("Exception {}", e.getMessage());
                                return akka.actor.SupervisorStrategy.resume();
                            })
                            .matchAny(o -> akka.actor.SupervisorStrategy.escalate())
                            .build());


    public static SupervisorStrategy getStrategy() {
        return strategy;
    }
}
