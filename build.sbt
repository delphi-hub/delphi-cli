scalaVersion := "2.12.4"

name := "delphi-cli"
version := "1.0.0-SNAPSHOT"
maintainer := "Ben Hermann <ben.hermann@upb.de>"

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

packageSummary := "Windows Package for the Delphi CLI"
packageDescription := """Windows Package for the Delphi CLI"""
wixProductId := "ce07be71-510d-414a-92d4-dff47631848a"
wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "10.0.11"
)

debianPackageDependencies := Seq("java8-runtime-headless")

lazy val cli = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(DebianPlugin).
  enablePlugins(WindowsPlugin).

  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "de.upb.cs.swt.delphi.cli"
  )
