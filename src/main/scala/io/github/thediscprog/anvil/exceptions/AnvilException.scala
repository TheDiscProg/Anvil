package io.github.thediscprog.anvil.exceptions

sealed abstract class AnvilException(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)

object AnvilException {
  final case class BindingError(msg: String)       extends AnvilException(msg)
  final case class ConversionError(msg: String)    extends AnvilException(msg)
  final case class MappingError(msg: String)       extends AnvilException(msg)
  final case class MemoizationError(msg: String)   extends AnvilException(msg)
  final case class NoReaderFoundError(msg: String) extends AnvilException(msg)
  final case class UnsupportedWhereClause(msg: String)
      extends AnvilException(msg)
}
