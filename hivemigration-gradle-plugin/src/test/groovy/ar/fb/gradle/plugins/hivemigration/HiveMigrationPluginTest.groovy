/*
   Copyright 2017 Fernando Raul Bucci

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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
