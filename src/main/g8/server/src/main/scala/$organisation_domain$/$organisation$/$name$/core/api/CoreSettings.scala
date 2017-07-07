package $organisation_domain$.$organisation$.$name$.core.api

import cakesolutions.config._
import cats.data.{NonEmptyList, Validated}
import cats.syntax.cartesian._
import com.typesafe.config.Config
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._

sealed abstract case class CoreSettings(
  host: String,
  port: Int,
  swaggerPath: String
)

object CoreSettings {
  def apply(
    implicit config: Config
  ): Validated[NonEmptyList[ValueFailure], CoreSettings] = {
    (
      unchecked[String](required("core.host", "NOT_SET")) |@|
        validate[Int Refined Positive](
          required("core.port", "NOT_SET"),
          RequiredValueNotSet
        )(_ >= 0) |@|
        unchecked[String](required("core.swagger-ui.path", "NOT_SET"))
    ).map(new CoreSettings(_, _, _) {})
  }
}
