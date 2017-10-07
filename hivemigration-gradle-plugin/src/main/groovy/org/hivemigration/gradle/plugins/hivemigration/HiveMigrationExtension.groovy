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

package org.hivemigration.gradle.plugins.hivemigration

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
		// Directory containing project build file.
		public String projectDir;	
		// Relative path to the location to scan for migrations. Optional. Defaults to "db/changelog".
		public String location
		// The target version up to which should consider migrations. Null if latest.
		public Integer target
}
