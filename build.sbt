scalaVersion := "2.12.4"

name := "delphi"
version := "1.0.0-SNAPSHOT"
maintainer := "Ben Hermann <ben.hermann@upb.de>"

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

packageSummary := "Windows Package for the Delphi CLI"
packageDescription := """Windows Package for the Delphi CLI"""
wixProductId := "ce07be71-510d-414a-92d4-dff47631848a"
wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"

val akkaVersion = "2.5.14"
val akkaHttpVersion = "10.1.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.3"
libraryDependencies += "de.vandermeer" % "asciitable" % "0.3.2"
libraryDependencies += "com.lihaoyi" %% "fansi" % "0.2.5"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"


libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.4"



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
    graalVMNativeImageOptions++=Seq(
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
  )
scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"

trapExit := false
fork := true
connectInput := true
