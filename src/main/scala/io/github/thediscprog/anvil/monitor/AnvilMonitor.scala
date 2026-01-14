package io.github.thediscprog.anvil.monitor

import kamon.Kamon
import kamon.metric.Timer.Started

trait AnvilMonitor {

  def selectCall(): Unit

  def startSelectTimer(): Started

  def stopTimer(timer: Started): Unit

  def insertCall(): Unit

  def startInsertTimer(): Started

  def updateCall(): Unit

  def startUpdateTimer(): Started

  def deleteCall(): Unit

  def startDeleteTimer(): Started

  def mismatchPrimaryKeyError(): Unit

  def unsupportedWhereClause(): Unit

  def bindingError(): Unit

  def conversionError(): Unit

  def memoizationError(): Unit
}

object AnvilMonitor {

  def getMonitor(table: String): AnvilMonitor = new KamonMonitor(table)

  def counter(counter: String): Unit =
    Kamon.counter(counter).withoutTags().increment()
}
