package cron4zio

import zio.ZIO
import zio.duration._
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestClock
import java.time.{OffsetDateTime, ZoneOffset}

object ScheduledTaskTestSuite {
  val time = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Schedule Tasks")(
      testM("Execute repeatEffectForCron where task time is less than interval time") {
        val everyThreeSeconds = parse("*/3 * * ? * *").get
        val printTime = zio.clock.localDateTime.flatMap(dt => zio.console.putStr("Started " + dt.toString)) *>
          ZIO.sleep(2.second) *>
          zio.clock.localDateTime.flatMap(dt => zio.console.putStrLn(" Ended " + dt.toString))
        val scheduled = repeatEffectForCron(printTime, everyThreeSeconds, 2)
        val program = for {
          _  <- TestClock.setDateTime(time)
          s  <- scheduled.fork
          _  <- TestClock.adjust(20.second)
          op <- s.join
        } yield op
        assertM(program.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      testM("Execute repeatEffectForCron where task time is greater than interval time") {
        val everyFiveSeconds = parse("*/5 * * ? * *").get
        val printTime = zio.clock.localDateTime.flatMap(dt => zio.console.putStr("Started " + dt.toString)) *>
          ZIO.sleep(6.second) *>
          zio.clock.localDateTime.flatMap(dt => zio.console.putStrLn(" Ended " + dt.toString))
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds, 2)
        val program = for {
          s  <- scheduled.fork
          _  <- TestClock.adjust(50.second)
          op <- s.join
        } yield op
        assertM(program.foldM(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      testM("Execute repeatEffectsForCron with multiple tasks") {
        val everyTwoSeconds = parse("*/2 * * ? * *").get
        val task            = zio.clock.localDateTime.flatMap(dt => zio.console.putStrLn(dt.toString))
        val tasks           = List((task, everyTwoSeconds), (task, everyTwoSeconds))
        val scheduled       = repeatEffectsForCron(tasks, 2)
        val program = for {
          s  <- scheduled.fork
          _  <- TestClock.adjust(10.second)
          op <- s.join
        } yield op
        assertM(program.foldM(ex => ZIO.succeed(List(ex.getMessage)), op => ZIO.succeed(op.map(_.toString))))(
          equalTo(List("2", "2"))
        )
      }
    ) @@ TestAspect.sequential
}
