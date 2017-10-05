package ar.fb.gradle.plugins.hivemigration.core.internal.utils.scripts;

public class SQLStatement {

	public SQLStatement(int line, String sql) {
		this.line = line;
		this.sql = sql;
	}

	public int line;

	public String sql;

}
