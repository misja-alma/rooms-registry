name := "room-registry"
organization := "com.ing"

scalaVersion := "2.12.6"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalacOptions += "-Ypartial-unification"

libraryDependencies += guice
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
//libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.11" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

