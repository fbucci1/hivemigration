/*
 *   Copyright 2017 Fernando Raul Bucci
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.files;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationManagedException;
import org.hivemigration.gradle.plugins.hivemigration.core.internal.migrate.Migrate;

public class ResourceUtil {

	// TODO IMPROVE ... a lot
	public static String loadResource(String location) {
		try {
			ClassLoader classLoader = Migrate.class.getClassLoader();
			//
			InputStream is = classLoader.getResourceAsStream(location);
			if (is == null) {
				throw new HiveMigrationManagedException("Unable to load resource from classpath: " + location);
			}
			//
			Reader reader = new InputStreamReader(is, "UTF-8");
			//
			StringWriter out = new StringWriter();
			char[] buffer = new char[4096];
			int charsRead;
			while ((charsRead = reader.read(buffer)) != -1) {
				out.write(buffer, 0, charsRead);
			}
			out.flush();
			//
			String aux = out.toString();
			//
			if (aux.startsWith("\ufeff")) { // BOM
				aux = aux.substring(1);
			}
			//
			reader.close();
			is.close();
			//
			return aux;
		} catch (Exception e) {
			throw new HiveMigrationManagedException("Unable to load resource from classpath: " + location, e);
		}
	}

}
