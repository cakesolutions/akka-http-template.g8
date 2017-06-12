package $organisation_domain$.$organisation$.$name$

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.{ AsyncFreeSpec, Matchers }
import scala.concurrent.ExecutionContext

trait RestApiIntegrationTest extends AsyncFreeSpec with Matchers {

  protected val config: Config = ConfigFactory.load()
  protected val appUrl: String = {
    val scheme = config.getString("services.app.scheme")
    val host  = config.getString("services.app.host")
    val port  = config.getInt("services.app.port")
    s"\$scheme://\$host:\$port"
  }
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = ExecutionContext.global

}
