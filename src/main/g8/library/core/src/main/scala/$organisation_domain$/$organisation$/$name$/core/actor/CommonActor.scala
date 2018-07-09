package $package_structure$.core.actor

import com.github.levkhomich.akka.tracing.{ActorTracing, TracingActorLogging}

/**
  * Common trait that all actors should extend. MDC logging and Zipkin tracing
  * are made available with lightweight actor lifecycle monitoring/logging.
  */
trait CommonActor extends ActorTracing with TracingActorLogging {

  /** @see http://doc.akka.io/api/akka/current/akka/actor/Actor.html */
  override def preStart(): Unit = {
    log.info("Actor starting")
    super.preStart()
  }

  /** @see http://doc.akka.io/api/akka/current/akka/actor/Actor.html */
  override def preRestart(exn: Throwable, msg: Option[Any]): Unit = {
    log.error(exn, s"Actor restarting - message: \$msg")
    super.preRestart(exn, msg)
  }

  /** @see http://doc.akka.io/api/akka/current/akka/actor/Actor.html */
  override def postStop(): Unit = {
    log.info("Actor stopping")
    super.postStop()
  }

  /** @see http://doc.akka.io/api/akka/current/akka/actor/Actor.html */
  override def unhandled(msg: Any): Unit = {
    log.warning(s"Unhandled message: \$msg")
    super.unhandled(msg)
  }

}
