package io.github.thediscprog.anvil

import cats.effect.kernel.Async
import io.github.thediscprog.anvil.adt.TableMapping

import scala.deriving.Mirror
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.adt.TableProperties
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*

@PrimaryKey(SINGLE, List("id"), false)
case class User(
    id: Int,
    name: String,
    age: Option[Int],
    hobbies: List[String]
)

object User {
  val userProperties =
    TableProperties(table = "users", isNamingSpecial=true, cachingKey = "userTable", DbVendor.POSTGRESQL, Seq())

  def userTableMapping[F[_]: Monad: Logger](connection: Connection) =
    TableMapping.getTableReader[F, User](userProperties, connection)

}
