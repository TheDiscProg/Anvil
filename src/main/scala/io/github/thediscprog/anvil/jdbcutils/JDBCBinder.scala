package io.github.thediscprog.anvil.jdbcutils

import java.sql.*
import java.time.*
import java.net.URL
import java.io.InputStream
import java.io.Reader
import io.github.thediscprog.anvil.dialects.SqlDialect
import scala.collection.immutable.BitSet
import java.util.{BitSet as JBitSet}

object JDBCBinder {

  def bindParameters(
      ptst: PreparedStatement,
      params: List[Any],
      connection: Connection,
      dialect: SqlDialect
  ): Unit = {
    params.zipWithIndex.map { (value, idx) =>
      bindParameter(ptst, value, idx + 1, connection, dialect)
    }
  }

  def bindParameter(
      ptst: PreparedStatement,
      value: Any,
      index: Int,
      connection: Connection,
      dialect: SqlDialect
  ): Unit = {
    val isVendorBinding = dialect.bindParameter(ptst, value, index)
    if (printDebugInfo) then
      println(s"Binding parameter index: $index: [$isVendorBinding]")
    if (!isVendorBinding) {
      value match
        case v: Array      => ptst.setArray(index, v)
        case v: BigDecimal => ptst.setBigDecimal(index, v.bigDecimal)
        case v: Blob       => ptst.setBlob(index, v)
        case v: Boolean    => ptst.setBoolean(index, v)
        case v: Byte       => ptst.setByte(index, v)
        case v: NClob      => ptst.setNClob(index, v)
        case v: Clob       => ptst.setClob(index, v)
        case v: Date       => ptst.setDate(index, v)
        case v: Double     => ptst.setDouble(index, v)
        case v: Float      => ptst.setFloat(index, v)
        case v: Int        => ptst.setInt(index, v)
        case v: Long       => ptst.setLong(index, v)
        case v: String     => ptst.setString(index, v)
        case null          => ptst.setNull(index, Types.NULL)
        case v: Ref        => ptst.setRef(index, v)
        case v: RowId      => ptst.setRowId(index, v)
        case v: Short      => ptst.setShort(index, v)
        case v: SQLXML     => ptst.setSQLXML(index, v)
        case v: Time       => ptst.setTime(index, v)
        case v: Timestamp  => ptst.setTimestamp(index, v)
        // non java.sql types that are supported
        case Some(value) =>
          bindParameter(ptst, value, index, connection, dialect)
        case None       => ptst.setNull(index, Types.NULL)
        case v: List[?] => bindCollection(ptst, v, index, connection, dialect)
        case v: InputStream   => ptst.setBinaryStream(index, v)
        case v: Reader        => ptst.setCharacterStream(index, v)
        case v: URL           => ptst.setURL(index, v)
        case v: LocalDate     => ptst.setObject(index, v)
        case v: LocalTime     => ptst.setObject(index, v)
        case v: LocalDateTime => ptst.setObject(index, v)
        case v: BitSet        =>
          val s = bitSetConverter(convertBitSet(v))
          ptst.setObject(index, s, Types.OTHER)
        case v: JBitSet =>
          val s = bitSetConverter(v)
          ptst.setObject(index, s, Types.OTHER)
        case other =>
          ptst.setObject(index, other, Types.OTHER)
    }
  }

  private def bitSetConverter(bs: JBitSet): String = {
    val sb  = new StringBuilder
    val len = bs.length()
    for (i <- 0 until len) {
      sb.append(if (bs.get(i)) '1' else '0')
    }
    sb.toString()
  }

  def bindCollection(
      ptst: PreparedStatement,
      vals: List[?],
      index: Int,
      connection: Connection,
      dialect: SqlDialect
  ): Unit = {
    val sqlType = vals.headOption match {
      case Some(value) => matchScalaToJDBC(value, dialect)
      case None        =>
        throw new IllegalArgumentException(
          "Cannot infer SQL type of empty list"
        )
    }
    val sqlArray = connection.createArrayOf(sqlType, vals.toArray)
    ptst.setArray(index, sqlArray)
  }

  def matchScalaToJDBC(value: Any, dialect: SqlDialect): String =
    dialect.getANSIType(value)

}
