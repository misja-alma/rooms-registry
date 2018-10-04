name := """room-registry"""
organization := "com.ing"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

//scalacOptions += "-Ypartial-unification"

libraryDependencies += guice
//libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

