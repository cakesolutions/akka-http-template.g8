package $organisation_domain$.$organisation$.$name$.application

import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.duration._
import scala.concurrent.{Future, blocking}
import scala.sys.ShutdownHookThread
import scala.util.Try
import scala.util.control.NonFatal

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import cakesolutions.config.ValueError
import cats.data.NonEmptyList
import com.github.levkhomich.akka.tracing.{TracingExtension, TracingExtensionImpl}
import com.typesafe.config.{Config, ConfigFactory}
import monix.eval.Task
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.Observable

import $organisation_domain$.$organisation$.$name$.utils.ValueDiscard

/**
  * CommonMain helpers and utilities.
  */
object CommonMain {

  /**
    * TODO:
    *
    * @param errors
    */
  final case class ConfigurationFailure(
    errors: NonEmptyList[ValueError]
  ) extends Exception {
    override def toString: String = {
      s"ConfigurationFailure(\${errors.toList.mkString(",")})"
    }
  }

  /**
    * TODO:
    *
    * @param workflow
    * @param cleanup
    */
  final case class ApplicationBootstrapping(
    workflow: Observable[Unit],
    cleanup: Task[Unit]
  )
}

/**
  * Abstract class defining a common start, stop, logging and recovery sequence
  * for **all** applications.
  *
  * Application exit codes:
  *   - 0 => normal termination of application
  *   - 1 => application terminated due to an exception
  *   - 2 => (BUG!) application terminated due to an unhandled non-fatal
  *     exception
  *   - 3 => application terminated due to a fatal exception
  *   - 4 => application terminated due to an OS signal
  *
  * System exits should **only** be executed within this abstract class!
  */
abstract class CommonMain {
  private[this] val systemExit: AtomicBoolean = new AtomicBoolean(false)

  /**
    * Main application (bootstrapping) workflow. Implementing code can define
    * complex bootstrapping (complete with retry logic) by implementing
    * functions of type:
    *   ValidatedConfig => In => ApplicationContext => ApplicationState[Out]
    * where ValidatedConfig, In and Out are type parameters.
    *
    * @param config loaded Typesafe configuration object
    * @param context application context
    * @return application's bootstrapping workflow and resource cleanup actions
    */
  protected def application(
    config: Config
  )(implicit
    context: ApplicationContext
  ): ApplicationBootstrapping

  /**
    * Application entrypoint.
    *
    * @param args command line arguments
    */
  def main(args: Array[String]): Unit = {
    bootstrap()
  }

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private def bootstrap(): Unit = {
    // TODO: CO-25: Ensure *all* configuration is fully validated
    val config = ConfigFactory.load("application.conf")
    val applicationName =
      getClass.getSimpleName.filter(_.toString.matches("[a-zA-Z0-9]"))

    implicit val system = ActorSystem(applicationName, config)
    implicit val scheduler = Scheduler(system.dispatcher)
    implicit val trace = TracingExtension(system)
    implicit val log = system.log

    val context =
      ApplicationContext(applicationName, log, scheduler, system, trace)

    uncaughtExceptionHandler(applicationName)
    jvmShutdownHandler()

    log.info(s"Application \$applicationName started")

    // TODO: CO-111: Generate startup log information

    ValueDiscard[Cancelable] {
      val appState = application(config)(context)

      appState
        .workflow
        .doAfterTerminateEval {
          case None =>
            log.info(s"Application \$applicationName shutting down normally")
            systemExit.set(true)
            appState.cleanup.doOnFinish(_ => Task(sys.exit(0)))
          case Some(exn) =>
            log.error(
              exn,
              s"Application \$applicationName shutting down due to an error"
            )
            systemExit.set(true)
            appState.cleanup.doOnFinish(_ => Task(sys.exit(1)))
        }
        .subscribe()
    }
  }

  private def uncaughtExceptionHandler(
    applicationName: String
  )(implicit
    log: LoggingAdapter
  ): Unit = {
    Thread.setDefaultUncaughtExceptionHandler(
      new Thread.UncaughtExceptionHandler {
        def uncaughtException(thread: Thread, exn: Throwable): Unit =
          exn match {
            case NonFatal(_) =>
              log.error(
                exn,
                "BUG: non-fatal exception should have been handled by " +
                  s"application \$applicationName!"
              )
              systemExit.set(true)
              sys.exit(2)
            case _: Throwable =>
              log.error(
                exn,
                s"Fatal exception thrown by application \$applicationName"
              )
              systemExit.set(true)
              sys.exit(3)
          }
      }
    )
  }

  // scalastyle:off magic.number
  // scalastyle:off while
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private def jvmShutdownHandler(
  )(implicit
    log: LoggingAdapter,
    system: ActorSystem,
    trace: TracingExtensionImpl
  ): Unit = {
    ValueDiscard[ShutdownHookThread] {
      sys.addShutdownHook {
        // We use the global execution context since we wish to terminate the
        // actor system!
        import scala.concurrent.ExecutionContext.Implicits.global

        // Docker stopping time window is 10 seconds
        val deadline = 10.seconds.fromNow
        val terminationFuture = system.terminate()

        blocking {
          while (!terminationFuture.isCompleted && deadline.hasTimeLeft()) {
            Thread.sleep(50.milliseconds.toMillis)
          }
        }
        if (!systemExit.get()) {
          // OS signalled termination
          Runtime.getRuntime.halt(4)
        }
      }
    }
  }
  // scalastyle:on magic.number
  // scalastyle:on while
}
