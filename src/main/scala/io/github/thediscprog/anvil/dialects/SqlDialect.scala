package io.github.thediscprog.anvil.dialects

trait SqlDialect {

  val specialIdentifierCharacter: Char

  def filter(columns: String, table: String, where: String): String

  def filterDistinct(columns: String, table: String, where: String): String

  def unique(columns: String, table: String, where: String): String

  def insert(columns: String, table: String, parameters: Array[Any]): String

  def update(columns: String, table: String, where: String): String

  def getANSIType(value: Any): String

  def getDBSpecificType(value: Any): String =
    throw new IllegalArgumentException(
      s"Unsupported database specific type: [${value.getClass()}]"
    )

}

object SqlDialect {

  def getDialect(vendor: DbVendor): SqlDialect = {
    vendor match
      case DbVendor.POSTGRESQL => new PostgresDialect()
      case DbVendor.MYSQL      => new MySqlDialect()
      case DbVendor.MARIADB    => new StandardSql()
      case DbVendor.ORACLE     => new StandardSql()
      case DbVendor.STANDARD   => new StandardSql()

  }
}
