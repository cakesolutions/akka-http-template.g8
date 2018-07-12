package $organisation_domain$.$organisation$

import sbt._

/**
  * Dependencies referenced in project auto-plugin and build code.
  */
object Dependencies {

  val cats: ModuleID = "org.typelevel" %% "cats" % "0.9.0"
  val typesafeConfig: ModuleID = "com.typesafe" % "config" % "1.3.3"
  // TODO: CO-117: Refactor server.conf to remove swagger version information
  val swagger: ModuleID = "org.webjars" % "swagger-ui" % "3.0.21"
  val validatedConfig: ModuleID = "net.cakesolutions" %% "validated-config" % "1.1.3"

  object Akka {
    val version: String = "2.5.13"

    val actor: ModuleID = "com.typesafe.akka" %% "akka-actor" % version
    val contrib: ModuleID = "com.typesafe.akka" %% "akka-contrib" % version

    object Http {
      val version: String = "10.1.3"

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

  object Refined {
    val version: String = "0.9.2"

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
