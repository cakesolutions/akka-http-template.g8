package $organisation_domain$.$organisation$.$name$.core.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.Config
import monix.eval.Task
import monix.reactive.Observable
import $organisation_domain$.$organisation$.$name$.application.CommonMain.ConfigurationFailure
import $organisation_domain$.$organisation$.$name$.application.{ApplicationContext, CommonMain}

trait CoreServer {

  /**
    * Binds a server instance to the specified host and port
    *
    * @param host
    * @param port
    * @param swaggerPath the path to the web files in the swagger-ui jar
    * @param system
    * @return an Observable with the ServerBinding
    */
  def bind(host: String, port: Int, swaggerPath: String)(
    implicit system: ActorSystem
  ): Observable[ServerBinding] = {
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val cores = routes.CoreRoutes.routes
    val docs = new routes.DocsRoutes(swaggerPath).routes

    Observable.fromFuture(Http().bindAndHandle(cores ~ docs, host, port))
  }
}

/**
  * Basic demo server with build info, health and swagger endpoints
  */
object ServerApp extends CommonMain with CoreServer {

  override protected def application(
    config: Config
  )(implicit context: ApplicationContext) = CoreSettings(config) match {
    case Valid(CoreSettings(host, port, swaggerPath)) =>
      CommonMain.ApplicationBootstrapping(
        bind(host, port, swaggerPath)(context.system)
          .flatMap(_ => Observable.never),
        Task.deferFuture(
          context.system
            .terminate()
            .map(_ => ())(scala.concurrent.ExecutionContext.global)
        )
      )
    case Invalid(errors) =>
      CommonMain.ApplicationBootstrapping(
        Observable.raiseError(ConfigurationFailure(errors)),
        Task.unit
      )
  }
}
