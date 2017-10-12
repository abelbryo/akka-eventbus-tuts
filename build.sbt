name := """hello-stream"""

version := "1.0-SNAPSHOT"


scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  jdbc,
  guice,
  ehcache,
  ws,
  "com.typesafe.akka" %% "akka-stream" % "2.5.6"
)


lazy val root = (project in file(".")).enablePlugins(PlayScala)
