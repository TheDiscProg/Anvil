package io.github.thediscprog.anvil.i18n

import org.scalatest.funsuite.AnyFunSuite

import java.util.Locale

class AnvilMessageTest extends AnyFunSuite {

  test("should return message without placeholders") {
    val msg = AnvilMessage("anvil.mapping.conversionfailed")
    assert(msg == "Unable for {0} to convert JDBC object to Scala")
  }

  test("should replace single placeholder") {
    val msg = AnvilMessage("anvil.mapping.conversionfailed", Seq("UUID Reader"))
    assert(msg == "Unable for UUID Reader to convert JDBC object to Scala")
  }

  test("should use French locale when provided") {
    given Locale = Locale.FRENCH

    val msg = AnvilMessage("anvil.mapping.conversionfailed", Seq("UUID Reader"))
    assert(
      msg == "UUID Reader ne parviens pas à convertir lobjet JDBC en Scala"
    )
  }

  test(
    "should fallback to default messages.properties if locale file missing"
  ) {
    given Locale = Locale.UK // en_GB file does not exist

    val msg = AnvilMessage("anvil.mapping.conversionfailed", Seq("Long Reader"))
    assert(msg == "Unable for Long Reader to convert JDBC object to Scala")
  }

  test("should throw MissingResourceException for missing key") {
    val thrown = intercept[java.util.MissingResourceException] {
      AnvilMessage("non.existent.key")
    }
    assert(thrown.getMessage.contains("non.existent.key"))
  }

}
