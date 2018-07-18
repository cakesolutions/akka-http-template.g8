package $package$.core.api

import scala.util.control.NonFatal

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.{Directives, ExceptionHandler}

/**
  * Akka HTTP exception handler.
  */
private object ExceptionHandling {
  import Directives._

  /**
    * Akka HTTP exception handler that all endpoints should use.
    *
    * @param log logging interface
    * @return exception handler
    */
  def exceptionHandler(implicit log: LoggingAdapter): ExceptionHandler =
    ExceptionHandler {
      case NonFatal(exn) =>
        complete {
          log.error(
            exn,
            "Unexpected exception thrown whilst processing connection " +
              "handler routes!"
          )

          InternalServerError
        }
    }
}
