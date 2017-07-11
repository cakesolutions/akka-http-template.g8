package $organisation_domain$.$organisation$

import sbt._

object Dependencies {

  val cats: ModuleID = "org.typelevel" %% "cats" % "0.9.0"
  val config: ModuleID = "com.typesafe" % "config" % "1.3.1"
  val swagger: ModuleID = "org.webjars" % "swagger-ui" % "3.0.10"
  val validatedConfig: ModuleID =
    "net.cakesolutions" %% "validated-config" % "1.0.2"

  object Akka {
    val version: String = "2.5.3"

    val actor: ModuleID = "com.typesafe.akka" %% "akka-actor" % version
    val contrib: ModuleID = "com.typesafe.akka" %% "akka-contrib" % version

    object Http {
      val version: String = "10.0.9"

      val core: ModuleID = "com.typesafe.akka" %% "akka-http" % version
      val jsonSpray: ModuleID =
        "com.typesafe.akka" %% "akka-http-spray-json" % version
      val testkit: ModuleID =
        "com.typesafe.akka" %% "akka-http-testkit" % version
    }

    val slf4j: ModuleID = "com.typesafe.akka" %% "akka-slf4j" % version
    val testkit: ModuleID = "com.typesafe.akka" %% "akka-testkit" % version
  }

  object Monix {
    val version: String = "2.3.0"

    val core: ModuleID = "io.monix" %% "monix" % version
    val execution: ModuleID = "io.monix" %% "execution" % version
    val eval: ModuleID = "io.monix" %% "monix-eval" % version
    val reactive: ModuleID = "io.monix" %% "monix-reactive" % version
  }

  object SbtPlugins {
    val sbtCake: ModuleID = "net.cakesolutions" % "sbt-cake" % "1.1.2"
    val scalafmt: ModuleID = "com.lucidchart" % "sbt-scalafmt" % "1.7"
    val sbtHeader: ModuleID = "de.heikoseeberger" % "sbt-header" % "2.0.0"
    val scalastyle: ModuleID =
      "org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0"
  }

  object Refined {
    val version: String = "0.8.0"

    val core: ModuleID = "eu.timepit" %% "refined" % version
    val scalacheck: ModuleID = "eu.timepit" %% "refined-scalacheck" % version
  }

  object Zipkin {
    val version: String = "0.6"

    val akkaHttp: ModuleID =
      "com.github.levkhomich" %% "akka-tracing-http" % version
    val core: ModuleID =
      "com.github.levkhomich" %% "akka-tracing-core" % version
  }
}
