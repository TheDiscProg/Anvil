package io.github.thediscprog.anvil

import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.frm.*
import java.sql.Connection
import cats.Monad
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.dialects.DbVendor

class DatabaseFixture[F[_]: {Monad, Logger}](
    connection: Connection,
    vendor: DbVendor
) {
  def jdbcCon: Connection                  = connection
  def addressFRM: TableMapping[F, Address] =
    Address.addressFrm(connection, vendor)
  def carModelFRM: TableMapping[F, CarModel] =
    CarModel.carModelFrm(connection, vendor)
  def customerFRM: TableMapping[F, Customer] =
    Customer.customerFRM(connection, vendor)
  def customerAddressFrm: TableMapping[F, CustomerAddress] =
    CustomerAddress.customerAddressFrm(connection, vendor)
  def customerCarFRM: TableMapping[F, CustomerCar] =
    CustomerCar.customerCarFrm(connection, vendor)
  def usersFRM: TableMapping[F, User]        = User.usersFrm(connection, vendor)
  def userTypeFRM: TableMapping[F, UserType] =
    UserType.userTypeFrm(connection, vendor)
}
