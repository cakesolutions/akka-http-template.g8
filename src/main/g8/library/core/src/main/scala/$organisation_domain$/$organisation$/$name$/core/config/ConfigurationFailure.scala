package $organisation_domain$.$organisation$.$name$.core.config

import cakesolutions.config.ValueError
import cats.data.NonEmptyList

/**
  * Exception case class that holds the configuration failures that occured
  * whilst attempting to validate a Typesafe configuration object.
  *
  * @param errors non-empty list of configuration errors
  */
final case class ConfigurationFailure(
  errors: NonEmptyList[ValueError]
) extends Exception {

  /** @see java.lang.Exception */
  override def toString: String = {
    s"ConfigurationFailure(\${errors.toList.mkString(",")})"
  }
}
