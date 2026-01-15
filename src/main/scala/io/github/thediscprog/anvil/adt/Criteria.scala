package io.github.thediscprog.anvil.adt

import io.github.thediscprog.anvil.monitor.AnvilMonitor

case class Criteria(criteria: List[Where])

object Criteria {

  enum CONDITION(val toStr: String) {
    case NONE extends CONDITION("")
    case AND  extends CONDITION("AND")
    case OR   extends CONDITION("OR")
    case IN   extends CONDITION("IN")
  }

  def getWhereClause(
      criteria: List[Where]
  )(using monitor: AnvilMonitor): (String, List[Any]) = {
    if (criteria.isEmpty) {
      ("", List())
    } else {
      val (whereClause, values) = extractClauses(criteria)
      ("WHERE " + whereClause, values.toList)
    }
  }

  private def extractClauses(
      criteria: List[Where]
  )(using monitor: AnvilMonitor): (String, Array[Any]) = {
    def loop(
        crit: List[Where],
        whereAcc: Array[String],
        argAcc: Array[Any]
    ): (String, Array[Any]) = {
      crit match
        case Nil          => (whereAcc.mkString(" AND "), argAcc)
        case head :: next =>
          head match
            case AND(clause) => {
              val and       = handleAndClause(clause)
              val extracted = extractValues(clause)
              loop(next, whereAcc ++ Array(and), argAcc ++ extracted)
            }
            case OR(clause) => {
              val or        = handleOrClause(clause)
              val extracted = extractValues(clause)
              loop(next, whereAcc ++ Array(or), argAcc ++ extracted)
            }
            case inClause: IN[?] => {
              val in        = handleInClause(inClause)
              val extracted = extractValuesFromInClause(inClause)
              loop(next, whereAcc ++ Array(in), argAcc ++ extracted)
            }
            case a =>
              monitor.unsupportedWhereClause()
              throw new RuntimeException(
                s"Unsupported clause for SQL WHERE: $a"
              )

    }
    loop(criteria, Array(), Array())
  }

  private def extractValues(kvs: List[KeyValue[?]]): Array[Any] =
    kvs.map(_.value).toArray

  private def extractValuesFromInClause(inClause: IN[?]): Array[Any] =
    inClause.values.toArray

  private def handleAndClause(clauses: List[KeyValue[?]]): String =
    combineClause(CONDITION.AND, clauses)

  private def handleOrClause(clauses: List[KeyValue[?]]): String =
    combineClause(CONDITION.OR, clauses)

  private def handleInClause(inClause: IN[?]): String =
    s"(${inClause.columnName} IN (${inClause.values.map(_ => "?").mkString(",")}))"

  private def combineClause(
      joinWith: CONDITION,
      clauses: List[KeyValue[?]]
  ): String = {
    val combined = clauses
      .map { kv =>
        s"${kv.key}${kv.operand.sign}?"
      } mkString (s" ${joinWith.toStr} ")
    s"($combined)"
  }
}
