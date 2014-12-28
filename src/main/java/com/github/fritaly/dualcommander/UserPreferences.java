/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.fritaly.dualcommander;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public final class UserPreferences {

	private final Logger logger = Logger.getLogger(this.getClass());

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * The name associated to the {@link #showHidden} property.
	 */
	public static final String PROPERTY_SHOW_HIDDEN = "show.hidden";

	public static final String PROPERTY_EDIT_FILE_COMMAND = "commands.edit.file";

	public static final String PROPERTY_VIEW_FILE_COMMAND = "commands.view.file";

	/**
	 * Whether hidden directories and files should be shown.
	 */
	private boolean showHidden = false;

	/**
	 * The command line for editing a file with an external process.
	 */
	private String editFileCommand = "edit";

	/**
	 * The command line for viewing a file with an external process.
	 */
	private String viewFileCommand = "open";

	/**
	 * Whether the object state has been initialized.
	 */
	private boolean initialized = false;

	public UserPreferences() {
	}

	// Copy constructor
	public UserPreferences(UserPreferences preferences) {
		Validate.notNull(preferences, "The given user preferences is null");

		this.showHidden = preferences.isShowHidden();
		this.editFileCommand = preferences.getEditFileCommand();
		this.viewFileCommand = preferences.getViewFileCommand();

		// The user preferences can be initialized only once
		this.initialized = true;
	}

	public void apply(UserPreferences preferences) {
		Validate.notNull(preferences, "The given user preferences is null");

		setShowHidden(preferences.isShowHidden());
		setEditFileCommand(preferences.getEditFileCommand());
		setViewFileCommand(preferences.getViewFileCommand());
	}

	private void assertInitialized() {
		if (!isInitialized()) {
			throw new IllegalStateException("The user preferences haven't been initialized");
		}
	}

	private void assertNotInitialized() {
		if (isInitialized()) {
			throw new IllegalStateException("The user preferences have already been initialized");
		}
	}

	private synchronized boolean isInitialized() {
		return this.initialized;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport.removePropertyChangeListener(listener);
	}

	public synchronized void init(Preferences preferences) {
		assertNotInitialized();

		Validate.notNull(preferences, "The given preferences is null");

		this.showHidden = preferences.getBoolean(PROPERTY_SHOW_HIDDEN, false);
		this.editFileCommand = preferences.get(PROPERTY_EDIT_FILE_COMMAND, "edit");
		this.viewFileCommand = preferences.get(PROPERTY_VIEW_FILE_COMMAND, "open");

		// The user preferences can be initialized only once
		this.initialized = true;

		if (logger.isInfoEnabled()) {
			logger.info("Initialized user preferences");
		}
	}

	public void saveState(Preferences preferences) {
		assertInitialized();

		Validate.notNull(preferences, "The given preferences is null");

		preferences.putBoolean(PROPERTY_SHOW_HIDDEN, this.showHidden);
		preferences.put(PROPERTY_EDIT_FILE_COMMAND, this.editFileCommand);
		preferences.put(PROPERTY_VIEW_FILE_COMMAND, this.viewFileCommand);

		if (logger.isInfoEnabled()) {
			logger.info("Saved user preferences");
		}
	}

	public String getEditFileCommand() {
		assertInitialized();

		return editFileCommand;
	}

	public String getViewFileCommand() {
		assertInitialized();

		return viewFileCommand;
	}

	public void setEditFileCommand(String command) {
		Validate.notNull(command, "The given edit file command is null");

		assertInitialized();

		// No need to notify listeners
		this.editFileCommand = command;
	}

	public void setViewFileCommand(String command) {
		Validate.notNull(editFileCommand, "The given view file command is null");

		assertInitialized();

		// No need to notify listeners
		this.viewFileCommand = command;
	}

	public boolean isShowHidden() {
		assertInitialized();

		return showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		assertInitialized();

		final boolean oldValue = this.showHidden;

		this.showHidden = showHidden;

		if (oldValue != showHidden) {
			this.changeSupport.firePropertyChange(PROPERTY_SHOW_HIDDEN, oldValue, showHidden);
		}
	}
}