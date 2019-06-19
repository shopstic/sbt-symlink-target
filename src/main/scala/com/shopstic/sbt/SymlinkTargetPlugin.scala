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

  private def createLink(src: File, dest: File) = {
    val srcPath = src.toPath
    val destPath = dest.toPath

    IO.createDirectory(src)
    IO.createDirectory(dest)

    if (Files.exists(srcPath, LinkOption.NOFOLLOW_LINKS) &&
      (!Files.isSymbolicLink(srcPath) || srcPath.toRealPath().compareTo(destPath) != 0)) {
      IO.delete(src)
    }

    if (!Files.exists(srcPath, LinkOption.NOFOLLOW_LINKS)) {
      Files.createSymbolicLink(srcPath, destPath)
    }
  }

  override lazy val projectSettings = Seq(
    target := {
      val projectName = name.value
      val targetSrc = target.value
      val targetDest = symlinkTargetRoot.value / projectName

      createLink(targetSrc / "streams", targetDest / "streams")
      createLink(targetSrc / "test-reports", targetDest / "test-reports")
      createLink(targetSrc / "docker", targetDest / "docker")
      createLink(targetSrc / "universal", targetDest / "universal")
      createLink(targetSrc / "scala-2.12" / "classes", targetDest / "scala-2.12" / "classes")
      createLink(targetSrc / "scala-2.12" / "api", targetDest / "scala-2.12" / "api")
      createLink(targetSrc / "scala-2.12" / "test-classes", targetDest / "scala-2.12" / "test-classes")
      createLink(targetSrc / "scala-2.12" / "resolution-cache", targetDest / "scala-2.12" / "resolution-cache")

      targetSrc
    }
  )
}
