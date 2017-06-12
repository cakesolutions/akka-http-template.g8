package $organisation_domain$.$organisation$.$name$.core.api.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import $organisation_domain$.$organisation$.$name$.build.BuildInfo

object CoreRoutes {

  val routes =
    // format: off
    path("health") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, """{"status": "Ok"}"""))
      }
    } ~
    path("version") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, BuildInfo.toJson))
      }
    }
    // format: on

}