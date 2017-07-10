package net.cakesolutions.akkarepo.server.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.cakesolutions.akkarepo.build.BuildInfo

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
