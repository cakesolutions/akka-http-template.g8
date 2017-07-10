import com.typesafe.sbt.packager.archetypes.AshScriptPlugin
import net.cakesolutions._
import sbt._

/**
  * Common project Docker build settings.
  */
object ProjectDockerBuildPlugin extends AutoPlugin {

  /** @see [[sbt.AutoPlugin]] */
  override def requires: Plugins =
    ProjectPlugin &&
      CakeJavaAppPlugin &&
      AshScriptPlugin &&
      CakeDockerPlugin &&
      CakeDockerComposePlugin
}
