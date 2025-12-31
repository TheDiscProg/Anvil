package io.github.thediscprog.anvil

import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.frm.*
import java.sql.Connection
import cats.Monad
import org.typelevel.log4cats.Logger

class DatabaseFixture[F[_]: {Monad, Logger}](connection: Connection) {
  def jdbcCon: Connection                    = connection
  def addressFRM: TableMapping[F, Address]   = Address.addressFrm(connection)
  def carModelFRM: TableMapping[F, CarModel] = CarModel.carModelFrm(connection)
  def customerFRM: TableMapping[F, Customer] = Customer.customerFRM(connection)
  def customerAddressFrm: TableMapping[F, CustomerAddress] =
    CustomerAddress.customerAddressFrm(connection)
  def customerCarFRM: TableMapping[F, CustomerCar] =
    CustomerCar.customerCarFrm(connection)
  def usersFRM: TableMapping[F, User]        = User.usersFrm(connection)
  def userTypeFRM: TableMapping[F, UserType] = UserType.userTypeFrm(connection)
}
