import com.typesafe.sbt.packager.docker.DockerChmodType

name := """hello-stream"""

version := "1.0-SNAPSHOT"

val AkkaHttpV = "10.2.8"
val PlayVersion = "2.8.13"
val PlayTwirlVersion = "1.6.0-M1"
val PlayJsonVersion = "2.10.0-RC5"

lazy val dockerSettings = Seq(
  dockerExposedPorts ++= Seq(9000, 9001),
  dockerExposedUdpPorts += 4444,
  dockerBaseImage := "openjdk:17-alpine",
  // dockerChmodType := DockerChmodType.UserGroupWriteExecute,
  Universal / javaOptions ++= Seq(
    "--add-opens",
    "java.base/java.lang=ALL-UNNAMED",
    "-Dplay.server.pidfile.path=/dev/null"
  )
)

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, JavaAppPackaging, AshScriptPlugin)
  .settings(dockerSettings)
  .settings(
    scalaVersion := "3.0.2",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-guice" % PlayVersion,
      "com.typesafe.play" %% "play-akka-http-server" % PlayVersion,
      "com.typesafe.play" %% "play-server" % PlayVersion,
      "com.typesafe.play" %% "play" % PlayVersion,
      "com.typesafe.play" %% "play-logback" % PlayVersion,
      "com.typesafe.play" %% "filters-helpers" % PlayVersion,
      "com.typesafe.play" %% "play-test" % PlayVersion,
      "com.typesafe.play" %% "twirl-api" % PlayTwirlVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpV
    )
      .map(_.cross(CrossVersion.for3Use2_13)) ++ Seq(
      "com.typesafe" % "config" % "1.4.2",
      "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
      "org.scalameta" %% "munit" % "1.0.0-M1" % Test
    ) map (_.exclude("com.typesafe.play", "play-json_2.13")),
    scalacOptions ++= Seq(
      "-Xfatal-warnings"
    ),
    javaOptions ++= Seq(
      "--add-opens",
      "java.base/java.lang=ALL-UNNAMED"
    ),
    Compile / mainClass := Option("play.core.server.ProdServerStart"),
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings")
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
