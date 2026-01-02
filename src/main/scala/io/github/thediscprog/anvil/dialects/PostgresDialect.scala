package io.github.thediscprog.anvil.dialects

import java.sql.PreparedStatement
import java.sql.Types
import io.github.thediscprog.anvil.jdbcutils.JDBCReader
import io.github.thediscprog.anvil.jdbcutils.JDBCReader.*

class PostgresDialect extends StandardSql {
  private val debug = false

  override def bindParameter(
      ptst: PreparedStatement,
      value: Any,
      index: Int
  ): Boolean = {
    // PostgreSQL does provide ParameterMetaData
    val metadata       = ptst.getParameterMetaData()
    val colType        = metadata.getParameterType(index)
    val columnTypeName = metadata.getParameterTypeName(index)
    if (debug) then
      println(
        s"Binding [$value] (Index: $index) using column type: [$colType] & column name [$columnTypeName]"
      )

    value match
      case v: Double =>
        columnTypeName match
          case "money" =>
            ptst.setBigDecimal(index, new java.math.BigDecimal(v.toShort))
            true
          case _ => false
      case v: String =>
        columnTypeName match
          case "xml" =>
            ptst.setObject(index, v, Types.SQLXML)
            true
          case "tsvector" | "tsquery" | "xid" | "xid8" | "txid_snapshot" |
              "pg_lsn" =>
            ptst.setObject(index, v, Types.OTHER)
            true
          case "point" | "line" | "lseg" | "box" | "path" | "polygon" |
              "circle" =>
            ptst.setObject(index, v, Types.OTHER)
            true
          case _ => false
      case _ => false
  }

  override def getDBTypeReader(
      jdbcType: Int,
      scalaFieldName: String,
      columnTypeName: String
  ): JDBCReader[?] | Boolean = {
    jdbcType match
      case Types.BIT =>
        if (scalaFieldName == "Boolean" || columnTypeName == "bool")
          booleanReader
        else
          bitSetReader
      case Types.OTHER =>
        columnTypeName match
          case "tsvector" | "tsquery" => stringReader
          case "xid" | "xid8" | "cid" | "txid_snapshot" | "pg_lsn" =>
            stringReader // Postgres internal/system types
          case "point" | "line" | "lseg" | "box" | "path" | "polygon" |
              "circle" =>
            stringReader // Postgres geometric
          case "citext" => stringReader
          case _        => false
      case _ => false
  }
}
