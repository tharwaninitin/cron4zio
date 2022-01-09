/*
// Github Publish Settings
ThisBuild / publishTo := Some("GitHub tharwaninitin Apache Maven Packages" at s"https://maven.pkg.github.com/tharwaninitin/${name.value}")
ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials("GitHub Package Registry", "maven.pkg.github.com", "tharwaninitin", System.getenv("GITHUB_TOKEN"))
 */

// Mandatory Publish Settings
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots".at(nexus + "content/repositories/snapshots"))
  else Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
}
ThisBuild / publishMavenStyle := true

// Optional Publish Settings
ThisBuild / organization         := "com.github.tharwaninitin"
ThisBuild / organizationName     := "github"
ThisBuild / organizationHomepage := Some(url(s"https://github.com/tharwaninitin/${name.value}"))
ThisBuild / homepage             := Some(url(s"https://github.com/tharwaninitin/${name.value}"))
ThisBuild / scmInfo := Some(
  ScmInfo(url(s"https://github.com/tharwaninitin/$project"), s"scm:git@github.com:tharwaninitin/${name.value}.git")
)
ThisBuild / developers := List(
  Developer("tharwaninitin", "Nitin Tharwani", "tharwaninitin182@gmail.com", url("https://github.com/tharwaninitin"))
)
ThisBuild / description := "Library to schedule ZIO effects based on cron expressions"
ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
