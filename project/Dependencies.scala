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

  lazy val all = Seq(
    "com.zaxxer" % "HikariCP" % hikariVersion,
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
    "org.postgresql" % "postgresql" % postgresVersion % Test
  )
}
