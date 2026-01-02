package io.github.thediscprog.anvil.frm

import java.util.UUID
import java.time.LocalDate
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import cats.Monad
import java.sql.Connection
import io.github.thediscprog.anvil.adt.TableMapping
import org.typelevel.log4cats.Logger

@PrimaryKey(SINGLE, List("modelId"), false)
final case class CarModel(
    modelId: UUID,
    manufacturer: String,
    engine: String,
    manufactedOn: LocalDate,
    registration: Option[String]
)

object CarModel {
  private def carModelProperties(vendor: DbVendor) = TableProperties(
    table = "car_models",
    isNamingSpecial = false,
    cachingKey = "car-models",
    dialect = vendor,
    columnNames = Seq(
      "model_id",
      "manufacturer",
      "engine_description",
      "date_of_manufacture",
      "registration"
    )
  )

  def carModelFrm[F[_]: {Monad, Logger}](
      connection: Connection,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CarModel](carModelProperties(vendor), connection)
}
