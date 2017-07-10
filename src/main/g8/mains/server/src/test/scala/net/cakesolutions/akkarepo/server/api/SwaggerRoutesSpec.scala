package net.cakesolutions.akkarepo.server.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.data.Validated
import com.typesafe.config.Config
import net.cakesolutions.akkarepo.core.config.{ConfigHelper, ConfigurationFailure}
import net.cakesolutions.akkarepo.server.config.ValidatedServerConfig
import net.cakesolutions.akkarepo.server.config.ValidatedServerConfig.ServerConfig
import org.scalatest.{Matchers, WordSpec}

object SwaggerRoutesSpec {
  val requiredEnvVars: Map[String, String] =
    Map(
      "SERVER_HOST" -> "example-host.com",
      "SERVER_PORT" -> "6969",
      "ZIPKIN_HOST" -> "example-zipkin.com",
      "ZIPKIN_PORT" -> "9696"
    )

  val optionalEnvVars: Map[String, String] =
    Map(
      "ZIPKIN_SAMPLE_RATE" -> "42"
    )
}

class SwaggerRoutesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest {

  import ConfigHelper._
  import SwaggerRoutesSpec._

  // We should have control over the actor system configuration
  override def testConfig: Config =
    validateWithEnvironmentOverrides(
      "application.conf"
    )(
      requiredEnvVars,
      optionalEnvVars
    ).get
  val routes: Route =
    ValidatedServerConfig(testConfig) match {
      case Validated.Valid(ServerConfig(swaggerPath, _)) =>
        SwaggerRoutes(swaggerPath)

      case Validated.Invalid(errors) =>
        fail(ConfigurationFailure(errors))
    }

  "Swagger Documentation Routes" should {
    "return the open API specs for GET on the /specs.yml endpoint" in {
      Get("/specs.yml") ~> routes ~> check {
        status.intValue() shouldBe 200
      }
    }

    "redirect to the index.html when root url is requested" in {
      Get() ~> routes ~> check {
        status.intValue() shouldBe 303
      }
    }

    "redirect to the index.html when url parameter is missing" in {
      Get("/docs") ~> routes ~> check {
        status.intValue() shouldBe 303
      }
      Get("/docs/index.html") ~> routes ~> check {
        status.intValue() shouldBe 303
      }
    }

    "redirect to the index.html page when requesting the root endpoint" in {
      Get("/docs?url=/specs.yml") ~> routes ~> check {
        status.intValue() shouldBe 303
      }
    }

    "return the index.html page for GET on the /docs/index.html endpoint" in {
      Get("/docs/index.html?url=/specs.yml") ~> routes ~> check {
        status.intValue() shouldBe 200
      }
    }

    "serve swagger-ui resources" in {
      Get("/docs/swagger-ui.js") ~> routes ~> check {
        status.intValue() shouldBe 200
      }
    }
  }

}
