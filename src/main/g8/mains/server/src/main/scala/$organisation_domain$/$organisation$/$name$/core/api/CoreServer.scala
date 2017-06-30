package $organisation_domain$.$organisation$.$name$.core.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

/**
  * TODO:
  */
trait CoreServer {

  /**
    * TODO:
    *
    * @param config
    * @param system
    * @param materializer
    * @return
    */
  def bind(
    config: Config
  )(implicit
    system: ActorSystem,
    materializer: Materializer
  ): Future[ServerBinding] = {
    val host = config.getString("host")
    val port = config.getInt("port")
    val swaggerPath = config.getString("swagger-ui.path")

    val cores = routes.CoreRoutes.routes
    val docs = new routes.DocsRoutes(swaggerPath).routes

    Http().bindAndHandle(cores ~ docs, host, port)
  }
}

/**
  * TODO:
  */
object ServerApp extends App with CoreServer {

  private implicit val system: ActorSystem = ActorSystem("CoreServer")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  import system.dispatcher

  // TODO: configuration **needs** to be validated!!!
  private val config = ConfigFactory.load("application.conf")
  private val server = bind(config.getConfig("core"))

  sys
    .addShutdownHook(
      server
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    )
    .join()

}
