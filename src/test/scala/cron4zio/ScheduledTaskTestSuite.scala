package cron4zio

import zio.{durationInt, ZIO}
import zio.test.Assertion.equalTo
import zio.test._
import java.time.{OffsetDateTime, ZoneOffset}

@SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
object ScheduledTaskTestSuite {
  val time: OffsetDateTime = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
  val spec: Spec[Any, Any] =
    suite("Schedule Tasks")(
      test("Execute repeatEffectForCron where task time is less than interval time") {
        val everyThreeSeconds = parse("*/3 * * ? * *").get
        val printTime = zio.Clock.localDateTime.flatMap(dt => zio.Console.print("Started " + dt.toString)) *>
          ZIO.sleep(2.second) *>
          zio.Clock.localDateTime.flatMap(dt => zio.Console.printLine(" Ended " + dt.toString))
        val scheduled = repeatEffectForCron(printTime, everyThreeSeconds, 2)
        val program = for {
          _  <- TestClock.setTime(time.toInstant)
          s  <- scheduled.fork
          _  <- TestClock.adjust(20.second)
          op <- s.join
        } yield op
        assertZIO(program.foldZIO(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      test("Execute repeatEffectForCron where task time is greater than interval time") {
        val everyFiveSeconds = parse("*/5 * * ? * *").get
        val printTime = zio.Clock.localDateTime.flatMap(dt => zio.Console.print("Started " + dt.toString)) *>
          ZIO.sleep(6.second) *>
          zio.Clock.localDateTime.flatMap(dt => zio.Console.printLine(" Ended " + dt.toString))
        val scheduled = repeatEffectForCron(printTime, everyFiveSeconds, 2)
        val program = for {
          s  <- scheduled.fork
          _  <- TestClock.adjust(50.second)
          op <- s.join
        } yield op
        assertZIO(program.foldZIO(ex => ZIO.succeed(ex.getMessage), l => ZIO.succeed(l.toString)))(equalTo("2"))
      },
      test("Execute repeatEffectsForCron with multiple tasks") {
        val everyTwoSeconds = parse("*/2 * * ? * *").get
        val task            = zio.Clock.localDateTime.flatMap(dt => zio.Console.print(dt.toString))
        val tasks           = List((task, everyTwoSeconds), (task, everyTwoSeconds))
        val scheduled       = repeatEffectsForCron(tasks, 2)
        val program = for {
          s  <- scheduled.fork
          _  <- TestClock.adjust(10.second)
          op <- s.join
        } yield op
        assertZIO(program.foldZIO(ex => ZIO.succeed(List(ex.getMessage)), op => ZIO.succeed(op.map(_.toString))))(
          equalTo(List("2", "2"))
        )
      }
    ) @@ TestAspect.sequential
}
