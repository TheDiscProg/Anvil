package io.github.thediscprog.anvil.monitor

object AnvilMetrics {

  def select(table: String) =
    s"anvil.$table.select"

  def selectTimer(table: String) =
    s"anvil.$table.select.timer"

  def insert(table: String) =
    s"anvil.$table.insert"

  def insertTimer(table: String) =
    s"anvil.$table.insert.timer"

  def update(table: String) =
    s"anvil.$table.update"

  def updateTimer(table: String) =
    s"anvil.$table.update.timer"

  def delete(table: String) =
    s"anvil.$table.delete"

  def deleteTimer(table: String) =
    s"anvil.$table.delete.timer"

  def pkMismatch(table: String) =
    s"anvil.$table.error.pk.mismatch"

  def unsupportedWhere(table: String) =
    s"anvil.$table.error.where.unsupported"

  def bindingError(table: String) =
    s"anvil.$table.error.binding"

  def conversionError(table: String) =
    s"anvil.$table.error.conversion"

  def memoizationError(table: String) =
    s"anvil.$table.error.memoization"

  val jdbcReaderNotFound = "anvil.reader.notfound"

  def typeConversionError(tpe: String) = s"anvil.conversion.error.$tpe"
}
