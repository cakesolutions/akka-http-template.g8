package $organisation_domain$.$organisation$.$name$.templating

import scala.io.Source

import org.scalatest.{Matchers, WordSpec}

class CakeNewCommandSpec extends WordSpec with Matchers {

  "CakeNewCommand" should {
    "reassign akka_template_version token" in {
      "$akka_template_version$" should not be "unknown"
    }

    "replace akka_template_version token in README.md file" in {
      Source.fromFile("../../README.md").getLines.mkString should include(
        "$akka_template_version$"
      )
    }
  }

}
