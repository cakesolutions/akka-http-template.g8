package net.cakesolutions.akkarepo.core.api

import scala.util.control.NonFatal

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.{Directives, ExceptionHandler}

/**
  * TODO:
  */
private object ExceptionHandling {
  import Directives._

  /**
    * TODO:
    *
    * @param log
    * @return
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
