package yankov.sbt.plugin

import sbt.Keys.fullClasspath
import sbt.{Def, File, taskKey, *}

import java.nio.file.{Files, Path, Paths}
import scala.collection.JavaConverters.*

object SbtCollectDependencies extends AutoPlugin {
  private def collectJars(files: Seq[Path], destDir: Path): Unit = {
    files.filter(x => x.toString.endsWith(".jar")).foreach(
      x => {
        val src = x
        val dest = Paths.get(destDir.toString, x.getFileName.toString)
        Files.copy(src, dest)
      }
    )
  }

  object autoImport {
    val collectDependencies = taskKey[Unit]("collect dependencies")
    val jarDir = settingKey[String]("destination jar directory")
  }

  import autoImport.*

  private val defaultSettings: Seq[Def.Setting[?]] = Seq(
    jarDir := "jar",

    collectDependencies := {
      val jarPath = Paths.get(jarDir.value)
      if (!Files.exists(jarPath)) Files.createDirectories(jarPath)

      val cp: Seq[File] = (Runtime / fullClasspath).value.files
      collectJars(cp.map(x => x.toPath), jarPath)
      collectJars(Files.walk(Paths.get("target", "scala-2.12"), 1).iterator().asScala.toList, jarPath)
    }
  )

  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[?]] = defaultSettings
}
