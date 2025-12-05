package io.github.thediscprog.anvil

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.zaxxer.hikari.HikariConfig
import io.github.thediscprog.anvil.adt.Operand.EQ
import io.github.thediscprog.anvil.adt.{Criteria, KeyValue, TableMapping}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*
import org.scalatest.time.{Millis, Seconds, Span}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scala.deriving.Mirror
import java.sql.Connection
import io.github.thediscprog.anvil.adt.AND
import scala.runtime.EmptyMethodCache
import io.github.thediscprog.anvil.adt.IN
import javax.xml.crypto.Data
import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues
import io.github.thediscprog.anvil.adt.Operand

class JdbcTest
    extends AnyFlatSpec
    with Matchers
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterAll {
  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(30, Seconds), interval = Span(100, Millis))

  private given unsafeLogger: SelfAwareStructuredLogger[IO] =
    Slf4jLogger.getLogger[IO]

  val config = new HikariConfig()
  config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres")
  config.setUsername("docker")
  config.setPassword("docker")

  val dbConn: Connection = JdbcConnection.openJdbcConnection(config)
  override protected def afterAll(): Unit = {
    JdbcConnection.closeJdbcConnection(dbConn)
  }

  it should "open and close connection without any error" in {
    val result =
      for {
        connection <- JdbcConnection.openJdbcConnectionF[IO](config)
        result     <- JdbcConnection.closeJdbcConnectionF[IO](connection)
      } yield result

    whenReady(result.unsafeToFuture()) { r =>
      r should be(())
    }
  }

  it should "read from database table using case class property name" in {
    val userTable: TableMapping[IO, User] =
      User.userTableMapping(dbConn)
    val criteron               = KeyValue[Int]("id", 1, EQ)
    val criteria               = Criteria(List(AND(List(criteron))))
    val result: IO[List[User]] = userTable filter criteria

    whenReady(result.unsafeToFuture()) { r =>
      r.size should be(1)
      r.head.id shouldBe 1
    }
  }

  it should "read from database table using supplied column names" in {
    val employees: TableMapping[IO, Employee] =
      Employee.employeeMapping(dbConn)
    val criteron = KeyValue[Boolean]("active", true)
    val criteria = Criteria(List(AND(List(criteron))))
    val result   = employees filter criteria

    whenReady(result.unsafeToFuture()) { r =>
      r.size shouldBe 2
      r.foreach(println)
      r.map(_.isEmployed) shouldBe List(true, true)
    }
  }

  it should "read from database table handling strings" in {
    val userTable: TableMapping[IO, User] =
      User.userTableMapping(dbConn)
    val criteria = Criteria(List(IN[String]("name", List("Alice", "Bob"))))
    val result   = userTable.filter(criteria)

    whenReady(result.unsafeToFuture()) { r =>
      r.size should be(2)
      val names = r.map(_.name)
      names should contain("Alice")
      names should contain("Bob")
    }
  }

  it should "read distinct rows from a table" in {}

  it should "fetch a unique row from the table as an option" in {}

  it should "return a none for a unique fetch when it fails to find a matching row" in {}

  it should "insert into the database when the key is defined" in {
    val userTable: TableMapping[IO, User] =
      User.userTableMapping(dbConn)
    val newUser =
      User(id = 6, name = "Robert", age = None, hobbies = List("Astronomy"))
    val criteria = Criteria(List(AND(List(KeyValue("name", newUser.name)))))
    val result   =
      for {
        n <- userTable <+ newUser
        r <- userTable.headOption(criteria)
      } yield (n, r)

    whenReady(result.unsafeToFuture()) { (n, u) =>
      n shouldBe 1
      u.isDefined shouldBe true
      u.value.id == 6 shouldBe true
      u.value shouldBe newUser.copy(age = Some(0))
    }
  }
}
