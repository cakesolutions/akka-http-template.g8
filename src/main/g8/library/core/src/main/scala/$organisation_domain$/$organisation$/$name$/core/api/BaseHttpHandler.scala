package $organisation_domain$.$organisation$.$name$.core.api

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import monix.eval.Task
import monix.reactive.Observable
import $organisation_domain$.$organisation$.$name$.core.application.ApplicationGlobalContext
import $organisation_domain$.$organisation$.$name$.core.application.workflow.Layer

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
  ): Layer[Unit] = {
    implicit val system: ActorSystem = globalContext.system
    implicit val materializer: Materializer = ActorMaterializer()

    val route =
      logRequestResult(globalContext.applicationName) {
        handleExceptions(exceptionHandler(globalContext.log)) {
          handleRejections(rejectionHandler(globalContext.log)) {
            baseRoute ~ routeReject
          }
        }
      }
    val httpSocketBind =
      Http().bindAndHandle(Route.handlerFlow(route), hostname, port.value)

    // We unbind using the global execution context as the actor system may
    // already have died
    val httpCleanUp = Task.deferFuture {
      httpSocketBind
        .flatMap(_.unbind())(ExecutionContext.global)
    }
    val httpBootstrap = {
      implicit val ec: ExecutionContext = globalContext.system.dispatcher

      Observable.fromFuture(httpSocketBind.map(_ => ()))
    }

    Layer(httpBootstrap, httpCleanUp)
  }

  private def routeReject: StandardRoute =
    complete(NotFound)
}
