package io.github.thediscprog.anvil.adt
import io.github.thediscprog.anvil.dialects.DbVendor

final case class TableProperties(
    table: String,
    isNamingSpecial: Boolean,
    cachingKey: String,
    dialect: DbVendor,
    columnNames: Seq[String]
)
