package test_net.test_cakesolutions.akkarepo.core.config

import cakesolutions.config.ValueError
import cats.data.NonEmptyList

/**
  * TODO:
  *
  * @param errors
  */
final case class ConfigurationFailure(
  errors: NonEmptyList[ValueError]
) extends Exception {

  /** @see java.lang.Exception */
  override def toString: String = {
    s"ConfigurationFailure(${errors.toList.mkString(",")})"
  }
}
