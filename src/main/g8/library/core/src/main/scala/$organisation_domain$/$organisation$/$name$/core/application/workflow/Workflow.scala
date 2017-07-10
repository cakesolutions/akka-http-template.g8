package $organisation_domain$.$organisation$.$name$.core.application.workflow

import scala.concurrent.duration._

import monix.eval.Task
import monix.reactive.Observable
import $organisation_domain$.$organisation$.$name$.core.application.workflow.internal.{OnErrorRetryLimitedWithBackoff, OnErrorRetryUnlimitedWithBackoff}

/**
  * TODO:
  *
  * @tparam X
  */
sealed trait Workflow[X] {

  /**
    * TODO:
    *
    * @return
    */
  def bootstrap: Observable[X]

  /**
    * TODO:
    *
    * @return
    */
  def cleanUp: Task[Unit]
}

/**
  * TODO:
  *
  * @param bootstrap
  * @param cleanUp
  * @tparam X
  */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class Layer[X](
  bootstrap: Observable[X],
  cleanUp: Task[Unit] = Task.unit
) extends Workflow[X]

/**
  * TODO:
  *
  * @param leftWorkflow
  * @param rightWorkflow
  * @tparam X
  * @tparam Y
  */
final case class Zip[X, Y](
  leftWorkflow: Workflow[X],
  rightWorkflow: Workflow[Y]
) extends Workflow[(X, Y)] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[(X, Y)] = {
    leftWorkflow.bootstrap.zip(rightWorkflow.bootstrap)
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    Task
      .gatherUnordered(List(leftWorkflow.cleanUp, rightWorkflow.cleanUp))
      .map(_ => ())
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param f
  * @tparam X
  * @tparam Y
  */
final case class Map[X, Y](
  workflow: Workflow[X],
  f: X => Y
) extends Workflow[Y] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[Y] = {
    workflow.bootstrap.map(f)
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    workflow.cleanUp
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param f
  * @tparam X
  * @tparam Y
  */
final case class FlatMap[X, Y](workflow: Workflow[X], f: X => Workflow[Y])
    extends Workflow[Y] {
  private val flow: Observable[Workflow[Y]] = workflow.bootstrap.map(f)

  /** @see [[Workflow]] */
  override def bootstrap: Observable[Y] = {
    flow.map(_.bootstrap).flatten
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    flow
      .map(_.cleanUp)
      .foldLeftL(Task.unit)(
        (x, y) => Task.gatherUnordered(List(x, y)).map(_ => ())
      )
      .flatten
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param condition
  * @tparam X
  */
final case class Filter[X](workflow: Workflow[X], condition: X => Boolean)
    extends Workflow[X] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[X] = {
    workflow.bootstrap.filter(condition)
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    workflow.cleanUp
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param pf
  * @tparam X
  * @tparam Y
  */
final case class Collect[X, Y](
  workflow: Workflow[X],
  pf: PartialFunction[X, Y]
) extends Workflow[Y] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[Y] = {
    workflow.bootstrap.collect(pf)
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    workflow.cleanUp
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param strategy
  * @tparam X
  */
final case class OnError[X](workflow: Workflow[X], strategy: RetryStrategy)
    extends Workflow[X] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[X] = {
    strategy match {
      case RetryUnlimited =>
        withObservable.onErrorRestartUnlimited

      case RetryLimited(n) =>
        withObservable.onErrorRestart(n.value.toLong)

      case RetryUnlimitedWithBackoff(backoff, jitter) =>
        withObservable
          .transform { self =>
            OnErrorRetryUnlimitedWithBackoff(self, backoff, jitter)
          }

      case RetryLimitedWithBackoff(n, backoff, jitter) =>
        withObservable
          .transform { self =>
            OnErrorRetryLimitedWithBackoff(self, n, backoff, jitter)
          }
    }
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    workflow.cleanUp
  }

  private def withObservable: Observable[X] = {
    workflow.bootstrap.doOnErrorEval(_ => workflow.cleanUp)
  }
}

/**
  * TODO:
  *
  * @param workflow
  * @param timeout
  * @tparam X
  */
final case class TimeoutAfter[X](
  workflow: Workflow[X],
  timeout: FiniteDuration
) extends Workflow[X] {

  /** @see [[Workflow]] */
  override def bootstrap: Observable[X] = {
    workflow.bootstrap.takeByTimespan(timeout)
  }

  /** @see [[Workflow]] */
  override def cleanUp: Task[Unit] = {
    workflow.cleanUp
  }
}
