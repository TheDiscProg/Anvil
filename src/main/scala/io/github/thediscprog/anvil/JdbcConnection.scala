package io.github.thediscprog.anvil

import cats.effect.kernel.Async
import cats.syntax.all.*
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection

//lll

object JdbcConnection {

  def openJdbcConnectionF[F[_]: Async](config: HikariConfig): F[Connection] = {
    openJdbcConnection(config).pure[F]
  }

  def closeJdbcConnectionF[F[_]: Async](connection: Connection): F[Unit] =
    closeJdbcConnection(connection).pure[F]

  def openJdbcConnection(config: HikariConfig): Connection = {
    val dataSource = new HikariDataSource(config)
    dataSource.getConnection
  }

  def closeJdbcConnection(connection: Connection): Unit =
    connection.close()

}
