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
    val jarDir = settingKey[Path]("destination jar directory")
    val targetDir = settingKey[Path]("project target directory")
  }

  import autoImport.*

  private val defaultSettings: Seq[Def.Setting[?]] = Seq(
    jarDir := Paths.get("jar"),
    targetDir := Paths.get("target", "scala-2.12"),

    collectDependencies := {
      if (!Files.exists(jarDir.value)) Files.createDirectories(jarDir.value)

      val cp: Seq[File] = (Runtime / fullClasspath).value.files
      collectJars(cp.map(x => x.toPath), jarDir.value)
      collectJars(Files.walk(targetDir.value, 1).iterator().asScala.toList, jarDir.value)
    }
  )

  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[?]] = defaultSettings
}
