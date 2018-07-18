package $package$.core.api

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.{Directives, RejectionHandler, ValidationRejection}

/**
  * Akka HTTP rejection handler.
  */
private object RejectionHandling {
  import Directives._

  /**
    * Akka HTTP rejection handler that all endpoints should use.
    *
    * @param log logging interface
    * @return rejection handler
    */
  def rejectionHandler(implicit log: LoggingAdapter): RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case rejection: ValidationRejection =>
          log.error(s"Validation Rejection: \$rejection")

          complete(NotFound)
      }
      .result()
}
