package io.github.thediscprog.anvil.frm

import io.github.thediscprog.anvil.adt.TableMapping
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType

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

  def userTypeFrm[F[_]: {Monad, Logger}](
      connection: Connection,
      vendor: DbVendor
  ) =
    TableMapping.getFRM[F, UserType](userTypeProperties(vendor), connection)
}
