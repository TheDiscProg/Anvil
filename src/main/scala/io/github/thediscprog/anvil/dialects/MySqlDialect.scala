package io.github.thediscprog.anvil.dialects

import java.sql.PreparedStatement
import io.github.thediscprog.anvil.jdbcutils.JDBCReader
import java.sql.Types
import io.github.thediscprog.anvil.jdbcutils.JDBCReader.{
  byteToUUIDReader,
  bitSetByteArrayReader,
  binaryReader
}
import scala.collection.immutable.BitSet
import java.util.{BitSet as JBitSet}
import io.github.thediscprog.anvil.jdbcutils.convertBitSet
import io.github.thediscprog.anvil.jdbcutils.JDBCReader.booleanReader

class MySqlDialect extends StandardSql {
  override val specialIdentifierCharacter: Char = '`'

  override def bindParameter(
      ptst: PreparedStatement,
      value: Any,
      index: Int
  ): Boolean = {
    value match
      case v: BitSet =>
        val jbitset = convertBitSet(v)
        ptst.setBytes(index, jbitset.toByteArray())
        true
      case v: JBitSet =>
        ptst.setBytes(index, v.toByteArray())
        true
      case v: Array[Byte] =>
        ptst.setBytes(index, v)
        true
      case _ => false
  }

  override def getDBTypeReader(
      jdbcType: Int,
      scalaFieldName: String,
      columnTypeName: String
  ): JDBCReader[?] | Boolean = {
    jdbcType match
      case Types.BIT =>
        if (scalaFieldName == "Boolean") {
          booleanReader
        } else {
          bitSetByteArrayReader
        }
      case Types.BINARY =>
        if (scalaFieldName == "UUID") {
          byteToUUIDReader
        } else {
          binaryReader
        }
      case _ => false

  }
}
