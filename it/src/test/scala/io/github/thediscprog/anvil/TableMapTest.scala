package io.github.thediscprog.anvil

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Span
import org.scalatest.time.Seconds
import org.scalatest.time.Millis
import org.typelevel.log4cats.SelfAwareLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.IO
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.scalatest.BeforeAndAfterAll
import com.zaxxer.hikari.HikariConfig
import java.sql.Connection
import org.testcontainers.utility.MountableFile
import io.github.thediscprog.anvil.frm.*
import io.github.thediscprog.anvil.adt.TableMapping
import io.github.thediscprog.anvil.adt.KeyValue
import io.github.thediscprog.anvil.adt.Criteria
import io.github.thediscprog.anvil.adt.AND
import io.github.thediscprog.anvil.adt.Operand
import cats.effect.unsafe.implicits.global
import org.scalatest.OptionValues
import java.time.LocalDate
import java.util.UUID
import java.time.LocalDateTime
import cats.Monad
import org.typelevel.log4cats.Logger
import scala.concurrent.Future
import io.github.thediscprog.anvil.jdbcutils.JdbcConnection

abstract class TableMapTest()
    extends AnyFlatSpec
    with Matchers
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterAll {

  given defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(20, Seconds), interval = Span(100, Millis))

  given unsafeLogger: SelfAwareStructuredLogger[IO] =
    Slf4jLogger.getLogger[IO]

  def dbFixtures: DatabaseFixture[IO]

  it should "Read from table rows matching the where clause" in {
    val repo        = dbFixtures
    val customerFrm = repo.customerFRM

    val criteron = KeyValue[Long]("customer_id", 1L, Operand.EQ)
    val criteria = Criteria(List(AND(List(criteron))))

    val customers = customerFrm /? criteria

    whenReady(customers.unsafeToFuture()) { cstm =>
      cstm.isDefined shouldBe true
    }
  }

  it should "Read from table all rows" in {
    val repo        = dbFixtures
    val userTypeFrm = repo.userTypeFRM

    val userTypes = userTypeFrm.filter(Criteria(List()))

    whenReady(userTypes.unsafeToFuture()) { userTpes =>
      userTpes.size shouldBe 5
      userTpes.map(_.typeKey) should contain allElementsOf List(
        "CST",
        "CSP",
        "ENG",
        "SLF",
        "ADM"
      )
    }
  }

  it should "Read from table distinct rows" in {
    val repo = dbFixtures
    val frm  = repo.carModelFRM

    val carModels = frm.filter(Criteria(List()), true)

    whenReady(carModels.unsafeToFuture()) { models =>
      models.size shouldBe 4
      models.map(_.modelId.toString()) should contain allElementsOf List(
        "87dd82a8-31fa-41ac-bc6c-c4eec8b6386f",
        "c483a731-6970-41a1-9354-29e23c132667",
        "fb7b56b7-4fb8-4fd0-a561-74870d31d0d2",
        "f7b4f080-ba57-46e7-8bd7-22f85d011bf6"
      )
    }
  }

  it should "Carry out full CRUD in a for comprehension" in {
    val repo               = dbFixtures
    val customerFRM        = repo.customerFRM
    val addressFRM         = repo.addressFRM
    val customerAddressFrm = repo.customerAddressFrm

    val andrewS = Customer(
      0,
      "Mr",
      "Andrew",
      "Smithy",
      "andrew@test.com",
      "07777 777 777",
      LocalDate.parse("2025-11-20"),
      Some("New to bussiness")
    )
    val addressAS =
      Address(4L, "24 The Road", Some("Newtown"), "Middlesex", "MS1 1TT", "UK")

    val email = KeyValue[String]("contact_email", "andrew@test.com", Operand.EQ)
    val mobile = KeyValue[String]("contact_mobile", "07777 777 777", Operand.EQ)
    val customerCriteria = Criteria(List(AND(List(email, mobile))))

    val postcode        = KeyValue("post_code", "MS1 1TT")
    val addressCriteria = Criteria(List(AND(List(postcode))))

    val result = for {
      numberCAdded  <- customerFRM.add(andrewS)
      _             <- unsafeLogger.info(s"Adding address")
      numberAAdded  <- addressFRM <+ addressAS
      addedCustomer <- customerFRM.headOption(customerCriteria)
      addedAddress  <- addressFRM /? addressCriteria

      customerId = addedCustomer match
        case Some(aCustomer) => aCustomer.customerId
        case None            =>
          fail("No customer matched the criteria for the added customer")

      addressId = addedAddress match
        case Some(anAddress) => anAddress.addressId
        case None            =>
          fail("No address matched the criteria for the added address")

      customerAddress = CustomerAddress(
        customerId,
        addressId,
        Some("New temporary customer")
      )
      numCAAdded <- customerAddressFrm <+ customerAddress

      customerIdCriteria = KeyValue("customer_id", customerId)
      addressIdCriteria  = KeyValue("address_id", addressId)

      custAddCriteria = Criteria(
        List(AND(List(customerIdCriteria, addressIdCriteria)))
      )
      updateComment = KeyValue[Any]("comment", Some("New permanent customer"))
      numberUpdated <- customerAddressFrm.updateWhere(custAddCriteria)(
        List(updateComment)
      )
      updatedComment <- customerAddressFrm /? custAddCriteria

      droppedCA  <- customerAddressFrm <-- custAddCriteria
      droppedAdd <- addressFRM.deleteWhere(
        Criteria(List(AND(List(addressIdCriteria))))
      )
      droppedCus <- customerFRM.deleteWhere(
        Criteria(List(AND(List(customerIdCriteria))))
      )

    } yield (
      addedCustomer,
      addedAddress,
      numCAAdded,
      updatedComment,
      droppedCA,
      droppedAdd,
      droppedCus
    )

    whenReady(result.unsafeToFuture()) { (ac, aa, ncaa, uc, dca, da, dc) =>
      ac.value.contactEmail shouldBe andrewS.contactEmail
      aa.value.postCode shouldBe addressAS.postCode
      ncaa shouldBe 1
      uc.value.comment.value shouldBe "New permanent customer"
      dca shouldBe 1
      da shouldBe 1
      dc shouldBe 1
    }

  }

  it should "Handle UUID as primary keys" in {
    val repo           = dbFixtures
    val customerFRM    = repo.customerFRM
    val usersFrm       = repo.usersFRM
    val customerCarFRM = repo.customerCarFRM
    val userTypeFRM    = repo.userTypeFRM

    val andrewS = Customer(
      0,
      "Mr",
      "Andrew",
      "Smithy",
      "andrew@test.com",
      "07777 777 777",
      LocalDate.parse("2025-11-20"),
      Some("New to bussiness")
    )
    val userType =
      UserType("SER", "Customer Service", Some("Customer service staff"))
    val uuid = UUID.randomUUID()
    val user = User(
      uuid,
      "andrewsmithy",
      andrewS.contactEmail,
      true,
      false,
      Some(LocalDateTime.now()),
      0,
      "SER"
    )

    val email = KeyValue[String]("contact_email", "andrew@test.com", Operand.EQ)
    val mobile = KeyValue[String]("contact_mobile", "07777 777 777", Operand.EQ)
    val customerCriteria = Criteria(List(AND(List(email, mobile))))

    val userKey          = KeyValue("type_key", "SER")
    val userTypeCriteria = Criteria(List(AND(List(userKey))))

    val result = for {
      numUserTypeAdded <- userTypeFRM <+ userType
      serMaybe         <- userTypeFRM /? userTypeCriteria
      serType = serMaybe match
        case Some(value) => value
        case None => fail("No user type matched the added user type criteria")
      numberCustAdded <- customerFRM <+ andrewS
      addedCustomer   <- customerFRM.headOption(customerCriteria)
      customerId = addedCustomer match
        case Some(value) => value.customerId
        case None        => fail("No customer found that matched criteria")

      userCriteria = Criteria(
        List(AND(List(KeyValue("customer_id", customerId))))
      )
      numberUserAdded <- usersFrm <+ user.copy(customerId = customerId)
      addedUser       <- usersFrm /? userCriteria

      droppedUser     <- usersFrm <-- userCriteria
      droppedST       <- userTypeFRM <-- userTypeCriteria
      droppedCustomer <- customerFRM <-- customerCriteria
    } yield (
      numUserTypeAdded,
      numUserTypeAdded,
      addedCustomer,
      numberUserAdded,
      droppedUser,
      droppedST,
      droppedCustomer
    )

    whenReady(result.unsafeToFuture()) {
      (
          userTypeAdded,
          typeAdded,
          customer,
          userAdded,
          droppedUsr,
          droppedUT,
          droppedCust
      ) =>
        userTypeAdded shouldBe 1
        typeAdded shouldBe 1
        customer.value.contactEmail shouldBe andrewS.contactEmail
        userAdded shouldBe 1
        droppedUsr shouldBe 1
        droppedUT shouldBe 1
        droppedCust shouldBe 1
    }

  }

}
