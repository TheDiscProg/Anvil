package io.github.thediscprog.anvil.adt

import java.sql.Types
import java.sql.*

trait JDBCReader[T] {

  def read(value: Any): T

  def toOption: JDBCReader[Option[T]] = (value: Any) => Option(read(value))

}

object JDBCReader {

  given bitReader: JDBCReader[Boolean] = booleanReader

  given tinyIntReader: JDBCReader[Byte] = (value: Any) =>
    value.asInstanceOf[Byte]

  given smallIntReader: JDBCReader[Short] = (value: Any) =>
    value.asInstanceOf[Short]

  given intReader: JDBCReader[Int] = (value: Any) => value.asInstanceOf[Int]

  given bigIntReader: JDBCReader[BigInt] = (value: Any) =>
    value.asInstanceOf[BigInt]

  given floatReader: JDBCReader[Float] = (value: Any) =>
    value.asInstanceOf[Float]

  given doubleReader: JDBCReader[Double] = (value: Any) =>
    value.asInstanceOf[Double]

  given bigDecimalReader: JDBCReader[BigDecimal] = (value: Any) =>
    value.asInstanceOf[BigDecimal]

  given charReader: JDBCReader[Char] = (value: Any) => value.asInstanceOf[Char]

  given stringReader: JDBCReader[String] = (value: Any) =>
    value.asInstanceOf[String]

  given dateReader: JDBCReader[Date] = (value: Any) => value.asInstanceOf[Date]
  given timeReader: JDBCReader[Time] = (value: Any) => value.asInstanceOf[Time]
  given timeStampReader: JDBCReader[Timestamp] = (value: Any) =>
    value.asInstanceOf[Timestamp]
  given byteArrayReader: JDBCReader[Array] = (value: Any) =>
    value.asInstanceOf[Array]
  given objectReader: JDBCReader[Object] = (value: Any) =>
    value.asInstanceOf[Object]
  given structReader: JDBCReader[Struct] = (value: Any) =>
    value.asInstanceOf[Struct]
  given blobReader: JDBCReader[Blob] = (value: Any) => value.asInstanceOf[Blob]
  given clobReader: JDBCReader[Clob] = (value: Any) => value.asInstanceOf[Clob]
  given refReader: JDBCReader[Ref]   = (value: Any) => value.asInstanceOf[Ref]

  given booleanReader: JDBCReader[Boolean] = (value: Any) =>
    value.asInstanceOf[Boolean]

  given rowIdReader: JDBCReader[RowId] = (value: Any) =>
    value.asInstanceOf[RowId]
  given nclobReader: JDBCReader[NClob] = (value: Any) =>
    value.asInstanceOf[NClob]
  given sqlXmlReader: JDBCReader[SQLXML] = (value: Any) =>
    value.asInstanceOf[SQLXML]

  given arrayReader: JDBCReader[Array] = (value: Any) =>
    value.asInstanceOf[Array]

  def getJdbcReader(value: Int): JDBCReader[?] =
    value match
      case Types.BIT         => (value: Any) => bitReader.read(value)
      case Types.TINYINT     => (value: Any) => tinyIntReader.read(value)
      case Types.SMALLINT    => (value: Any) => smallIntReader.read(value)
      case Types.INTEGER     => (value: Any) => intReader.read(value)
      case Types.BIGINT      => (value: Any) => bigIntReader.read(value)
      case Types.FLOAT       => (value: Any) => floatReader.read(value)
      case Types.REAL        => (value: Any) => floatReader.read(value)
      case Types.DOUBLE      => (value: Any) => doubleReader.read(value)
      case Types.NUMERIC     => (value: Any) => bigDecimalReader.read(value)
      case Types.DECIMAL     => (value: Any) => bigDecimalReader.read(value)
      case Types.CHAR        => (value: Any) => charReader.read(value)
      case Types.VARCHAR     => (value: Any) => stringReader.read(value)
      case Types.LONGVARCHAR => (value: Any) => stringReader.read(value)
      case Types.DATE        => (value: Any) => dateReader.read(value)
      case Types.TIME        => (value: Any) => timeReader.read(value)
      case Types.TIMESTAMP   =>
        (value: Any) => timeStampReader.read(value)
      case Types.BINARY        => (value: Any) => byteArrayReader.read(value)
      case Types.VARBINARY     => (value: Any) => byteArrayReader.read(value)
      case Types.LONGVARBINARY => (value: Any) => byteArrayReader.read(value)
      case Types.NULL          => _ => None
      case Types.OTHER         => (value: Any) => objectReader.read(value)
      case Types.JAVA_OBJECT   => (value: Any) => objectReader.read(value)
      case Types.DISTINCT      =>
        (value: Any) =>
          objectReader.read(value) // This is a user-defined data type
      case Types.STRUCT   => (value: Any) => structReader.read(value)
      case Types.ARRAY    => (value: Any) => arrayReader.read(value)
      case Types.BLOB     => (value: Any) => blobReader.read(value)
      case Types.CLOB     => (value: Any) => clobReader.read(value)
      case Types.REF      => (value: Any) => refReader.read(value)
      case Types.DATALINK =>
        (value: Any) =>
          stringReader.read(value) // This is a URL avaiable after getUrl
      case Types.BOOLEAN            => (value: Any) => booleanReader.read(value)
      case Types.ROWID              => (value: Any) => rowIdReader.read(value)
      case Types.NCHAR              => (value: Any) => stringReader.read(value)
      case Types.NVARCHAR           => (value: Any) => stringReader.read(value)
      case Types.LONGNVARCHAR       => (value: Any) => stringReader.read(value)
      case Types.NCLOB              => (value: Any) => nclobReader.read(value)
      case Types.SQLXML             => (value: Any) => sqlXmlReader.read(value)
      case Types.REF_CURSOR         => (value: Any) => objectReader.read(value)
      case Types.TIME_WITH_TIMEZONE => (value: Any) => timeReader.read(value)
      case Types.TIMESTAMP_WITH_TIMEZONE =>
        (value: Any) => timeStampReader.read(value)
      case _ =>
        throw new RuntimeException(s"JDBC type [$value] is not supported")

}
