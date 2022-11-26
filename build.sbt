ThisBuild / version := "latest"
ThisBuild / organization := "yankov"
ThisBuild / homepage := None
ThisBuild / isSnapshot := true // make possible to overwrite the artifact on localPublish

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-collect-dependencies",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.2.8" // set minimum sbt version
      }
    }
  )
