package $organisation_domain$.$organisation$.$name$.core.application.workflow

package internal

import scala.collection.immutable.StreamIterator

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import monix.eval.Task

private[workflow] object OnErrorRetryLimited {

  def apply[A](
    source: Task[A],
    maxRetries: Int Refined Positive
  ): Iterator[Task[A]] = {
    new OnErrorRetry(source, Some(maxRetries))
  }
}

private[workflow] object OnErrorRetryUnlimited {

  def apply[A](
    source: Task[A]
  ): Iterator[Task[A]] = {
    new OnErrorRetry(source, None)
  }
}

private[internal] final class OnErrorRetry[A](
  source: Task[A],
  maxRetries: Option[Int Refined Positive]
) extends Iterator[Task[A]] {

  private[this] val taskGen = {
    lazy val gen: Stream[BigInt] = BigInt(0) #:: gen.map(n => n + 1)
    val limitedGen = maxRetries.fold(gen)(n => gen.take(n.value))

    new StreamIterator(limitedGen.map(_ => source))
  }

  /** @see [[Iterator]] */
  def hasNext: Boolean = {
    true
  }

  /** @see [[Iterator]] */
  def next(): Task[A] = {
    if (taskGen.hasNext) {
      taskGen.next()
    } else {
      Task.raiseError(
        new RetryExceededException(
          s"Limited retry failed after \$maxRetries tries"
        )
      )
    }
  }
}
