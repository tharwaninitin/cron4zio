# cron4zio

**cron4zio** is a library which provides support for running ZIO effects based on [Cron4s][Cron4s] cron expressions.

## Examples

```scala
import java.time.LocalTime
import cron4zio._
import cron4s.Cron
import zio.{Runtime, Task}

val everyFiveSeconds = Cron.unsafeParse("*/5 * * ? * *")

val printTime = Task(println(LocalTime.now))

val scheduled = repeatEffectForCron(printTime,everyFiveSeconds)

Runtime.default.unsafeRun(scheduled)
```