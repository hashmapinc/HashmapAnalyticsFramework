//package com.hashmap.haf.metadata.config.test.actors;
//
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import com.hashmap.haf.metadata.config.actors.ActorSystemContext;
//import com.hashmap.haf.metadata.config.actors.ManagerActor;
//import com.hashmap.haf.metadata.config.actors.message.metadata.CreateMetadataConfigMsg;
//import com.hashmap.haf.metadata.config.model.MetadataConfig;
//import com.hashmap.haf.metadata.config.model.MetadataConfigId;
//import com.sun.org.glassfish.external.probe.provider.annotations.Probe;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import scala.concurrent.duration.Duration;
//
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//public class ManagerActorTest {
//
//    private ActorSystem system;
//    private MetadataConfig metadataConfig;
//
//    @Autowired
//    private ActorSystemContext actorSystemContext;
//
//    @Before
//    public void before() {
//        system = ActorSystem.create("TestMetadataConfigActorSystem");
//        metadataConfig = new MetadataConfig();
//        metadataConfig.setId(new MetadataConfigId(UUID.fromString("d1829aae-72e3-11e8-addf-f3fd387598e3")));
//        metadataConfig.setOwnerId("3f5d7365-694c-11e8-ab22-67adeaabe4d6");
//    }
//
//    @After
//    public void tearDown() {
//        TestKit.shutdownActorSystem(system, Duration.create(2, TimeUnit.SECONDS), true);
//        system = null;
//    }
//
//    @Test
//    public void createMetadataConfigMsg() {
//       TestKit probe = new TestKit(system);
//        ActorRef testActor = probe.testActor();
//        ActorRef managerActor = system.actorOf(ManagerActor.props(), "TestMetadataConfigManagerActor");
////
//        managerActor.tell(new CreateMetadataConfigMsg(metadataConfig), testActor);
//       probe.expectMsgClass(CreateMetadataConfigMsg.class);
//
//
//        new TestKit(system) {{
//
//            Assert.assertEquals("TestMetadataConfigActorSystem", system.name());
//
//
////            final Props props = Props.create(ManagerActor.class);
////            final ActorRef managerActor = system.actorOf(props);
////
////            final TestKit probe = new TestKit(system);
////
////
////            managerActor.tell(new CreateMetadataConfigMsg(metadataConfig), ActorRef.noSender());
////            probe.expectMsgClass(CreateMetadataConfigMsg.class);
//
////            within(java.time.Duration.ofSeconds(3), () -> {
////                managerActor.tell("hello", getRef());
////
////                // This is a demo: would normally use expectMsgEquals().
////                // Wait time is bounded by 3-second deadline above.
////                awaitCond(probe::msgAvailable);
////
////                // response must have been enqueued to us before probe
////                expectMsg(java.time.Duration.ZERO, "world");
////                // check that the probe we injected earlier got the msg
////                probe.expectMsg(java.time.Duration.ZERO, "hello");
////                Assert.assertEquals(getRef(), probe.lastSender());
////
////                // Will wait for the rest of the 3 seconds
////                expectNoMsg();
////                return null;
////            });
//        }};
//
//
//    }
//}