package io.github.thediscprog.anvil.frm

import java.time.LocalDate
import java.util.UUID
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import java.sql.Connection
import io.github.thediscprog.anvil.adt.TableMapping
import cats.Monad
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType

@PrimaryKey(PrimaryKeyType.SINGLE, List("id"), true)
final case class CustomerCar(
    id: Long,
    customerId: Long,
    modelId: UUID,
    datePurchased: Option[LocalDate],
    serviceDate: Option[LocalDate],
    comments: Option[String]
)

object CustomerCar {
  private def customerCarProperties(vendor: DbVendor) = TableProperties(
    table = "user_cars",
    isNamingSpecial = false,
    cachingKey = "customer-cars",
    dialect = vendor,
    columnNames = Seq(
      "id",
      "customer_id",
      "model_id",
      "date_purchased",
      "service_date",
      "comments"
    )
  )

  def customerCarFrm[F[_]: {Monad, Logger}](
      connection: Connection,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CustomerCar](
      customerCarProperties(vendor),
      connection
    )
}
