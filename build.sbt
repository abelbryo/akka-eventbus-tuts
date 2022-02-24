name := """hello-stream"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(guice)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scalacOptions ++= Seq(
      "-Wdead-code",
      "-Wextra-implicit",
      "-Wnumeric-widen",
      "-Woctal-literal",
      "-Wunused:explicits",
      "-Wunused:implicits",
      "-Wunused:imports",
      "-Wunused:locals",
      "-Wunused:patvars",
      "-Wunused:privates",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unused",
      "-Xverify",
      "-Xsource:3",
      "-Ydelambdafy:inline",
      "-Yrangepos",
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-unchecked"
    ),
    Compile / console / scalacOptions --= Seq(
      "-Wunused:linted",
      "-Wunused:imports",
      "-Xfatal-warnings",
      "-Xlint:unused"
    )
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
