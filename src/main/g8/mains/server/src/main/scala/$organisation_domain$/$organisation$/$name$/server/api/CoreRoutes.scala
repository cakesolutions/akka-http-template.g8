package $organisation_domain$.$organisation$.$name$.server.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import $organisation_domain$.$organisation$.$name$.build.BuildInfo

/**
  * TODO:
  */
object CoreRoutes {

  /**
    * TODO:
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
