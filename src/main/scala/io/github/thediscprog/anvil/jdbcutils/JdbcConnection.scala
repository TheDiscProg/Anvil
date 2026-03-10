package io.github.thediscprog.anvil.jdbcutils

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object JdbcConnection {

  def getHikariDataSource(config: HikariConfig): HikariDataSource =
    new HikariDataSource(config)

}
