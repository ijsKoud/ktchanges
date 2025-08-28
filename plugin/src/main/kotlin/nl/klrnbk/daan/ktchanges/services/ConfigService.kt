package nl.klrnbk.daan.ktchanges.services

import net.mamoe.yamlkt.Yaml
import nl.klrnbk.daan.ktchanges.models.KtChangesConfig
import java.io.File

class ConfigService {
    val yaml = Yaml()

    fun getContents(path: String): String = File(path).readText(Charsets.UTF_8)

    fun parseContents(contents: String): KtChangesConfig = yaml.decodeFromString(KtChangesConfig.serializer(), contents)

    fun getConfigPath(basePath: String): String = File(basePath, ".ktchanges/config.yaml").absolutePath

    fun getConfig(basePath: String): KtChangesConfig {
        val path = getConfigPath(basePath)
        val contents = getContents(path)
        return parseContents(contents)
    }
}
