package nl.klrnbk.daan.ktchanges.services

import org.gradle.internal.impldep.bsh.commands.dir
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

class FileSystemService {
    fun getAllProjectPaths(
        rootDir: File,
        sources: List<String>,
    ): Set<String> =
        sources
            .map { File(rootDir, it).absolutePath }
            .map { Path(it).listDirectoryEntries() }
            .flatten()
            .map { it.absolutePathString() }
            .toSet()

    fun writeChangesetFile(
        directory: String,
        bumps: Map<String, String>,
    ) {
        val name = "${Instant.now().epochSecond}-changeset.yaml"
        val file = File(directory, ".ktchanges/$name")

        val contents = bumps.map { (path, type) -> "$path: $type" }.joinToString("\n")
        file.writeText(contents)
    }
}
