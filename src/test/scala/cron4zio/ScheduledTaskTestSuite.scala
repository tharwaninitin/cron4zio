package cron4zio

import zio.clock.Clock
import zio.test.Assertion._
import zio.test._
import zio.{Task, ZIO}
import java.time.LocalTime

object ScheduledTaskTestSuite {
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Schedule Tasks")(
      testM("Execute repeatEffectForCron where task time is less than interval time") {
        val everyThreeSeconds = parse("*/3 * * ? * *").get
        val printTime         = Task(println(LocalTime.now))
        val scheduled         = repeatEffectForCron(printTime, everyThreeSeconds, 2).provideLayer(Clock.live)
        assertM(scheduled.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      testM("Execute repeatEffectForCron where task time is greater than interval time") {
        val everyFiveSeconds = parse("*/5 * * ? * *").get
        val printTime = Task {
          println("Started " + LocalTime.now)
          Thread.sleep(6000)
          println("Ended " + LocalTime.now)
        }
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds, 2).provideLayer(Clock.live)
        assertM(scheduled.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      testM("Execute repeatEffectsForCron with multiple tasks") {
        val everyTwoSeconds = parse("*/2 * * ? * *").get
        val task            = Task(println(LocalTime.now))
        val tasks           = List((task, everyTwoSeconds), (task, everyTwoSeconds))
        val scheduled       = repeatEffectsForCron(tasks).provideLayer(Clock.live)
        assertM(scheduled.foldM(ex => ZIO.succeed(List(ex.getMessage)), op => ZIO.succeed(op.map(_.toString))))(
          equalTo(List("2", "3"))
        )
      }
    ) @@ TestAspect.sequential
}
