package com.hashmap.haf.scheduler.impl

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit._
import com.hashmap.haf.scheduler.SchedulerApplicationConfig
import com.hashmap.haf.scheduler.executor.actors.ExecutorActor
import com.hashmap.haf.scheduler.extension.SpringExtension
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, TestPropertySource}

import scala.concurrent.duration.{Duration, _}


@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[SpringExtension], classOf[QuartzScheduler], classOf[ExecutorActor]))
@ContextConfiguration(classes = Array(classOf[SchedulerApplicationConfig]), loader = classOf[AnnotationConfigContextLoader])
@TestPropertySource(Array("classpath:application-test.yml"))
@ActiveProfiles(Array("test"))
class QuartzSchedulerTest {

  @Autowired
  private var springExtension: SpringExtension = _
  @Autowired
  private var quartzScheduler: QuartzScheduler = _
  @Autowired
  private var system: ActorSystem = _

  private val every5secondsCronExpr = "0/5 0/1 * 1/1 * ? *"

  @Test
  def shouldNotCreateSameJobAgain = {
    val bool = quartzScheduler.createJob("job1", every5secondsCronExpr)
    assertTrue(bool)
    val bool2 = quartzScheduler.createJob("job1", every5secondsCronExpr)
    assertFalse(bool2)
  }

  @Test
  def shouldNotCreateJobOnPassingInvalidCron = {
    assertFalse(quartzScheduler.createJob("job2", "* * *")) // Invalid cron
  }

  @Test
  def shouldCreateJobOnPassingValidNameAndCron = {
    assertTrue(quartzScheduler.createJob("job3", every5secondsCronExpr))
  }



  @Test
  def shouldNotScheduleJobForNonExistingJobName = {
    val receiver = system.actorOf(Props(new ScheduleTestReceiver))
    implicit val _system = system
    val probe: TestProbe = TestProbe()
    receiver ! NewProbe(probe.ref)
    assertFalse(quartzScheduler.submitJob("job4", receiver, Tick))
  }

  @Test
  def shouldScheduleJobForExistingJobName = {
    val receiver = system.actorOf(Props(new ScheduleTestReceiver))
    implicit val _system = system
    val probe: TestProbe = TestProbe()
    receiver ! NewProbe(probe.ref)

    // Running job on every 5 seconds
    quartzScheduler.createJob("job5", every5secondsCronExpr)
    assertTrue(quartzScheduler.submitJob("job5", receiver, Tick))

    // Try to listen 2 messages and wait for 10 seconds to finish (num of messages * message interval) i.e. (2 * 5)
    // Thread will wait for each message for 5 seconds so idle time is 5 seconds
    val receipt = probe.receiveWhile(Duration(10, SECONDS), Duration(5, SECONDS), 2) {
      case Tock =>
        Tock
    }
    assertTrue(receipt.contains(Tock))
    assertTrue(receipt.size == 2)
  }

  @Test
  def shouldAbleToUpdateValidJob = {
    val receiver = system.actorOf(Props(new ScheduleTestReceiver))
    implicit val _system = system
    val probe: TestProbe = TestProbe()
    receiver ! NewProbe(probe.ref)

    quartzScheduler.createJob("job6", every5secondsCronExpr)
    assertTrue(quartzScheduler.submitJob("job6", receiver, Tick))

    val receipt = probe.receiveWhile(Duration(16, SECONDS), Duration(5, SECONDS), 3) {
      case Tock =>
        Tock
    }
    assertTrue(receipt.contains(Tock))
    assertTrue(receipt.size == 3)

    // Update job for 2 second interval
    val every2SecondsCronExpr = "0/2 0/1 * 1/1 * ? *"
    assertTrue(quartzScheduler.updateJob("job6", receiver, every2SecondsCronExpr, Tick))

    val receipt1 = probe.receiveWhile(Duration(7, SECONDS), Duration(2, SECONDS), 3) {
      case Tock =>
        Tock
    }
    assertTrue(receipt1.contains(Tock))
    assertTrue(receipt1.size == 3)
  }

  @Test
  def shouldSuspendAndResumeJobForExistingJobName = {
    val receiver = system.actorOf(Props(new ScheduleTestReceiver))
    implicit val _system = system
    val probe: TestProbe = TestProbe()
    receiver ! NewProbe(probe.ref)

    // Running job on every 5 seconds
    quartzScheduler.createJob("job7", every5secondsCronExpr)
    assertTrue(quartzScheduler.submitJob("job7", receiver, Tick))
    assertTrue(quartzScheduler.suspendJob("job7"))
    assertTrue(quartzScheduler.resumeJob("job7"))
  }
}


case class NewProbe(probe: ActorRef)
case object Tick
case object Tock
case class TockWithFireTime(scheduledFireTime:Long)

class ScheduleTestReceiver extends Actor with ActorLogging {
  var probe: ActorRef = _
  def receive = {
    case NewProbe(_p) =>
      probe = _p
    case Tick =>
      log.info(s"Got a Tick.")
      probe ! Tock

  }
}

