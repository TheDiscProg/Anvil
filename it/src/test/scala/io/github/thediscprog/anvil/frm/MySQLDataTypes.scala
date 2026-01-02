package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.Instant
import java.io.InputStream
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*
import scala.collection.immutable.BitSet

@PrimaryKey(SINGLE, List("colId"), true)
case class MySQLDataTypes(
    colId: Long,
    colTinyInt: Option[Byte],
    colSmallInt: Option[Short],
    colMediumInt: Option[Int],
    colInt: Option[Int],
    colBigInt: Option[Long],
    colDecimal: Option[BigDecimal],
    colNumeric: Option[BigDecimal],
    colFloat: Option[Float],
    colDouble: Option[Double],
    colBit: Option[BitSet],
    colBoolean: Option[Boolean],
    colDate: Option[LocalDate],
    colTime: Option[LocalTime],
    colDateTime: Option[LocalDateTime],
    colTimestamp: Option[Instant],
    // colYear: Option[Int],
    colChar: Option[String],
    colVarchar: Option[String],
    colTinyText: Option[String],
    colText: Option[String],
    colMediumText: Option[String],
    colLongText: Option[String],
    colBinary: Option[Array[Byte]],
    colVarBinary: Option[Array[Byte]],
    colTinyBlob: Option[Array[Byte]],
    colBlob: Option[InputStream],
    colMediumBlob: Option[InputStream],
    colLongBlob: Option[InputStream],
    colEnum: Option[String],
    colSet: Option[String],
    colJson: Option[String],
    colPoint: Option[Array[Byte]],
    colLineString: Option[Array[Byte]],
    colPolygon: Option[Array[Byte]]
)

object MySQLDataTypes {
  private val tableProperties = TableProperties(
    table = "mysql_all_types",
    isNamingSpecial = false,
    cachingKey = "mysql-all-types",
    dialect = DbVendor.MYSQL,
    columnNames = List(
      "col_id",
      "col_tinyint",
      "col_smallint",
      "col_mediumint",
      "col_int",
      "col_bigint",
      "col_decimal",
      "col_numeric",
      "col_float",
      "col_double",
      "col_bit",
      "col_boolean",
      "col_date",
      "col_time",
      "col_datetime",
      "col_timestamp",
      // "col_year",
      "col_char",
      "col_varchar",
      "col_tinytext",
      "col_text",
      "col_mediumtext",
      "col_longtext",
      "col_binary",
      "col_varbinary",
      "col_tinyblob",
      "col_blob",
      "col_mediumblob",
      "col_longblob",
      "col_enum",
      "col_set",
      "col_json",
      "col_point",
      "col_linestring",
      "col_polygon"
    )
  )

  def mysqlDataTypesFRM[F[_]: {Monad, Logger}](connection: Connection) =
    TableMapping.getFRM[F, MySQLDataTypes](tableProperties, connection)
}
