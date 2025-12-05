package io.github.thediscprog.anvil.adt

enum Operand(val sign: String):
  case EQ           extends Operand("=")
  case LESS_THAN    extends Operand("<")
  case GREATER_THAN extends Operand(">")
  case LIKE         extends Operand(" LIKE ")
