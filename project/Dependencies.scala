import Versions._
import sbt._

object Dependencies {

  lazy val core = List(
    "dev.zio"      %% "zio"        % ZioVersion,
    "com.cronutils" % "cron-utils" % CronUtilsVersion
  )

  lazy val testLibs = List(
    "dev.zio" %% "zio-test"     % ZioVersion,
    "dev.zio" %% "zio-test-sbt" % ZioVersion
  ).map(_ % Test)

}
