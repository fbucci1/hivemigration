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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationExtension;
import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationManagedException;
import org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.PlaceholderUtil;
import org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.scripts.ScriptExecutionUtil;

public abstract class AbstractTask extends DefaultTask {

	static final Logger logger = LogManager.getLogger(ScriptExecutionUtil.class.getName());

	// Extensions
	// The jdbc url to use to connect to the database
	public static final String KEY_URL = "url";
	// The driver to use to connect to the database
	public static final String KEY_DRIVER = "driver";
	// The environment to use
	public static final String KEY_ENV = "ENV";
	// The user to use to connect to the database
	public static final String KEY_USER = "user";
	// The password to use to connect to the database
	public static final String KEY_PASSWORD = "password";
	// The schema to create
	public static final String KEY_SCHEMA = "schema";
	// The name of the table used for metadata
	public static final String KEY_TABLE = "table";
	// The root folder to use
	public static final String KEY_PROJECT_DIR = "projectDir";
	// Relative path to the location to scan for migrations
	public static final String KEY_LOCATION = "location";
	// The target version up to which should consider migrations
	public static final String KEY_TARGET = "target";

	// Environment variables
	private static final String ENV_KEY_ENV = "MIGRATE_ENV";
	private static final String ENV_KEY_USER = "MIGRATE_DB_USER";
	private static final String ENV_KEY_PASSWORD = "MIGRATE_DB_PASSWORD";

	HiveMigrationExtension extension;
	Map<String, String> config = new HashMap<String, String>();

	AbstractTask() {
		setGroup("HIVEMIGRATION");
		//
		getProject().afterEvaluate(new Action<Project>() {
			@Override
			public void execute(Project project) {
				extension = (HiveMigrationExtension) project.getExtensions().getByName(HiveMigrationExtension.NAME);
			}
		});
	}

	@TaskAction
	void exec() {
		logger.warn("Executing " + this.getClass().getSimpleName() + " task!");
		try {
			setConfigurations();
			//
			run();
		} catch (HiveMigrationManagedException e) {
			getLogger().error("Error " + e);
			if (e.getCause() != null) {
				getLogger().error("Caused by " + e.getCause(), e);
			}
		} catch (Throwable e) {
			getLogger().error("Error " + e, e);
		}
		logger.warn("Task " + this.getClass().getSimpleName() + " finished.");
	}

	void run() {
		throw new RuntimeException("Method run() not implemented for task " + this.getClass().getName());
	}

	void setConfigurations() {
		//
		loadValuesFromExtensions();

		//
		// Load values from command line arguments if not loaded so far
		//
		// Not supported yet. Has to be implemented in build.gradle as follows:
		// hivemigration {
		// url="jdbc:hive2://localhost:10000"
		// schema="STAGE_${ENV}"
		// ENV=findProperty("ENV") // Reads argument -PENV=xxx
		// user=findProperty("user") // Reads argument -Puser=xxx
		// password=findProperty("password") // Reads argument -Ppassword=xxx
		// }
		// Then it can be called: gradle migrate -PENV=xxx -Puser=xxx -Ppassword=xxx

		loadValuesFromEnvVariables();
		//
		setDefaultValues();
		//
		checkMandatoryFields();
		//
		replaceTokens();
		//
		validateSemantics();
	}

	private void loadValuesFromExtensions() {
		//
		// Load values from extensions
		//
		if (extension != null) {
			config.put(KEY_URL, extension.url);
			config.put(KEY_DRIVER, extension.driver);
			config.put(KEY_ENV, extension.ENV);
			config.put(KEY_USER, extension.user);
			config.put(KEY_PASSWORD, extension.password);
			config.put(KEY_SCHEMA, extension.schema);
			config.put(KEY_TABLE, extension.table);
			config.put(KEY_PROJECT_DIR, extension.projectDir);
			config.put(KEY_LOCATION, extension.location);
			config.put(KEY_TARGET, (extension.target == null ? null : extension.target.toString()));
		}
	}

