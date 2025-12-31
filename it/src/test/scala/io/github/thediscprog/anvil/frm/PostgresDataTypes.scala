package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.UUID
import java.util.BitSet
import io.github.thediscprog.anvil.adt.TableMapping
import java.sql.Connection
import cats.Monad
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType

@PrimaryKey(
  PrimaryKeyType.COMPOSITE,
  List("colSmallSerial", "colSerial", "colBigSerial"),
  true
)
case class PostgresDataTypes(
    colSmallInt: Option[Short],
    colInteger: Option[Int],
    colBigInt: Option[Long],
    colDecimal: Option[BigDecimal],
    colNumeric: Option[BigDecimal],
    colReal: Option[Float],
    colDouble: Option[Double],
    colSmallSerial: Short,
    colSerial: Int,
    colBigSerial: Long,
    colMoney: Option[BigDecimal],
    colChar: Option[Char],
    colVarChar: Option[String],
    colText: Option[String],
    colCIText: Option[String],
    colName: Option[String],
    colBoolean: Option[Boolean],
    colDate: Option[LocalDate],
    colTime: Option[LocalTime],
    colTimeZ: Option[OffsetTime],
    colTimestamp: Option[LocalDateTime],
    colTimestampZ: Option[OffsetDateTime],
    colInterval: Option[Duration],
    colBytea: Option[Array[Byte]],
    colUUID: Option[UUID],
    colJson: Option[String],
    colJsonb: Option[String],
    colXml: Option[String],
    colIntArray: Option[Array[Int]],
    colTextArray: Option[Array[String]],
    colInt4Range: Option[String],
    colInt8Range: Option[String],
    colNumRange: Option[String],
    colDateRange: Option[String],
    colTSRange: Option[String],
    colTSTZRange: Option[String],
    colInt4Multirange: Option[String],
    colNumMultirange: Option[String],
    colTSMultiRange: Option[String],
    colInet: Option[String],
    colCidr: Option[String],
    colMacAddr: Option[String],
    colMaddAddr8: Option[String],
    colBit: Option[BitSet],
    colVarBit: Option[BitSet],
    colTSVVector: Option[String],
    colTSQuery: Option[String],
    colOID: Option[Long],
    colRegClass: Option[String],
    colRegProc: Option[String],
    colRegType: Option[String],
    colRegRole: Option[String],
    colRegNamespace: Option[String],
    colXid: Option[String],
    colXid8: Option[String],
    colCID: Option[String],
    colTxIdSnapshot: Option[String],
    colPgLSN: Option[String],
    colPoint: Option[String],
    colLine: Option[String],
    colLSeg: Option[String],
    colBox: Option[String],
    colPath: Option[String],
    colPolygon: Option[String],
    colCircle: Option[String],
    colLTree: Option[String]
)

object PostgresDataTypes {
  private val tableProperties = TableProperties(
    table = "postgres_every_type",
    isNamingSpecial = false,
    cachingKey = "pgsql-datatype",
    dialect = DbVendor.POSTGRESQL,
    columnNames = List(
      "col_smallint",
      "col_integer",
      "col_bigint",
      "col_decimal",
      "col_numeric",
      "col_real",
      "col_double",
      "col_smallserial",
      "col_serial",
      "col_bigserial",
      "col_money",
      "col_char",
      "col_varchar",
      "col_text",
      "col_citext",
      "col_name",
      "col_boolean",
      "col_date",
      "col_time",
      "col_timetz",
      "col_timestamp",
      "col_timestamptz",
      "col_interval",
      "col_bytea",
      "col_uuid",
      "col_json",
      "col_jsonb",
      "col_xml",
      "col_int_array",
      "col_text_array",
      "col_int4range",
      "col_int8range",
      "col_numrange",
      "col_daterange",
      "col_tsrange",
      "col_tstzrange",
      "col_int4multirange",
      "col_nummultirange",
      "col_tsmultirange",
      "col_inet",
      "col_cidr",
      "col_macaddr",
      "col_macaddr8",
      "col_bit",
      "col_varbit",
      "col_tsvector",
      "col_tsquery",
      "col_oid",
      "col_regclass",
      "col_regproc",
      "col_regtype",
      "col_regrole",
      "col_regnamespace",
      "col_xid",
      "col_xid8",
      "col_cid",
      "col_txid_snapshot",
      "col_pg_lsn",
      "col_point",
      "col_line",
      "col_lseg",
      "col_box",
      "col_path",
      "col_polygon",
      "col_circle",
      "col_ltree"
    )
  )

  def postgresDataTypesFrm[F[_]: {Monad, Logger}](connection: Connection) =
    TableMapping.getFRM[F, PostgresDataTypes](tableProperties, connection)
}
