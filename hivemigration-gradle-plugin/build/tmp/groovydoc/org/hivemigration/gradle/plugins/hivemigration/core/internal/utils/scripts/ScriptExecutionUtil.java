/*
   Copyright 2017 Fernando Raul Bucci

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.scripts;

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
