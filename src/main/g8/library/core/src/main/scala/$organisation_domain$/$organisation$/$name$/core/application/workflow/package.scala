package $organisation_domain$.$organisation$.$name$.core.application

import scala.concurrent.duration.FiniteDuration

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
  implicit class WorkflowHelpers[X](workflow: Workflow[X]) {

    /**
      * TODO:
      *
      * @param that
      * @tparam Y
      * @return
      */
    def zip[Y](that: Workflow[Y]): Workflow[(X, Y)] = {
      Zip(workflow, that)
    }

    /**
      * TODO:
      *
      * @param f
      * @tparam Y
      * @return
      */
    def map[Y](f: X => Y): Workflow[Y] = {
      Map(workflow, f)
    }

    /**
      * TODO:
      *
      * @param f
      * @tparam Y
      * @return
      */
    def flatMap[Y](f: X => Workflow[Y]): Workflow[Y] = {
      FlatMap(workflow, f)
    }

    /**
      * TODO:
      *
      * @param cond
      * @return
      */
    def filter(cond: X => Boolean): Workflow[X] = {
      Filter(workflow, cond)
    }

    /**
      * TODO:
      *
      * @param pf
      * @tparam Y
      * @return
      */
    def collect[Y](pf: PartialFunction[X, Y]): Workflow[Y] = {
      Collect(workflow, pf)
    }

    /**
      * TODO:
      *
      * @param strategy
      * @return
      */
    def onError(strategy: RetryStrategy): Workflow[X] = {
      OnError(workflow, strategy)
    }

    /**
      * TODO:
      *
      * @param timeout
      * @return
      */
    def timeoutAfter(timeout: FiniteDuration): Workflow[X] = {
      TimeoutAfter(workflow, timeout)
    }
  }
}
