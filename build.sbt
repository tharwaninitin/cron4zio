import Dependencies._
import ScalaCompileOptions._
import Versions._

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / version           := cron4ZIOVersion

lazy val cron4zio = (project in file("."))
  .settings(
    name                       := "cron4zio",
    scalaVersion               := scala212,
    dependencyUpdatesFailBuild := true,
    dependencyUpdatesFilter -= moduleFilter(organization = "org.scala-lang"),
    libraryDependencies ++= core ++ testLibs,
    crossScalaVersions := allScalaVersions,
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12)) => s2copts ++ s212copts
        case Some((2, 13)) => s2copts
        case Some((3, _))  => s3copts
        case _             => Seq()
      }
    },
    Test / parallelExecution := false,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val docs = project
  .in(file("modules/docs")) // important: it must not be docs/
  .dependsOn(cron4zio)
  .settings(
    name           := "cron4zio-docs",
    publish / skip := true,
    mdocVariables  := Map("VERSION" -> version.value, "Scala212" -> scala212, "Scala213" -> scala213, "Scala3" -> scala3),
    mdocIn         := new File("docs/readme.template.md"),
    mdocOut        := new File("README.md")
  )
  .enablePlugins(MdocPlugin)
