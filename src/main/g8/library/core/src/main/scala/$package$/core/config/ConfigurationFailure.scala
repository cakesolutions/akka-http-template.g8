package $package$.core.config

import cats.data.NonEmptyList
import net.cakesolutions.config.ValueError

/**
  * Exception case class that holds the configuration failures that occured
  * whilst attempting to validate a Typesafe configuration object.
  *
  * @param errors non-empty list of configuration errors
  */
final case class ConfigurationFailure(
  errors: NonEmptyList[ValueError]
) extends Exception(errors.toList.mkString(", "))
