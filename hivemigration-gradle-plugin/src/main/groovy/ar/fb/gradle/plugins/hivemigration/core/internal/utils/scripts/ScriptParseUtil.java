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

package ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ar.fb.gradle.plugins.hivemigration.HiveMigrationManagedException;

public class ScriptParseUtil {

	private static final String DELIMITER = ";";

	public static List<SQLStatement> readStatementsFromScript(String script, String scriptName) {
		List<SQLStatement> list = new ArrayList<SQLStatement>();
		//
		List<String> lines = readLines(script, scriptName);
		//
		// Starts with empty buffer and firstLineNumber
		StringBuilder buffer = new StringBuilder();
		int firstLine = -1;
		//
		for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
			String line = lines.get(lineNumber);
			//
			line = clean(line);
			//
			if (!line.isEmpty() && !isComment(line)) {
				//
				if (firstLine == -1) {
					firstLine = lineNumber;
				}
				//
				boolean endsWithDelimiter = endsWithDelimiter(line);
				if (endsWithDelimiter) {
					line = line.substring(0, line.length() - 1);
				}
				//
				if (buffer.length() != 0) {
					buffer.append("\n");
				}
				buffer.append(line);
				//
				if (endsWithDelimiter) {
					SQLStatement sqlStatement = new SQLStatement(firstLine + 1, buffer.toString());
					list.add(sqlStatement);
					// Reset buffer and firstLineNumber
					buffer = new StringBuilder();
					firstLine = -1;
				}
			}
		}
		//
		if (buffer.length()!=0) {
			SQLStatement sqlStatement = new SQLStatement(firstLine + 1, buffer.toString());
			list.add(sqlStatement);
		}
		//
		return list;
	}

	private static boolean endsWithDelimiter(String line) {
		return line.endsWith(DELIMITER);
	}

	private static boolean isComment(String line) {
		return line.startsWith("--");
	}

	private static String clean(String line) {
		line = line.replaceAll("\\s+", " ");
		line = line.trim();
		line = line.toUpperCase();
		return line;
	}

	public static List<String> readLines(String script, String scriptName) {
		List<String> list = new ArrayList<String>();
		//
		BufferedReader reader = new BufferedReader(new StringReader(script));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			throw new HiveMigrationManagedException("Unable to read lines from script:" + scriptName);
		}
		//
		return list;
	}
}
