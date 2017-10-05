package ar.fb.gradle.plugins.hivemigration.core.internal.migrate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.fb.gradle.plugins.hivemigration.core.internal.utils.PlaceholderUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.files.ResourceUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.SQLStatement;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.JDBCUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.ScriptExecutionUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.ScriptParseUtil;
import ar.fb.gradle.plugins.hivemigration.tasks.AbstractTask;

public class MigrateSchemaCreator {
	static final Logger logger = LogManager.getLogger(MigrateSchemaCreator.class.getName());

	public static void ensureSchemaExists(Map<String, String> config, Connection con) {
		//
		String schema = config.get(AbstractTask.KEY_SCHEMA);
		//
		if (!doesSchemaExist(con, schema)) {
			logger.info("Schema does not exist: " + schema);
			createSchema(config, con, schema);
			logger.info("Schema created.");
		} else {
			logger.info("Schema already exists: " + schema);
		}
		//
	}

	private static void createSchema(Map<String, String> config, Connection con, String schema) {
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			//
			// TODO: look first in Filesystem if overriden by project in db/support
			String resourceLocation = "db/createSchema.sql";
			String script = ResourceUtil.loadResource(resourceLocation);
			String scriptName = resourceLocation.replaceAll("db/", "res:");
			//
			Map<String, String> map = new HashMap<String, String>();
			map.put("ENV", config.get(AbstractTask.KEY_ENV));
			map.put("schema", config.get(AbstractTask.KEY_SCHEMA));
			script = PlaceholderUtil.replaceTokens(map, script);
			//
			List<SQLStatement> statements = ScriptParseUtil.readStatementsFromScript(script, scriptName);
			//
			ScriptExecutionUtil.executeScript(config, stmt, scriptName, statements);
			//
		} finally {
			JDBCUtil.closeStatement(stmt);
		}
		//
	}

	private static boolean doesSchemaExist(Connection con, String schema) {
		//
		boolean found = false;
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			ResultSet rs = JDBCUtil.executeQuery(stmt, "show databases");
			//
			while (!found && JDBCUtil.next(rs)) {
				String schema2 = JDBCUtil.getStringFromResultset(rs, 1);
				if (schema.equalsIgnoreCase(schema2)) {
					found = true;
				}
			}
			//
		} finally {
			JDBCUtil.closeStatement(stmt);
		}
		//
		return found;
	}

}
