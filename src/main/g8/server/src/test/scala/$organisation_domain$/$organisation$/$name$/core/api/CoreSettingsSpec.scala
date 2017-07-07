package $organisation_domain$.$organisation$.$name$.core.api

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

class CoreSettingsSpec extends WordSpec with Matchers {

  val config = ConfigFactory.parseString(
    s"""
       |core {
       |  host = "0.0.0.0"
       |  port = 9000
       |  swagger-ui.path = "META-INF/resources/webjars/swagger-ui/3.0.10/"
       |}
     """.stripMargin
  )

  "CoreSettings" should {
    "accept valid config" in {
      CoreSettings(config) match {
        case Valid(CoreSettings(host, port, path)) =>
          host shouldBe config.getString("core.host")
          port shouldBe config.getInt("core.port")
          path shouldBe config.getString("core.swagger-ui.path")
        case _ => fail()
      }
    }
    "reject config with invalid port" in {
      val wrongConfig = ConfigFactory
        .parseString("core.port = -9000")
        .withFallback(config)
      CoreSettings(wrongConfig) shouldBe a [Invalid[_]]
    }
    "reject config with missing host" in {
      val wrongConfig = config.withoutPath("core.host")
      CoreSettings(wrongConfig) shouldBe a [Invalid[_]]
    }
    "reject config with missing swagger config" in {
      val wrongConfig = config.withoutPath("core.swagger-ui")
      CoreSettings(wrongConfig) shouldBe a [Invalid[_]]
    }
  }

}
