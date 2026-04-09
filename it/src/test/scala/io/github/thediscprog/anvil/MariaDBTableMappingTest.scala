package io.github.thediscprog.anvil

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.zaxxer.hikari.HikariConfig
import io.github.thediscprog.anvil.adt.{AND, Criteria, KeyValue}
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.frm.MariaDBDataTypes
import io.github.thediscprog.anvil.jdbcutils.JdbcConnection
import org.testcontainers.mariadb.MariaDBContainer
import org.typelevel.log4cats.Logger

import java.time.LocalDateTime

class MariaDBTableMappingTest extends TableMapTest {

  private val mariadb = new MariaDBContainer("mariadb:11.8.6")
    .withDatabaseName("anvil")
    .withUsername("anvil")
    .withPassword("anvil")
    .withInitScript("sql/mariadb/init-db.sql")

  override def dbFixtures: DatabaseFixture[IO] = {
    val dbUrl    = mariadb.getJdbcUrl()
    val username = mariadb.getUsername()
    val password = mariadb.getPassword()
    val config   = new HikariConfig()
    config.setJdbcUrl(dbUrl)
    config.setUsername(username)
    config.setPassword(password)
    val ds = JdbcConnection.getHikariDataSource(config)
    new DatabaseFixture[IO](ds, DbVendor.MARIADB)
  }

  override protected def beforeAll(): Unit = {
    mariadb.start()
  }

  override protected def afterAll(): Unit = {
    mariadb.stop()
  }

  it should "handle all different MariaDB data types" in {
    val frm = MariaDBDataTypes.mariaDBDataTypesFRM[IO](dbFixtures.getDataSource)
    val now = LocalDateTime.now()
    val criteria = Criteria(List(AND(List(KeyValue("t_datetime", now)))))

    val result =
      for {
        rowMayBe <- frm.headOption(Criteria(List()))
        row = rowMayBe match
          case Some(row) => row
          case None      => fail("Failed to fetch a row that should exist")
        rowToAdd = row.copy(colDateTime = Some(now))
        rowToAdd2 = row.copy(colDateTime = Some(now))
        added <- frm.add(rowToAdd)
        added2 <- frm.add(rowToAdd2)
        rows  <- frm.filter(Criteria(List()))
      } yield (row, added + added2, rows)

    whenReady(result.unsafeToFuture()) { (row, added, rows) =>
      row.colBigInt.value should be(12345678901L)
      added should be(2)
      rows.size should be(3)
    }

  }
}
