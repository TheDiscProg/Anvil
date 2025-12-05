import sbt.librarymanagement.CrossVersion
import sbt.url
import xerial.sbt.Sonatype._

import scala.collection.Seq

lazy val scala2 = "2.13.15"
lazy val scala3 = "3.7.3"
lazy val supportedScalaVersions = List(scala2, scala3)

lazy val commonSettings = Seq(
  scalaVersion := scala3,
  libraryDependencies ++= Dependencies.all
)

lazy val root = (project in file("."))
  .enablePlugins(
    ScalafmtPlugin
  )
  .settings(
    commonSettings,
    name := "anvil",
    scalacOptions ++= Scalac.options,
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2,13)) => Seq("-Ytasty-reader")
        case _ => Seq("-Yretain-trees")
      }
    },
    crossScalaVersions := supportedScalaVersions
  )

lazy val integrationTest = (project in file("it"))
  .enablePlugins(ScalafmtPlugin)
  .settings(
    commonSettings,
    name := "anvil-integration-test",
    publish / skip := true,
    libraryDependencies ++= Dependencies.it,
    parallelExecution := false,
    coverageFailOnMinimum := true,
    coverageMinimumStmtTotal := 85,
    coverageMinimumBranchTotal := 80
  )
  .dependsOn(root % "test->test; compile->compile")
  .aggregate(root)

ThisBuild / version := "0.0.0"
ThisBuild / organization := "io.github.thediscprog"
ThisBuild / organizationName := "thediscprog"
ThisBuild / organizationHomepage := Some(url("https://github.com/TheDiscProg"))

ThisBuild / description := "Anvil - A Functional library for Databases"

// Sonatype/Maven Publishing
ThisBuild / publishMavenStyle := true
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "io.github.thediscprog"
ThisBuild / licenses := List("GNU-3.0" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html"))
ThisBuild / homepage := Some(url("https://github.com/TheDiscProg/anvil"))
ThisBuild / sonatypeProjectHosting := Some(GitHubHosting("TheDiscProg", "anvil", "TheDiscProg@gmail.com"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TheDiscProg/anvil"),
    "scm:git@github.com:thediscprog/anvil.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "thediscprog",
    name = "TheDiscProg",
    email = "TheDiscProg@gmail.com",
    url = url("https://github.com/TheDiscProg")
  )
)

usePgpKeyHex("FC6901A47E5DA2533DCF25D51615DCC33B57B2BF")

sonatypeCredentialHost := "central.sonatype.com"
sonatypeRepository := "https://central.sonatype.com/api/v1/publisher/"

ThisBuild / versionScheme := Some("early-semver")

addCommandAlias("cleanTest", ";clean;scalafmt;test:scalafmt;test;")
addCommandAlias("cleanCoverage", ";clean;scalafmt;test:scalafmt;coverage;test;coverageReport;")