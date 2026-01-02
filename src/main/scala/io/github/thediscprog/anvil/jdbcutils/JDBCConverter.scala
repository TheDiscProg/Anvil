package io.github.thediscprog.anvil.jdbcutils

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import java.time.LocalTime
import java.io.InputStream
import java.io.Reader
import java.util.BitSet
import java.time.OffsetTime
import java.time.OffsetDateTime
import java.{util => ju}

trait JDBCConverter[T] {

  def convertToScala(value: Any): T

}

object JDBCConverter {

  given booleanConverter: JDBCConverter[Boolean] with {

    override def toString(): String = "Boolean Coverter"

    def convertToScala(value: Any): Boolean = value.asInstanceOf[Boolean]
  }

  given charConverter: JDBCConverter[Char] with {

    override def toString(): String = "Char Converter"

    def convertToScala(value: Any): Char = value.asInstanceOf[Char]
  }

  given stringConverter: JDBCConverter[String] with {

    override def toString(): String = "String Converter"

    def convertToScala(value: Any): String = value.asInstanceOf[String]
  }

  given byteConverter: JDBCConverter[Byte] with {

    override def toString(): String = "Byte Converter"

    override def convertToScala(value: Any): Byte = value.asInstanceOf[Byte]
  }

  given shortConverter: JDBCConverter[Short] with {

    override def toString(): String = "Short Converter"

    override def convertToScala(value: Any): Short = value.asInstanceOf[Short]
  }

  given integerConverter: JDBCConverter[Int] with {

    override def toString(): String = "Int Converter"

    def convertToScala(value: Any): Int = value.asInstanceOf[Int]
  }

  given longConverter: JDBCConverter[Long] with {

    override def toString(): String = "Long Converter"

    def convertToScala(value: Any): Long = {
      value match
        case l: Long => l
        case _       =>
          throw new RuntimeException(
            s"JDBC ${{ toString() }} Unable to convert from JDBC Object to Scala object"
          )
    }
  }

  given floatConverter: JDBCConverter[Float] with {
    override def toString(): String = "Float Converter"

    override def convertToScala(value: Any): Float = value match
      case n: Float => n
      case _        =>
        throw new RuntimeException(
          s"JDBC ${{ toString() }} Unable to convert from JDBC Object to Scala object"
        )
  }

  given doubleConverter: JDBCConverter[Double] with {
    override def toString(): String = "Double Converter"

    override def convertToScala(value: Any): Double = value match
      case n: Double => n
      case _         =>
        throw new RuntimeException(
          s"JDBC ${{ toString() }} Unable to convert from JDBC Object to Scala object"
        )
  }

  given bigDecimalConverter: JDBCConverter[BigDecimal] with {
    override def toString(): String = "BigDecimal Converter"

    override def convertToScala(value: Any): BigDecimal = value match
      case n: BigDecimal => n
      case _             =>
        throw new RuntimeException(
          s"JDBC ${{ toString() }} Unable to convert from JDBC Object to Scala object"
        )
  }

  given dateConverter: JDBCConverter[LocalDate] with {

    override def toString(): String = "LocalDate Converter"

    def convertToScala(value: Any): LocalDate =
      value.asInstanceOf[LocalDate]
  }

  given timeConverter: JDBCConverter[LocalTime] with {

    override def toString(): String = "LocalTime Converter"

    def convertToScala(value: Any): LocalTime = value.asInstanceOf[LocalTime]

  }

  given dateTimeConverter: JDBCConverter[LocalDateTime] with {

    override def toString(): String = "LocalDateTime Converter"

    def convertToScala(value: Any): LocalDateTime =
      value.asInstanceOf[LocalDateTime]
  }

  given byteArrayConverter: JDBCConverter[Array[Byte]] with {

    override def toString(): String = "Array[Byte] (Binary) Converter"

    override def convertToScala(value: Any): Array[Byte] =
      value.asInstanceOf[Array[Byte]]

  }

  given binaryConverter: JDBCConverter[Array[Byte]] with {
    override def toString(): String = "Binary to Array Converter"

    override def convertToScala(value: Any): Array[Byte] = {
      value.asInstanceOf[Array[Byte]]
    }
  }

  given offsetTimeConverter: JDBCConverter[OffsetTime] with {
    override def toString(): String = "Offset Time Converter"

    override def convertToScala(value: Any): OffsetTime =
      value.asInstanceOf[OffsetTime]
  }

  given offsetDateTimeStampConverter: JDBCConverter[OffsetDateTime] with {
    override def toString(): String = "Offset Datetime Converter"

    override def convertToScala(value: Any): OffsetDateTime =
      value.asInstanceOf[OffsetDateTime]
  }

  given arrayConverter: JDBCConverter[Array[Any]] with {

    override def toString(): String = "Array Converter"

    def convertToScala(value: Any): Array[Any] =
      value.asInstanceOf[Array[Any]]
  }

  given blobConverter: JDBCConverter[InputStream] with {
    override def toString(): String = "Blob -> Inputstream Converter"

    def convertToScala(value: Any): InputStream =
      value match
        case b: java.sql.Blob => b.getBinaryStream()
        case _                =>
          throw new RuntimeException(
            s"JDBC ${{ toString() }} Unable to convert JDBC object ${value} to InputStream"
          )

  }

  given clobConverter: JDBCConverter[Reader] with {
    override def toString(): String = "Clob/NClob -> Inputstream Converter"

    def convertToScala(value: Any): Reader =
      value match
        case nc: java.sql.NClob => nc.getCharacterStream()
        case c: java.sql.Clob   => c.getCharacterStream()
        case _                  =>
          throw new RuntimeException(
            s"JDBC ${{ toString() }} Unable to convert JDBC object ${value} to Converter"
          )

  }

  given uuidConverter: JDBCConverter[UUID] with {

    override def toString(): String = "UUID Converter"

    override def convertToScala(value: Any): UUID =
      value.asInstanceOf[UUID]
  }

  given byteToUUIDConverter: JDBCConverter[UUID] with {
    override def toString(): String = "Byte to UUID Converter"

    override def convertToScala(value: Any): ju.UUID = {
      val array = value.asInstanceOf[Array[Byte]]
      bytesToUUID(array)
    }
  }

  given unitConverter: JDBCConverter[Unit] with {
    override def toString(): String = "NULL/Unit type Converter"

    override def convertToScala(value: Any): Unit = ()
  }

  given anyObjectConverter: JDBCConverter[Any] with {
    override def toString(): String = "Any/Object Converter"

    override def convertToScala(value: Any): Any = value
  }

  given bitSetConverter: JDBCConverter[BitSet] with {
    override def toString(): String = "BitSet Converter"

    override def convertToScala(value: Any): BitSet = value.asInstanceOf[BitSet]
  }

  given bitSetByteArrayConverter: JDBCConverter[BitSet] with {
    override def toString(): String = "Bit -> ByteArray Converter"

    override def convertToScala(value: Any): ju.BitSet = {
      val bytes = value.asInstanceOf[Array[Byte]]
      ju.BitSet.valueOf(bytes)
    }
  }

}
