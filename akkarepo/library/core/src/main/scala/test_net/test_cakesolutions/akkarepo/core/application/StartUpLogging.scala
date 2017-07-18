package test_net.test_cakesolutions.akkarepo.core.application

import java.lang.management.ManagementFactory

import scala.collection.JavaConverters._

import akka.event.LoggingAdapter
import com.typesafe.config.Config

/**
  * Defines standard logging of configuration, environment, properties and
  * system information.
  */
private object StartUpLogging {
  /**
    * Logs environment, JVM, properties and configurations data.
    * @param config Typesafe configuration object
    * @param log the logging adapter to which the logs will be sent
    */
  def apply(config: Config)(implicit log: LoggingAdapter): Unit = {
    logEnvironmentData()
    logJVMData()
    logPropertyData()
    logConfigurationData(config)
  }

  private def logEnvironmentData()(implicit log: LoggingAdapter): Unit = {
    sys.env.toList.sortBy(_._1).foreach {
      case (key, value) =>
        log.info(s"environment: $key=$value")
    }
  }

  private def logJVMData()(implicit log: LoggingAdapter): Unit = {
    val heap = ManagementFactory.getMemoryMXBean.getHeapMemoryUsage
    val nonHeap = ManagementFactory.getMemoryMXBean.getNonHeapMemoryUsage

    log.info(
      s"java.lang.memory.heap: committed=${pprintBytes(heap.getCommitted)}"
    )
    log.info(s"java.lang.memory.heap: initial=${pprintBytes(heap.getInit)}")
    log.info(s"java.lang.memory.heap: maximum=${pprintBytes(heap.getMax)}")
    log.info(s"java.lang.memory.heap: used=${pprintBytes(heap.getUsed)}")
    log.info(
      "java.lang.memory.non-heap: committed=" +
        pprintBytes(nonHeap.getCommitted)
    )
    log.info(
      s"java.lang.memory.non-heap: initial=${pprintBytes(nonHeap.getInit)}"
    )
    log.info(
      s"java.lang.memory.non-heap: maximum=${pprintBytes(nonHeap.getMax)}"
    )
    log.info(s"java.lang.memory.non-heap: used=${pprintBytes(nonHeap.getUsed)}")
    log.info(
      "runtime: available-processors=" +
        Runtime.getRuntime.availableProcessors().toString
    )
  }

  private def logPropertyData()(implicit log: LoggingAdapter): Unit = {
    sys.props.toList.sortBy(_._1).foreach {
      case (key, value) =>
        log.info(s"property: $key=$value")
    }
  }

  private def logConfigurationData(
    config: Config
  )(implicit
    log: LoggingAdapter
  ): Unit = {
    val configData = config.entrySet().asScala.toList.sortBy(_.getKey)
    for (entry <- configData) {
      log.info(s"configuration: ${entry.getKey}=${entry.getValue.unwrapped()}")
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def pprintBytes(byteValue: Long): String = {
    val unit = 1000
    if (byteValue < 0) {
      "undefined"
    } else if (byteValue < unit) {
      s"${byteValue}B"
    } else {
      val exp = (Math.log(byteValue.toDouble) / Math.log(unit.toDouble)).toInt
      val pre = "kMGTPE".charAt(exp - 1)
      f"${byteValue / Math.pow(unit.toDouble, exp.toDouble)}%.1f${pre}B"
    }
  }
}
