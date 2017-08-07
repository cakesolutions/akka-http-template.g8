package $organisation_domain$.$organisation$.$name$.templating

import java.nio.file.Paths

import scala.io.Source

import org.scalatest.{Matchers, WordSpec}

class CakeNewCommandSpec extends WordSpec with Matchers {

  "CakeNewCommand" should {
    "reassign akka_template_version token" in {
      "$akka_template_version$" should not be "unknown"
    }

    "replace akka_template_version token in README.md file" in {
      import sys.process._
      val gitTopLevelPath = "git rev-parse --show-toplevel".!!.trim
      val readMePath = Paths.get(gitTopLevelPath, "$name$", "README.md")

      Source.fromFile(readMePath.toUri).getLines.mkString should include(
        "$akka_template_version$"
      )
    }
  }

}
