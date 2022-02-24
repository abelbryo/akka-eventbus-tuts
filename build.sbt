name := """hello-stream"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  // jdbc,
  guice,
  ehcache,
  ws
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

Global / onChangedBuildSource := ReloadOnSourceChanges
