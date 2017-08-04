import sbt.{Def, _}
import sbt.complete.Parser
import sbtdynver.DynVerPlugin
import sbtdynver.DynVerPlugin.autoImport._

import Keys._
import complete.DefaultParsers._

object CakeNewCommand extends sbt.AutoPlugin {

  import CajeNewCommandUtil._

  override val requires: Plugins = DynVerPlugin
  override val trigger: PluginTrigger = allRequirements

  def cakeNewCommand(version: String): Command =
    Command(CommandName, BriefHelp, TemplateDetailed)(newCakeCommandParser)(runNewCake(version))

  private def newCakeCommandParser(state: State): Parser[Seq[String]] =
    (token(Space) ~> repsep(StringBasic, token(Space))) | (token(EOF) map (_ => Nil))

  private def runNewCake(version: String)(state: State, inputArg: Seq[String]): State = {
    val args = if(inputArg.nonEmpty) inputArg else state.remainingCommands
    val newSate = state.copy(
      remainingCommands = args :+ s"--akka_template_version=$version"
    )
    State.stateOps(newSate).::("new")
  }

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    commands += cakeNewCommand(dynverGitDescribeOutput.value.fold("NaN")(_.version))
  )

}

object CajeNewCommandUtil {

  val CommandName: String = "cakeNew"

  val BriefHelp: (String, String) = CommandName -> "Creates a new sbt build."

  val TemplateDetailed: String = CommandName + """ [--options] <template>
  Reassign `akka_template_version` token and create a new sbt build based on the given template."""

}