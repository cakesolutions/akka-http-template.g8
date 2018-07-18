package $package$.core.application.workflow

package internal

import scala.concurrent.duration._
import scala.util.Random

import monix.eval.Task
import org.scalatest._

// scalastyle:off magic.number

class OnErrorRetryWithBackoffTest extends FreeSpecLike with Matchers {

  val retryObject =
    new OnErrorRetryWithBackoff(
      Task.unit,
      None,
      Linear(MILLISECONDS),
      0.seconds
    )

  "with jitter" - {
    "produces positive and negative jitter offsets in expected range" in {
      for (count <- 0 to 100) {
        val baseTime = Random.nextInt(100).milliseconds
        val jitter = (Random.nextInt(100) + 1).milliseconds
        val timeValue = retryObject.withJitter(baseTime, jitter)

        timeValue.toNanos should be(baseTime.toNanos +- jitter.toNanos)
      }
    }

    "sufficient non-trivial jitter offsets are generated" in {
      val limit = 100
      val jitterOffsetValues =
        (0 to limit).map { _ =>
          val baseTime = Random.nextInt(100).milliseconds
          val jitter = (Random.nextInt(100) + 1).milliseconds

          retryObject.withJitter(baseTime, jitter)
        }.toSet

      jitterOffsetValues.size should be >= (limit / 2)
    }

    "usable with multiple time units" in {
      for (count <- 0 to 50) {
        val baseTime = Random.nextInt(100).seconds
        val jitter = (Random.nextInt(100) + 1).milliseconds
        val timeValue = retryObject.withJitter(baseTime, jitter)

        timeValue.toNanos should be(baseTime.toNanos +- jitter.toNanos)
      }
      for (count <- 0 to 50) {
        val baseTime = Random.nextInt(100).milliseconds
        val jitter = (Random.nextInt(100) + 1).seconds
        val timeValue = retryObject.withJitter(baseTime, jitter)

        timeValue.toNanos should be(baseTime.toNanos +- jitter.toNanos)
      }
    }
  }

  "with no jitter" in {
    for (count <- 0 to 100) {
      val baseTime = Random.nextInt(100).milliseconds
      val timeValue = retryObject.withJitter(baseTime, 0.milliseconds)

      timeValue.toNanos shouldEqual baseTime.toNanos
    }
  }
}
