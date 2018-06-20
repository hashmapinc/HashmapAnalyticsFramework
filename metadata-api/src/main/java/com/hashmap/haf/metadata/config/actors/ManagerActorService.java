package com.hashmap.haf.metadata.config.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
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

    @Autowired
    private ActorSystemContext actorSystemContext;

    private ActorSystem system;

    private ActorRef managerActor;

    @PostConstruct
    public void initActorSystem() {
        log.info("Initializing Metadata Config Actor System");
        actorSystemContext.setActorService(this);

        system = ActorSystem.create("MetadataConfigActorSystem");

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


    public void process(MetadataMessage metadataMessage) {
        log.trace("Process Metadata Message msgType : {}", metadataMessage.getMessageType());
        managerActor.tell(metadataMessage, ActorRef.noSender());
    }

    public void process(QueryMessage queryMessage) {
        log.trace("Process Query Message msgType : {}", queryMessage.getMessageType());
        managerActor.tell(queryMessage, ActorRef.noSender());
    }
}
