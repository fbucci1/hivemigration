package ar.fb.gradle.plugins.hivemigration;

public class HiveMigrationManagedException extends RuntimeException {

	private static final long serialVersionUID = -6443419742558060638L;

	public HiveMigrationManagedException() {
		super();
	}

	public HiveMigrationManagedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HiveMigrationManagedException(String message, Throwable cause) {
		super(message, cause);
	}

	public HiveMigrationManagedException(String message) {
		super(message);
	}

	public HiveMigrationManagedException(Throwable cause) {
		super(cause);
	}

}
