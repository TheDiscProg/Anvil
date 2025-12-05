package io.github.thediscprog.anvil.adt

import java.sql.*
import java.time.*
import java.net.URL
import io.github.thediscprog.anvil.utilities.SqlConverter.*
import java.io.InputStream
import java.io.Reader
import io.github.thediscprog.anvil.dialects.SqlDialect

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
      case v: Option[?] =>
        v match
          case Some(v) => bindParameter(ptst, v, index, connection, dialect)
          case None    => ptst.setNull(index, Types.NULL)
      case v: List[?]     => bindCollection(ptst, v, index, connection, dialect)
      case v: InputStream => ptst.setBinaryStream(index, v)
      case v: Reader      => ptst.setCharacterStream(index, v)
      case v: URL         => ptst.setURL(index, v)
      case v: LocalDate   => ptst.setDate(index, convertDateToSql(v))
      case v: LocalTime   => ptst.setTime(index, convertTimeToSql(v))
      case v: LocalDateTime =>
        ptst.setTimestamp(index, convertTimestampToSql(v))
      case other => ptst.setObject(index, other)
  }

  def bindCollection(
      ptst: PreparedStatement,
      vals: List[?],
      index: Int,
      connection: Connection,
      dialect: SqlDialect
  ): Unit = {
    val array   = vals.toArray
    val sqlType = array.headOption match {
      case Some(value) => matchScalaToJDBC(value, dialect)
      case None        =>
        throw new IllegalArgumentException(
          "Cannot infer SQL type of empty list"
        )
    }
    val sqlArray = connection.createArrayOf(sqlType, array)
    ptst.setArray(index, sqlArray)
  }

  def matchScalaToJDBC(value: Any, dialect: SqlDialect): String =
    dialect.getANSIType(value)

}
