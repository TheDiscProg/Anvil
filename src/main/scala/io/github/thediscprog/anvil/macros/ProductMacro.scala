package io.github.thediscprog.anvil.macros

import scala.deriving.Mirror
import compiletime.*
import scala.quoted.Type
import scala.quoted.Quotes
import scala.quoted.Expr

object ProductMacro {

  inline def getColumnNames[L <: Tuple]: List[String] =
    constValueTuple[L].toList.map(_.toString())

  inline def fieldNamesOf[A](using
      m: Mirror.ProductOf[A]
  ): List[String] =
    constValueTuple[m.MirroredElemLabels].toList.map(_.toString())

  inline def getCaseClassLabels[T]: List[String] =
    ${ getCaseClassLabelsImpl[T] }

  private def getCaseClassLabelsImpl[T: Type](using
      q: Quotes
  ): Expr[List[String]] = {
    import q.reflect.*
    val tpe = TypeRepr.of[T]
    val sym = tpe.typeSymbol
    if (!sym.flags.is(Flags.Case)) {
      report.error(
        s"${sym.name} is not a case class - only case class can be used in this context"
      )
      '{ Nil }
    } else {
      val fields = sym.primaryConstructor.paramSymss.flatten.map(_.name)
      Expr.ofList(fields.map(Expr(_)))
    }
  }

  inline def getFieldLabelAndTypes[T]: List[(String, String)] =
    ${ getFieldLabelAndTypesImpl[T] }

  private def getFieldLabelAndTypesImpl[T: Type](using
      q: Quotes
  ): Expr[List[(String, String)]] = {
    import q.reflect.*
    val tpe = TypeRepr.of[T]
    val sym = tpe.typeSymbol
    if (!sym.flags.is(Flags.Case)) {
      report.error(
        s"${sym.name} is not a case class - only case classes can be used for mapping"
      )
      '{ Nil }
    } else {
      val fields = sym.caseFields
      Expr.ofList(
        fields.map { f =>
          val tpe = f.tree.asInstanceOf[ValDef].tpt.tpe
          Expr((f.name, tpe.typeSymbol.name))
        }
      )
    }
  }

}
