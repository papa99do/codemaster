name := "CodeMaster"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.0",
  "org.webjars" % "ace" % "07.31.2013",
  "org.webjars" % "angularjs" % "1.2.14",
  "org.webjars" % "underscorejs" % "1.6.0-1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)     

play.Project.playScalaSettings
