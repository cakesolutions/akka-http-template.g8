package net.cakesolutions.akkarepo.core.api

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.{Directives, RejectionHandler, ValidationRejection}

/**
  * TODO:
  */
private object RejectionHandling {
  import Directives._

  /**
    * TODO:
    *
    * @param log
    * @return
    */
  def rejectionHandler(implicit log: LoggingAdapter): RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case rejection: ValidationRejection =>
          log.error(s"Validation Rejection: $rejection")

          complete(NotFound)
      }
      .result()
}
