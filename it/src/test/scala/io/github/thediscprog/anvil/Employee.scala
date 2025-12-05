package io.github.thediscprog.anvil

import io.github.thediscprog.anvil.adt.TableProperties
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.adt.TableMapping
import cats.Monad
import org.typelevel.log4cats.Logger
import java.sql.Connection
import io.github.thediscprog.anvil.annotations.PrimaryKey
import io.github.thediscprog.anvil.annotations.PrimaryKeyType.*

@PrimaryKey(SINGLE, List("id"), true)
final case class Employee(
    userId: Int,
    userPosition: String,
    isEmployed: Boolean
)

object Employee {

  val employeeProperties = TableProperties(
    "employees",
    true,
    "employeeTable", 
    DbVendor.POSTGRESQL,
    Seq("id", "position", "active")
  )

  def employeeMapping[F[_]: Monad: Logger](connection: Connection) =
    TableMapping.getTableReader[F, Employee](employeeProperties, connection)
}
