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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.hivemigration.gradle.plugins.hivemigration.tasks.MigrateTask

class HiveMigrationPlugin implements Plugin<Project> {

	public static final NAME = HiveMigrationExtension.NAME + "-gradle-plugin"

	@Override
	void apply(Project project) {
		HiveMigrationExtension extension = project.extensions.create(HiveMigrationExtension.NAME, HiveMigrationExtension)
		project.task(MigrateTask.TASK_NAME, type: MigrateTask)
	}
}
