package ar.fb.gradle.plugins.hivemigration.core.internal.utils;

import java.util.Map;

public class PlaceholderUtil {

	public static String replaceTokens(Map<String, String> map, String value) {
		//
		for (String key2 : map.keySet()) {
			value = value.replaceAll("\\$\\{" + key2 + "\\}", map.get(key2));
		}
		//
		return value;
		//
	}

}
