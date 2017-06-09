// Please read https://github.com/cakesolutions/sbt-cake
//
// NOTE: common settings are in project/ProjectPlugin.scala but
//       anything specific to a project should go in this file. Check
//       sbt-cake for standard libraryDependencies used in various
//       Cake projects, otherwise just add dependencies explicitly
//       in this file.

// example akka-http server
val server = project
  .enablePlugins(BuildInfoPlugin, DockerPlugin, AshScriptPlugin)
  .enableIntegrationTests
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % "10.0.7",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
      "org.webjars"       %  "swagger-ui"           % "3.0.10"
    ),
    pipelineStages := Seq(digest, gzip)
  )

