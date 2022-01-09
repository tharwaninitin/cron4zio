# Cron4ZIO
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Tests](https://github.com/tharwaninitin/scalatemplate/actions/workflows/tests.yml/badge.svg)](https://github.com/tharwaninitin/etlflow/actions/workflows/tests.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tharwaninitin/cron4zio_2.12/badge.svg)](https://mvnrepository.com/artifact/com.github.tharwaninitin/cron4zio)
[![javadoc](https://javadoc.io/badge2/com.github.tharwaninitin/cron4zio_2.12/javadoc.svg)](https://javadoc.io/doc/com.github.tharwaninitin/cron4zio_2.12)
[![codecov](https://codecov.io/gh/tharwaninitin/cron4zio/branch/master/graph/badge.svg?token=HWKAPV7TTW)](https://codecov.io/gh/tharwaninitin/cron4zio)
**cron4zio** is a library which provides support for scheduling ZIO effects based on cron expressions.

## Scala Version Compatibility Matrix
| Scala 2.12           | Scala 2.13  | Scala 3.1  | 
|:--------------------:| -----------:| ----------:|
| ✅                   | ✅          | ✅          |

Add the latest release as a dependency to your project

[![Latest Version](https://maven-badges.herokuapp.com/maven-central/com.github.tharwaninitin/cron4zio_2.12/badge.svg)](https://mvnrepository.com/artifact/com.github.tharwaninitin/cron4zio)

__SBT__
```
libraryDependencies += "com.github.tharwaninitin" %% "cron4zio" % "x.x.x"
```
__Maven__
```
<dependency>
    <groupId>com.github.tharwaninitin</groupId>
    <artifactId>cron4zio_2.12</artifactId>
    <version>x.x.x</version>
</dependency>
```

## Examples
```scala
import java.time.LocalTime
import cron4zio._
import zio.{Runtime, Task, UIO}

val everyFiveSeconds = parseCron("*/5 * * ? * *").get

val printTime = UIO(println(LocalTime.now))

val scheduled = repeatEffectForCron(printTime,everyFiveSeconds)

Runtime.default.unsafeRun(scheduled)
```