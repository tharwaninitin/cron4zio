lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.2"
lazy val supportedScalaVersions = List(scala212,scala213)

lazy val ZioVersion = "1.0.0-RC20"
lazy val Cron4s = "0.6.0"

lazy val core = (project in file("."))
  .settings(
    name := "cron4zio",
    organization := "com.github.tharwaninitin",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % ZioVersion,
      "dev.zio" %% "zio-test-sbt" % ZioVersion % Test,
      "com.github.alonsodomin.cron4s" %% "cron4s-core" % Cron4s,
    ),
    crossScalaVersions := supportedScalaVersions,
    Test / parallelExecution := false,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

