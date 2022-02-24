// The Play plugin

// addSbtPlugin(
//   ("com.typesafe.play" %% "sbt-plugin" % "2.8.13").cross(
//     CrossVersion.for3Use2_13
//   )
// )

addSbtPlugin(
  ("io.spray" % "sbt-revolver" % "0.9.1")
    .cross(CrossVersion.for3Use2_13)
)
