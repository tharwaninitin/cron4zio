package cron4zio

import zio.ZIO
import zio.test.Assertion.equalTo
import zio.test._

object CronParserTestSuite {
  val spec: ZSpec[environment.TestEnvironment, Any] =
    suite("Cron Parser")(
      test("parse should parse cron correctly: A run frequency of once at 16:25 on December 18, 2018 ") {
        assertTrue(parse("0 25 16 18 DEC ? 2018").isSuccess)
      },
      testM("parse should parse cron correctly but should fail to generate next run") {
        assertM(
          getNextRunDuration(parse("0 25 16 18 DEC ? 2018").get)
            .foldM(ex => ZIO.succeed(ex.getMessage), _ => ZIO.succeed("ok"))
        )(equalTo("Cannot generate next run from provided cron => 0 25 16 18 12 ? 2018"))
      },
      test("parse should parse cron correctly: A run frequency of 12:00 PM (noon) every day") {
        assertTrue(parse("0 0 12 * * ?").isSuccess)
      },
      test("parse should parse cron correctly: A run frequency of 11:00 PM every weekday night") {
        assertTrue(parse("0 0 23 ? * MON-FRI").isSuccess)
      },
      test("parse should parse cron correctly: A run frequency of 10:15 AM every day") {
        assertTrue(parse("0 15 10 * * ?").isSuccess)
      },
      test(
        "parse should parse cron correctly: A run frequency of 10:15 AM every Monday, Tuesday, Wednesday, Thursday and Friday"
      ) {
        assertTrue(parse("0 15 10 ? * MON-FRI").isSuccess)
      },
      test("parse should parse cron correctly: A run frequency of 12:00 PM (noon) every first day of the month") {
        assertTrue(parse("0 0 12 1 1/1 ? *").isSuccess)
      },
      test(
        "parse should parse cron correctly: A run frequency of every hour between 8:00 AM and 5:00 PM Monday-Friday"
      ) {
        assertTrue(parse("0 0 8-17 ? * MON-FRI").isSuccess)
      },
      test("parse should return Error when incorrect cron '0 */2 * ' provided") {
        assertTrue(
          parse("0 */2 * ").failed.get.getMessage == "Cron expression contains 3 parts but we expect one of [6, 7]"
        )
      },
      test("parse should return Error when incorrect cron '0 0 8-17 ? * MON-FRII' provided") {
        assertTrue(
          parse(
            "0 0 8-17 ? * MON-FRII"
          ).failed.get.getMessage == "Failed to parse cron expression. Invalid chars in expression! Expression: FRII Invalid chars: FRII"
        )
      },
      test("parse should return Error when incorrect cron  provided: Seconds are out of range") {
        assertTrue(
          parse(
            "155 15 10 ? * MON-FRI"
          ).failed.get.getMessage == "Failed to parse cron expression. Value 155 not in range [0, 59]"
        )
      },
      test("parse should return Error when incorrect cron  provided: Minutes are out of range") {
        assertTrue(
          parse(
            "0 155 10 ? * MON-FRI"
          ).failed.get.getMessage == "Failed to parse cron expression. Value 155 not in range [0, 59]"
        )
      },
      test("parse should return Error when incorrect cron  provided: Hours are out of range") {
        assertTrue(
          parse(
            "0 15 100 ? * MON-FRI"
          ).failed.get.getMessage == "Failed to parse cron expression. Value 100 not in range [0, 23]"
        )
      },
      test("parse should return Error when incorrect cron  provided: DayOfMonth are out of range") {
        assertTrue(
          parse(
            "0 15 10 33 * MON-FRI"
          ).failed.get.getMessage == "Failed to parse cron expression. Value 33 not in range [1, 31]"
        )
      },
      test("parse should return Error when incorrect cron  provided: Months are out of range") {
        assertTrue(
          parse(
            "0 15 10 ? 13 MON-FRI"
          ).failed.get.getMessage == "Failed to parse cron expression. Value 13 not in range [1, 12]"
        )
      }
    ) @@ TestAspect.sequential
}
