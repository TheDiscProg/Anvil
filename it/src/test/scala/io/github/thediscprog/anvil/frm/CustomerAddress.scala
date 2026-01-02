package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import cats.Monad
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.adt.TableMapping
import java.sql.Connection

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

  def customerAddressFrm[F[_]: {Monad, Logger}](
      connection: Connection,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CustomerAddress](
      customerAddressProperties(vendor),
      connection
    )
}
