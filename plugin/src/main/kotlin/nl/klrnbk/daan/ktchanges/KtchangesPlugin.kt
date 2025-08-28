package nl.klrnbk.daan.ktchanges

import nl.klrnbk.daan.ktchanges.facade.KtChangesFacade
import org.gradle.api.Plugin
import org.gradle.api.Project

class KtchangesPlugin : Plugin<Project> {
    private val ktChangesFacade = KtChangesFacade()

    override fun apply(project: Project) {
        project.tasks.register("ktchanges") { task ->
            task.group = "release"
            task.description = "Creates a changeset like file containing details about the module version bump"

            task.doLast { ktChangesFacade.start(project.rootDir) }
        }
    }
}
