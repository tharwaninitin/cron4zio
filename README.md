# cron4zio

**cron4zio** is a library which provides support for running ZIO effects based on Cron4s cron expressions.

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