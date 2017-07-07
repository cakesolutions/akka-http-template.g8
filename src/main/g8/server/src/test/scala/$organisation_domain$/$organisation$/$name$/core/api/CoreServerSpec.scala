package $organisation_domain$.$organisation$.$name$.core.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import org.scalatest.{AsyncWordSpecLike, Matchers}

import scala.util.Random

class CoreServerSpec extends AsyncWordSpecLike with Matchers with CoreServer {

  implicit val system: ActorSystem = ActorSystem("CoreServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val host = "localhost"
  val port = 10000 + Random.nextInt(55536)
  val swaggerPath = "META-INF/resources/webjars/swagger-ui/3.0.10"

  val server = bind(host, port, swaggerPath).firstL.runAsync(monix.execution.Scheduler.global)

  "CoreServer" should {
    "serve the /health endpoint" in {
      Http()
        .singleRequest(HttpRequest(uri = s"http://localhost:\$port/health"))
        .map(_.status.intValue() shouldBe 200)
    }

    "serve the /version endpoint" in {
      Http()
        .singleRequest(HttpRequest(uri = s"http://localhost:\$port/version"))
        .map(_.status.intValue() shouldBe 200)
    }

    "serve the /specs.yml" in {
      Http()
        .singleRequest(HttpRequest(uri = s"http://localhost:\$port/specs.yml"))
        .map(_.status.intValue() shouldBe 200)
    }
  }

}
