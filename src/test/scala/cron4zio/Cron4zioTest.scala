package cron4zio

import java.time.LocalTime
import cron4s.Cron
import zio.Task
import zio.clock.Clock
import zio.test.Assertion._
import zio.test._

object Cron4zioTest extends DefaultRunnableSpec {

  def spec: ZSpec[environment.TestEnvironment, Any] =
    suite("cron4zio")(
      testM("Execute repeatEffectForCron where task time is less than interval time") {
        val everyFiveSeconds = Cron.unsafeParse("*/5 * * ? * *")
        val printTime = Task(println(LocalTime.now))
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds,2).provideLayer(Clock.live)
        assertM(scheduled)(equalTo(2))
      },
      testM("Execute repeatEffectForCron where task time is greater than interval time") {
        val everyFiveSeconds = Cron.unsafeParse("*/5 * * ? * *")
        val printTime = Task{
          println("Started " + LocalTime.now)
          Thread.sleep(6000)
          println("Ended " + LocalTime.now)
        }
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds,2).provideLayer(Clock.live)
        assertM(scheduled)(equalTo(2))
      }
    )
}


