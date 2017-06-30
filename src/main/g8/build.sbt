// Please read https://github.com/cakesolutions/sbt-cake
//
// NOTE: common settings are in project/ProjectPlugin.scala but
//       anything specific to a project should go in this file. Check
//       sbt-cake for standard libraryDependencies used in various
//       Cake projects, otherwise just add dependencies explicitly
//       in this file.

import Dependencies._
import net.cakesolutions.CakePlatformKeys.PlatformDependencies

lazy val core = project.in(file("library/core"))

lazy val serverMain = project.in(file("mains/server"))
  .enableIntegrationTests
  .settings(
    libraryDependencies ++= Seq(
      Akka.actor,
      Akka.contrib,
      Akka.Http.core,
      Akka.Http.jsonSpray,
      Akka.Http.testkit % "test",
      cats,
      Monix.core,
      swagger,
      validatedConfig,
      Zipkin.akkaHttp
    ) ++ PlatformDependencies.testing(IntegrationTest)
  )

lazy val root = project.in(file("."))
  .enablePlugins(
    CakeBuildInfoPlugin,
    CakeDockerComposePlugin,
    CakePublishMavenPlugin,
    CakeStandardsPlugin
  )
  .aggregate(
    core,
    serverMain
  )
