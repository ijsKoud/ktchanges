package nl.klrnbk.daan.ktchanges.models

data class ProjectSelectionDetails(
    val changed: List<ProjectSelectionDetailsEntry>,
    val unchanged: List<ProjectSelectionDetailsEntry>,
) {
    fun getList(): List<ProjectSelectionDetailsBaseEntry> {
        val selectAllChangedHeader = ProjectSelectionDetailsHeader("Changed projects:", true, changed)
        val selectAllUnchangedHeader = ProjectSelectionDetailsHeader("Unchanged projects:", false, unchanged)

        val list: MutableList<ProjectSelectionDetailsBaseEntry> = mutableListOf()
        if (changed.isNotEmpty()) {
            list.add(selectAllChangedHeader)
            list.addAll(changed)
        }

        if (unchanged.isNotEmpty()) {
            list.add(selectAllUnchangedHeader)
            list.addAll(unchanged)
        }

        return list.toList()
    }
}

abstract class ProjectSelectionDetailsBaseEntry(
    val name: String,
    val changed: Boolean,
    var selected: Boolean = false,
)

class ProjectSelectionDetailsEntry(
    val path: String,
    name: String,
    changed: Boolean,
) : ProjectSelectionDetailsBaseEntry(name, changed)

class ProjectSelectionDetailsHeader(
    name: String,
    changed: Boolean,
    val collection: List<ProjectSelectionDetailsEntry>,
) : ProjectSelectionDetailsBaseEntry(name, changed) {
    fun getNewSelectionStatus(): Boolean {
        if (collection.all(ProjectSelectionDetailsEntry::selected)) return false
        return !selected
    }
}
