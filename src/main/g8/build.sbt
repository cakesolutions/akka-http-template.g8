import net.cakesolutions.CakePlatformKeys.PlatformBundles

import $organisation_domain$.$organisation$.Dependencies._

// FIXME: the following Settings need to be defined on a per project basis
snapshotRepositoryResolver := None
repositoryResolver := None
issueManagementUrl := None
issueManagementProject := None

lazy val core = project
  .in(file("library/core"))
  .enablePlugins(ProjectPlugin)
  .settings(
    libraryDependencies ++= Seq(
      Akka.actor,
      Akka.Http.core,
      cats,
      Monix.core,
      Refined.core,
      validatedConfig,
      Zipkin.akkaHttp
    )
  )

lazy val serverMain = project
  .in(file("mains/server"))
  .dependsOn(core % "compile->compile;test->test;it->test")
  .enablePlugins(ProjectDockerBuildPlugin)
  .enableIntegrationTests
  .settings(
    name := "server",
    mainClass in Compile :=
      Some("$package$.server.ServerMain"),
    libraryDependencies ++= Seq(
      Akka.contrib,
      Akka.Http.jsonSpray,
      Akka.Http.testkit % "test",
      swagger
    ) ++ PlatformBundles.testing(IntegrationTest)
  )

lazy val root = project
  .in(file("."))
  .aggregate(core, serverMain)
