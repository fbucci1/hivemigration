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

package org.hivemigration.gradle.plugins.hivemigration.core.internal.utils.scripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hive.jdbc.HiveDriver;
import org.apache.hive.jdbc.Utils.JdbcConnectionParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hivemigration.gradle.plugins.hivemigration.HiveMigrationManagedException;
import org.hivemigration.gradle.plugins.hivemigration.tasks.AbstractTask;

public class JDBCUtil {

	static final Logger logger = LogManager.getLogger(JDBCUtil.class.getName());

	public static boolean next(ResultSet rs) {
		try {
			return rs.next();
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error fetching next row from resultset", e);
		}
	}

	public static ResultSet executeQuery(Statement stmt, String sql) {
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error executing query: " + sql, e);
		}
	}

	public static void execute(Statement stmt, String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error executing query: " + sql, e);
		}
	}

	public static Statement createStatement(Connection con) {
		try {
			return con.createStatement();
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error creating statement.", e);
		}
	}

	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
		}
	}

	public static Connection openConnection(Map<String, String> config) {
		//
		// Connection via DriverManager does not work when executing as a gradle
		// plugin, that is why we instantiate HiveDriver directly, not via
		// DriverManager.
		//
		// Unfortuately the JDBC Hive driver only allows connecting to the "default"
		// database - so to get around this just use the table aliasing in dot notation,
		// like this: select * from store.sales Connects to database store, table sales!
		// This is the page that points out the limitation in Hadoop Hive JDBC driver:
		// https://cwiki.apache.org/confluence/display/Hive/HiveClient#HiveClient-JDBC
		//
		logger.info("Instantiating JDBC driver");
		HiveDriver driver = new HiveDriver();
		//
		Properties info = new Properties();
		if (config.get(AbstractTask.KEY_USER) != null) {
			info.setProperty(JdbcConnectionParams.AUTH_USER, config.get(AbstractTask.KEY_USER));
		}
		if (config.get(AbstractTask.KEY_PASSWORD) != null) {
			info.setProperty(JdbcConnectionParams.AUTH_PASSWD, config.get(AbstractTask.KEY_PASSWORD));
		}
		//
		String url = config.get(AbstractTask.KEY_URL);
		logger.info("Connecting to " + url);
		Connection con;
		try {
			con = driver.connect(url, info);
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Unable to connect to " + url, e);
		}
		//
		if (con == null) {
			throw new HiveMigrationManagedException("Unable to connect to " + url);
		}
		return con;
	}

	public static void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
		}
	}

	public static String getStringFromResultset(ResultSet rs, int i) {
		try {
			return rs.getString(i);
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error fetching String from resultset", e);
		}
	}

	public static int executeUpdate(Connection con, String sql, List<Object> values) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			//
			for (int i = 0; i < values.size(); i++) {
				Object value = values.get(i);
				if (value instanceof java.util.Date) {
					stmt.setDate(i + 1, new java.sql.Date(((java.util.Date) value).getTime()));
				} else {
					stmt.setObject(i + 1, value);
				}
			}
			//
			return stmt.executeUpdate();
			//
		} catch (SQLException e) {
			throw new HiveMigrationManagedException("Error executing update: " + sql, e);
		} finally {
			JDBCUtil.closeStatement(stmt);
		}
	}

}
