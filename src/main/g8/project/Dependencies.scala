import sbt._

object Dependencies {
  object Akka {
    val version = "2.4.17"

    val actor = "com.typesafe.akka" %% "akka-actor" % version
    val contrib = "com.typesafe.akka" %% "akka-contrib" % version

    object Http {
      val version = "10.0.5"

      val core = "com.typesafe.akka" %% "akka-http" % version
      val jsonSpray = "com.typesafe.akka" %% "akka-http-spray-json" % version
      val testkit = "com.typesafe.akka" %% "akka-http-testkit" % version
    }

    val slf4j = "com.typesafe.akka" %% "akka-slf4j" % version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
  }

  val cats = "org.typelevel" %% "cats" % "0.9.0"
  val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

  object Monix {
    val version = "2.2.1"

    val core = "io.monix" %% "monix" % version
    val execution = "io.monix" %% "execution" % version
    val eval = "io.monix" %% "monix-eval" % version
    val reactive = "io.monix" %% "monix-reactive" % version
  }

  object Refined {
    val version = "0.8.0"

    val core = "eu.timepit" %% "refined" % version
    val scalacheck = "eu.timepit" %% "refined-scalacheck" % version
  }

  val swagger = "org.webjars" % "swagger-ui" % "3.0.10"
  val validatedConfig = "net.cakesolutions" %% "validated-config" % "1.0.1"

  object Zipkin {
    val version = "0.6"

    val akkaHttp = "com.github.levkhomich" %% "akka-tracing-http" % version
    val core = "com.github.levkhomich" %% "akka-tracing-core" % version
  }
}
