package $organisation_domain$.$organisation$.$name$.server.api

import akka.http.scaladsl.model.{ContentTypes, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig

/**
  * Factory defining the Swagger documentation endpoints.
  */
object SwaggerRoutes {

  /**
    * Factory method for generating Akka HTTP routes for serving Swagger API
    * documentation.
    *
    * @param swaggerPath path at which Swagger API documentation will be served
    * @return Akka HTTP route
    */
  def apply(swaggerPath: ValidatedServerConfig.SwaggerPathString): Route =
    path("specs.yml") {
      get {
        getFromResource("$name$.yml", ContentTypes.`text/plain(UTF-8)`)
      }
    } ~
      pathPrefix("docs") {
        get {
          pathEndOrSingleSlash {
            parameter("url".?) { url =>
              redirect(
                Uri(s"/docs/index.html?url=\${url getOrElse "/specs.yml"}"),
                StatusCodes.SeeOther
              )
            }
          } ~
          path("index.html") {
            parameter("url".?) {
              case Some(_) => getFromResource(swaggerPath.value + "index.html")
              case None =>
                redirect(
                  Uri("/docs/index.html?url=/specs.yml"),
                  StatusCodes.SeeOther
                )
            }
          } ~
          path(RemainingPath) { file =>
            getFromResource(swaggerPath.value + file.toString)
          }
        }
      } ~
      pathEndOrSingleSlash {
        redirect(Uri("/docs/index.html?url=/specs.yml"), StatusCodes.SeeOther)
      }
}
