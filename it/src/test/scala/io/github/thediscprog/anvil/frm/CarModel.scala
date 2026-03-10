package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import java.time.LocalDate
import java.util.UUID
import javax.sql.DataSource

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

  def carModelFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, CarModel](carModelProperties(vendor), dataSource)
}
