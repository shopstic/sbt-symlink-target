# sbt-symlink-target [ ![Download](https://api.bintray.com/packages/shopstic/sbt-plugins/sbt-symlink-target/images/download.svg) ](https://bintray.com/shopstic/sbt-plugins/sbt-symlink-target/_latestVersion)

Automatically create symlinks for various target directories, so that compiled artifacts stay physically outside of the source's directory. At the same time, full IDE support via Intellij still works, including generated code resolution (e.g using `scalapb` and such).

This is useful when developing a project on multiple machines, using a file syncing service such as OneDrive, Google Drive, etc.
