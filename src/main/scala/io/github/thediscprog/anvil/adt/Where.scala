package io.github.thediscprog.anvil.adt

trait Where

case class AND(clause: List[KeyValue[?]]) extends Where

case class OR(clause: List[KeyValue[?]]) extends Where

case class IN[T](columnName: String, values: List[T]) extends Where
