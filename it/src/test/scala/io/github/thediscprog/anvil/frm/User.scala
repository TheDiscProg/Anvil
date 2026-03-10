package io.github.thediscprog.anvil.frm

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.{TableMapping, TableProperties}
import io.github.thediscprog.anvil.annotations.{PrimaryKey, PrimaryKeyType}
import io.github.thediscprog.anvil.dialects.DbVendor
import org.typelevel.log4cats.Logger
import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

@PrimaryKey(PrimaryKeyType.SINGLE, List("userId"), true)
final case class User(
    userId: UUID,
    userName: String,
    email: String,
    isActive: Boolean,
    isExpired: Boolean,
    lastLogin: Option[LocalDateTime],
    customerId: Long,
    userType: String
)

object User {

  private def userProperties(vendor: DbVendor) = TableProperties(
    table = "users",
    isNamingSpecial = false,
    cachingKey = "users",
    dialect = vendor,
    columnNames = Seq(
      "user_id",
      "user_name",
      "email",
      "is_active",
      "is_expired",
      "last_login",
      "customer_id",
      "user_type"
    )
  )

  def usersFrm[F[_]: {Monad, Logger, Sync}](
      dataSource: DataSource,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, User](userProperties(vendor), dataSource)
}
