package io.github.thediscprog.anvil.jdbcutils

trait JDBCNullable

object JDBCNullable {
  case object NULLABLE    extends JDBCNullable
  case object NONNULLABLE extends JDBCNullable
}
