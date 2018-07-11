package $organisation_domain$.$organisation$

import sbt._

/**
  * Plugin dependencies referenced in project auto-plugin and build code.
  */
object PluginDependencies {

  val sbtCake: ModuleID = "net.cakesolutions" % "sbt-cake" % "1.1.10"
  val scalafmt: ModuleID = "com.lucidchart" % "sbt-scalafmt" % "1.10"
  val sbtHeader: ModuleID = "de.heikoseeberger" % "sbt-header" % "2.0.0"
  val scalastyle: ModuleID =
    "org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0"
}
