package io.github.thediscprog.anvil.jdbcutils

import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import scala.reflect.ClassTag
import java.util.UUID
import io.github.thediscprog.anvil.jdbcutils.JDBCConverter
import java.time.LocalTime
import java.io.InputStream
import java.io.Reader
import java.util.BitSet
import java.{util => ju}
import java.time.OffsetTime
import java.time.OffsetDateTime
import io.github.thediscprog.anvil.dialects.SqlDialect
import JDBCConverter.{
  byteToUUIDConverter,
  bitSetByteArrayConverter,
  binaryConverter
}

trait JDBCReader[T](using val converter: JDBCConverter[T]) {

  def readFromDb(index: Int, rs: ResultSet): T

  def readNullable(index: Int, rs: ResultSet): Option[T] =
    Option(readFromDb(index, rs))

  def readArray(
      columnName: String,
      index: Int,
      isNullable: Boolean,
      rs: ResultSet,
      reader: JDBCReaderSelector,
      dialect: SqlDialect
  ): List[Any] | Option[List[Any]] = {
    if (isNullable) {
      val sqlArray = Option(rs.getArray(index))
      sqlArray.map(array => readArrayValues(columnName, array, reader, dialect))
    } else {
      readArrayValues(columnName, rs.getArray(index), reader, dialect)
    }
  }

  private def readArrayValues(
      columnName: String,
      sqlArray: java.sql.Array,
      reader: JDBCReaderSelector,
      dialect: SqlDialect
  ): List[Any] = {
    val scalaArray = sqlArray.getArray().asInstanceOf[Array[Any]]
    val baseReader = reader.getJdbcReader(
      columnName,
      sqlArray.getBaseType(),
      "ARRAY",
      "",
      dialect
    )
    scalaArray.toList.map(baseReader.converter.convertToScala)
  }

}

object JDBCReader {

  val byteReader = new JDBCReader[Byte] {
    override def readFromDb(index: Int, rs: ResultSet): Byte = rs.getByte(index)

    override def toString(): String = "Byte Reader"
  }

  val shortReader = new JDBCReader[Short] {
    override def readFromDb(index: Int, rs: ResultSet): Short =
      rs.getShort(index)

    override def toString(): String = "Short Reader"
  }

  val intReader = new JDBCReader[Int]() {
    override def readFromDb(index: Int, rs: ResultSet): Int =
      rs.getInt(index)

    override def toString(): String = "Int Reader"
  }

  val longReader = new JDBCReader[Long]() {
    def readFromDb(index: Int, rs: ResultSet): Long =
      rs.getLong(index)

    override def toString(): String = "Long Reader"
  }

  val floatReader = new JDBCReader[Float]() {
    def readFromDb(index: Int, rs: ResultSet): Float =
      rs.getFloat(index)

    override def toString(): String = "Float Reader"
  }

  val doubleReader = new JDBCReader[Double] {
    def readFromDb(index: Int, rs: ResultSet): Double =
      rs.getDouble(index)

    override def toString(): String = "Double Reader"
  }

  val bigDecimalReader = new JDBCReader[BigDecimal] {
    def readFromDb(index: Int, rs: ResultSet): BigDecimal =
      rs.getBigDecimal(index)

    override def toString(): String = "BigDecimal Reader"
  }

  val charReader = new JDBCReader[Char] {
    def readFromDb(index: Int, rs: ResultSet): Char =
      getObjectAs[Char](index, rs)

    override def toString(): String = "Char Reader"
  }

  val stringReader = new JDBCReader[String]() {
    def readFromDb(index: Int, rs: ResultSet): String =
      rs.getString(index)

    override def toString(): String = "String Reader"
  }

  val dateReader = new JDBCReader[LocalDate]() {
    override def readFromDb(index: Int, rs: ResultSet): LocalDate =
      getObjectAs[LocalDate](index, rs)

    override def toString(): String = "LocalDate Reader"
  }

  val timeReader = new JDBCReader[LocalTime] {
    override def readFromDb(index: Int, rs: ResultSet): LocalTime =
      getObjectAs[LocalTime](index, rs)

    override def toString(): String = "LocalTime Reader"
  }

  val dateTimeStampReader = new JDBCReader[LocalDateTime]() {
    override def readFromDb(index: Int, rs: ResultSet): LocalDateTime =
      getObjectAs[LocalDateTime](index, rs)

    override def toString(): String = "LocalDateTime Reader"
  }

