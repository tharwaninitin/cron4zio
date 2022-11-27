import Versions._
import sbt._

object Dependencies {

  lazy val core = List(
    "dev.zio"      %% "zio"        % zioVersion,
    "com.cronutils" % "cron-utils" % cronUtilsVersion
  )

  lazy val testLibs = List(
    "dev.zio" %% "zio-test"     % zioVersion,
    "dev.zio" %% "zio-test-sbt" % zioVersion
  ).map(_ % Test)

}
