package $organisation_domain$.$organisation$.$name$.core.api.routes

import akka.http.scaladsl.model.{ContentTypes, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._

/**
  * TODO:
  *
  * @param swaggerPath
  */
class DocsRoutes(swaggerPath: String) {

  /**
    * TODO:
    */
  val routes =
    // format: off
    path("specs.yml") {
      get {
        getFromResource("$name$.yml", ContentTypes.`text/plain(UTF-8)`)
      }
    } ~
    pathPrefix("docs") {
      get {
        pathEndOrSingleSlash {
          parameter("url".?) { url =>
            redirect(Uri(s"/docs/index.html?url=\${url getOrElse "/specs.yml"}"), StatusCodes.SeeOther)
          }
        } ~
        path("index.html") {
          parameter("url".?) {
            case Some(_) => getFromResource(swaggerPath + "index.html")
            case None    => redirect(Uri("/docs/index.html?url=/specs.yml"), StatusCodes.SeeOther)
          }
        } ~
        path(RemainingPath) { file =>
          getFromResource(swaggerPath + file.toString)
        }
      }
    } ~
    pathEndOrSingleSlash {
      redirect(Uri("/docs/index.html?url=/specs.yml"), StatusCodes.SeeOther)
    }
    // format: on
}
