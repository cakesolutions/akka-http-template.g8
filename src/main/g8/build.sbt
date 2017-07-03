// Please read https://github.com/cakesolutions/sbt-cake
//
// NOTE: common settings are in project/ProjectPlugin.scala but
//       anything specific to a project should go in this file. Check
//       sbt-cake for standard libraryDependencies used in various
//       Cake projects, otherwise just add dependencies explicitly
//       in this file.

import Dependencies._
import net.cakesolutions.CakePlatformKeys.{ deps => PlatformDependencies }

// example akka-http server
val server = project
  .enablePlugins(
    CakeBuildInfoPlugin,
    CakeDockerComposePlugin,
//    CakePublishMavenPlugin,
    CakeStandardsPlugin
  )
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
