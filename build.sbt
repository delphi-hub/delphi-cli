ThisBuild / organization := "de.upb.cs.swt.delphi"
ThisBuild / organizationName := "Delphi Project"
ThisBuild / organizationHomepage := Some(url("https://delphi.cs.uni-paderborn.de/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/delphi-hub/delphi-cli"),
    "scm:git@github.com:delphi-hub/delphi-cli.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "bhermann",
    name  = "Ben Hermann",
    email = "ben.hermann@upb.de",
    url   = url("https://www.thewhitespace.de")
  )
)

ThisBuild / description := "The command line client for Delphi"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://delphi.cs.uni-paderborn.de/"))

lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.1"
lazy val supportedScalaVersions = List(scala212, scala213)

ThisBuild / scalaVersion := scala213

name := "delphi"
version := "1.0.0-SNAPSHOT"
maintainer := "Ben Hermann <ben.hermann@upb.de>"

packageSummary := "Windows Package for the Delphi CLI"
packageDescription := """Windows Package for the Delphi CLI"""
wixProductId := "ce07be71-510d-414a-92d4-dff47631848a"
wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"

val http4sVersion = "0.18.21"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.3"
libraryDependencies += "de.vandermeer" % "asciitable" % "0.3.2"
libraryDependencies += "com.lihaoyi" %% "fansi" % "0.2.5"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp" %% "core" % "1.5.4",
  "com.softwaremill.sttp" %% "spray-json" % "1.5.4"
)


debianPackageDependencies := Seq("java8-runtime-headless")

lazy val cli = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  enablePlugins(ScalastylePlugin).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(DebianPlugin).
  enablePlugins(WindowsPlugin).
  enablePlugins(GraalVMNativeImagePlugin).
  settings(
    graalVMNativeImageOptions ++= Seq(
      "--enable-https",
      "--enable-http",
      "--enable-all-security-services",
      "--allow-incomplete-classpath",
      "--enable-url-protocols=http,https"
    )
  ).
  enablePlugins(JDKPackagerPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "de.upb.cs.swt.delphi.cli",
    crossScalaVersions := supportedScalaVersions
  )
scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"

trapExit := false
fork := true
connectInput := true