  val offsetTimeReader = new JDBCReader[OffsetTime] {

    override def readFromDb(index: Int, rs: ResultSet): OffsetTime =
      getObjectAs[OffsetTime](index, rs)

    override def toString(): String = "Offset Time Reader"
  }

  val offsetDateTimeStampReader = new JDBCReader[OffsetDateTime] {
    override def readFromDb(index: Int, rs: ResultSet): OffsetDateTime =
      getObjectAs[OffsetDateTime](index, rs)

    override def toString(): String = "Offset Datetime Reader"
  }

  val byteArrayReader = new JDBCReader[Array[Byte]] {
    override def readFromDb(index: Int, rs: ResultSet): Array[Byte] =
      rs.getBytes(index)

    override def toString(): String = "Arry[Byte] Reader"
  }

  val objectReader = new JDBCReader[Any]() {
    override def readFromDb(index: Int, rs: ResultSet): Any = {
      val colType     = rs.getMetaData().getColumnType(index)
      val colTypeName = rs.getMetaData().getColumnTypeName(index)
      if (printDebugInfo) then
        println(
          s"Object Reader reading [$colType] [$colTypeName] [${rs.getObject(index)}]"
        )
      rs.getObject(index).asInstanceOf[Any]
    }

    override def toString(): String = "Object Reader"
  }

  val uuidReader = new JDBCReader[UUID]() {

    override def readFromDb(index: Int, rs: ResultSet): UUID =
      getObjectAs[UUID](index, rs)

    override def toString(): String = "UUID Reader"
  }

  val byteToUUIDReader = new JDBCReader[UUID](using byteToUUIDConverter) {

    override def readFromDb(index: Int, rs: ResultSet): ju.UUID = {
      bytesToUUID(rs.getBytes(index))
    }
  }

  val unitReader = new JDBCReader[Unit] {
    override def readFromDb(index: Int, rs: ResultSet): Unit = ()

    override def toString(): String = "Unit/NULL Reader"
  }

  val arrayReader = new JDBCReader[Array[Any]] {

    override def readFromDb(index: Int, rs: ResultSet): Array[Any] =
      rs.getArray(index).getArray().asInstanceOf[Array[Any]]

    override def toString(): String = "Array[Any] Reader"
  }

  val binaryReader = new JDBCReader[Array[Byte]](using binaryConverter) {
    override def readFromDb(index: Int, rs: ResultSet): Array[Byte] = {
      rs.getBytes(index)
    }

    override def toString(): String = "Binary to Array[Byte] Reader"
  }

  val blobReader = new JDBCReader[InputStream] {
    override def readFromDb(index: Int, rs: ResultSet): InputStream =
      rs.getBlob(index).getBinaryStream()

    override def toString(): String = "InputStream Reader"
  }

  val clobReader = new JDBCReader[Reader] {
    override def readFromDb(index: Int, rs: ResultSet): Reader =
      rs.getClob(index).getCharacterStream()

    override def toString(): String = "Reader Stream Reader"
  }

  val booleanReader = new JDBCReader[Boolean] {
    override def readFromDb(index: Int, rs: ResultSet): Boolean =
      rs.getBoolean(index)

    override def toString(): String = "Boolean Reader"
  }

  val nStringReader = new JDBCReader[String] {
    override def readFromDb(index: Int, rs: ResultSet): String =
      rs.getNString(index)

    override def toString(): String = "NString/String Reader"
  }

  /** This is a default BIT reader, but it will not work with all databases.
    * PostgreSQL Bit
    */
  val bitSetReader = new JDBCReader[BitSet] {
    override def readFromDb(index: Int, rs: ResultSet): ju.BitSet = {
      val bitStr = rs.getString(index)
      ju.BitSet.valueOf(Array(java.lang.Long.parseLong(bitStr, 2)))
    }

    override def toString(): String = "BitSet Reader"
  }

  /** MySQL Bit Reader
    */
  val bitSetByteArrayReader =
    new JDBCReader[BitSet](using bitSetByteArrayConverter) {
      override def readFromDb(index: Int, rs: ResultSet): ju.BitSet = {
        val bytes = rs.getBytes(index)
        ju.BitSet.valueOf(bytes)
      }

      override def toString(): String = "BitSet to ByteArray Reader"
    }

  private transparent inline def getObjectAs[V: ClassTag](
      index: Int,
      rs: ResultSet
  ): V = {
    val cls = summon[ClassTag[V]].runtimeClass.asInstanceOf[Class[V]]
    rs.getObject(index, cls)
  }

}
