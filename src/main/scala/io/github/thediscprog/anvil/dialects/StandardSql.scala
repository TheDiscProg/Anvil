package io.github.thediscprog.anvil.dialects

class StandardSql extends SqlDialect {

  override val specialIdentifierCharacter: Char = '\"'

  override def filter(columns: String, table: String, where: String): String =
    s"SELECT $columns FROM $table $where"

  override def filterDistinct(
      columns: String,
      table: String,
      where: String
  ): String =
    s"SELECT DISTINCT $columns FROM $table $where"

  override def unique(columns: String, table: String, where: String): String =
    s"SELECT $columns FROM $table $where"

  override def insert(
      columns: String,
      table: String,
      parameters: Array[Any]
  ): String =
    s"INSERT INTO $table ($columns) VALUES (${parameters.map(_ => "?").mkString(",")})"

  override def update(columns: String, table: String, where: String): String =
    ???

  override def getANSIType(value: Any): String =
    value match
      case _: String             => "VARCHAR"
      case _: Int                => "INTEGER"
      case _: Long               => "BIGINT"
      case _: Double             => "DOUBLE"
      case _: Float              => "REAL"
      case _: Boolean            => "BOOLEAN"
      case _: BigDecimal         => "NUMERIC"
      case _: java.sql.Date      => "DATE"
      case _: java.sql.Time      => "TIME"
      case _: java.sql.Timestamp => "TIMESTAMP"
      case other                 => getDBSpecificType(value)

}
