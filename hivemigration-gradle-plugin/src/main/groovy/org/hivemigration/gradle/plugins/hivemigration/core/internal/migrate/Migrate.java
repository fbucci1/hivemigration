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

package org.hivemigration.gradle.plugins.hivemigration.core.internal.migrate;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.scripts.JDBCUtil;
import org.hivemigration.gradle.plugins.hivemigration.tasks.AbstractTask;

public class Migrate {

	static final Logger logger = LogManager.getLogger(Migrate.class.getName());

	public static void run(Map<String, String> config) {
		//
		String projectDir = config.get(AbstractTask.KEY_PROJECT_DIR);
		String location = config.get(AbstractTask.KEY_LOCATION);
		//
		File locationFolder = new File(projectDir, location);
		if (!locationFolder.exists()) {
			logger.info("projectDir: " + projectDir + ", location: " + location);
			logger.info("location folder does not exist (" + locationFolder.getAbsolutePath()
					+ "). There is nothing left to do.");
			return;
		}
		//
		process(config, locationFolder);
	}

	private static void process(Map<String, String> config, File locationFolder) {
		Connection con = null;
		try {
			con = JDBCUtil.openConnection(config);
			//
			processSchema(config, locationFolder, con);
			//
		} finally {
			JDBCUtil.closeConnection(con);
		}
	}

	private static void processSchema(Map<String, String> config, File schemaFolder, Connection con) {
		//
		MigrateSchemaCreator.ensureSchemaExists(config, con);
		//
		boolean tableExisted = MigrateMetadataTableCreator.ensureTableExists(config, con);
		//
		MigrateScriptExecutor.executeScripts(config, con, schemaFolder, tableExisted);
		//
	}

}
