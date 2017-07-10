package net.cakesolutions.akkarepo.core.application

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.github.levkhomich.akka.tracing.TracingExtensionImpl
import monix.execution.Scheduler

/**
  * Context that should be made available throughout the application. Doing
  * this simplifies code that is parameterised by application context members.
  *
  * @param log application logging adapter
  * @param scheduler application scheduler
  * @param system application's actor system
  * @param trace application's Zipkin tracing context
  */
final case class ApplicationGlobalContext(
  applicationName: String,
  log: LoggingAdapter,
  scheduler: Scheduler,
  system: ActorSystem,
  trace: TracingExtensionImpl
)
