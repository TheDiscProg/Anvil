import sbt.librarymanagement.CrossVersion
import sbt.url
import xerial.sbt.Sonatype._

import scala.collection.Seq

lazy val scala3LTS = "3.3.7"
lazy val scala37 = "3.7.4"
lazy val scala38 = "3.8.3"
lazy val supportedScalaVersions = List(scala3LTS, scala37, scala38)

lazy val commonSettings = Seq(
  scalaVersion := scala38,
  libraryDependencies ++= Dependencies.all,
  crossScalaVersions := supportedScalaVersions,
  scalacOptions ++= Scalac.options
)

lazy val root = (project in file("."))
  .enablePlugins(
    ScalafmtPlugin
  )
  .settings(
    commonSettings,
    name := "anvil"
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

ThisBuild / version := "0.3.1"
ThisBuild / organization := "io.github.thediscprog"
ThisBuild / organizationName := "thediscprog"
ThisBuild / organizationHomepage := Some(url("https://github.com/TheDiscProg"))

ThisBuild / description := "Anvil - A Functional Relational Mapping library for Databases"

// Sonatype/Maven Publishing
ThisBuild / publishMavenStyle := true
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "io.github.thediscprog"
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
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

addCommandAlias("formatAll", ";scalafmt;test:scalafmt;integrationTest/test:scalafmt;")
addCommandAlias("cleanAll", ";clean;integrationTest/clean")
addCommandAlias("itTest", ";integrationTest/clean;integrationTest/test:scalafmt;integrationTest/test")
addCommandAlias("cleanTest", ";clean;scalafmt;test:scalafmt;test;")
addCommandAlias("testAll", ";cleanAll;formatAll;test;itTest;")
addCommandAlias("cleanCoverage", ";cleanAll;formatAll;coverage;testAll;coverageReport;")