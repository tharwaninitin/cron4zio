import com.cronutils.model.Cron
import com.cronutils.model.definition.{CronConstraintsFactory, CronDefinition, CronDefinitionBuilder}
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import zio.{Duration, IO, Schedule, ZIO}
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
  val cronDefinition: CronDefinition = CronDefinitionBuilder.defineCron()
    .withSeconds().withValidRange(0, 59).and()
    .withMinutes().withValidRange(0, 59).and()
    .withHours().withValidRange(0, 23).and()
    .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()
    .withMonth().withValidRange(1, 12).and()
    .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(1).supportsHash().supportsL().supportsQuestionMark().and()
    .withYear().withValidRange(1970, 2099).withStrictRange().optional().and()
    .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())
    .instance()
  // format: on

  val cronParser: CronParser = new CronParser(cronDefinition)

  /** Parses a Cron expression string into a Cron instance.
   *
   * @param cron
   *   The Cron expression string to parse.
   * @return
   *   A Cron instance corresponding to the parsed Cron expression, or a Failure if the expression does not match the Cron definition.
   */
  def parse(cron: String): Try[Cron] = Try(cronParser.parse(cron))

  /** Parses a Cron expression string into a Cron instance. This method does not handle parsing errors gracefully.
   *
   * @param cron
   *   The Cron expression string to parse.
   * @return
   *   A Cron instance corresponding to the parsed Cron expression, or throws an IllegalArgumentException if the expression
   *   does not match the Cron definition.
   */
  def unsafeParse(cron: String): Cron = cronParser.parse(cron)

  /** Calculates the duration until the next execution time of a Cron job.
   *
   * @param cron
   *   The Cron instance representing the Cron job.
   * @param zoneId
   *   The time zone to use for calculating the current time.
   * @return
   *   The duration until the next execution time of the Cron job, wrapped in an IO monad.
   *   Fails with IllegalArgumentException if the next run cannot be generated for the provided Cron.
   */
  def getNextRunDuration(
      cron: Cron,
      zoneId: ZoneId = TimeZone.getDefault.toZoneId
  ): IO[IllegalArgumentException, Duration] =
    for {
      timeNow <- ZIO.succeed(LocalDateTime.now().atZone(zoneId))
      timeNext <- ZIO.attempt(
        ExecutionTime
          .forCron(cron)
          .nextExecution(timeNow)
          .orElseThrow(() => new IllegalArgumentException(s"Cannot generate next run from provided cron => ${cron.asString()}"))
      ).refineToOrDie[IllegalArgumentException]
      durationInNanos = timeNow.until(timeNext, ChronoUnit.NANOS)
      duration        = Duration.fromNanos(durationInNanos)
    } yield duration

  /** Sleeps in a non-blocking way until the next execution time of a Cron job.
   *
   * @param cron
   *   The Cron instance representing the Cron job.
   * @param zoneId
   *   The time zone to use for calculating the current time.
   * @return
   *   An effect that sleeps until the next execution time of the Cron job.
   */
  def sleepForCron(
      cron: Cron,
      zoneId: ZoneId = TimeZone.getDefault.toZoneId
  ): IO[IllegalArgumentException, Unit] =
    getNextRunDuration(cron, zoneId).flatMap(duration => ZIO.sleep(duration))

  /** Repeats an effect according to the schedule defined by a Cron job.
   *
   * @param effect
   *   The effect to repeat.
   * @param cron
   *   The Cron instance representing the schedule.
   * @param maxRecurs
   *   Maximum number of times to repeat the effect. If 0, the effect will repeat indefinitely.
   * @param zoneId
   *   The time zone to use for calculating the current time.
   * @return
   *   An effect that repeats the given effect according to the Cron schedule.
   */
  def repeatEffectForCron[R, E >: Throwable, A](
      effect: ZIO[R, E, A],
      cron: Cron,
      maxRecurs: Int = 0,
      zoneId: ZoneId = TimeZone.getDefault.toZoneId
  ): ZIO[R, E, Long] = {
    val effectWithSleep = sleepForCron(cron, zoneId) *> effect
    if (maxRecurs != 0)
      effectWithSleep.repeat(Schedule.recurs(maxRecurs))
    else
      effectWithSleep.repeat(Schedule.forever)
  }

  /** Repeats a list of effects according to their respective Cron schedules.
   *
   * @param tasks
   *   A list of effects paired with their Cron schedules.
   * @param maxRecurs
   *   Maximum number of times to repeat each effect. If 0, the effects will repeat indefinitely.
   * @param zoneId
   *   The time zone to use for calculating the current time.
   * @return
   *   An effect that repeats each effect in the list according to their Cron schedules.
   */
  def repeatEffectsForCron[R, E >: Throwable, A](
      tasks: List[(ZIO[R, E, A], Cron)],
      maxRecurs: Int = 0,
      zoneId: ZoneId = TimeZone.getDefault.toZoneId
  ): ZIO[R, E, List[Long]] =
    ZIO.foreachPar(tasks)(input => repeatEffectForCron(input._1, input._2, maxRecurs, zoneId))
}
