package nl.klrnbk.daan.ktchanges.models

import kotlinx.serialization.Serializable

@Serializable
class KtChangesConfig(
    val enabled: Boolean,
    val sources: List<String>,
    val baseBranch: String,
)
