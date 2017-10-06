package ar.fb.gradle.plugins.hivemigration.core.internal.migrate;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ar.fb.gradle.plugins.hivemigration.HiveMigrationManagedException;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.PlaceholderUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.files.FileUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.JDBCUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.SQLStatement;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.ScriptExecutionUtil;
import ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts.ScriptParseUtil;
import ar.fb.gradle.plugins.hivemigration.tasks.AbstractTask;

public class MigrateScriptExecutor {

	static final Logger logger = LogManager.getLogger(MigrateScriptExecutor.class.getName());

	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void executeScripts(Map<String, String> config, Connection con, File schemaFolder) {
		//
		Integer initialVersion = retrieveInitialVersion(config, con);
		//
		String auxTarget = config.get(AbstractTask.KEY_TARGET);
		Integer target = auxTarget == null ? null : Integer.valueOf(auxTarget);
		//
		logger.info("Current version in schema is " + initialVersion + ". Target is " + target);
		//
		Map<Integer, File> sortedVFiles = getSortedVFiles(schemaFolder);
		//
		for (Integer version : sortedVFiles.keySet()) {
			//
			File file = sortedVFiles.get(version);
			//
			if (target != null && version > target) {
				logger.info("Ignoring script: " + file.getName());
			} else if (initialVersion != null && version <= initialVersion) {
				logger.info("Ignoring script: " + file.getName());
			} else {
				//
				int success = 0;
				long initTime = System.currentTimeMillis();
				try {
					executeScript(config, con, version, file);
					success = 1;
				} catch (NullPointerException e) {
				}
				long endTime = System.currentTimeMillis();
				//
				long executionTime = endTime - initTime;
				insertRowInMetadataTable(config, con, version, file, initTime, executionTime, success);
			}
			//
		}
	}

	private static void insertRowInMetadataTable(Map<String, String> config, Connection con, Integer version, File file,
			long initTime, long executionTime, int success) {
		//
		String sql = "INSERT INTO ${schema}.${table} " + //
				"(seq,version,script,installed_by,installed_on,execution_time,success) values " + //
				"(?,?,?,?,?,?,?)";
		//
		List<Object> values = new ArrayList<Object>();
		values.add(System.currentTimeMillis());
		values.add(version);
		values.add(file.getName());
		values.add(sdf.format(new Date(initTime)));
		values.add(config.get(AbstractTask.KEY_USER));
		values.add(executionTime);
		values.add(success);
		//
		Map<String, String> map = new HashMap<String, String>();
		map.put("ENV", config.get(AbstractTask.KEY_ENV));
		map.put("schema", config.get(AbstractTask.KEY_SCHEMA));
		map.put("table", config.get(AbstractTask.KEY_TABLE));
		sql = PlaceholderUtil.replaceTokens(map, sql);
		//
		JDBCUtil.executeUpdate(con, sql, values);
		//
	}

	private static Integer retrieveInitialVersion(Map<String, String> config, Connection con) {
		//
		Integer initialVersion = null;
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			//
			String sql = "Select version from ${schema}.${table} where success=1 order by version desc limit 1";
			//
			Map<String, String> map = new HashMap<String, String>();
			map.put("ENV", config.get(AbstractTask.KEY_ENV));
			map.put("schema", config.get(AbstractTask.KEY_SCHEMA));
			map.put("table", config.get(AbstractTask.KEY_TABLE));
			sql = PlaceholderUtil.replaceTokens(map, sql);
			//
			ResultSet rs = JDBCUtil.executeQuery(stmt, sql);
			//
			if (JDBCUtil.next(rs)) {
				String auxMax = JDBCUtil.getStringFromResultset(rs, 1);
				if (auxMax != null) {
					initialVersion = Integer.valueOf(auxMax);
				}
			}
			//
		} finally {
			JDBCUtil.closeStatement(stmt);
		}
		//
		return initialVersion;
	}

	private static void executeScript(Map<String, String> config, Connection con, Integer version, File file) {
		//
		Statement stmt = null;
		try {
			stmt = JDBCUtil.createStatement(con);
			//
			String script = FileUtil.loadFile(file);
			String scriptName = file.getName();
			//
			Map<String, String> map = new HashMap<String, String>();
			map.put("ENV", config.get(AbstractTask.KEY_ENV));
			map.put("schema", config.get(AbstractTask.KEY_SCHEMA));
			//
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

	private static Map<Integer, File> getSortedVFiles(File schemaFolder) {
		//
		List<File> files = scanForFiles(schemaFolder);
		//
		Map<Integer, File> sortedFiles = new TreeMap<Integer, File>();
		//
		for (File file : files) {
			//
			String fileName = file.getName();
			if (fileName.startsWith("V")) { // ignore R files
				//
				fileName = fileName.substring(1, fileName.length() - 3); // Remove preffix (V) and suffix (.sql)
				//
				int pos = fileName.indexOf("__");
				if (pos == -1) {
					throw new HiveMigrationManagedException(
							"Wrong file name format. It must have '__' after version number. File: " + file);
				}
				//
				Integer version = Integer.valueOf(fileName.substring(0, pos));
				//
				if (sortedFiles.containsKey(version)) {
					throw new HiveMigrationManagedException(
							"Version number must be unique. There are 2 or more files with the same version: " + version
									+ ". One of them is: " + file);
				}
				//
				sortedFiles.put(version, file);
			}
		}
		//
		return sortedFiles;
	}

	private static List<File> scanForFiles(File schemaFolder) {
		//
		List<File> list = new ArrayList<File>();
		//
		File[] files = schemaFolder.listFiles();
		for (File file : files) {
			//
			if (file.isFile()) {
				//
				if (file.getName().startsWith("R") || file.getName().startsWith("V")) {
					if (file.getName().endsWith(".sql")) {
						list.add(file);
					} else {
						throw new HiveMigrationManagedException(
								"Wrong sufix for file name. It must be '.sql'. File: " + file);
					}
				} else {
					throw new HiveMigrationManagedException(
							"Wrong prefix for file name. It must be 'V' or 'R'. File: " + file);
				}
			}
			//
		}
		//
		return list;
		//
	}

}
