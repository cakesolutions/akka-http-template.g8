// Copyright: 2017 https://github.com/cakesolutions/akka-http-template.g8/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0

// TODO: CO-43: SBT Resolvers
lazy val root = (project in file(".")).
  settings(
    resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
  )
