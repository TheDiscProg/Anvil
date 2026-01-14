package io.github.thediscprog.anvil.caching

import cats.Monad
import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import scala.concurrent.duration._
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import io.github.thediscprog.anvil.monitor.AnvilMonitor

sealed trait Memoize[K, F[_]] {

  def has(k: K): Boolean

  def memoize[V](k: K)(f: K => F[V])(using monitor: AnvilMonitor): F[V]

}

class CaffeineMemoize[F[_]: {Monad, Logger}] extends Memoize[String, F] {

  val cache: Cache[String, Any] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(1.hour)
      .build[String, Any]()

  override def has(k: String): Boolean = cache.getIfPresent(k) match
    case Some(_) => true
    case _       => false

  override def memoize[V](k: String)(
      f: String => F[V]
  )(using monitor: AnvilMonitor): F[V] = {
    val value = cache.getIfPresent(k)
    if (value == null || value.isEmpty) {
      for {
        _ <- Logger[F].debug(s"Memoize: Storing for $k")
        v <- f(k)
        _ <- cache.put(k, v).pure[F]
      } yield v
    } else {
      value match
        case Some(stored) =>
          Logger[F].debug(s"Memoize: Have stored value for $k") >> (stored
            .asInstanceOf[V])
            .pure[F]
        case None =>
          monitor.memoizationError()
          throw new RuntimeException(
            "Memoization function failed to get value for $k"
          )
    }
  }

}

object Memoize {

  def getMemoizeFunction[F[_]: {Monad, Logger}] = new CaffeineMemoize[F]()
}
