package io.github.thediscprog.anvil.i18n

import java.text.MessageFormat
import java.util.{Locale, ResourceBundle}

object AnvilMessage {

  private val baseName = "messages"

  def apply(
      key: String,
      args: Seq[Any] = Nil
  )(using locale: Locale = Locale.getDefault): String = {

    val bundle  = ResourceBundle.getBundle(baseName, locale)
    val pattern = bundle.getString(key)

    MessageFormat.format(pattern, args.map(_.asInstanceOf[AnyRef])*)
  }

}
