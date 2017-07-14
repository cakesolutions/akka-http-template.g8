package $organisation_domain$.$organisation$.$name$.core.application

import monix.eval.Task

import $organisation_domain$.$organisation$.$name$.core.application.workflow.internal._

/**
  * TODO:
  */
package object workflow {

  /**
    * TODO:
    *
    * @param workflow
    * @tparam X
    */
  implicit class OnError[X](workflow: Task[X]) {
    def onError(strategy: RetryStrategy): Task[X] = {
      new OnErrorHelper(workflow, strategy).task()
    }

    private class OnErrorHelper(workflow: Task[X], strategy: RetryStrategy) {
      val taskGen: Iterator[Task[X]] = {
        strategy match {
          case RetryUnlimited =>
            OnErrorRetryUnlimited(workflow)

          case RetryLimited(n) =>
            OnErrorRetryLimited(workflow, n)

          case RetryUnlimitedWithBackoff(backoff, jitter) =>
            OnErrorRetryUnlimitedWithBackoff(workflow, backoff, jitter)

          case RetryLimitedWithBackoff(n, backoff, jitter) =>
            OnErrorRetryLimitedWithBackoff(workflow, n, backoff, jitter)
        }
      }

      def task(): Task[X] = {
        taskGen.next().onErrorHandleWith(_ => task())
      }
    }
  }
}
