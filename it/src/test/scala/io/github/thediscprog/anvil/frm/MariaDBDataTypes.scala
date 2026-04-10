package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import java.io.InputStream
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import java.util.UUID
import javax.sql.DataSource
import scala.collection.immutable.BitSet

@PrimaryKey(SINGLE, List("colId"), true)
case class MariaDBDataTypes(
    colId: Long,
    colTinyInt: Option[Byte],
    colSmallInt: Option[Short],
    colMediumInt: Option[Int],
    colInt: Option[Int],
    colBigInt: Option[Long],
    colFloat: Option[Float],
    colDouble: Option[Double],
    colDecimal: Option[BigDecimal],
    colBoolean: Option[Boolean],
//    colBit: Option[BitSet],
    colDate: Option[LocalDate],
    colDateTime: Option[LocalDateTime],
    colTimestamp: Option[Instant],
    colTime: Option[LocalTime],
//    colYear: Option[Int],
    colChar: Option[String],
    colVarchar: Option[String],
    colText: Option[String],
    colTinyText: Option[String],
    colMediumText: Option[String],
    colLongText: Option[String],
//    colBinary: Option[Array[Byte]],
//    colVarBinary: Option[Array[Byte]],
//    colBlob: Option[InputStream],
//    colTinyBlob: Option[Array[Byte]],
//    colMediumBlob: Option[InputStream],
//    colLongBlob: Option[InputStream],
    colEnum: Option[String],
    colSet: Option[String],
    colJson: Option[String],
//    colPoint: Option[Array[Byte]],
    colUuid: Option[UUID]
)

object MariaDBDataTypes {
  private val tableProperties = TableProperties(
    table = "mariadb_all_types",
    isNamingSpecial = false,
    cachingKey = "mariadb-all-types",
    dialect = DbVendor.MARIADB,
    columnNames = List(
      "t_id",
      "t_tinyint",
      "t_smallint",
      "t_mediumint",
      "t_int",
      "t_bigint",
      "t_float",
      "t_double",
      "t_decimal",
      "t_boolean",
//      "t_bit",
      "t_date",
      "t_datetime",
      "t_timestamp",
      "t_time",
//      "t_year",
      "t_char",
      "t_varchar",
      "t_text",
      "t_tinytext",
      "t_mediumtext",
      "t_longtext",
//      "t_binary",
//      "t_varbinary",
//      "t_blob",
//      "t_tinyblob",
//      "t_mediumblob",
//      "t_longblob",
      "t_enum",
      "t_set",
      "t_json",
//      "t_point",
      "t_uuid"
    )
  )

  def mariaDBDataTypesFRM[F[_]: {Monad, Logger, Sync}](dataSource: DataSource) =
    TableMapping.getFRM[F, MariaDBDataTypes](tableProperties, dataSource)
}
