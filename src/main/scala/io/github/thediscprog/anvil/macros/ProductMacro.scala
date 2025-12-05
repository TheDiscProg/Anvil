package io.github.thediscprog.anvil.macros

import scala.deriving.Mirror
import compiletime.*

object ProductMacro {

  inline def getColumnNames[L <: Tuple]: List[String] = {
    inline erasedValue[L] match {
      case _: EmptyTuple   => List[String]()
      case lab: (lh *: lt) =>
        val labelName = constValue[lh].toString()
        labelName +: getColumnNames[lt]
    }
  }

  inline def fieldNamesOf[A](using
      m: Mirror.ProductOf[A]
  ): List[String] =
    constValueTuple[m.MirroredElemLabels].toList.asInstanceOf[List[String]]

}
