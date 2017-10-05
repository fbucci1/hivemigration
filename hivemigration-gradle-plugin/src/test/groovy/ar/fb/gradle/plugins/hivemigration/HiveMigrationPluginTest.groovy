package ar.fb.gradle.plugins.hivemigration

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test

import ar.fb.gradle.plugins.hivemigration.HiveMigrationExtension
import ar.fb.gradle.plugins.hivemigration.tasks.AbstractTask
import ar.fb.gradle.plugins.hivemigration.tasks.MigrateTask

import static org.junit.Assert.*

class HiveMigrationPluginTest {

	@Test
	public void hivemigrationPluginAddsMigrateTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply HiveMigrationPlugin.NAME
		assertTrue(project.tasks.migrate instanceof MigrateTask)
	}

	@Test
	public void hivemigrationPluginAddsExtensionToProject() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply HiveMigrationPlugin.NAME
		assertTrue(project.extensions[HiveMigrationExtension.NAME] instanceof HiveMigrationExtension)
	}

	@Test
	public void hivemigrationPluginExecuteMigrate() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply HiveMigrationPlugin.NAME
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_URL,"jdbc:hive2://localhost:10000")
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_ENV,"UAT")
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_SCHEMA,"STAGE_\${ENV}")
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_PROJECT_ROOT,"../hivemigration-sample")
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_LOCATION,"db/changelog")
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_TARGET,"2")
		project.tasks.migrate.execute()
	}
}
