package ar.fb.gradle.plugins.hivemigration

import org.gradle.api.Plugin
import org.gradle.api.Project

import ar.fb.gradle.plugins.hivemigration.tasks.MigrateTask

class HiveMigrationPlugin implements Plugin<Project> {
	
	public static final NAME = "gradle-hivemigration-plugin"
	
	@Override
	void apply(Project target) {
		HiveMigrationExtension extension = target.extensions.create(HiveMigrationExtension.NAME, HiveMigrationExtension)
		target.task(MigrateTask.TASK_NAME, type: MigrateTask)
	}
}
