import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import cron4s.expr.CronExpr
import cron4s.lib.javatime._
import zio.clock.{Clock, sleep}
import zio.duration.Duration
import zio.{IO, Schedule, UIO, ZIO}

package object cron4zio {

  def sleepForCron(cronExpr: CronExpr): ZIO[Clock, Throwable, Unit] =
    getNextDuration(cronExpr).flatMap(duration => {
      // println(s"Sleeping for ${duration.toString}")
      sleep(duration)
    })

  def getNextDuration(cronExpr: CronExpr): IO[Throwable, Duration] = {
    for {
      timeNow           <- UIO.succeed(LocalDateTime.now)
      timeNext          <- ZIO.fromOption(cronExpr.next(timeNow)).mapError(_ => new Throwable("Non Recoverable Error"))
      durationInNanos   = timeNow.until(timeNext, ChronoUnit.NANOS)
      duration          = Duration.fromNanos(durationInNanos)
    } yield duration
  }

  def repeatEffectForCron[A](effect: IO[Throwable,A], cronExpr: CronExpr, maxRecurs: Int = 0): ZIO[Clock, Throwable, Int] =
    if (maxRecurs != 0)
      (sleepForCron(cronExpr) *> effect).repeat(Schedule.recurs(maxRecurs))
    else
      (sleepForCron(cronExpr) *> effect).repeat(Schedule.forever)

}
