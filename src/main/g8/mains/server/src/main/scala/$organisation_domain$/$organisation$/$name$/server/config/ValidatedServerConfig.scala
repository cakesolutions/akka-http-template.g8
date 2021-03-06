package $organisation_domain$.$organisation$.$name$.server.config

import cakesolutions.config._
import cats.data.{NonEmptyList => NEL, Validated}
import cats.syntax.cartesian._
import com.typesafe.config.Config
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._

/**
  * Validated server configuration.
  */
object ValidatedServerConfig {
  type PositiveInt = Int Refined Positive
  type SwaggerPathString =
    String Refined MatchesRegex[W.`"[a-zA-Z0-9_/.-]*/"`.T]

  /**
    * Validated configuration for the services HTTP endpoints.
    *
    * @param host hostname or IP address at which the HTTP server will listen
    * @param port port at which the HTTP server will listen
    */
  sealed abstract case class HttpConfig(host: String, port: PositiveInt)

  /**
    * Validated configuration for the server.
    *
    * @param swaggerPath URI path on which Swagger API documentation will be
    *   served
    * @param http validated HTTP configuration
    */
  sealed abstract case class ServerConfig(
    swaggerPath: SwaggerPathString,
    http: HttpConfig
  )

  /**
    * Factory for creating immutable, type safe validated configuration
    * instances.
    *
    * @param config raw and unchecked Typesafe configuration object
    * @return validation errors or a validated configuration instance
    */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def apply(
    implicit config: Config
  ): Validated[NEL[ValueFailure], ServerConfig] = {
    via[ServerConfig]("server") { implicit config =>
      (unchecked[SwaggerPathString]("swagger-ui.path") |@|
        (unchecked[String](required("host", "NOT_SET")) |@|
          unchecked[PositiveInt](required("port", "NOT_SET")))
          .map(new HttpConfig(_, _) {})).map(new ServerConfig(_, _) {})
    }
  }
}
