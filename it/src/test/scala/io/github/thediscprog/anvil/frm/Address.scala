package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import javax.sql.DataSource

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

  def addressFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, Address](addressProperties(vendor), dataSource)
}
