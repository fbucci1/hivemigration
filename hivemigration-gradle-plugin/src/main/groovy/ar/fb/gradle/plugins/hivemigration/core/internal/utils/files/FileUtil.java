package ar.fb.gradle.plugins.hivemigration.core.internal.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import ar.fb.gradle.plugins.hivemigration.HiveMigrationManagedException;

public class FileUtil {

	// TODO IMPROVE ... a lot
	public static String loadFile(File file) {
		try {
			//
			InputStream is = new FileInputStream(file);
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
			throw new HiveMigrationManagedException("Unable to load file: " + file, e);
		}
	}
}
