package ar.fb.gradle.plugins.hivemigration.core.internal.utils.files;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import ar.fb.gradle.plugins.hivemigration.HiveMigrationManagedException;
import ar.fb.gradle.plugins.hivemigration.core.internal.migrate.Migrate;

public class ResourceUtil {

	//TODO IMPROVE ... a lot
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
