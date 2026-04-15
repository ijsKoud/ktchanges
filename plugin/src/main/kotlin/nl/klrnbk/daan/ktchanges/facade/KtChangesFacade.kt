package nl.klrnbk.daan.ktchanges.facade

import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.textLine
import nl.klrnbk.daan.ktchanges.confirmation
import nl.klrnbk.daan.ktchanges.getListSelection
import nl.klrnbk.daan.ktchanges.models.KtChangesConfig
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetails
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsEntry
import nl.klrnbk.daan.ktchanges.services.ChangesService
import nl.klrnbk.daan.ktchanges.services.ConfigService
import nl.klrnbk.daan.ktchanges.services.FileSystemService
import nl.klrnbk.daan.ktchanges.services.GitService
import java.io.File
import kotlin.system.exitProcess

class KtChangesFacade {
    private val gitService = GitService()
    private val configService = ConfigService()
    private val fileSystemService = FileSystemService()
    private val changesService = ChangesService()

    fun start(pluginRootDir: File) {
        val rootDir = gitService.getProjectRoot(pluginRootDir)

        val projectConfig = configService.getConfig(rootDir.absolutePath)
        if (!projectConfig.enabled) return println("KtChanges is disabled, skipping...")

        val changeDetails = getSelectionList(rootDir, projectConfig)

        session {
            val selectedProjects = getListSelection(changeDetails.getList(), "Which projects would you like to include?")
            if (selectedProjects.isEmpty()) return@session

            var entries = changesService.getSelectionEntries(selectedProjects)
            val projectsForMajorBump = getListSelection(entries.toList(), "Which projects should receive a MAJOR bump?")
            entries = changesService.getSelectionEntries(selectedProjects.filterNot { it in projectsForMajorBump })

            var projectsForMinorBump = emptyList<ProjectSelectionDetailsEntry>()
            if (entries.size > 1) {
                projectsForMinorBump = getListSelection(entries.toList(), "Which projects should receive a MINOR bump?")
            }

            val projectsForPatchBump = selectedProjects.filterNot { it in projectsForMinorBump || it in projectsForMajorBump }
            val bumpMap =
                projectsForMajorBump.associate { it.path to "major" } +
                    projectsForMinorBump.associate { it.path to "minor" } +
                    projectsForPatchBump.associate { it.path to "patch" }

            val finish =
                confirmation(
                    """
                    The following choices were selected:
                    - Major bump: ${projectsForMajorBump.joinToString(", ") { it.name }}
                    - Minor bump: ${projectsForMinorBump.joinToString(", ") { it.name }}
                    - Patch bump: ${projectsForPatchBump.joinToString(", ") { it.name }}
                    
                    Do you want to continue?
                    """.trimIndent(),
                )

            if (finish) {
                fileSystemService.writeChangesetFile(rootDir.absolutePath, bumpMap)
                section { textLine("Changeset created!") }.run()

                return@session
            }

            section { textLine("Aborted.") }.run()
        }
    }

    fun getSelectionList(
        rootDir: File,
        projectConfig: KtChangesConfig,
    ): ProjectSelectionDetails {
        val projects = fileSystemService.getAllProjectPaths(rootDir, projectConfig.sources)
        val changedProjects = gitService.getChangedProjects(rootDir, projectConfig.baseBranch, projects.toList())
        val changesDetails = changesService.getSelectionList(rootDir.absolutePath, projects.toList(), changedProjects.toList())
        return changesDetails
    }
}
