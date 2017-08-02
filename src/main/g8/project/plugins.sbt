import $organisation_domain$.$organisation$.Dependencies.SbtPlugins._

unmanagedSources in Compile +=
  baseDirectory.value / "project" / "Dependencies.scala"

ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin(sbtCake)
addSbtPlugin(sbtHeader)
addSbtPlugin(scalafmt)
addSbtPlugin(scalastyle)
