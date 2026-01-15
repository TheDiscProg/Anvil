package io.github.thediscprog.anvil.monitor

import kamon.Kamon
import kamon.metric.Timer.Started

class KamonMonitor(table: String) extends AnvilMonitor {

  override def selectCall(): Unit =
    Kamon.counter(s"anvil.$table.select").withoutTags().increment()

  override def startSelectTimer(): Started =
    Kamon.timer(s"anvil.$table.select.timer").withoutTags().start()
  override def insertCall(): Unit =
    Kamon.counter(s"anvil.$table.insert").withoutTags().increment()
  override def startInsertTimer(): Started =
    Kamon.timer(s"anvil.$table.insert.timer").withoutTags().start()

  override def updateCall(): Unit =
    Kamon.counter(s"anvil.$table.update").withoutTags().increment()

  override def startUpdateTimer(): Started =
    Kamon.timer(s"anvil.$table.update.timer").withoutTags().start()

  override def deleteCall(): Unit =
    Kamon.counter(s"anvil.$table.delete").withoutTags().increment()

  override def startDeleteTimer(): Started =
    Kamon.timer(s"anvil.$table.delete.timer").withoutTags().start()

  override def stopTimer(timer: Started): Unit = timer.stop()

  override def mismatchPrimaryKeyError(): Unit =
    Kamon.counter(s"anvil.$table.error.pk.mismatch").withoutTags().increment()

  override def unsupportedWhereClause(): Unit = Kamon
    .counter(s"anvil.$table.error.where.unsupported")
    .withoutTags()
    .increment()

  override def bindingError(): Unit =
    Kamon.counter(s"anvil.$table.error.binding").withoutTags().increment()

  override def conversionError(): Unit =
    Kamon.counter(s"anvil.$table.error.conversion").withoutTags().increment()

  override def memoizationError(): Unit =
    Kamon.counter(s"anvil.$table.error.memoization").withoutTags().increment()

}
