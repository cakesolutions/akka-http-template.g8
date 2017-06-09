package $organisation_domain$.$organisation$.$name$.core.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object CoreServer extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config = ConfigFactory.load("application.conf")
  val host = config.getString("core.host")
  val port = config.getInt("core.port")
  val swaggerPath = config.getString("core.swagger-ui.path")

  val cores = routes.CoreRoutes.routes
  val docs = new routes.DocsRoutes(swaggerPath).routes

  import system.dispatcher
  val server = Http().bindAndHandle(cores ~ docs, host, port)
  sys.addShutdownHook(
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  ).join()

}