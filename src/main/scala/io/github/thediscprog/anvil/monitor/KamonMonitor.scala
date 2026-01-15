package io.github.thediscprog.anvil.monitor

import kamon.Kamon
import kamon.metric.Timer.Started

class KamonMonitor(table: String) extends AnvilMonitor {

  override def selectCall(): Unit =
    Kamon.counter(AnvilMetrics.select(table)).withoutTags().increment()

  override def startSelectTimer(): Started =
    Kamon.timer(AnvilMetrics.selectTimer(table)).withoutTags().start()

  override def insertCall(): Unit =
    Kamon.counter(AnvilMetrics.insert(table)).withoutTags().increment()

  override def startInsertTimer(): Started =
    Kamon.timer(AnvilMetrics.insertTimer(table)).withoutTags().start()

  override def updateCall(): Unit =
    Kamon.counter(AnvilMetrics.update(table)).withoutTags().increment()

  override def startUpdateTimer(): Started =
    Kamon.timer(AnvilMetrics.updateTimer(table)).withoutTags().start()

  override def deleteCall(): Unit =
    Kamon.counter(AnvilMetrics.delete(table)).withoutTags().increment()

  override def startDeleteTimer(): Started =
    Kamon.timer(AnvilMetrics.deleteTimer(table)).withoutTags().start()

  override def stopTimer(timer: Started): Unit = timer.stop()

  override def mismatchPrimaryKeyError(): Unit =
    Kamon.counter(AnvilMetrics.pkMismatch(table)).withoutTags().increment()

  override def unsupportedWhereClause(): Unit = Kamon
    .counter(AnvilMetrics.unsupportedWhere(table))
    .withoutTags()
    .increment()

  override def bindingError(): Unit =
    Kamon.counter(AnvilMetrics.bindingError(table)).withoutTags().increment()

  override def conversionError(): Unit =
    Kamon.counter(AnvilMetrics.conversionError(table)).withoutTags().increment()

  override def memoizationError(): Unit =
    Kamon
      .counter(AnvilMetrics.memoizationError(table))
      .withoutTags()
      .increment()

}
