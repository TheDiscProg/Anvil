package io.github.thediscprog.anvil.adt

import java.sql.ResultSetMetaData
import io.github.thediscprog.anvil.jdbcutils.*
import io.github.thediscprog.anvil.dialects.SqlDialect

case class ColumnDescriptor(
    index: Int,
    columnName: String,
    isCaseSensitive: Boolean,
    dataType: Int,
    length: Int,
    precision: Int,
    scale: Int,
    nullable: Boolean,
    isCurrency: Boolean,
    isReadOnly: Boolean,
    reader: JDBCReader[?]
)

object ColumnDescriptor {

  def getColumnInformation(
      metadata: ResultSetMetaData,
      fieldTypes: List[(String, String)],
      readerSelector: JDBCReaderSelector,
      dialect: SqlDialect
  ) = {
    for index <- 1 to metadata.getColumnCount
    yield new ColumnDescriptor(
      index = index,
      columnName = metadata.getColumnLabel(index),
      isCaseSensitive = metadata.isCaseSensitive(index),
      dataType = metadata.getColumnType(index),
      length = metadata.getColumnDisplaySize(index),
      precision = metadata.getPrecision(index),
      scale = metadata.getScale(index),
      nullable = isNullable(metadata.isNullable(index)),
      isCurrency = metadata.isCurrency(index),
      isReadOnly = metadata.isReadOnly(index),
      reader = readerSelector.getJdbcReader(
        metadata.getColumnName(index),
        metadata.getColumnType(index),
        fieldTypes((index - 1))._2,
        metadata.getColumnTypeName(index),
        dialect
      )
    )
  }

  private def isNullable(value: Int): Boolean =
    value match
      case ResultSetMetaData.columnNoNulls => false
      case _                               => true

}
