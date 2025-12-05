package io.github.thediscprog.anvil.caching

import cats.Monad
import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import scala.concurrent.duration._
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import java.sql.ResultSetMetaData

sealed trait Memoize[K, V, F[_]] {

  def memoize(k: K)(f: K => F[V]): F[V]

}

class CaffeineMemoize[F[_]: Monad: Logger]
    extends Memoize[String, ResultSetMetaData, F] {

  val cache: Cache[String, ResultSetMetaData] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(1.hour)
      .build[String, ResultSetMetaData]()

  override def memoize(k: String)(
      f: String => F[ResultSetMetaData]
  ): F[ResultSetMetaData] = {
    val value: Option[ResultSetMetaData] = cache.getIfPresent(k)
    if (value == null || value.isEmpty) {
      for {
        _ <- Logger[F].debug(s"Memoize: Storing for $k")
        v <- f(k)
        _ <- cache.put(k, v).pure[F]
      } yield v
    } else {
      value match
        case Some(stored) =>
          Logger[F].debug(s"Memoize: Have stored value for $k") >> stored
            .pure[F]
        case None =>
          throw new RuntimeException(
            "Memoization function failed to get ResultsetMetaData for $k"
          )
    }
  }

}

object Memoize {

  def getMemoizeFunction[F[_]: Monad: Logger] = new CaffeineMemoize[F]()
}
