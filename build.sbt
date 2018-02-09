name := "delphi-cli"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "10.0.11"
)

lazy val root = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "de.upb.cs.swt.delphi.cli"
  )

