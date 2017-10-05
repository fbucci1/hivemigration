package ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptExecutionUtil {

	static final Logger logger = LogManager.getLogger(ScriptExecutionUtil.class.getName());

	public static void executeScript(Map<String, String> config, Statement stmt, String scriptName,
			List<SQLStatement> statements) {
		//
		logger.warn("Executing script: " + scriptName);
		//
		for (SQLStatement s : statements) {
			//
			logger.warn(scriptName + " " + s.line + ": [" + s.sql + "]");
			//
			JDBCUtil.execute(stmt, s.sql);
			//
		}
	}

}
