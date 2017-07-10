import net.cakesolutions.Dependencies.SbtPlugins._

sourceGenerators in Compile += Def.task {
  val deps = (baseDirectory in Compile).value / "project" / "Dependencies.scala"
  val projectDeps = (sourceManaged in Compile).value / "Dependencies.scala"

  IO.copyFile(deps, projectDeps)

  Seq(projectDeps)
}.taskValue

ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin(sbtCake)
addSbtPlugin(scalafmt)
addSbtPlugin(scalastyle)
addSbtPlugin(sbtHeader)
