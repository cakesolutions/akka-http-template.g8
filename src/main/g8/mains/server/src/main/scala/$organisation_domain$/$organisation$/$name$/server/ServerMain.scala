package $organisation_domain$.$organisation$.$name$.server

import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import com.typesafe.config.Config
import monix.eval.Task

import $organisation_domain$.$organisation$.$name$.core.api.BaseHttpHandler
import $organisation_domain$.$organisation$.$name$.core.application.{ApplicationBootstrapping, ApplicationGlobalContext}
import $organisation_domain$.$organisation$.$name$.core.config.ConfigurationFailure
import $organisation_domain$.$organisation$.$name$.server.api.{CoreRoutes, SwaggerRoutes}
import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig
import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig.ServerConfig

// \$COVERAGE-OFF\$

/**
  * Service boostrapping.
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

// \$COVERAGE-ON\$
