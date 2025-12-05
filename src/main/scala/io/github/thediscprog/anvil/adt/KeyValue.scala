package io.github.thediscprog.anvil.adt

case class KeyValue[T](key: String, value: T, operand: Operand = Operand.EQ)
