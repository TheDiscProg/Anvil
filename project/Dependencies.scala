import sbt._

object Dependencies {

  private lazy val scalacticVersion = "3.2.19"

  lazy val all = Seq(
    "org.scalactic" %% "scalactic" % scalacticVersion,
    "org.scalatest" %% "scalatest" % scalacticVersion % Test,
  )
}
