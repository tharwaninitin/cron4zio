val scala212 = "2.12.15"
val scala213 = "2.13.7"
val scala3 = "3.1.0"
val scala2Versions = List(scala212 ,scala213)
val allScalaVersions = List(scala212, scala213, scala3)
val EtlFlowVersion = "0.1.0"

lazy val ZioVersion = "1.0.13"
lazy val CronUtilsVersion = "9.1.6"

lazy val cron4zio = (project in file("."))
  .settings(
    name := "cron4zio",
    organization := "com.github.tharwaninitin",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % ZioVersion,
      "com.cronutils" % "cron-utils" % CronUtilsVersion,
      "dev.zio" %% "zio-test-sbt" % ZioVersion % Test,
    ),
    crossScalaVersions := allScalaVersions,
    Test / parallelExecution := false,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

