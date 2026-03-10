package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger

import javax.sql.DataSource

@PrimaryKey(PrimaryKeyType.SINGLE, List("typeKey"), false)
final case class UserType(
    typeKey: String,
    title: String,
    description: Option[String]
)

object UserType {

  private def userTypeProperties(vendor: DbVendor) = TableProperties(
    table = "user_type",
    isNamingSpecial = false,
    cachingKey = "user-type",
    dialect = vendor,
    columnNames = Seq("type_key", "title", "description")
  )

  def userTypeFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, UserType](userTypeProperties(vendor), dataSource)
}
