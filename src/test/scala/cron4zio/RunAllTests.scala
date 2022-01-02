package cron4zio

import zio.test._

object RunAllTests extends DefaultRunnableSpec {
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Cron Test Suites") (
      ParseCronTestSuite.spec,
      Cron4zioTest.spec
    )
}
