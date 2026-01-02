package io.github.thediscprog.anvil

import cats.effect.IO
import org.scalatest.time.Span
import org.scalatest.time.Seconds
import org.scalatest.time.Millis
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import cats.effect.unsafe.implicits.global
import com.zaxxer.hikari.HikariConfig
import io.github.thediscprog.anvil.jdbcutils.JdbcConnection
import io.github.thediscprog.anvil.adt.Criteria
import io.github.thediscprog.anvil.frm.PostgresDataTypes
import java.util.UUID
import io.github.thediscprog.anvil.adt.KeyValue
import io.github.thediscprog.anvil.adt.AND
import io.github.thediscprog.anvil.dialects.DbVendor

class PostgresTableMapTest extends TableMapTest {

  private val postgres: PostgreSQLContainer =
    new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
      .withCopyFileToContainer(
        MountableFile.forClasspathResource("sql/postgres/init-db.sql"),
        "/docker-entrypoint-initdb.d/"
      )

  def dbFixtures: DatabaseFixture[IO] = {
    val dbUrl    = postgres.getJdbcUrl()
    val username = postgres.getUsername()
    val password = postgres.getPassword()
    val config   = new HikariConfig()
    config.setJdbcUrl(dbUrl)
    config.setUsername(username)
    config.setPassword(password)
    val connection = JdbcConnection.openJdbcConnection(config)
    new DatabaseFixture[IO](connection, DbVendor.POSTGRESQL)
  }

  override protected def beforeAll(): Unit = {
    postgres.start()
  }

  override protected def afterAll(): Unit = {
    postgres.stop()
  }

  it should "handle all different PostgreSQL data types" in {
    val connection = dbFixtures.jdbcCon
    val frm        = PostgresDataTypes.postgresDataTypesFrm[IO](connection)
    val uuid       = UUID.randomUUID()
    val uuidKey    = KeyValue[UUID]("col_uuid", uuid)
    val criteria   = Criteria(List(AND(List(uuidKey))))

    val result =
      for {
        rowMaybe <- frm.headOption(Criteria(List()))
        row = rowMaybe match
          case Some(row) => row
          case None      => fail("Failed to fetch a row that should exist")
        rowToAdd = row.copy(colUUID = Some(uuid))
        added  <- frm.add(rowToAdd)
        newRow <- frm.headOption(criteria)
      } yield (row, added, newRow)

    whenReady(result.unsafeToFuture()) { (row, added, newRow) =>
      row.colBigInt shouldBe Some(BigInt("10000000000"))
      newRow.isDefined shouldBe true
      added shouldBe 1
      newRow.value.colUUID shouldBe Some(uuid)
    }

  }

}
