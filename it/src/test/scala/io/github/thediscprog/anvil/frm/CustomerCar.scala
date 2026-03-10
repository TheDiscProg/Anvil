package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import java.time.LocalDate
import java.util.UUID
import javax.sql.DataSource

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

  def customerCarFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CustomerCar](
      customerCarProperties(vendor),
      dataSource
    )
}
