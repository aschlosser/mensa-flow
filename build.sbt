name := """mensa-flow"""

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.neo4j.driver" % "neo4j-java-driver" % "1.0.5"
)



fork in run := true