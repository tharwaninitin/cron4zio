package cron4zio

import zio.test._

object RunAllTests extends DefaultRunnableSpec {
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Cron Test Suites")(
      CronParserTestSuite.spec,
      ScheduledTaskTestSuite.spec
    ) @@ TestAspect.sequential
}
