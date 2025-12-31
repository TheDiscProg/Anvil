package io.github.thediscprog.anvil.macros

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.util.UUID
import java.time.LocalTime
import java.time.LocalDate
import java.time.LocalDateTime

class ProductMacroTest extends AnyFlatSpec, Matchers {

  it should "get a list of case class field label and type as a string" in {
    val labelsAndTypes = ProductMacro.getFieldLabelAndTypes[TestClass]

    labelsAndTypes shouldBe List(
      ("anInt", "Int"),
      ("aLong", "Long"),
      ("aShort", "Short"),
      ("aByte", "Byte"),
      ("aBoolean", "Boolean"),
      ("aChar", "Char"),
      ("aString", "String"),
      ("anID", "UUID"),
      ("time", "LocalTime"),
      ("date", "LocalDate"),
      ("dateTime", "LocalDateTime")
    )
  }

}

private case class TestClass(
    anInt: Int,
    aLong: Long,
    aShort: Short,
    aByte: Byte,
    aBoolean: Boolean,
    aChar: Char,
    aString: String,
    anID: UUID,
    time: LocalTime,
    date: LocalDate,
    dateTime: LocalDateTime
)
