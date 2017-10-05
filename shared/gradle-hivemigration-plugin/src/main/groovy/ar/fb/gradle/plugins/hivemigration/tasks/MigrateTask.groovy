package ar.fb.gradle.plugins.hivemigration.tasks;

import ar.fb.gradle.plugins.hivemigration.core.internal.migrate.Migrate

class MigrateTask extends AbstractTask {

	public static String TASK_NAME = "migrate";

	MigrateTask() {
		super();
		setDescription("Migrate (create schema and apply all DDLs)");
	}

	void run() {
		Migrate.run(config);
	}

}
