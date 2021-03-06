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

package org.hivemigration.gradle.plugins.hivemigration.tasks;

import org.hivemigration.gradle.plugins.hivemigration.core.internal.migrate.Migrate

class MigrateTask extends AbstractTask {

	public static String TASK_NAME = "migrate";

	MigrateTask() {
		super();
		setDescription("Migrate (create schema and apply all DDLs)");
	}

	void run() {
		Migrate.run(config);
	}
}
