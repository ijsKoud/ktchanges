package nl.klrnbk.daan.ktchanges.services

import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsEntry
import nl.klrnbk.daan.ktchanges.models.ProjectSelectionDetailsHeader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ChangesServiceTest {
    private val service = ChangesService()

    @Test
    fun `getSelectionList correctly categorizes changed and unchanged projects`() {
        val rootDir = "/root"
        val allProjects =
            listOf(
                "/root/project1",
                "/root/project2",
                "/root/project3",
            )
        val changedProjects =
            listOf(
                "/root/project1",
                "/root/project3",
            )

        val result = service.getSelectionList(rootDir, allProjects, changedProjects)

        assertEquals(2, result.changed.size)
        assertEquals(1, result.unchanged.size)

        assertTrue(result.changed.any { it.path == "project1" && it.name == "project1" })
        assertTrue(result.changed.any { it.path == "project3" && it.name == "project3" })
        assertTrue(result.unchanged.any { it.path == "project2" && it.name == "project2" })
    }

    @Test
    fun `getSelectionList handles nested project paths correctly`() {
        val rootDir = "/root"
        val allProjects =
            listOf(
                "/root/group/project1",
                "/root/project2",
            )
        val changedProjects = listOf("/root/group/project1")

        val result = service.getSelectionList(rootDir, allProjects, changedProjects)

        assertEquals(1, result.changed.size)
        assertEquals(1, result.unchanged.size)

        assertEquals("group/project1", result.changed[0].path)
        assertEquals("project1", result.changed[0].name)

        assertEquals("project2", result.unchanged[0].path)
        assertEquals("project2", result.unchanged[0].name)
    }

    @Test
    fun `getAllProjectsHeader creates correct header`() {
        val entries =
            listOf(
                ProjectSelectionDetailsEntry("project1", "project1", true),
                ProjectSelectionDetailsEntry("project2", "project2", false),
            )

        val header = service.getAllProjectsHeader(entries)

        assertEquals("All projects:", header.name)
        assertEquals(entries, header.collection)
    }

    @Test
    fun `getSelectionEntries combines header and projects correctly`() {
        val entries =
            listOf(
                ProjectSelectionDetailsEntry("project1", "project1", true),
                ProjectSelectionDetailsEntry("project2", "project2", false),
            )

        val result = service.getSelectionEntries(entries)
        assertEquals(3, result.size)

        val header = result[0]
        assertTrue(header is ProjectSelectionDetailsHeader)
        assertEquals("All projects:", (header as ProjectSelectionDetailsHeader).name)

        assertEquals(entries[0], result[1])
        assertEquals(entries[1], result[2])
    }
}
