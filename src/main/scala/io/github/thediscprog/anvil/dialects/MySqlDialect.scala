package io.github.thediscprog.anvil.dialects

class MySqlDialect extends StandardSql {
  override val specialIdentifierCharacter: Char = '`'
}
