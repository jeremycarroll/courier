package org.coursera.courier.sbt

import java.io.File
import sbt.internal.io.Source
import sbt._
import Keys._
import sbt.util.CacheStore

trait CourierPluginCompat {
  protected val sourceFileFilter: FileFilter = ("*.pdsc" || "*.courier")

  implicit def fileToInternalSource(file: File): Source =
    new Source(file, AllPassFilter, NothingFilter)

  def watchSourcesSetting(scope: Configuration) =
    watchSources in scope := List(
      WatchSource(
        (sourceDirectory in scope).value,
        sourceFileFilter,
        (excludeFilter in scope).value))

  /**
   * Returns an indication of whether `sourceFiles` and their modify dates differ from what is
   * recorded in `cacheFile`, plus a function that can be called to write `sourceFiles` and their
   * modify dates to `cacheFile`.
   */
  protected def prepareCacheUpdate(
      cacheFile: File,
      sourceFiles: Seq[File],
      streams: std.TaskStreams[_]): (Boolean, () => Unit) = {
    val fileToModifiedMap = sourceFiles.map(f => f -> FileInfo.lastModified(f)).toMap

    val (_, previousFileToModifiedMap) =
      Sync.readInfo(CacheStore(cacheFile))(FileInfo.lastModified.format)
    //we only care about the source files here
    val relation = Seq.fill(sourceFiles.size)(file(".")).zip(sourceFiles)

    streams.log.debug(
      s"${fileToModifiedMap.size} <- current VS previous -> ${previousFileToModifiedMap.size}")
    val anyFilesChanged = !cacheFile.exists || (previousFileToModifiedMap != fileToModifiedMap)
    def updateCache() {
      Sync.writeInfo(
        CacheStore(cacheFile),
        Relation.empty[File, File] ++ relation.toMap,
        sourceFiles.map(f => f -> FileInfo.lastModified(f)).toMap)(FileInfo.lastModified.format)
    }
    (anyFilesChanged, updateCache)
  }

  protected def cloneArtifact(
      originalArtifact: Artifact,
      newName: String,
      newConfigurations: Vector[ConfigRef]): Artifact = {
    originalArtifact.withName(newName).withConfigurations(newConfigurations)
  }

  /**
   * Generates settings that place the artifact generated by `packagingTaskKey` in the specified
   * `ivyConfig`, while also suffixing the artifact name with "-" and the `ivyConfig`.
   */
  protected def restliArtifactSettings(packagingTaskKey: TaskKey[File])(
      ivyConfig: String): Seq[Def.Setting[_]] = {
    val ivyConfigObject = Configurations.config(ivyConfig)

    Seq(
      (artifact in packagingTaskKey) := {
        val packagingArtifact = (artifact in packagingTaskKey).value
        cloneArtifact(
          packagingArtifact,
          packagingArtifact.name + "-" + ivyConfig,
          packagingArtifact.configurations ++ Seq(ConfigRef(ivyConfig)))
      },
      ivyConfigurations += ivyConfigObject
    )
  }

  protected def createCourierConfiguration = {
    Configuration.of(
      id = "courier",
      name = "courier",
      description = "Courier generated data templates.",
      isPublic = true,
      extendsConfigs = Vector(Compile),
      transitive = true)
  }

  protected def createCourierArtifactConfiguration = {
    ConfigRef.configToConfigRef(
      Configuration.of(
        id = "courier",
        name = "courier",
        description = "Courier generated data templates.",
        isPublic = true,
        extendsConfigs = Vector(Compile),
        transitive = true))
  }
}
