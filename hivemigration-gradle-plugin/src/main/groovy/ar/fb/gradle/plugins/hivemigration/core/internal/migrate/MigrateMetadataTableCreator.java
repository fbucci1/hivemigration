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

public class MigrateMetadataTableCreator {

	static final Logger logger = LogManager.getLogger(MigrateMetadataTableCreator.class.getName());

	public static void ensureTableExists(Map<String, String> config, Connection con) {
		//
		String schema = config.get(AbstractTask.KEY_SCHEMA);
		String table = config.get(AbstractTask.KEY_TABLE);
		//
		if (!doesTableExist(con, schema, table)) {
			logger.info("Metadata table does not exist: "+table);
			createTable(config, con, schema, table);
			logger.info("Metadata table created: "+table);
		} else {
			logger.info("Metadata table already exists: "+table);
		}
		//
	}

	private static void createTable(Map<String, String> config, Connection con, String schema, String table) {
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			//
			// TODO: look first in Filesystem if overriden by project in db/support
			String resourceLocation = "db/createMetadataTable.sql";
			String script = ResourceUtil.loadResource(resourceLocation);
			String scriptName = resourceLocation.replaceAll("db/", "res:");
			//
			Map<String, String> map = new HashMap<String, String>();
			map.put("ENV", config.get(AbstractTask.KEY_ENV));
			map.put("schema", config.get(AbstractTask.KEY_SCHEMA));
			map.put("table", config.get(AbstractTask.KEY_TABLE));
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

	private static boolean doesTableExist(Connection con, String schema, String table) {
		//
		boolean found = false;
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			ResultSet rs = JDBCUtil.executeQuery(stmt, "show tables in " + schema);
			//
			while (!found && JDBCUtil.next(rs)) {
				String table2 = JDBCUtil.getStringFromResultset(rs, 1);
				if (table.equalsIgnoreCase(table2)) {
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
