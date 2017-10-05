package ar.fb.gradle.plugins.hivemigration.core.internal.migrate;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.fb.gradle.plugins.hivemigration.tasks.AbstractTask;

public class Migrate {

	static final Logger logger = LogManager.getLogger(Migrate.class.getName());

	public static void run(Map<String, String> config) {
		//
		String projectRoot = config.get(AbstractTask.KEY_PROJECT_ROOT);
		String location = config.get(AbstractTask.KEY_LOCATION);
		logger.debug("Project root: " + projectRoot + ", Location: " + location);
		//
		File locationFolder = new File(projectRoot, location);
		if (!locationFolder.exists()) {
			logger.info("location folder does not exist (" + locationFolder.getAbsolutePath()
					+ "). There is nothing left to do.");
			return;
		}
		//
		process(config,locationFolder);
	}

	private static void process(Map<String, String> config, File locationFolder) {
		//
		String schema = config.get(AbstractTask.KEY_SCHEMA);
		logger.info("Processing schema " + schema);
		//
		Connection con = null;
		try {
			con = SQLUtil.openConnection(config);
			//
			processSchema(config, locationFolder, con);
			//
		} finally {
			SQLUtil.closeConnection(con);
		}
	}

	private static void processSchema(Map<String, String> config, File schemaFolder, Connection con) {
		//
		String schemaName = config.get(AbstractTask.KEY_SCHEMA);
		String env = config.get(AbstractTask.KEY_ENV);
		schemaName= schemaName.replaceAll("\\$\\{ENV\\}", env);
		//
		ensureSchemaExists(config, con, schemaName);
		//
	}

	private static void ensureSchemaExists(Map<String, String> config, Connection con, String schemaName) {
		//
		if (!doesSchemaExist(con, schemaName)) {
			logger.info("Schema did not exist.");
			createSchema(config, con, schemaName);
			logger.info("Schema created.");
		} else {
			logger.info("Schema already existed.");
		}
		//
	}

	private static void createSchema(Map<String, String> config, Connection con, String schemaName) {
		//
		Statement stmt = null;
		try {
			stmt = SQLUtil.createStatement(con);
			//
			String env = config.get(AbstractTask.KEY_ENV);
			//
			String sql = "CREATE SCHEMA IF NOT EXISTS "+schemaName+" LOCATION '/environments/${ENV}/HIVE_DB/STAGE_${ENV}'";
			sql = sql.replaceAll("\\$\\{ENV\\}", env);
			//
			logger.warn(sql);
			//
			SQLUtil.execute(stmt, sql);
			//
			//
			// create table IF NOT EXISTS STAGE_${ENV}.__VERSIONING__ (script string,ddate
			// string)
			//
		} finally {
			SQLUtil.closeStatement(stmt);
		}
		//
	}

	private static boolean doesSchemaExist(Connection con, String schemaName) {
		//
		boolean found = false;
		//
		Statement stmt = null;
		try {
			stmt = SQLUtil.createStatement(con);
			ResultSet rs = SQLUtil.executeQuery(stmt, "show databases");
			//
			while (!found && SQLUtil.next(rs)) {
				String schema = SQLUtil.getStringFromResultset(rs, 1);
				if (schemaName.equalsIgnoreCase(schema)) {
					found = true;
				}
			}
			//
		} finally {
			SQLUtil.closeStatement(stmt);
		}
		//
		return found;
	}

}
