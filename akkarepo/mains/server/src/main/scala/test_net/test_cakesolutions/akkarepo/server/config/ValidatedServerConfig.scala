package test_net.test_cakesolutions.akkarepo.server.config

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
  * TODO:
  */
object ValidatedServerConfig {
  type PositiveInt = Int Refined Positive
  type SwaggerPathString =
    String Refined MatchesRegex[W.`"[a-zA-Z0-9_/.-]*/"`.T]

  /**
    * TODO:
    *
    * @param host
    * @param port
    */
  sealed abstract case class HttpConfig(host: String, port: PositiveInt)

  /**
    * TODO:
    *
    * @param swaggerPath
    * @param http
    */
  sealed abstract case class ServerConfig(
    swaggerPath: SwaggerPathString,
    http: HttpConfig
  )

  /**
    * TODO:
    *
    * @return
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
