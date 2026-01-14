package io.github.thediscprog.anvil.adt

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.github.thediscprog.anvil.monitor.AnvilMonitor
import kamon.metric.Timer.Started

class CriteriaTest extends AnyFlatSpec, Matchers {
  given monitor: AnvilMonitor = new AnvilMonitor {

    override def selectCall(): Unit = ()

    override def startSelectTimer(): Started = ???

    override def stopTimer(timer: Started): Unit = ()

    override def insertCall(): Unit = ()

    override def startInsertTimer(): Started = ???

    override def updateCall(): Unit = ()

    override def startUpdateTimer(): Started = ???

    override def deleteCall(): Unit = ()

    override def startDeleteTimer(): Started = ???

    override def mismatchPrimaryKeyError(): Unit = ()

    override def unsupportedWhereClause(): Unit = ()

    override def bindingError(): Unit = ()

    override def conversionError(): Unit = ()

    override def memoizationError(): Unit = ()

  }

  it should "send an empty string for no where clause" in {
    val clause = Criteria.getWhereClause(List())

    clause._1 shouldBe ""
    clause._2.isEmpty shouldBe true
  }

  it should "handle greater than operand" in {
    val andClause = AND(List(KeyValue("id", 1, Operand.GREATER_THAN)))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id>?)"
    clause._2 shouldBe List(1)
  }

  it should "handle less than operand" in {
    val andClause = AND(List(KeyValue("id", 1, Operand.LESS_THAN)))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id<?)"
    clause._2 shouldBe Array(1)
  }

  it should "handle LIKE operand" in {
    val andClause = AND(List(KeyValue("id", 1, Operand.LIKE)))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id LIKE ?)"
    clause._2 shouldBe Array(1)
  }

  it should "handle single AND criteria" in {
    val andClause = AND(List(KeyValue("id", 1)))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id=?)"
    clause._2 shouldBe Array(1)
  }

  it should "handle multiple AND criteria" in {
    val andClause = AND(List(KeyValue("id", 1), KeyValue("name", "John")))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id=? AND name=?)"
    clause._2 shouldBe Array(1, "John")
  }

  it should "handle single OR criteria" in {
    val andClause = AND(List(KeyValue("id", 1)))
    val clause    = Criteria.getWhereClause(List(andClause))

    clause._1 shouldBe "WHERE (id=?)"
    clause._2 shouldBe Array(1)
  }

  it should "handle multiple OR criteria" in {
    val orClause = OR(List(KeyValue("id", 1), KeyValue("name", "John")))
    val clause   = Criteria.getWhereClause(List(orClause))

    clause._1 shouldBe "WHERE (id=? OR name=?)"
    clause._2 shouldBe Array(1, "John")
  }

  it should "handle single IN criteria" in {
    val inClause = IN[Int]("id", List(1))
    val clause   = Criteria.getWhereClause(List(inClause))

    clause._1 shouldBe "WHERE (id IN (?))"
    clause._2 shouldBe Array(1)
  }

  it should "handle multiple IN criteria" in {
    val inClause = IN[Int]("id", List(1, 2))
    val clause   = Criteria.getWhereClause(List(inClause))

    clause._1 shouldBe "WHERE (id IN (?,?))"
    clause._2 shouldBe Array(1, 2)
  }

  it should "handle combination of AND, OR & IN" in {
    val inClause  = IN[Int]("id", List(1, 2))
    val orClause  = OR(List(KeyValue("id", 1), KeyValue("name", "John")))
    val andClause = AND(List(KeyValue("id", 2), KeyValue("name", "Alice")))
    val clauses   = Criteria.getWhereClause(List(inClause, orClause, andClause))

    clauses._1 shouldBe "WHERE (id IN (?,?)) AND (id=? OR name=?) AND (id=? AND name=?)"
    clauses._2 shouldBe Array(1, 2, 1, "John", 2, "Alice")
  }
}
