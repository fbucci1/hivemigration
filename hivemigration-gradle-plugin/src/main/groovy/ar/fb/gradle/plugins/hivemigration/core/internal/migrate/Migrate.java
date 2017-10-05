package ar.fb.gradle.plugins.hivemigration.core.internal.migrate;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.JDBCUtil;
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
		MigrateMetadataTableCreator.ensureTableExists(config, con);
		//
		MigrateScriptExecutor.executeScripts(config, con, schemaFolder);
		//
	}

}
