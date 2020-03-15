ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "wumpus-game",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.2",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"
  )
