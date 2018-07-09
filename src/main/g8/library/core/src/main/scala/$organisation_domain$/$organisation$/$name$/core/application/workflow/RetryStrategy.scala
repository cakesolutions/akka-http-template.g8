package $package_structure$.core.application.workflow

import scala.concurrent.duration._

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

/**
  * Retry strategy that is to be used in case of (for example) task failure.
  */
sealed trait RetryStrategy

/**
  * Keep retrying with no upper bound on the number of retry attempts.
  */
case object RetryUnlimited extends RetryStrategy

/**
  * Retry a fixed or limited number of times.
  *
  * @param n positive integer indicating how many times we will attempt to retry
  */
final case class RetryLimited(n: Int Refined Positive) extends RetryStrategy

/**
  * Keep retrying with no upper bound on the number of retry attempts. A backoff
  * strategy (with jitter) is applied upon each retry attempt.
  *
  * @param backoff backoff strategy that we will apply between retry attempts
  * @param jitter random jitter that will be applied to each retry attempt
  */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class RetryUnlimitedWithBackoff(
  backoff: BackoffStrategy,
  jitter: FiniteDuration = 0.seconds
) extends RetryStrategy

/**
  * Retry a fixed or limited number of times. A backoff strategy (with jitter)
  * is applied upon each retry attempt.
  *
  * @param n positive integer indicating how many times we will attempt to retry
  * @param backoff backoff strategy that we will apply between retry attempts
  * @param jitter random jitter that will be applied to each retry attempt
  */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class RetryLimitedWithBackoff(
  n: Int Refined Positive,
  backoff: BackoffStrategy,
  jitter: FiniteDuration = 0.seconds
) extends RetryStrategy
