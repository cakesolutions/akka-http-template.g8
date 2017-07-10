// Copyright: 2014-2016 https://github.com/monix/monix/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0

package net.cakesolutions.akkarepo.core.application.workflow

package internal

import scala.collection.immutable.StreamIterator
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import monix.execution.{Ack, Cancelable, Scheduler}
import monix.execution.cancelables.MultiAssignmentCancelable
import monix.execution.Ack.Continue
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import net.cakesolutions.akkarepo.core.utils.ValueDiscard

private[workflow] object OnErrorRetryLimitedWithBackoff {

  def apply[A](
    source: Observable[A],
    maxRetries: Int Refined Positive,
    backoff: BackoffStrategy,
    jitter: FiniteDuration
  ): Observable[A] = {
    new OnErrorRetryWithBackoff(source, Some(maxRetries), backoff, jitter)
  }
}

private[workflow] object OnErrorRetryUnlimitedWithBackoff {

  def apply[A](
    source: Observable[A],
    backoff: BackoffStrategy,
    jitter: FiniteDuration
  ): Observable[A] = {
    new OnErrorRetryWithBackoff(source, None, backoff, jitter)
  }
}

private final class OnErrorRetryWithBackoff[+A](
  source: Observable[A],
  maxRetries: Option[Int Refined Positive],
  backoff: BackoffStrategy,
  jitter: FiniteDuration
) extends Observable[A] {

  private[this] val delayGen = new StreamIterator(backoff.sequenceGenerator)

  /** @inheritdoc */
  override def unsafeSubscribeFn(subscriber: Subscriber[A]): Cancelable = {
    val task = MultiAssignmentCancelable()
    loop(subscriber, task, retryIdx = 0)
    task
  }

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private def loop(
    subscriber: Subscriber[A],
    task: MultiAssignmentCancelable,
    retryIdx: Long
  ): Unit = {
    val cancelable = source.unsafeSubscribeFn(new Subscriber[A] {
      implicit val scheduler: Scheduler = subscriber.scheduler

      private[this] var isDone = false
      private[this] var ack: Future[Ack] = Continue

      override def onNext(elem: A): Future[Ack] = {
        ack = subscriber.onNext(elem)
        ack
      }

      override def onComplete(): Unit = {
        if (!isDone) {
          isDone = true
          subscriber.onComplete()
        }
      }

      @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
      override def onError(ex: Throwable): Unit = {
        if (!isDone) {
          isDone = true

          if (maxRetries.isEmpty || retryIdx < maxRetries.get.value) {
            // need asynchronous execution to avoid a synchronous loop
            // blowing out the call stack
            ValueDiscard[Cancelable] {
              scheduler.scheduleOnce(withJitter(delayGen.next(), jitter)) {
                loop(subscriber, task, retryIdx + 1)
              }
            }
          } else {
            subscriber.onError(ex)
          }
        }
      }
    })

    // We need to do an `orderedUpdate`, because `onError` might have
    // already and resubscribed by now.
    ValueDiscard[MultiAssignmentCancelable] {
      task.orderedUpdate(cancelable, retryIdx)
    }
  }

  private def withJitter(
    time: FiniteDuration,
    jitter: FiniteDuration
  ): FiniteDuration = {

    // FIXME: is there a better way of doing this?
    val offset = Random.nextInt((2 * jitter.toNanos).toInt) - jitter.toNanos

    time + offset.nanoseconds
  }
}
