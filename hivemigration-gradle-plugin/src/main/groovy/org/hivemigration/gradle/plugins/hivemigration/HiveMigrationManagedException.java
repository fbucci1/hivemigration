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

package org.hivemigration.gradle.plugins.hivemigration;

public class HiveMigrationManagedException extends RuntimeException {

	private static final long serialVersionUID = -6443419742558060638L;

	public HiveMigrationManagedException() {
		super();
	}

	public HiveMigrationManagedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
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
