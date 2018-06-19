package com.hashmap.haf.metadata.config.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import com.hashmap.haf.metadata.config.actors.message.metadata.CreateMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.DeleteMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.UpdateMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.query.CreateQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.DeleteQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.UpdateQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
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

    public void createMetadataConfig(MetadataConfig metadataConfig) {
        log.trace("Processing Create Metadata Config msg");
        managerActor.tell(new CreateMetadataConfigMsg(metadataConfig), ActorRef.noSender());
    }

    public void updateMetadataConfig(MetadataConfig metadataConfig) {
        log.trace("Processing Update Metadata Config  msg");
        managerActor.tell(new UpdateMetadataConfigMsg(metadataConfig), ActorRef.noSender());
    }

    public void deleteMetadataConfig(MetadataConfig metadataConfig) {
        log.trace("Processing Delete Metadata Config  msg");
        managerActor.tell(new DeleteMetadataConfigMsg(metadataConfig), ActorRef.noSender());
    }

    public void createQuery(String query, MetadataConfig metadataConfig) {
        log.trace("Processing Create Query  msg");
        managerActor.tell(new CreateQueryMsg(query, metadataConfig), ActorRef.noSender());
    }

    public void updateQuery(String query, MetadataConfig metadataConfig) {
        log.trace("Processing Update Query msg");
        managerActor.tell(new UpdateQueryMsg(query, metadataConfig), ActorRef.noSender());
    }

    public void deleteQuery(String query, MetadataConfig metadataConfig) {
        log.trace("Processing Delete Query  msg");
        managerActor.tell(new DeleteQueryMsg(query, metadataConfig), ActorRef.noSender());
    }
}
