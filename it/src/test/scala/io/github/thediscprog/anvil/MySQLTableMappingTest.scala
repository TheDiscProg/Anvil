package io.github.thediscprog.anvil

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.zaxxer.hikari.HikariConfig
import io.github.thediscprog.anvil.adt.{AND, Criteria, KeyValue}
import io.github.thediscprog.anvil.dialects.DbVendor
import io.github.thediscprog.anvil.frm.MySQLDataTypes
import io.github.thediscprog.anvil.jdbcutils.JdbcConnection.getHikariDataSource
import org.testcontainers.mysql.MySQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime

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
    val ds = getHikariDataSource(config)
    new DatabaseFixture[IO](
      ds,
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
    val frm = MySQLDataTypes.mysqlDataTypesFRM[IO](dbFixtures.getDataSource)
    val now = LocalDateTime.now()
    val criteria = Criteria(List(AND(List(KeyValue("col_datetime", now)))))

    val result =
      for
        rowMayBe <- frm.headOption(Criteria(List()))
        row = rowMayBe match
          case Some(row) => row
          case None      => fail("Failed to fetch a row that should exist")
        rowToAdd = row.copy(colDateTime = Some(now))
        added <- frm.add(rowToAdd)
        rows  <- frm.filter(Criteria(List()))
      yield (row, added, rows)

    whenReady(result.unsafeToFuture()) { (row, added, rows) =>
      row.colBigInt.value should be(1234567890123L)
      added should be(1)
      rows.size should be(2)
    }
  }
}
