package com.shopstic.sbt

import java.nio.file.{Files, LinkOption}

import sbt.Keys._
import sbt.{AutoPlugin, _}

//noinspection TypeAnnotation
object SymlinkTargetPlugin extends AutoPlugin {

  object autoImport {
    val symlinkTargetRoot = settingKey[File]("Symlink target root")
  }

  import autoImport._

  private def createLink(src: File, dest: File): Unit = {
    val srcPath = src.toPath
    val destPath = dest.toPath

    IO.createDirectory(src)
    IO.createDirectory(dest)

    if (Files.exists(srcPath, LinkOption.NOFOLLOW_LINKS) &&
      (!Files.isSymbolicLink(srcPath) || srcPath
        .toRealPath()
        .compareTo(destPath) != 0)) {
      IO.delete(src)
    }

    if (!Files.exists(srcPath, LinkOption.NOFOLLOW_LINKS)) {
      val _ = Files.createSymbolicLink(srcPath, destPath)
    }
  }

  private lazy val createSymlinks = taskKey[Unit]("Create symlinks")
  private lazy val deleteSymlinkTargetRoot = taskKey[Unit]("Delete symlinks")
  private lazy val deleteTargetStreams = taskKey[Unit]("Delete target streams")

  override lazy val projectSettings =
    Seq(
      createSymlinks := {
        val projectName = name.value
        val targetSrc = target.value
        val targetDest = symlinkTargetRoot.value / projectName

        streams.value.log.info(s"Creating target symlinks: src=$targetSrc dest=$targetDest")

        Seq("streams", "test-reports", "docker", "universal")
          .foreach { dir =>
            createLink(targetSrc / dir, targetDest / dir)
          }

        Seq("classes", "test-classes", "resolution-cache")
          .foreach { dir =>
            createLink(
              targetSrc / "scala-2.12" / dir,
              targetDest / "scala-2.12" / dir
            )
          }
      },
      deleteSymlinkTargetRoot := {
        val file = symlinkTargetRoot.value / name.value
        streams.value.log.info(s"Deleting target symlink root: $file")
        IO.delete(file)
      },
      deleteTargetStreams := {
        val file = target.value / "streams"
        streams.value.log.info(s"Deleting target streams: $file")
        IO.delete(file)
      },
      clean := (deleteTargetStreams dependsOn (deleteSymlinkTargetRoot dependsOn clean)).value,
      Test / compile := ((Test / compile) dependsOn createSymlinks).value,
      Compile / compile := ((Compile / compile) dependsOn createSymlinks).value
    )
}
