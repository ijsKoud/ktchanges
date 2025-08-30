package nl.klrnbk.daan.ktchanges.services

import nl.klrnbk.daan.ktchanges.models.KtChangesConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ConfigServiceTest {
    @Test
    fun `parseContents parses yaml to config`() {
        val service = spy(ConfigService())
        val yamlString = "enabled: true\nbaseBranch: origin/main\nsources:\n- test-project/src"

        val result = service.parseContents(yamlString)
        assertEquals(true, result.enabled)
        assertContentEquals(listOf("test-project/src"), result.sources)
        assertEquals("origin/main", result.baseBranch)
    }

    @Test
    fun `getConfigPath returns correct path`() {
        val service = ConfigService()
        val basePath = "/base"
        val expected = File(basePath, ".ktchanges/config.yaml").absolutePath
        val result = service.getConfigPath(basePath)
        assertEquals(expected, result)
    }

    @Test
    fun `getConfig returns parsed config`() {
        val service = spy(ConfigService())
        val basePath = "/base"
        val configPath = "/base/.ktchanges/config.yaml"
        val contents = "key: value"
        val expectedConfig = mock(KtChangesConfig::class.java)

        doReturn(configPath).`when`(service).getConfigPath(basePath)
        doReturn(contents).`when`(service).getContents(configPath)
        doReturn(expectedConfig).`when`(service).parseContents(contents)

        val result = service.getConfig(basePath)
        assertEquals(expectedConfig, result)
    }
}
