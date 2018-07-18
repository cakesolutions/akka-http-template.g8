package $package$.core.application.workflow

import scala.concurrent.duration._
import scala.util.Random

import cats.data.NonEmptyList
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import org.scalatest._

// scalastyle:off magic.number

class BackoffStrategyTest extends FreeSpecLike with Matchers {

  "Linear backoff strategy" - {
    "correct sequence prefix" in {
      val limit = 100
      val gen = Linear(MILLISECONDS)

      gen.internalSequence
        .take(limit)
        .toList shouldEqual (0 until limit).toList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      (0 until limit).map(_.milliseconds).toList
    }

    "able to change timeunits" in {
      val limit = 100
      val gen = Linear(HOURS)

      gen.internalSequence
        .take(limit)
        .toList shouldEqual (0 until limit).toList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      (0 until limit).map(_.hours).toList
    }
  }

  "Defined backoff strategy" - {
    "correct sequence prefix" in {
      val randomList =
        (0 to 50)
          .map(_ => refineV[Positive](Random.nextInt(100) + 1).right.get)
          .toList
      val gen = Defined(NonEmptyList.fromListUnsafe(randomList), MILLISECONDS)

      gen.internalSequence.toList shouldEqual
      randomList.map(n => BigInt(n.value))
      gen.sequenceGenerator.toList shouldEqual
      randomList.map(_.value.milliseconds)
    }

    "able to change timeunits" in {
      val randomList =
        (0 to 50)
          .map(_ => refineV[Positive](Random.nextInt(100) + 1).right.get)
          .toList
      val gen = Defined(NonEmptyList.fromListUnsafe(randomList), SECONDS)

      gen.internalSequence.toList shouldEqual
      randomList.map(n => BigInt(n.value))
      gen.sequenceGenerator.toList shouldEqual
      randomList.map(_.value.seconds)
    }
  }

  "Fibonnaci backoff strategy" - {
    "correct sequence prefix" in {
      val limit = 21
      val gen = Fibonnaci(MILLISECONDS)
      val expectedList =
        List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987,
          1597, 2584, 4181, 6765).map(n => BigInt(n))

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.milliseconds)
    }

    "able to change timeunits" in {
      val limit = 21
      val gen = Fibonnaci(DAYS)
      val expectedList = List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144,
        233, 377, 610, 987, 1597, 2584, 4181, 6765).map(n => BigInt(n))

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.days)
    }
  }

  "Exponential backoff strategy" - {
    "correct sequence prefix" in {
      val limit = 10
      val factor = refineMV[Positive](2)
      val gen = Exponential(factor, MILLISECONDS)
      val expectedList =
        (0 until limit).map(n => BigInt(factor.value).pow(n)).toList

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.milliseconds)
    }

    "able to change timeunits" in {
      val limit = 10
      val factor = refineMV[Positive](4)
      val gen = Exponential(factor, NANOSECONDS)
      val expectedList =
        (0 until limit).map(n => BigInt(factor.value).pow(n)).toList

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.nanoseconds)
    }
  }

  "Polynomial backoff strategy" - {
    "correct sequence prefix" in {
      val limit = 10
      val factor = refineMV[Positive](5)
      val gen = Polynomial(factor, MILLISECONDS)
      val expectedList =
        (0 until limit).map(n => BigInt(n).pow(factor.value)).toList

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.milliseconds)
    }

    "able to change timeunits" in {
      val limit = 10
      val factor = refineMV[Positive](6)
      val gen = Polynomial(factor, MINUTES)
      val expectedList =
        (0 until limit).map(n => BigInt(n).pow(factor.value)).toList

      gen.internalSequence.take(limit).toList shouldEqual expectedList
      gen.sequenceGenerator.take(limit).toList shouldEqual
      expectedList.map(_.toInt.minutes)
    }
  }
}
