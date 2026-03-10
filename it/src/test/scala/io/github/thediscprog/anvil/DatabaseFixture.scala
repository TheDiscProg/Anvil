package io.github.thediscprog.anvil

import cats.Monad
import cats.effect.kernel.Sync
import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.frm.*
import org.typelevel.log4cats.Logger

import javax.sql.DataSource

class DatabaseFixture[F[_]: {Monad, Logger, Sync}](
    dataSource: DataSource,
    vendor: DbVendor
) {

  def getDataSource: DataSource = dataSource

  def addressFRM: TableMapping[F, Address] =
    Address.addressFrm(dataSource, vendor)
  def carModelFRM: TableMapping[F, CarModel] =
    CarModel.carModelFrm(dataSource, vendor)
  def customerFRM: TableMapping[F, Customer] =
    Customer.customerFRM(dataSource, vendor)
  def customerAddressFrm: TableMapping[F, CustomerAddress] =
    CustomerAddress.customerAddressFrm(dataSource, vendor)
  def customerCarFRM: TableMapping[F, CustomerCar] =
    CustomerCar.customerCarFrm(dataSource, vendor)
  def usersFRM: TableMapping[F, User]        = User.usersFrm(dataSource, vendor)
  def userTypeFRM: TableMapping[F, UserType] =
    UserType.userTypeFrm(dataSource, vendor)
}
