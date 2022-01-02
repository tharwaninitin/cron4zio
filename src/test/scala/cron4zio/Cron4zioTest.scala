package cron4zio

import zio.clock.Clock
import zio.test.Assertion._
import zio.test._
import zio.{UIO, ZIO}
import java.time.LocalTime

object Cron4zioTest {
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Schedule Tasks")(
      testM("Execute repeatEffectForCron where task time is less than interval time") {
        val everyFiveSeconds = parseCron("*/5 * * ? * *").get
        val printTime = UIO(println(LocalTime.now))
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds,2).provideLayer(Clock.live)
        assertM(scheduled.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      testM("Execute repeatEffectForCron where task time is greater than interval time") {
        val everyFiveSeconds = parseCron("*/5 * * ? * *").get
        val printTime = UIO{
          println("Started " + LocalTime.now)
          Thread.sleep(6000)
          println("Ended " + LocalTime.now)
        }
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds,2).provideLayer(Clock.live)
        assertM(scheduled.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      }
    ) @@ TestAspect.sequential
}


