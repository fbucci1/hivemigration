package ar.fb.gradle.plugins.hivemigration

class HiveMigrationExtension {

		public static final String NAME = 'hivemigration'
				
		// The jdbc url to use to connect to the database. Mandatory.
		public String url
		// The driver to use to connect to the database. Optional. Defaults to org.apache.hive.jdbc.HiveDriver
		public String driver
		// The environment to use. Mandatory. 
		public String ENV
		// The user to use to connect to the database. Optional. Defaults to "".
		public String user
		// The password to use to connect to the database. Optional. Defaults to "".
		public String password
		// The name of the schema. Mandatory.
		public String schema;
		// The name of the table used for metadata. Optional. Defaults to "VERSIONING_METADATA".
		public String table
		// Project root folder. Optional. Defaults to gradlew file.
		public String projectRoot;	
		// Relative path to the location to scan for migrations. Optional. Defaults to "db/changelog".
		public String location
		// The target version up to which should consider migrations. Null if latest.
		public Integer target
}
