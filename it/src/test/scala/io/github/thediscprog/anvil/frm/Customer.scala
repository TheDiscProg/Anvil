package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import java.time.LocalDate
import javax.sql.DataSource

@PrimaryKey(PrimaryKeyType.SINGLE, List("customerId"), true)
final case class Customer(
    customerId: Long,
    title: String,
    firstName: String,
    surname: String,
    contactEmail: String,
    contactMobile: String,
    dateJoined: LocalDate,
    comments: Option[String]
)

object Customer {
  private def customerProperties(vendor: DbVendor) = TableProperties(
    table = "customer",
    isNamingSpecial = false,
    cachingKey = "customer",
    dialect = vendor,
    columnNames = Seq(
      "customer_id",
      "title",
      "first_name",
      "surname",
      "contact_email",
      "contact_mobile",
      "date_joined",
      "comment"
    )
  )

  def customerFRM[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, Customer](customerProperties(vendor), dataSource)
}
