package test_net.test_cakesolutions.akkarepo.core.application.workflow

package internal

import scala.collection.immutable.StreamIterator
import scala.concurrent.duration._
import scala.util.Random

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import monix.eval.Task

private[workflow] object OnErrorRetryLimitedWithBackoff {

  def apply[A](
    source: Task[A],
    maxRetries: Int Refined Positive,
    backoff: BackoffStrategy,
    jitter: FiniteDuration
  ): Iterator[Task[A]] = {
    new OnErrorRetryWithBackoff(source, Some(maxRetries), backoff, jitter)
  }
}

private[workflow] object OnErrorRetryUnlimitedWithBackoff {

  def apply[A](
    source: Task[A],
    backoff: BackoffStrategy,
    jitter: FiniteDuration
  ): Iterator[Task[A]] = {
    new OnErrorRetryWithBackoff(source, None, backoff, jitter)
  }
}

private final class OnErrorRetryWithBackoff[A](
  source: Task[A],
  maxRetries: Option[Int Refined Positive],
  backoff: BackoffStrategy,
  jitter: FiniteDuration
) extends Iterator[Task[A]] {

  private[this] val delayedTaskGen = {
    val gen = backoff.sequenceGenerator
    val limitedGen = maxRetries.fold(gen)(n => gen.take(n.value))

    new StreamIterator(
      limitedGen.map(delay => source.delayExecution(withJitter(delay, jitter)))
    )
  }

  /** @see [[Iterator]] */
  def hasNext: Boolean = {
    true
  }

  /** @see [[Iterator]] */
  def next(): Task[A] = {
    if (delayedTaskGen.hasNext) {
      delayedTaskGen.next()
    } else {
      Task.raiseError(
        new RetryExceededException(
          s"$backoff failed after $maxRetries tries"
        )
      )
    }
  }

  private def withJitter(
    time: FiniteDuration,
    jitter: FiniteDuration
  ): FiniteDuration = {

    // offset is randomly chosen from the integer interval [-jitter, +jitter]
    val offset = Random.nextInt((2 * jitter.toNanos).toInt) - jitter.toNanos

    time + offset.nanoseconds
  }
}
