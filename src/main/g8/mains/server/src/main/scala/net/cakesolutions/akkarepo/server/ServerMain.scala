package net.cakesolutions.akkarepo.server

import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import com.typesafe.config.Config
import monix.reactive.Observable
import net.cakesolutions.akkarepo.core.api.BaseHttpHandler
import net.cakesolutions.akkarepo.core.application.{ApplicationBootstrapping, ApplicationGlobalContext}
import net.cakesolutions.akkarepo.core.application.workflow._
import net.cakesolutions.akkarepo.core.config.ConfigurationFailure
import net.cakesolutions.akkarepo.server.api.{CoreRoutes, SwaggerRoutes}
import net.cakesolutions.akkarepo.server.config.ValidatedServerConfig
import net.cakesolutions.akkarepo.server.config.ValidatedServerConfig.ServerConfig

// $COVERAGE-OFF$

/**
  * TODO:
  */
object ServerMain extends ApplicationBootstrapping {

  /** @inheritdoc */
  override protected def application(
    config: Config
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Workflow[Unit] = {
    validateConfig(config).flatMap(httpService)
  }

  private def validateConfig(
    config: Config
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Layer[ServerConfig] = {
    ValidatedServerConfig(config) match {
      case Validated.Valid(serverConfig) =>
        Layer(Observable(serverConfig))

      case Validated.Invalid(errors) =>
        Layer(Observable.raiseError(ConfigurationFailure(errors)))
    }
  }

  private def httpService(
    validatedConfig: ServerConfig
  )(
    implicit globalContext: ApplicationGlobalContext
  ): Layer[Unit] = {
    val http = validatedConfig.http
    val cores = CoreRoutes()
    val swagger = SwaggerRoutes(validatedConfig.swaggerPath)

    BaseHttpHandler(cores ~ swagger, http.host, http.port)
  }
}

// $COVERAGE-ON$
