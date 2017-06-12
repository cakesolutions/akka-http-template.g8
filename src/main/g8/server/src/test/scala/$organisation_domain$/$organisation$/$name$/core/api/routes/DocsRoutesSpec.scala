package $organisation_domain$.$organisation$.$name$.core.api.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

class DocsRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  val config = ConfigFactory.load("application.conf")
  val routes = new DocsRoutes(config.getString("core.swagger-ui.path")).routes

  "DocsRoutes" should {
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
