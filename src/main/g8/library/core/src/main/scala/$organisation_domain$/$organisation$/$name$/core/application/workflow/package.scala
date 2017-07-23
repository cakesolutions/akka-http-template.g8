package $organisation_domain$.$organisation$.$name$.core.application

import monix.eval.Task

import $organisation_domain$.$organisation$.$name$.core.application.workflow.internal._

/**
  * Package object for adding retry logic to tasks.
  */
package object workflow {

  /**
    * Implicit class used to add extra methods to tasks.
    *
    * @param workflow task (e.g. representing the application bootstrapping
    *   workflow) on which methods will be added
    * @tparam X type of value that the task will create
    */
  implicit class OnError[X](workflow: Task[X]) {

    /**
      * Allows a task to be retried should it error or fail. Between retries, a
      * retry strategy is applied - thus allowing for limited retries and
      * backoff strategies to be applied.
      *
      * @param strategy retry strategy that will be applied should the task
      *   error or fail
      * @return task with the retry strategy applied
      */
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
