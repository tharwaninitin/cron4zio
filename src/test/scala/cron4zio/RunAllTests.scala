package cron4zio

import zio.test._

object RunAllTests extends ZIOSpecDefault {
  val spec: Spec[TestEnvironment, Any] =
    suite("Cron Test Suites")(
      CronParserTestSuite.spec,
      ScheduledTaskTestSuite.spec
    ) @@ TestAspect.sequential
}
