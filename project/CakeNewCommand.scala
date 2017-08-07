// Copyright: 2017 https://github.com/cakesolutions/akka-http-template.g8/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0

import sbt.{Def, _}
import sbt.complete.Parser
import sbtdynver.DynVerPlugin
import sbtdynver.DynVerPlugin.autoImport._

import Keys._
import complete.DefaultParsers._

/**
  * It creates the `cakeNew` sbt command which calls `sbt new` with a
  * dynamically calculated `akka_template_version` template token.
  */
object CakeNewCommand extends sbt.AutoPlugin {

  import CakeNewCommandUtil._

  /**
    * This AutoPlugin requires the plugins the Plugins matcher returned by this
    * method.
    * @see http://www.scala-sbt.org/0.13/api/#sbt.AutoPlugin
    */
  override val requires: Plugins = DynVerPlugin

  /**
    * Determines whether this AutoPlugin will be activated for this project when
    * the requires clause is satisfied.
    * @see http://www.scala-sbt.org/0.13/api/#sbt.AutoPlugin
    */
  override val trigger: PluginTrigger = allRequirements

  private def cakeNewCommand(version: String): Command =
    Command(CommandName, BriefHelp, TemplateDetailed)(
      newCakeCommandParser)(runNewCake(version))

  private def newCakeCommandParser(state: State): Parser[Seq[String]] =
    (token(Space) ~> repsep(StringBasic, token(Space))) | (token(EOF) map (_ => Nil))

  private def runNewCake(version: String)(state: State, inputArg: Seq[String]): State = {
    val args = if(inputArg.nonEmpty) inputArg else state.remainingCommands
    val newSate = state.copy(
      remainingCommands = args :+ s"--$Token=$version"
    )
    State.stateOps(newSate).::("new")
  }

  /**
    * The Settings to add in the scope of each project that activates this
    * AutoPlugin.
    * @see http://www.scala-sbt.org/0.13/api/#sbt.AutoPlugin
    */
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    commands += cakeNewCommand(
      dynverGitDescribeOutput.value.fold("unknown")(_.version))
  )

  private object CakeNewCommandUtil {

    val CommandName: String = "cakeNew"

    val BriefHelp: (String, String) = CommandName -> "Creates a new sbt build."

    val TemplateDetailed: String = CommandName + """ [--options] <template>
      Reassign `akka_template_version` token and create a new sbt build based on the given template."""

    val Token: String = "akka_template_version"

  }

}
