package io.github.thediscprog.anvil.exceptions

object AnvilMessageKey {
  val BindingEmptyListError       = "anvil.binding.unknowntype.emptylist"
  val ColumnMismatchError         = "anvil.mapping.colummismatch"
  val ConversionError             = "anvil.mapping.conversionfailed"
  val MemoizationError            = "anvil.memoization.failed"
  val NoReaderFoundError          = "anvil.reader.noreaderfound"
  val UnsupportedDBTypeError      = "anvil.mapping.unsupportedtype"
  val UnsupportedWhereClauseError = "anvil.where.unsupported"

}