	private void loadValuesFromEnvVariables() {
		//
		// Load values from environment variables if not loaded so far
		//
		// KEY_URL cannot be loaded from env variables
		// KEY_DRIVER cannot be loaded from env variables
		if (config.get(KEY_ENV) == null && System.getenv(ENV_KEY_ENV) != null) {
			logger.info("Using environment variable: " + ENV_KEY_ENV);
			config.put(KEY_ENV, System.getenv(ENV_KEY_ENV));
		}
		if (config.get(KEY_USER) == null && System.getenv(ENV_KEY_USER) != null) {
			logger.info("Using environment variable: " + ENV_KEY_USER);
			config.put(KEY_USER, System.getenv(ENV_KEY_USER));
		}
		if (config.get(KEY_PASSWORD) == null && System.getenv(ENV_KEY_PASSWORD) != null) {
			logger.info("Using environment variable: " + ENV_KEY_PASSWORD);
			config.put(KEY_PASSWORD, System.getenv(ENV_KEY_PASSWORD));
		}
		// KEY_SCHEMA cannot be loaded from env variables
		// KEY_TABLE cannot be loaded from env variables
		// KEY_PROJECT_DIR cannot be loaded from env variables
		// KEY_LOCATION cannot be loaded from env variables
		// KEY_TARGET cannot be loaded from env variables
	}

	private void setDefaultValues() {
		//
		// Set default values, respecting pre-existing values (set for testing)
		//
		// KEY_URL has no default value
		if (config.get(KEY_DRIVER) == null)
			config.put(KEY_DRIVER, "org.apache.hive.jdbc.HiveDriver");
		// KEY_ENV has no default value
		// KEY_USER has no default value
		// KEY_PASSWORD has no default value
		// KEY_SCHEMA has no default value
		if (config.get(KEY_TABLE) == null)
			config.put(KEY_TABLE, "VERSIONING_METADATA");
		if (config.get(KEY_PROJECT_DIR) == null)
			config.put(KEY_PROJECT_DIR, getProject().getProjectDir().getAbsolutePath());
		if (config.get(KEY_LOCATION) == null)
			config.put(KEY_LOCATION, "db/changelog");
		// KEY_TARGET has defaults to null
	}

	private void checkMandatoryFields() {
		//
		// Ensure mandatory fields have values at last
		//
		if (config.get(KEY_URL) == null) {
			throw new HiveMigrationManagedException("Property '" + KEY_URL + "' is mandatory and it was not set.");
		}
		// KEY_DRIVER skipped as it is not mandatory
		if (config.get(KEY_ENV) == null) {
			throw new HiveMigrationManagedException("Property '" + KEY_ENV + "' is mandatory and it was not set.");
		}
		// KEY_USER skipped as it is not mandatory
		// KEY_PASSWORD skipped as it is not mandatory
		if (config.get(KEY_SCHEMA) == null) {
			throw new HiveMigrationManagedException("Property '" + KEY_SCHEMA + "' is mandatory and it was not set.");
		}
		//
		// KEY_TABLE skipped as it is not mandatory;
		// KEY_PROJECT_DIR skipped as it is not mandatory
		// KEY_LOCATION skipped as it is not mandatory
		// KEY_TARGET skipped as it is not mandatory
	}

	private void replaceTokens() {
		//
		// Replace tokens
		//
		config.put(AbstractTask.KEY_SCHEMA, PlaceholderUtil.replaceTokens(config, config.get(AbstractTask.KEY_SCHEMA)));
	}

	private void validateSemantics() {
		//
		// Semantic validation
		//
		if (!config.get(KEY_DRIVER).equals("org.apache.hive.jdbc.HiveDriver")) {
			throw new HiveMigrationManagedException("driver not supported: " + config.get(KEY_DRIVER)
					+ ". Only supported driver is 'org.apache.hive.jdbc.HiveDriver'.");
		}
	}

	public void setConfiguration(String key, String value) {
		config.put(key, value);
	}
}
