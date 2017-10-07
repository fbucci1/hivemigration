/*
 *   Copyright 2017 Fernando Raul Bucci
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.hivemigration.gradle.plugins.hivemigration

import org.gradle.api.Project;

import org.gradle.testfixtures.ProjectBuilder;
import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationExtension
import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationPlugin
import org.hivemigration.gradle.plugins.hivemigration.tasks.AbstractTask
import org.hivemigration.gradle.plugins.hivemigration.tasks.MigrateTask
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

class HiveMigrationPluginTest {

	static Project project;

	@BeforeClass
	public static void setUp() {
		project = ProjectBuilder.builder().build();
		project.pluginManager.apply HiveMigrationPlugin.NAME;
	}


	@Test
	public void hivemigrationPluginAddsMigrateTaskToProject() {
		assertTrue(project.tasks.migrate instanceof MigrateTask);
	}

	@Test
	public void hivemigrationPluginAddsExtensionToProject() {
		assertTrue(project.extensions[HiveMigrationExtension.NAME] instanceof HiveMigrationExtension);
	}

	@Test
	public void hivemigrationPluginExecuteMigrate() {
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_URL,"jdbc:hive2://localhost:10000");
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_ENV,"UAT");
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_SCHEMA,"STAGE_\${ENV}");
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_PROJECT_DIR,"../hivemigration-sample");
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_LOCATION,"db/changelog");
		project.tasks.migrate.setConfiguration(AbstractTask.KEY_TARGET,"2");
		project.tasks.migrate.execute()
	}
}
