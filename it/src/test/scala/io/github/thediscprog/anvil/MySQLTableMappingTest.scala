package io.github.thediscprog.anvil

import cats.effect.IO
import com.zaxxer.hikari.HikariConfig
import io.github.thediscprog.anvil.jdbcutils.JdbcConnection
import org.testcontainers.utility.{DockerImageName, MountableFile}
import org.testcontainers.mysql.MySQLContainer
import io.github.thediscprog.anvil.adt.Criteria
import cats.effect.unsafe.implicits.global
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.frm.Customer
import java.time.LocalDate
import io.github.thediscprog.anvil.frm.Address
import io.github.thediscprog.anvil.adt.KeyValue
import io.github.thediscprog.anvil.adt.Operand
import io.github.thediscprog.anvil.adt.AND
import io.github.thediscprog.anvil.frm.CustomerAddress
import io.github.thediscprog.anvil.frm.MySQLDataTypes
import java.time.LocalDateTime
import io.github.thediscprog.anvil.frm.UserType
import java.util.UUID
import io.github.thediscprog.anvil.frm.User

class MySQLTableMappingTest extends TableMapTest {

  private val mysql: MySQLContainer =
    new MySQLContainer(DockerImageName.parse("mysql:latest"))
      .withDatabaseName("anvil")
      .withUsername("anvil")
      .withPassword("anvil")
      .withInitScript("sql/mysql/init-db.sql")

  override def dbFixtures: DatabaseFixture[IO] = {
    val dbUrl    = mysql.getJdbcUrl()
    val username = mysql.getUsername()
    val password = mysql.getPassword()
    val config   = new HikariConfig()
    config.setJdbcUrl(dbUrl)
    config.setUsername(username)
    config.setPassword(password)
    new DatabaseFixture[IO](
      JdbcConnection.openJdbcConnection(config),
      DbVendor.MYSQL
    )
  }

  override protected def beforeAll(): Unit = {
    mysql.start()
  }

  override protected def afterAll(): Unit = {
    mysql.stop()
  }

  it should "handle all different MySQL data types" in {
    val connection = dbFixtures.jdbcCon
    val frm        = MySQLDataTypes.mysqlDataTypesFRM[IO](connection)
    val now        = LocalDateTime.now()
    val criteria   = Criteria(List(AND(List(KeyValue("col_datetime", now)))))

    val result =
      for {
        rowMayBe <- frm.headOption(Criteria(List()))
        row = rowMayBe match
          case Some(row) => row
          case None      => fail("Failed to fetch a row that should exist")
        rowToAdd = row.copy(colDateTime = Some(now))
        added <- frm.add(rowToAdd)
        rows  <- frm.filter(Criteria(List()))
      } yield (row, added, rows)

    whenReady(result.unsafeToFuture()) { (row, added, rows) =>
      row.colBigInt.value shouldBe 1234567890123L
      added shouldBe 1
      rows.size shouldBe 2
    }
  }
}
