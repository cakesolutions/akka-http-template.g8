package test_net.test_cakesolutions.akkarepo.core.api

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import monix.eval.Task

import test_net.test_cakesolutions.akkarepo.core.application.ApplicationGlobalContext

/**
  * Common Http handler bootstrapping.
  */
object BaseHttpHandler {
  import ExceptionHandling._
  import RejectionHandling._

  /**
    * Code to bootstrap the endpoints for a HTTP server. Should the
    * application's bootstrapping workflow abnormally terminate, then we unbind
    * from the endpoint's socket.
    *
    * @param baseRoute server endpoint definitions
    * @param hostname hostname HTTP server is to listen on
    * @param port port HTTP server is to listen on
    * @return Kleisli workflow describing HTTP bootstrapping behaviour and
    *  resource cleanup actions
    */
  def apply(
    baseRoute: Route,
    hostname: String,
    port: Int Refined Positive
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Task[Unit] = {
    implicit val system: ActorSystem = globalContext.system
    implicit val materializer: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher

    val route =
      logRequestResult(globalContext.applicationName) {
        handleExceptions(exceptionHandler(globalContext.log)) {
          handleRejections(rejectionHandler(globalContext.log)) {
            baseRoute ~ routeReject
          }
        }
      }

    def httpSocketBind: Future[Http.ServerBinding] =
      Http().bindAndHandle(Route.handlerFlow(route), hostname, port.value)

    def httpCleanUp: Task[Unit] = Task.deferFutureAction { implicit ec =>
      httpSocketBind
        .flatMap(_.unbind())
    }

    Task
      .fromFuture(httpSocketBind.map(_ => ()))
      .doOnFinish {
        case None =>
          Task.unit
        case Some(_) =>
          httpCleanUp
      }
  }

  private def routeReject: StandardRoute =
    complete(NotFound)
}
