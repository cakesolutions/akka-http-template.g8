package $organisation_domain$.$organisation$.$name$.core.api.routes

import akka.http.scaladsl.model.{ContentTypes, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._

class DocsRoutes(swaggerPath: String) {

  val routes =
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
    }

}