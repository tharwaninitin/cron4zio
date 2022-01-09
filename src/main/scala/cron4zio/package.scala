import com.cronutils.model.definition.{CronConstraintsFactory, CronDefinition, CronDefinitionBuilder}
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import zio.clock.{Clock, sleep}
import zio.duration.Duration
import zio.{RIO, Schedule, Task, ZIO}
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
import java.util.TimeZone
import scala.util.Try

package object cron4zio {
  // format: off
  /* Our cron definition uses below cron expressions that go from seconds to day of week in the following order:
   Seconds	0-59	                    - * /
   Minutes	0-59	                    - * /
   Hours	0-23	                      - * /
   Day (of month)	1-31	            * ? / L W
   Month	1-12 or JAN-DEC	            - * /
   Day (of week)	1-7 or SUN-SAT	    - * ? / L #
   Year (optional)	empty, 1970-2099	- * /
 */
  val initiateCron: CronDefinition = CronDefinitionBuilder.defineCron()
    .withSeconds().withValidRange(0, 59).and()
    .withMinutes().withValidRange(0, 59).and()
    .withHours().withValidRange(0, 23).and()
    .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()
    .withMonth().withValidRange(1, 12).and()
    .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(0).supportsHash().supportsL().supportsQuestionMark().and()
    .withYear().withValidRange(1970, 2099).withStrictRange().optional().and()
    .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())
    .instance()
  // format: on

  val zoneId: ZoneId = TimeZone.getDefault.toZoneId

  def parseCron(cron: String): Try[ExecutionTime] =
    Try(ExecutionTime.forCron(new CronParser(initiateCron).parse(cron)))

  def sleepForCron(cron: ExecutionTime): RIO[Clock, Unit] =
    getNextDuration(cron).flatMap(duration => sleep(duration))

  def getNextDuration(cron: ExecutionTime): Task[Duration] =
    for {
      timeNow  <- ZIO.effectTotal(LocalDateTime.now().atZone(zoneId))
      timeNext <- Task(cron.nextExecution(timeNow).get()).orElseFail(new Throwable("Non Recoverable Error"))
      durationInNanos = timeNow.until(timeNext, ChronoUnit.NANOS)
      duration        = Duration.fromNanos(durationInNanos)
    } yield duration

  def repeatEffectForCron[R, A](
      effect: RIO[R, A],
      cron: ExecutionTime,
      maxRecurs: Int = 0
  ): RIO[R with Clock, Long] =
    if (maxRecurs != 0)
      (sleepForCron(cron) *> effect).repeat(Schedule.recurs(maxRecurs))
    else
      (sleepForCron(cron) *> effect).repeat(Schedule.forever)

  type CronTasks[R, A] = (RIO[R, A], ExecutionTime, Int)

  def repeatEffectsForCron[R, A](tasks: List[CronTasks[R, A]]): RIO[R with Clock, List[Long]] =
    ZIO.foreachPar(tasks)(input => repeatEffectForCron(input._1, input._2, input._3))
}
