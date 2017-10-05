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
		logger.info("Executing script: " + scriptName);
		//
		for (SQLStatement s : statements) {
			//
			logger.info(scriptName + ":" + s.line + " " + s.sql.substring(0, Math.min(s.sql.length(), 15))+"... ");
			//
			JDBCUtil.execute(stmt, s.sql);
			//
		}
	}

}
