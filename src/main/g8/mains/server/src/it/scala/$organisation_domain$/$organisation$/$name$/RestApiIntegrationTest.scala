package $organisation_domain$.$organisation$.$name$

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import $organisation_domain$.$organisation$.$name$.core.config.ConfigHelper
import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig
import org.scalatest.{AsyncFreeSpec, Matchers}

object RestApiIntegrationTest {
  val requiredEnvVars: Map[String, String] = {
    // In AWS environments, we use the eth0 or local-ipv4 address of the slave
    // instead of localhost
    val appHost = sys.env.getOrElse("APP_HOST", "localhost")

    Map(
      "SERVER_HOST" -> appHost,
      "SERVER_PORT" -> "9000",
      "ZIPKIN_HOST" -> appHost,
      "ZIPKIN_PORT" -> "9410"
    )
  }

  val optionalEnvVars: Map[String, String] = Map()
}

trait RestApiIntegrationTest extends AsyncFreeSpec with Matchers {

  import ConfigHelper._
  import RestApiIntegrationTest._

  private val config: Config =
    validateWithEnvironmentOverrides(
      "application.conf"
    )(
      requiredEnvVars,
      optionalEnvVars
    ).get

  private val validatedConfig = {
    ValidatedServerConfig(config)
      .getOrElse(fail("Failed to validate application.conf"))
  }

  val appUrl: String = {
    val host = validatedConfig.http.host
    val port = validatedConfig.http.port

    s"http://\$host:\$port"
  }

  implicit val actorSystem: ActorSystem =
    ActorSystem(actorSystemNameFrom(getClass), config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = ExecutionContext.global

  private def actorSystemNameFrom(clazz: Class[_]) =
    clazz.getName
      .replace('.', '-')
      .replace('_', '-')
      .filter(_ != '\$')
}
