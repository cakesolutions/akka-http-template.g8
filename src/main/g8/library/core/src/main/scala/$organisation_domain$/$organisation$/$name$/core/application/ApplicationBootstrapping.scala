package $package_structure$.core.application

import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.blocking
import scala.concurrent.duration._
import scala.sys.ShutdownHookThread
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.github.levkhomich.akka.tracing.{TracingExtension, TracingExtensionImpl}
import com.typesafe.config.{Config, ConfigFactory}
import monix.eval.Task
import monix.execution.{Cancelable, Scheduler}
import org.slf4j.LoggerFactory

import $package_structure$.core.utils.ValueDiscard

// \$COVERAGE-OFF\$

/**
  * Trait defining a common start, stop, logging and recovery sequence for
  * **all** applications.
  *
  * Application exit codes:
  *   - 1 => application terminated due to a bootstrapping exception
  *   - 2 => (BUG!) application terminated due to an unhandled non-fatal
  *     exception
  *   - 3 => application terminated due to a fatal exception
  *   - 4 => application terminated due to an OS signal or (BUG!) an illicit
  *     use of sys.exit
  *
  * System exits should **only** be executed within this trait!
  */
trait ApplicationBootstrapping {
  private[this] val systemExitAllowed: AtomicBoolean = new AtomicBoolean(false)

  /**
    * Task defining how an application will bootstrap itself. Should a task
    * return an error, then retry strategies may be applied to add some
    * additional resilience.
    *
    * @param config unvalidate Typesafe configuration object that the
    *   application will use
    * @param globalContext global context that the application will use
    * @return task defining how an application will bootstrap itself
    */
  protected def application(
    config: Config
  )(implicit globalContext: ApplicationGlobalContext): Task[Unit]

  /**
    * Application entrypoint.
    *
    * @param args command line arguments
    */
  final def main(args: Array[String]): Unit = {
    bootstrap()
  }

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private def bootstrap(): Unit = {
    val applicationName =
      getClass.getName.filter(_.toString.matches("[a-zA-Z0-9]"))

    // TODO: CO-???: support other configuration sources - eg. consul, etcd,..
    // TODO: CO-25: Ensure *all* configuration is fully validated
    Try(ConfigFactory.load("application.conf")) match {
      case Failure(exn) =>
        val log = LoggerFactory.getLogger(getClass)
        log.error(
          "Unable to load configuration data - application " +
            s"\$applicationName shutting down",
          exn
        )
        systemExitAllowed.set(true)
        sys.exit(1)

      case Success(config) =>
        implicit val system: ActorSystem = ActorSystem(applicationName, config)
        implicit val scheduler: Scheduler = Scheduler(system.dispatcher)
        implicit val trace: TracingExtensionImpl = TracingExtension(system)
        implicit val log: LoggingAdapter = system.log

        val globalContext =
          ApplicationGlobalContext(
            applicationName,
            log,
            scheduler,
            system,
            trace
          )

        uncaughtExceptionHandler(applicationName)
        jvmShutdownHandler()

        StartUpLogging(config)

        ValueDiscard[Cancelable] {
          application(config)(globalContext)
            .runOnComplete(_ match {
              case Success(_) =>
                log.info(s"Application \$applicationName started")
              case Failure(exn) =>
                log.error(
                  exn,
                  s"Application \$applicationName shutting down due to an error"
                )
                systemExitAllowed.set(true)
                sys.exit(1)
            })
        }
    }
  }

  private def uncaughtExceptionHandler(
    applicationName: String
  )(implicit log: LoggingAdapter): Unit = {
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
              systemExitAllowed.set(true)
              sys.exit(2)
            case _: Throwable =>
              log.error(
                exn,
                s"Fatal exception thrown by application \$applicationName"
              )
              systemExitAllowed.set(true)
              sys.exit(3)
          }
      }
    )
  }

  // scalastyle:off magic.number
  // scalastyle:off while
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private def jvmShutdownHandler()(
    implicit log: LoggingAdapter,
    system: ActorSystem,
    trace: TracingExtensionImpl
  ): Unit = {
    ValueDiscard[ShutdownHookThread] {
      sys.addShutdownHook {
        // Docker stopping time window is 10 seconds
        val deadline = 10.seconds.fromNow
        val terminationFuture = system.terminate()

        blocking {
          while (!terminationFuture.isCompleted && deadline.hasTimeLeft()) {
            Thread.sleep(50.milliseconds.toMillis)
          }
        }
        if (!systemExitAllowed.get()) {
          // OS signalled termination or illicit use of sys.exit
          Runtime.getRuntime.halt(4)
        }
      }
    }
  }
  // scalastyle:on magic.number
  // scalastyle:on while
}

// \$COVERAGE-ON\$
