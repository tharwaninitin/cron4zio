// publish to github packages settings
ThisBuild / publishTo := Some("GitHub tharwaninitin Apache Maven Packages" at "https://maven.pkg.github.com/tharwaninitin/cron4zio")
ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "tharwaninitin",
  System.getenv("GITHUB_TOKEN")
)