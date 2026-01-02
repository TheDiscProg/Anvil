package io.github.thediscprog.anvil.jdbcutils

import java.sql.Types
import JDBCReader.*
import io.github.thediscprog.anvil.dialects.SqlDialect

class JDBCReaderSelector {

  def getJdbcReader(
      columnName: String,
      jdbcType: Int,
      scalaFieldName: String,
      columnTypeName: String,
      dialect: SqlDialect
  ): JDBCReader[?] = {
    val readerOrBoolean =
      dialect.getDBTypeReader(jdbcType, scalaFieldName, columnTypeName)
    if (printDebugInfo) then
      println(
        s"DB Specific Reader Selected? [${readerOrBoolean.toString()}] for [$columnName] [$jdbcType] ${scalaFieldName} and ${columnTypeName}"
      )

    readerOrBoolean match
      case r: JDBCReader[?] => r
      case _: Boolean       => {
        jdbcType match
          case Types.BIT         => bitSetReader
          case Types.TINYINT     => byteReader
          case Types.SMALLINT    => shortReader
          case Types.INTEGER     => intReader
          case Types.BIGINT      => longReader
          case Types.FLOAT       => floatReader
          case Types.REAL        => floatReader
          case Types.DOUBLE      => doubleReader
          case Types.NUMERIC     => bigDecimalReader
          case Types.DECIMAL     => bigDecimalReader
          case Types.CHAR        => stringReader
          case Types.VARCHAR     => stringReader
          case Types.LONGVARCHAR => stringReader
          case Types.DATE        => dateReader
          case Types.TIME        =>
            if (scalaFieldName == "OffsetTime" || columnTypeName == "timetz")
              offsetTimeReader
            else
              timeReader
          case Types.TIMESTAMP =>
            if (
              scalaFieldName == "OffsetDateTime" || columnTypeName == "timestamptz"
            )
              offsetDateTimeStampReader
            else
              dateTimeStampReader
          case Types.BINARY        => byteArrayReader
          case Types.VARBINARY     => byteArrayReader
          case Types.LONGVARBINARY => byteArrayReader
          case Types.NULL          => unitReader
          case Types.OTHER         =>
            if (printDebugInfo) then
              println(
                s"Reader Selector for ${scalaFieldName} and ${columnTypeName}"
              )
            scalaFieldName match
              case "UUID" => uuidReader
              case _      => objectReader
          //   case Types.JAVA_OBJECT   => (value: Any) => objectReader.read(value)
          //   case Types.DISTINCT      =>
          //     (value: Any) =>
          //       objectReader.read(value) // This is a user-defined data type
          //   case Types.STRUCT   => (value: Any) => structReader.read(value)
          case Types.ARRAY => arrayReader
          case Types.BLOB  => blobReader
          case Types.CLOB  => clobReader
          //   case Types.REF      => (value: Any) => refReader.read(value)
          //   case Types.DATALINK =>
          //     (value: Any) =>
          //       stringReader.read(value) // This is a URL avaiable after getUrl
          case Types.BOOLEAN => booleanReader
          //   case Types.ROWID              => (value: Any) => rowIdReader.read(value)
          case Types.NCHAR                   => nStringReader
          case Types.NVARCHAR                => nStringReader
          case Types.LONGNVARCHAR            => nStringReader
          case Types.NCLOB                   => clobReader
          case Types.SQLXML                  => stringReader
          case Types.REF_CURSOR              => objectReader
          case Types.TIME_WITH_TIMEZONE      => offsetTimeReader
          case Types.TIMESTAMP_WITH_TIMEZONE => offsetDateTimeStampReader
          case _                             =>
            throw new RuntimeException(
              s"No reader for JDBC type [$jdbcType] found."
            )
      }
  }
}
