import com.cronutils.model.Cron
import java.time.LocalTime
import cron4zio._
import zio._

object Main extends ZIOAppDefault {
  val everyTwoSeconds: Cron = unsafeParse("*/2 * * ? * *")

  val printTime: UIO[Unit] = ZIO.logInfo(LocalTime.now.toString)

  def run: Task[Unit] = repeatEffectForCron(printTime, everyTwoSeconds).unit
}
