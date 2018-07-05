package com.hashmap.haf.metadata.config.actors.service;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.ManagerActor;
import com.hashmap.haf.metadata.config.actors.MetadataSchedulerActor;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class ManagerActorService {

    public static final String OWNER_DISPATCHER = "owner-dispatcher";
    public static final String METADATA_DISPATCHER = "metadata-dispatcher";
    public static final String QUERY_DISPATCHER = "query-dispatcher";
    private static String schedulerPath;

    @Autowired
    private ActorSystemContext actorSystemContext;

    private ActorSystem system;

    private ActorRef managerActor;

    private ActorRef schedulerActor;

    @PostConstruct
    public void initActorSystem() {
        log.info("Initializing Metadata Config Actor System");
        actorSystemContext.setActorService(this);

        system = ActorSystem.create("MetadataConfigActorSystem", actorSystemContext.getConfig());

        log.info("Creating Metadata Scheduler Actor");
        schedulerActor = system.actorOf(MetadataSchedulerActor.props(), "MetadataSchedulerActor");
        schedulerPath = schedulerActor.path().toString();

        log.info("Creating Metadata Manager Actor");
        managerActor = system.actorOf(ManagerActor.props(), "MetadataConfigManagerActor");

    }

    @PreDestroy
    public void stopActorSystem() {
        Future<Terminated> status = system.terminate();
        try {
            Terminated terminated = Await.result(status, Duration.Inf());
            log.info("Actor system terminated: {}", terminated);
        } catch (Exception e) {
            log.error("Failed to terminate actors system.", e);
        }
    }

    public static String getSchedulerPath() {
        return schedulerPath;
    }


    public void process(MetadataMessage metadataMessage) {
        log.trace("Process Metadata Message msgType : {}", metadataMessage.getMessageType());
        managerActor.tell(metadataMessage, ActorRef.noSender());
    }

    public void process(QueryMessage queryMessage) {
        log.trace("Process MetadataQuery Message msgType : {}", queryMessage.getMessageType());
        managerActor.tell(queryMessage, ActorRef.noSender());
    }
}
