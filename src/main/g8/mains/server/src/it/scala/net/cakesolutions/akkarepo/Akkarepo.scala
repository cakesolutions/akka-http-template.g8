package net.cakesolutions.akkarepo

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import org.scalatest.Tag

object Docker extends Tag("Docker")

class AkkarepoIntegrationTest extends RestApiIntegrationTest {
  "When application is running" - {
    "Health-check" - {
      "should always return status okay" taggedAs Docker in {
        Http()
          .singleRequest(HttpRequest(uri = s"$appUrl/health"))
          .map(_.status.intValue() shouldEqual 200)
      }
    }

    "Build info" - {
      "should return a JSON object with current version" taggedAs Docker in {
        Http()
          .singleRequest(HttpRequest(uri = s"$appUrl/version"))
          .map(_.status.intValue() shouldEqual 200)
      }
    }

    "OpenAPI specs" - {
      "should return the yaml specs" taggedAs Docker in {
        Http()
          .singleRequest(HttpRequest(uri = s"$appUrl/specs.yml"))
          .map(_.status.intValue() shouldEqual 200)
      }

      "should redirect to the API docs" taggedAs Docker in {
        Http()
          .singleRequest(HttpRequest(uri = s"$appUrl/docs"))
          .map(_.status.intValue() shouldEqual 303)
      }

      "should show the API docs" taggedAs Docker in {
        Http()
          .singleRequest(
            HttpRequest(uri = s"$appUrl/docs/index.html?url=specs.yml")
          )
          .map(_.status.intValue() shouldEqual 200)
      }

      "default route should re-direct to API docs" taggedAs Docker in {
        Http()
          .singleRequest(HttpRequest(uri = s"$appUrl/"))
          .map(_.status.intValue() shouldEqual 303)
      }
    }
  }
}
