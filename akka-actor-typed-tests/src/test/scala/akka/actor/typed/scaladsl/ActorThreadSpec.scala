/*
 * Copyright (C) 2019 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.actor.typed.scaladsl

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import scala.concurrent.Future

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.adapter._
import org.scalatest.Matchers
import org.scalatest.WordSpecLike

class ActorThreadSpec extends ScalaTestWithActorTestKit("""
    akka.loggers = ["akka.testkit.TestEventListener"]
  """) with WordSpecLike with Matchers {

  // needed for the event filter
  implicit val untypedSystem = system.toUntyped

  "Actor thread-safety checks" must {

    "detect illegal access to ActorContext from outside" in {
      @volatile var context: ActorContext[String] = null
      val probe = createTestProbe[String]()

      spawn(Behaviors.setup[String] { ctx =>
        // here it's ok
        ctx.children
        context = ctx
        probe.ref ! "initialized"
        Behaviors.empty
      })

      probe.expectMessage("initialized")
      intercept[UnsupportedOperationException] {
        context.children
      }.getMessage should include("[children]")

    }

    "detect illegal access to ActorContext from other thread" in {
      val probe = createTestProbe[UnsupportedOperationException]()

      val ref = spawn(Behaviors.receive[CountDownLatch] {
        case (context, latch) =>
          Future {
            try {
              context.children
            } catch {
              case e: UnsupportedOperationException =>
                probe.ref ! e
            }
          }(context.executionContext)
          latch.await(5, TimeUnit.SECONDS)
          Behaviors.same
      })

      val l = new CountDownLatch(1)
      try {
        ref ! l
        probe.receiveMessage().getMessage should include("[children]")
      } finally {
        l.countDown()
      }
    }

  }

}
