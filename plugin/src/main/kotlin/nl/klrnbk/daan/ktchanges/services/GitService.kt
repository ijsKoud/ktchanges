package nl.klrnbk.daan.ktchanges.services

import java.io.File

class GitService {
    fun getProjectRoot(currentDir: File): File {
        val process =
            ProcessBuilder("git", "rev-parse", "--show-toplevel")
                .directory(currentDir)
                .redirectErrorStream(true)
                .start()

        val output = process.inputStream.bufferedReader().readText()
        return File(output.trimIndent())
    }

    fun getChangedProjects(
        rootDir: File,
        defaultBranch: String,
        projects: List<String>,
    ): Set<String> {
        val process =
            ProcessBuilder("git", "diff", "--name-only", "$defaultBranch...HEAD")
                .directory(rootDir)
                .redirectErrorStream(true)
                .start()

        val output = process.inputStream.bufferedReader().readText()
        val changedFiles =
            output
                .lines()
                .filter(String::isNotBlank)
                .map { File(rootDir, it).absolutePath }

        return projects
            .filter { project -> changedFiles.any { file -> file.startsWith(project) } }
            .toSet()
    }
}
