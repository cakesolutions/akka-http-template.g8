package $organisation_domain$.$organisation$.$name$.core.application.workflow

import scala.concurrent.duration._

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

/**
  * TODO:
  */
// TODO: add in variants of onErrorRestartIf
sealed trait RetryStrategy

/**
  * TODO:
  */
case object RetryUnlimited extends RetryStrategy

/**
  * TODO:
  *
  * @param n
  */
final case class RetryLimited(n: Int Refined Positive) extends RetryStrategy

/**
  * TODO:
  *
  * @param backoff
  * @param jitter
  */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class RetryUnlimitedWithBackoff(
  backoff: BackoffStrategy,
  jitter: FiniteDuration = 0.seconds
) extends RetryStrategy

/**
  * TODO:
  *
  * @param n
  * @param backoff
  * @param jitter
  */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class RetryLimitedWithBackoff(
  n: Int Refined Positive,
  backoff: BackoffStrategy,
  jitter: FiniteDuration = 0.seconds
) extends RetryStrategy
