package ar.fb.gradle.plugins.hivemigration

class HiveMigrationExtension {

		public static final String NAME = 'hivemigration'
				
		// The jdbc url to use to connect to the database
		public String url
		// The driver to use to connect to the database
		public String driver
		// The environment to use
		public String ENV
		// The user to use to connect to the database
		public String user
		// The password to use to connect to the database
		public String password
		// The name of the schema
		public String schema;
		// The name of the table used for metadata
		public String table
		// Project root folder
		public String projectRoot;	
		// Relative path to the location to scan for migrations
		public String location
		// The target version up to which should consider migrations
		public String target

}
