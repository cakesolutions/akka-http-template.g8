package test_net.test_cakesolutions.akkarepo.server

import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import com.typesafe.config.Config
import monix.eval.Task

import test_net.test_cakesolutions.akkarepo.core.api.BaseHttpHandler
import test_net.test_cakesolutions.akkarepo.core.application.{ApplicationBootstrapping, ApplicationGlobalContext}
import test_net.test_cakesolutions.akkarepo.core.config.ConfigurationFailure
import test_net.test_cakesolutions.akkarepo.server.api.{CoreRoutes, SwaggerRoutes}
import test_net.test_cakesolutions.akkarepo.server.config.ValidatedServerConfig
import test_net.test_cakesolutions.akkarepo.server.config.ValidatedServerConfig.ServerConfig

// $COVERAGE-OFF$

/**
  * TODO:
  */
object ServerMain extends ApplicationBootstrapping {

  /** @see ApplicationBootstrapping */
  override protected def application(
    config: Config
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Task[Unit] = {
    validateConfig(config).flatMap(httpService)
  }

  private def validateConfig(
    config: Config
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Task[ServerConfig] = {
    ValidatedServerConfig(config) match {
      case Validated.Valid(serverConfig) =>
        Task(serverConfig)

      case Validated.Invalid(errors) =>
        Task.raiseError(ConfigurationFailure(errors))
    }
  }

  private def httpService(
    validatedConfig: ServerConfig
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Task[Unit] = {
    val http = validatedConfig.http
    val cores = CoreRoutes()
    val swagger = SwaggerRoutes(validatedConfig.swaggerPath)

    BaseHttpHandler(cores ~ swagger, http.host, http.port)
  }
}

// $COVERAGE-ON$
