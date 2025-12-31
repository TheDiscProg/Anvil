package io.github.thediscprog.anvil.caching

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.IO
import org.typelevel.log4cats.SelfAwareStructuredLogger
import java.sql.ResultSetMetaData
import org.scalatest.concurrent.ScalaFutures
import cats.effect.unsafe.implicits.global

class MemoizeTest extends AnyFlatSpec, Matchers, ScalaFutures {

  private given unsafeLogger: SelfAwareStructuredLogger[IO] =
    Slf4jLogger.getLogger[IO]

  val memoizeFunction = Memoize.getMemoizeFunction[IO]

  it should "return a stored value" in {
    val result =
      (memoizeFunction.memoize("key")(_ => IO(metadata))).unsafeToFuture()

    whenReady(result) { r =>
      r.getScale(1) shouldBe 99
    }
  }

  val metadata = new ResultSetMetaData() {

    override def getScale(column: Int): Int = 99

    override def getColumnDisplaySize(column: Int): Int = ???

    override def getPrecision(column: Int): Int = ???

    override def getColumnTypeName(column: Int): String = ???

    override def isDefinitelyWritable(column: Int): Boolean = ???

    override def isWritable(column: Int): Boolean = ???

    override def isCurrency(column: Int): Boolean = ???

    override def getColumnCount(): Int = ???

    override def isCaseSensitive(column: Int): Boolean = ???

    override def getTableName(column: Int): String = ???

    override def getSchemaName(column: Int): String = ???

    override def isSigned(column: Int): Boolean = ???

    override def isNullable(column: Int): Int = ???

    override def getColumnClassName(column: Int): String = ???

    override def getCatalogName(column: Int): String = ???

    override def isReadOnly(column: Int): Boolean = ???

    override def getColumnType(column: Int): Int = ???

    override def getColumnLabel(column: Int): String = ???

    override def getColumnName(column: Int): String = ???

    override def isSearchable(column: Int): Boolean = ???

    override def isAutoIncrement(column: Int): Boolean = ???

    override def isWrapperFor(iface: Class[?]): Boolean = ???

    override def unwrap[T](iface: Class[T]): T = ???

  }
}
