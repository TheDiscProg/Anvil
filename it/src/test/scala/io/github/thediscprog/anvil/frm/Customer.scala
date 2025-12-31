package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import cats.Monad
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.adt.TableMapping
import java.sql.Connection
import java.time.LocalDate

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
  private val customerProperties = TableProperties(
    table = "customer",
    isNamingSpecial = false,
    cachingKey = "customer",
    dialect = DbVendor.POSTGRESQL,
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

  def customerFRM[F[_]: {Monad, Logger}](connection: Connection) =
    TableMapping.getFRM[F, Customer](customerProperties, connection)
}
