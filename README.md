# HiveMigration

HiveMigration is a Gradle plugin with the sole purpose of supporting Database Migrations best practice on HIVE.

# Concepts 

## Migrations

Database Migrations is about updating the database from one version to the next using migrations (migration scripts).

Migrations are written in HIVE QL and can consist of multiple statements.

Within a single migration, all statements are run within a single transaction.

## Metadata table

To keep track of which migrations have already been applied when and by whom, a special bookkeeping table is added to your schema. 

Metadata table also tracks whether or not the migrations were successful. 

In case a migration was not successfull, it will be re-tried the next time the plugin is executed.

# How it works

1. HIVEMigrate creates the HIVE schema/database, if it does not exist yet.

1. HIVEMigrate creates the metadata table, if it does not exist yet.

1. HIVEMigrate queries the file system looking for migration scripts.

1. HiveMigrate executes all remaining scripts so the schema/database structure reaches the level you desire. 

   HIVE schema can be migrated to a predefined version or to the latest version available. It is just up to you.

# Example

1. Download the repository to your computer.

   ```$ git clone https://github.com/fbucci1/hivemigration.git```

1. Build and install the HiveMigration plugin in your local Maven repository.

   ```hivemigration-gradle-plugin$ gradle install```

1. Check connection settings in the sample project.

   Use your favourite text editor to open *hivemigration-sample/build.gradle*

   ![Image](https://github.com/fbucci1/hivemigration/raw/master/doc/build.gradle.png)

1. Ensure your [HIVE Server - HiveServer2](https://cwiki.apache.org/confluence/display/Hive/Setting+Up+HiveServer2) is running.

1. Execute the plugin. It will create the schema and exeecute all migrations.

   ```hivemigration-sample$ gradle migrate```

   ![Image](https://github.com/fbucci1/hivemigration/raw/master/doc/migrate.png)

1. Let's see content in HIVE.

   ![Image](https://github.com/fbucci1/hivemigration/raw/master/doc/schemas.png)

   ![Image](https://github.com/fbucci1/hivemigration/raw/master/doc/tables.png)

   ![Image](https://github.com/fbucci1/hivemigration/raw/master/doc/metadata.png)

# Documentation

## Configuration

Properties in the build.gradle file:

|Name|Type|Description|
|---|---|---|
|url|String|The jdbc url to use to connect to the database. Mandatory.|
|driver|String|The driver to use to connect to the database. Optional. Defaults to org.apache.hive.jdbc.HiveDriver|
|ENV|String|The environment to use. Mandatory.|
|user|String|The user to use to connect to the database. Optional. Defaults to "".|
|password|String|The password to use to connect to the database. Optional. Defaults to "".|
|schema|String|The name of the schema. Mandatory.|
|table|String|The name of the table used for metadata. Optional. Defaults to "VERSIONING_METADATA".|
|projectRoot|String|Project root folder. Optional. Defaults to gradlew file.|
|location|String|Relative path to the location to scan for migrations. Optional. Defaults to "db/changelog".|
|target|String|The target version up to which should consider migrations. Use null if latest.|

Environment variables:

|Name|Type|Description|
|---|---|
|ENV|The environment to use. Mandatory.|
|user|The user to use to connect to the database. Optional. Defaults to "".|
|password|The password to use to connect to the database. Optional. Defaults to "".|

Notice environment variables are used only in case build.gradle file does not especify a value for the corresponding parameters.

Properties in command line:

As shown in the example above, properties can be passed from the command line. For instance: 

´´´gradle migrate -Puser=HeyItIsMe´´´

In order for the plugin to read the corresponding property, findProperty() method can be used. For instance:   

   ´´´user=findProperty("user")´´´

## Metadata table

|Name|Type|Description|
|---|---|---|
|seq|BIGINT|Timestamp when script started executing (System.currentTimeMillis())|
|version|INT|Database version. Retrieved from file name.|
|script|VARCHAR(1000)|Name of the script file.|
|installed_by|VARCHAR(100)|User used for HIVE connection.|
|installed_on|VARCHAR(23)|Timestamp when script started being executed.|
|execution_time|BIGINT|Milliseconds ellapsed for the execution of the script.|
|success|DECIMAL(1)|1 ok, 0 error.|

# Contributing

## Report Bugs
Have you found a bug? Please let us know in the [Issue Tracker](https://github.com/fbucci1/hivemigration/issues).

If someone else already submitted an issue for the feature, leave a comment with +1.

## Request features
Do you have an idea for a great feature? Please let us know in the [Issue Tracker](https://github.com/fbucci1/hivemigration/issues).
Make sure to do the following:
* be as specific as possible
* provide examples (so other people can better visualize what you mean)
* think about the entire Flyway ecosystem and how it may apply to other parts
* include details about the environment (OS, DB incl. version) where it will really shine
In general try to focus on quick wins, with a broad appeal.

If someone else already submitted an issue for the feature, leave a comment with +1.

## Write code
Is there an issue in the issue tracker that you would like to fix? 
Is there an new feature that you would like to implement?
Great! Just fork our [GitHub repository](https://github.com/fbucci1/hivemigration), write your code and, when you are done, create a pull request.

# Resources

## Database Migrations

* http://enterprisecraftsmanship.com/2015/08/10/database-versioning-best-practices/

## Similar solutions

These are projects that support database migrations. However, none of them currently support HIVE at the time HiveMigration was created.

* https://flywaydb.org/
* http://www.liquibase.org/
* https://github.com/fluentmigrator/fluentmigrator
* https://github.com/vkhorikov/DatabaseUpgradeTool

# License

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
