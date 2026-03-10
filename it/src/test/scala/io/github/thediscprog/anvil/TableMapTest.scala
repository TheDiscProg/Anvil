package io.github.thediscprog.anvil

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.github.thediscprog.anvil.adt.{AND, Criteria, KeyValue, Operand}
import io.github.thediscprog.anvil.frm.*
import kamon.Kamon
import org.scalatest.{BeforeAndAfterAll, OptionValues}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

abstract class TableMapTest()
    extends AnyFlatSpec
    with Matchers
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterAll {

  Kamon.init()

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
      cstm.isDefined should be(true)
    }
  }

  it should "Read from table all rows" in {
    val repo        = dbFixtures
    val userTypeFrm = repo.userTypeFRM

    val userTypes = userTypeFrm.filter(Criteria(List()))

    whenReady(userTypes.unsafeToFuture()) { userTpes =>
      userTpes.size should be(5)
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
      models.size should be(4)
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
      ncaa should be(1)
      uc.value.comment.value shouldBe "New permanent customer"
      dca should be(1)
      da should be(1)
      dc should be(1)
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
        userTypeAdded should be(1)
        typeAdded should be(1)
        customer.value.contactEmail shouldBe andrewS.contactEmail
        userAdded should be(1)
        droppedUsr should be(1)
        droppedUT should be(1)
        droppedCust should be(1)
    }

  }

}
