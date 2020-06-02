ThisBuild / organization := "com.github.tharwaninitin"
ThisBuild / organizationName := "github"
ThisBuild / organizationHomepage := Some(url("https://github.com/tharwaninitin/cron4zio"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/tharwaninitin/cron4zio"),
    "scm:git@github.com:tharwaninitin/cron4zio.git"
  )
)
ThisBuild / developers := List(Developer("tharwaninitin",
                             "Nitin Tharwani",
                             "tharwaninitin182@gmail.com",
                             url("https://github.com/tharwaninitin")))
ThisBuild / description := "Library to run ZIO effects based on cron expressions"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/tharwaninitin/cron4zio"))

// Add sonatype repository settings
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true