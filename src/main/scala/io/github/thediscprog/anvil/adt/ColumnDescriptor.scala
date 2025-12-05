package io.github.thediscprog.anvil.adt

import java.sql.JDBCType
import java.sql.Types
import java.sql.ResultSetMetaData

case class ColumnDescriptor(
    index: Int,
    columnName: String,
    isCaseSensitive: Boolean,
    dataType: JDBCType,
    length: Int,
    precision: Int,
    scale: Int,
    nullable: Boolean,
    isCurrency: Boolean,
    isReadOnly: Boolean,
    reader: JDBCReader[?]
) {}

object ColumnDescriptor {

  def getColumnInformation(
      metadata: ResultSetMetaData
  ) = {
    for index <- 1 to metadata.getColumnCount
    yield new ColumnDescriptor(
      index = index,
      columnName = metadata.getColumnLabel(index),
      isCaseSensitive = metadata.isCaseSensitive(index),
      dataType = getJdbcType(metadata.getColumnType(index)),
      length = metadata.getColumnDisplaySize(index),
      precision = metadata.getPrecision(index),
      scale = metadata.getScale(index),
      nullable = isNullable(metadata.isNullable(index)),
      isCurrency = metadata.isCurrency(index),
      isReadOnly = metadata.isReadOnly(index),
      reader = JDBCReader.getJdbcReader(metadata.getColumnType(index))
    )
  }

  private def getJdbcType(value: Int): JDBCType =
    value match
      case Types.BIT                     => JDBCType.BIT
      case Types.TINYINT                 => JDBCType.TINYINT
      case Types.SMALLINT                => JDBCType.SMALLINT
      case Types.INTEGER                 => JDBCType.INTEGER
      case Types.BIGINT                  => JDBCType.BIGINT
      case Types.FLOAT                   => JDBCType.FLOAT
      case Types.REAL                    => JDBCType.REAL
      case Types.DOUBLE                  => JDBCType.DOUBLE
      case Types.NUMERIC                 => JDBCType.NUMERIC
      case Types.DECIMAL                 => JDBCType.DECIMAL
      case Types.CHAR                    => JDBCType.CHAR
      case Types.VARCHAR                 => JDBCType.VARCHAR
      case Types.LONGVARCHAR             => JDBCType.LONGVARCHAR
      case Types.DATE                    => JDBCType.DATE
      case Types.TIME                    => JDBCType.TIME
      case Types.TIMESTAMP               => JDBCType.TIMESTAMP
      case Types.BINARY                  => JDBCType.BINARY
      case Types.VARBINARY               => JDBCType.VARBINARY
      case Types.LONGVARBINARY           => JDBCType.LONGVARBINARY
      case Types.NULL                    => JDBCType.NULL
      case Types.OTHER                   => JDBCType.OTHER
      case Types.JAVA_OBJECT             => JDBCType.JAVA_OBJECT
      case Types.DISTINCT                => JDBCType.DISTINCT
      case Types.STRUCT                  => JDBCType.STRUCT
      case Types.ARRAY                   => JDBCType.ARRAY
      case Types.BLOB                    => JDBCType.BLOB
      case Types.CLOB                    => JDBCType.CLOB
      case Types.REF                     => JDBCType.REF
      case Types.DATALINK                => JDBCType.DATALINK
      case Types.BOOLEAN                 => JDBCType.BOOLEAN
      case Types.ROWID                   => JDBCType.ROWID
      case Types.NCHAR                   => JDBCType.NCHAR
      case Types.NVARCHAR                => JDBCType.NVARCHAR
      case Types.LONGNVARCHAR            => JDBCType.LONGNVARCHAR
      case Types.NCLOB                   => JDBCType.NCLOB
      case Types.SQLXML                  => JDBCType.SQLXML
      case Types.REF_CURSOR              => JDBCType.REF_CURSOR
      case Types.TIME_WITH_TIMEZONE      => JDBCType.TIMESTAMP_WITH_TIMEZONE
      case Types.TIMESTAMP_WITH_TIMEZONE => JDBCType.TIMESTAMP_WITH_TIMEZONE
      case _                             =>
        throw new RuntimeException(s"JDBC type [$value] is not supported")

  private def isNullable(value: Int): Boolean =
    value match
      case ResultSetMetaData.columnNoNulls => false
      case _                               => true

}
