package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.adt.TableMapping

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
  private val addressProperties = TableProperties(
    table = "address",
    isNamingSpecial = false,
    cachingKey = "address",
    dialect = DbVendor.POSTGRESQL,
    columnNames =
      Seq("address_id", "street", "town", "county", "post_code", "country")
  )

  def addressFrm[F[_]: {Monad, Logger}](connection: Connection) =
    TableMapping.getFRM[F, Address](addressProperties, connection)
}
