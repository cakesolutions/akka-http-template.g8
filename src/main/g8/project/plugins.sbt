import $organisation_domain$.$organisation$.PluginDependencies._

ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin(sbtCake)
addSbtPlugin(sbtHeader)
addSbtPlugin(scalafmt)
addSbtPlugin(scalastyle)
