package nl.klrnbk.daan.ktchanges

import com.varabyte.kotter.foundation.collections.liveListOf
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.text.black
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.rgb
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.underline
import com.varabyte.kotter.runtime.Session
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsBaseEntry
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsEntry
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsHeader

fun Session.getListSelection(
    entries: List<ProjectSelectionDetailsBaseEntry>,
    title: String,
): List<ProjectSelectionDetailsEntry> {
    var cursorIndex by liveVarOf(0)
    val projects = liveListOf(entries)

    section {
        bold { textLine(title) }
        textLine()

        projects.forEachIndexed { idx, entry ->
            if (entry is ProjectSelectionDetailsHeader) {
                if (idx != 0) textLine()

                val isSelected = entry.collection.all(ProjectSelectionDetailsEntry::selected)
                val selectionIcon = if (isSelected) "●" else "○"
                val cursor = if (cursorIndex == idx) "> " else "  "

                text("$cursor$selectionIcon ")
                underline { textLine(entry.name) }
            }

            if (entry is ProjectSelectionDetailsEntry) {
                val isSelected = entry.selected
                val selectionIcon = if (isSelected) "●" else "○"
                val cursor = if (cursorIndex == idx) ">" else " "

                text("  $cursor $selectionIcon ")
                text(entry.name)

                rgb(GRAY_RGB) { textLine(" ${entry.path}") }
            }
        }

        textLine()
        black(isBright = true) { textLine("Use UP/DOWN to choose, SPACE to select and ENTER to continue") }
        textLine()
    }.runUntilSignal {
        fun toggleProject(idx: Int) {
            val entry = projects[idx]
            if (entry is ProjectSelectionDetailsHeader) {
                val newSelectionStatus = entry.getNewSelectionStatus()
                entry.selected = newSelectionStatus
                entry.collection.forEach { it.selected = newSelectionStatus }
            }

            if (entry is ProjectSelectionDetailsEntry) {
                val newSelectionStatus = !entry.selected
                entry.selected = newSelectionStatus

                val header =
                    projects.firstOrNull { it is ProjectSelectionDetailsHeader && it.changed == entry.changed }
                if (header != null && !newSelectionStatus) header.selected = false
            }

            rerender()
        }

        onKeyPressed {
            when (key) {
                Keys.UP -> cursorIndex -= 1
                Keys.DOWN -> cursorIndex += 1
                Keys.SPACE -> toggleProject(cursorIndex)
                Keys.ENTER -> {
                    signal()
                }
            }

            if (cursorIndex < 0) {
                cursorIndex = projects.size - 1
            } else if (cursorIndex > (projects.size - 1)) {
                cursorIndex = 0
            }
        }
    }

    return projects
        .filter { it is ProjectSelectionDetailsEntry && it.selected }
        .map {
            it.selected = false
            it as ProjectSelectionDetailsEntry
        }
}

fun Session.confirmation(prompt: String): Boolean {
    var selected by liveVarOf(0)

    section {
        textLine(prompt)
        textLine(if (selected == 0) "> Yes" else "  Yes")
        textLine(if (selected == 1) "> No" else "  No")
    }.runUntilSignal {
        onKeyPressed {
            when (key) {
                Keys.UP, Keys.DOWN -> {
                    selected = 1 - selected
                    rerender()
                }
                Keys.ENTER -> signal()
            }
        }
    }

    return selected == 0
}
