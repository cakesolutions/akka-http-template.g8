package $package$.server.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import $package$.build.BuildInfo

/**
  * Factory for defining core or common endpoints.
  */
object CoreRoutes {

  /**
    * Factory method for generating core Akka HTTP routes (e.g. health and
    * version endpoints).
    *
    * @return Akka HTTP route
    */
  def apply(): Route =
    path("health") {
      get {
        complete(
          HttpEntity(ContentTypes.`application/json`, """{"status": "Ok"}""")
        )
      }
    } ~
      path("version") {
        get {
          complete(
            HttpEntity(ContentTypes.`application/json`, BuildInfo.toJson)
          )
        }
      }

}
