package ar.fb.gradle.plugins.hivemigration.core.internal.migrate;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
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

	public static void executeScripts(Map<String, String> config, Connection con, File schemaFolder) {
		//
		Map<Integer, File> sortedVFiles = getSortedVFiles(schemaFolder);
		//
		for (Integer version : sortedVFiles.keySet()) {
			//
			File file = sortedVFiles.get(version);
			//
			String auxTarget = config.get(AbstractTask.KEY_TARGET);
			Integer target = auxTarget == null ? null : Integer.valueOf(auxTarget);
			//
			if (target != null && version > target) {
				logger.info("Ignoring script: " + file.getName() + ", target: " + target);
			} else {
				executeScript(config, con, version, file);
			}
			//
		}
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
