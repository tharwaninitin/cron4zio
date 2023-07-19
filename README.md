# Cron4ZIO
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![CI](https://github.com/tharwaninitin/cron4zio/actions/workflows/ci.yml/badge.svg)](https://github.com/tharwaninitin/cron4zio/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tharwaninitin/cron4zio_2.12/badge.svg)](https://mvnrepository.com/artifact/com.github.tharwaninitin/cron4zio)
[![javadoc](https://javadoc.io/badge2/com.github.tharwaninitin/cron4zio_2.12/javadoc.svg)](https://javadoc.io/doc/com.github.tharwaninitin/cron4zio_2.12)
[![codecov](https://codecov.io/gh/tharwaninitin/cron4zio/branch/master/graph/badge.svg?token=HWKAPV7TTW)](https://codecov.io/gh/tharwaninitin/cron4zio)

**cron4zio** is a library which provides support for scheduling ZIO effects based on cron expressions.

## Scala Version Compatibility Matrix
This project is compiled with scala versions 2.12.15, 2.13.10, 3.3.0

| Scala 2.12 | Scala 2.13 | Scala 3.X | 
|:----------:|-----------:|----------:|
|     ✅      |          ✅ |         ✅ |

Add the latest release as a dependency to your project

[![Latest Version](https://maven-badges.herokuapp.com/maven-central/com.github.tharwaninitin/cron4zio_2.12/badge.svg)](https://mvnrepository.com/artifact/com.github.tharwaninitin/cron4zio)

__SBT__
```
libraryDependencies += "com.github.tharwaninitin" %% "cron4zio" % "1.0.1"
```
__Maven__
```
<dependency>
    <groupId>com.github.tharwaninitin</groupId>
    <artifactId>cron4zio_2.12</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Examples

```scala
import com.cronutils.model.Cron
import java.time.LocalTime
import cron4zio._
import zio._

object Main extends ZIOAppDefault {
  val everyTwoSeconds: Cron = unsafeParse("*/2 * * ? * *")

  val printTime: UIO[Unit] = ZIO.logInfo(LocalTime.now.toString)

  def run: Task[Unit] = repeatEffectForCron(printTime, everyTwoSeconds).unit
}
```


## Cron Reference

| Field Name      |  Allowed Values  | Allowed Special Characters | 
|-----------------|:----------------:|---------------------------:|
| Seconds         |       0-59       |                      - * / |
| Minutes         |       0-59       |                      - * / |
| Hours           |       0-23       |                      - * / |
| Day (of month)  |       1-31       |                  * ? / L W |
| Month           | 1-12 or JAN-DEC  |                      - * / |
| Day (of week)	  |  1-7 or SUN-SAT  |                  - * ? / L |
| Year (optional) | empty, 1970-2199 |                      - * / |



There are several special characters that are used to specify values:

| Character |                           Specifies                           |                                                                                                                                    Notes | 
|-----------|:-------------------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------:|
| *         |                         All values.	                          |                                                                                                * in the minute field means every minute. |
| ?         | No specific value in the day of month and day of week fields. |                                                                                     ? specifies a value in one field, but not the other. |
| -         |                           A range.                            |                                                               10-12 in the hour field means the script will run at 10, 11 and 12 (noon). |
| '         |                      Additional values.                       |                           Typing "MON,WED,FRI" in the day-of-week field means the script will run only on Monday, Wednesday, and Friday. |
| /         |                          Increments.                          | 0/15 in the seconds field means the seconds 0, 15, 30, and 45. * before the '/' is equivalent to specifying 0 is the value to start with |
| #	        |                        Day of a month.                        |                                6#3 in the day of week field means the third Friday (day 6 is Friday; #3 is the 3rd Friday in the month). |
| L         |               The last day of a month or week.                |                                         L means the last day of the month. If used in the day of week field by itself, it means 7 or SAT |
| W         |       The weekday (Mon-Fri) nearest the specified day.        |                                             Specifying 15W means the CRON job will fire on the nearest weekday to the 15th of the month. |


## Cron Examples
|                                    Description                                    |                  Cron |
|:---------------------------------------------------------------------------------:|----------------------:|
|               A run frequency of once at 16:25 on December 18, 2018               | 0 25 16 18 DEC ? 2018 |
|                   A run frequency of 12:00 PM (noon) every day                    |          0 0 12 * * ? |
|                  A run frequency of 11:00 PM every weekday night                  |    0 0 23 ? * MON-FRI |
|                       A run frequency of 10:15 AM every day                       |         0 15 10 * * ? |
| A run frequency of 10:15 AM every Monday, Tuesday, Wednesday, Thursday and Friday |   0 15 10 ? * MON-FRI |
|          A run frequency of 12:00 PM (noon) every first day of the month          |      0 0 12 1 1/1 ? * |
|        A run frequency of every hour between 8:00 AM and 5:00 PM on Monday        |      0 0 8-17 ? * MON |