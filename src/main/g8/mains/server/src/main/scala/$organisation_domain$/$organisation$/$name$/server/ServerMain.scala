package $organisation_domain$.$organisation$.$name$.server

import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import com.typesafe.config.Config
import monix.reactive.Observable
import $organisation_domain$.$organisation$.$name$.core.api.BaseHttpHandler
import $organisation_domain$.$organisation$.$name$.core.application.{ApplicationBootstrapping, ApplicationGlobalContext}
import $organisation_domain$.$organisation$.$name$.core.application.workflow._
import $organisation_domain$.$organisation$.$name$.core.config.ConfigurationFailure
import $organisation_domain$.$organisation$.$name$.server.api.{CoreRoutes, SwaggerRoutes}
import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig
import $organisation_domain$.$organisation$.$name$.server.config.ValidatedServerConfig.ServerConfig

// \$COVERAGE-OFF\$

/**
  * TODO:
  */
object ServerMain extends ApplicationBootstrapping {

  /** @see ApplicationBootstrapping */
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

// \$COVERAGE-ON\$
