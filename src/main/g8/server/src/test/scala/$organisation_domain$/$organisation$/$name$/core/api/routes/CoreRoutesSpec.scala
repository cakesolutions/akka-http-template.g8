package $organisation_domain$.$organisation$.$name$.core.api.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class CoreRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "CoreRoutes" should {
    "return the status for GET on the /health endpoint" in {
      Get("/health") ~> CoreRoutes.routes ~> check {
        status.intValue() shouldBe 200
        responseAs[String] shouldBe """{"status": "Ok"}"""
      }
    }

    "return the build info for GET on the /version endpoint" in {
      Get("/version") ~> CoreRoutes.routes ~> check {
        status.intValue() shouldBe 200
      }
    }
  }

}
