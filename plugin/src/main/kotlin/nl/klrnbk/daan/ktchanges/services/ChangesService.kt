package nl.klrnbk.daan.ktchanges.services

import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetails
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsBaseEntry
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsEntry
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsHeader

class ChangesService {
    fun getSelectionList(
        rootDir: String,
        allProjects: List<String>,
        changed: List<String>,
    ): ProjectSelectionDetails {
        val relativePathsUnchanged =
            allProjects
                .filterNot { changed.contains(it) }
                .map { it.replace(rootDir, "") }
                .map { it.slice(1..it.lastIndex) }

        val relativePathsChanged =
            changed
                .map { it.replace(rootDir, "") }
                .map { it.slice(1..it.lastIndex) }

        val changedProjectEntries = relativePathsChanged.map { ProjectSelectionDetailsEntry(it, it.split("/").last(), true) }
        val unchangedProjectEntries = relativePathsUnchanged.map { ProjectSelectionDetailsEntry(it, it.split("/").last(), false) }
        return ProjectSelectionDetails(changedProjectEntries, unchangedProjectEntries.toMutableList())
    }

    fun getAllProjectsHeader(collection: List<ProjectSelectionDetailsEntry>): ProjectSelectionDetailsHeader =
        ProjectSelectionDetailsHeader("All projects:", true, collection)

    fun getSelectionEntries(projects: List<ProjectSelectionDetailsEntry>): List<ProjectSelectionDetailsBaseEntry> {
        val allProjectsHeader = getAllProjectsHeader(projects)
        val entries = mutableListOf<ProjectSelectionDetailsBaseEntry>(allProjectsHeader)
        entries.addAll(projects)

        return entries.toList()
    }
}
