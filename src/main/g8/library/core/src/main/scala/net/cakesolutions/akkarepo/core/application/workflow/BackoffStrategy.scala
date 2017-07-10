package net.cakesolutions.akkarepo.core.application.workflow

import scala.concurrent.duration.{FiniteDuration, TimeUnit}

import cats.data.NonEmptyList
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

/**
  * TODO:
  */
sealed trait BackoffStrategy {

  /**
    * TODO:
    *
    * @return
    */
  def units: TimeUnit

  /**
    * TODO:
    *
    * @return
    */
  def sequenceGenerator: Stream[FiniteDuration] = {
    internalSequence.map(n => FiniteDuration(n.longValue(), units))
  }

  /**
    * TODO:
    *
    * @return
    */
  private[workflow] def internalSequence: Stream[BigInt]
}

/**
  * A monotonically increasing strategy. For example:
  * {{
  *   0, 1, 2, 3, 4, 5
  * }}
  *
  * @param units @inheritdoc
  */
final case class Linear(units: TimeUnit) extends BackoffStrategy {

  /** @inheritdoc */
  override val internalSequence: Stream[BigInt] = {
    BigInt(0) #:: internalSequence.map(n => n + 1)
  }
}

/**
  * A predefined strategy for granular control
  *
  * @param data
  * @param units @inheritdoc
  */
final case class Defined(
  data: NonEmptyList[Int Refined Positive],
  units: TimeUnit
) extends BackoffStrategy {

  /** @inheritdoc */
  override val internalSequence: Stream[BigInt] = {
    Stream(data.toList.map(n => BigInt(n.value)): _*)
  }
}

/**
  * A strategy increasing according to a Fabonacci sequence. For example:
  * {{
  *   1, 1, 2, 3, 5, 8, 13
  * }}
  *
  * @param units @inheritdoc
  */
final case class Fibonnaci(units: TimeUnit) extends BackoffStrategy {

  /** @inheritdoc */
  override val internalSequence: Stream[BigInt] = {
    BigInt(0) #::
    BigInt(1) #::
    internalSequence
      .zip(internalSequence.drop(1))
      .map(n => n._1 + n._2)
  }
}

/**
  * An exponentially increasing strategy. For example:
  * {{
  *   1, factor, Math.pow(factor,2), Math.pow(factor,3), Math.pow(factor,4)
  * }}
  *
  * @param factor
  * @param units @inheritdoc
  */
final case class Exponential(
  factor: Int Refined Positive,
  units: TimeUnit
) extends BackoffStrategy {

  /** @inheritdoc */
  override val internalSequence: Stream[BigInt] = {
    val linearSequence = BigInt(0) #:: internalSequence.map(n => n + 1)

    linearSequence.map(n => BigInt(factor.value).pow(n.toInt))
  }
}

/**
  * A polynomially increasing strategy. For example:
  * {{
  *   0, 1, Math.pow(2,factor), Math.pow(3,factor), Math.pow(4,factor)
  * }}
  *
  * @param factor
  * @param units @inheritdoc
  */
final case class Polynomial(
  factor: Int Refined Positive,
  units: TimeUnit
) extends BackoffStrategy {

  /** @inheritdoc */
  override val internalSequence: Stream[BigInt] = {
    val linearSequence = BigInt(0) #:: internalSequence.map(n => n + 1)

    linearSequence.map(n => n.pow(factor.value))
  }
}
