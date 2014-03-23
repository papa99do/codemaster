name := "CtrlCP"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.0",
  "org.webjars" % "ace" % "07.31.2013",
  "org.webjars" % "angularjs" % "1.2.14",
  "mysql" % "mysql-connector-java" % "5.1.27"
)     

play.Project.playScalaSettings
