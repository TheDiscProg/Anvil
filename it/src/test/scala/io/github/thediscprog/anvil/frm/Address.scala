package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.dialects.SqlDialect

@PrimaryKey(PrimaryKeyType.SINGLE, List("addressId"), false)
final case class Address(
    addressId: Long,
    street: String,
    town: Option[String],
    county: String,
    postCode: String,
    country: String
)

object Address {
  private def addressProperties(vendor: DbVendor) = TableProperties(
    table = "address",
    isNamingSpecial = false,
    cachingKey = "address",
    dialect = vendor,
    columnNames =
      Seq("address_id", "street", "town", "county", "post_code", "country")
  )

  def addressFrm[F[_]: {Monad, Logger}](
      connection: Connection,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, Address](addressProperties(vendor), connection)
}
