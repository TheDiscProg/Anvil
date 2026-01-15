import sbt._

object Dependencies {

  private lazy val catsEffectVersion = "3.6.3"
  private lazy val hikariVersion = "7.0.2"
  private lazy val scalacticVersion = "3.2.19"
  private lazy val scalaLoggingVersion = "3.9.5"
  private lazy val scaffeineVersion = "5.3.0"
  private lazy val logbackClassicVersion = "1.5.21"
  private lazy val log4catsVersion = "2.7.1"
  private lazy val postgresVersion = "42.7.8"
  private lazy val mysqlVersion = "9.5.0"
  private lazy val testcontainerVersion = "2.0.1"
  private lazy val kamonVersion = "2.8.0"
  private lazy val kanelaAgentVersion = "2.0.0"

  lazy val all = Seq(
    "com.zaxxer" % "HikariCP" % hikariVersion,
    "io.kamon" %% "kamon-core" % kamonVersion,
    "io.kamon" %% "kamon-jdbc" % kamonVersion,
    "org.typelevel" %% "log4cats-core"    % log4catsVersion,
    "org.typelevel" %% "log4cats-slf4j"   % log4catsVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "com.github.blemale" %% "scaffeine" % scaffeineVersion,
    "ch.qos.logback" % "logback-classic" % logbackClassicVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.scalactic" %% "scalactic" % scalacticVersion,
    "org.scalatest" %% "scalatest" % scalacticVersion % Test
  )

  lazy val it = Seq(
    "org.postgresql" % "postgresql" % postgresVersion % Test,
    "org.testcontainers" % "testcontainers-postgresql" % testcontainerVersion % Test,
    "com.mysql" % "mysql-connector-j" % mysqlVersion % Test,
    "org.testcontainers" % "testcontainers-mysql" % testcontainerVersion % Test
  )
}
