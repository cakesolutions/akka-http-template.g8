package $package_structure$.server

import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import com.typesafe.config.Config
import monix.eval.Task

import $package_structure$.core.api.BaseHttpHandler
import $package_structure$.core.application.{ApplicationBootstrapping, ApplicationGlobalContext}
import $package_structure$.core.config.ConfigurationFailure
import $package_structure$.server.api.{CoreRoutes, SwaggerRoutes}
import $package_structure$.server.config.ValidatedServerConfig
import $package_structure$.server.config.ValidatedServerConfig.ServerConfig

// \$COVERAGE-OFF\$

/**
  * Service bootstrapping.
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
