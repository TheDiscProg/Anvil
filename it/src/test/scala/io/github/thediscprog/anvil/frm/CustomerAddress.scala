package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import javax.sql.DataSource

@PrimaryKey(PrimaryKeyType.COMPOSITE, List("customerId", "addressId"), false)
final case class CustomerAddress(
    customerId: Long,
    addressId: Long,
    comment: Option[String]
)

object CustomerAddress {
  private def customerAddressProperties(vendor: DbVendor) = TableProperties(
    table = "customer_address",
    isNamingSpecial = false,
    cachingKey = "customer-address",
    dialect = vendor,
    columnNames = Seq("customer_id", "address_id", "comment")
  )

  def customerAddressFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CustomerAddress](
      customerAddressProperties(vendor),
      dataSource
    )
}
