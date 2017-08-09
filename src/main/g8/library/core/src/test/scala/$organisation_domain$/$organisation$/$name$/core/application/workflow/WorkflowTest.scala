package $organisation_domain$.$organisation$.$name$.core.application.workflow

import scala.concurrent.duration._

import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.AtomicLong
import org.scalatest._

// scalastyle:off magic.number

final case class RetryHit(count: Long) extends Exception

class WorkflowTest extends AsyncFreeSpecLike with Matchers {

  "Retry unlimited" in {
    val errorCount = AtomicLong(0)
    val limit = refineMV[Positive](10)
    val task = Task[Unit] {
      val n = errorCount.incrementAndGet()
      if (n < limit.value) {
        throw RetryHit(n)
      } else {
        ()
      }
    }

    task.onError(RetryUnlimited).timeout(1.second).runAsync.map { _ =>
      errorCount.get shouldEqual limit.value
    }
  }

  "Retry limited" in {
    val errorCount = AtomicLong(0)
    val limit = refineMV[Positive](10)
    val task = Task[Unit] {
      val n = errorCount.incrementAndGet()
      if (n < limit.value) {
        throw RetryHit(n)
      } else {
        ()
      }
    }

    task.onError(RetryLimited(limit)).timeout(1.second).runAsync.map { _ =>
      errorCount.get shouldEqual limit.value
    }
  }

  "Retry unlimited with backoff" - {
    "with no jitter" in {
      val errorCount = AtomicLong(0)
      val limit = refineMV[Positive](10)
      val task = Task[Unit] {
        val n = errorCount.incrementAndGet()
        if (n < limit.value) {
          throw RetryHit(n)
        } else {
          ()
        }
      }
      val backoff = Linear(MILLISECONDS)

      task
        .onError(RetryUnlimitedWithBackoff(backoff))
        .timeout(1.second)
        .runAsync
        .map { _ =>
          errorCount.get shouldEqual limit.value
        }
    }

    "with jitter" in {
      val errorCount = AtomicLong(0)
      val limit = refineMV[Positive](10)
      val task = Task[Unit] {
        val n = errorCount.incrementAndGet()
        if (n < limit.value) {
          throw RetryHit(n)
        } else {
          ()
        }
      }
      val backoff = Linear(MILLISECONDS)
      val jitter = 1.millisecond

      task
        .onError(RetryUnlimitedWithBackoff(backoff, jitter))
        .timeout(1.second)
        .runAsync
        .map { _ =>
          errorCount.get shouldEqual limit.value
        }
    }
  }

  "Retry limited with backoff" - {
    "with no jitter" in {
      val errorCount = AtomicLong(0)
      val limit = refineMV[Positive](10)
      val task = Task[Unit] {
        val n = errorCount.incrementAndGet()
        if (n < limit.value) {
          throw RetryHit(n)
        } else {
          ()
        }
      }
      val backoff = Linear(MILLISECONDS)

      task
        .onError(RetryLimitedWithBackoff(limit, backoff))
        .timeout(1.second)
        .runAsync
        .map { _ =>
          errorCount.get shouldEqual limit.value
        }
    }

    "with jitter" in {
      val errorCount = AtomicLong(0)
      val limit = refineMV[Positive](10)
      val task = Task[Unit] {
        val n = errorCount.incrementAndGet()
        if (n < limit.value) {
          throw RetryHit(n)
        } else {
          ()
        }
      }
      val backoff = Linear(MILLISECONDS)
      val jitter = 1.millisecond

      task
        .onError(RetryLimitedWithBackoff(limit, backoff, jitter))
        .timeout(1.second)
        .runAsync
        .map { _ =>
          errorCount.get shouldEqual limit.value
        }
    }
  }
}
