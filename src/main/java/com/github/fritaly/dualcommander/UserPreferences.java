package com.github.fritaly.dualcommander;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;

import org.apache.commons.lang.Validate;

public final class UserPreferences {

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * Whether hidden directories and files should be shown.
	 */
	private boolean showHidden = false;

	public UserPreferences() {
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport.removePropertyChangeListener(listener);
	}

	public void init(Preferences preferences) {
		Validate.notNull(preferences, "The given preferences is null");

		this.showHidden = preferences.getBoolean("show.hidden", false);
	}

	public void saveState(Preferences preferences) {
		Validate.notNull(preferences, "The given preferences is null");

		preferences.putBoolean("show.hidden", this.showHidden);
	}

	public boolean isShowHidden() {
		return showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		final boolean oldValue = this.showHidden;

		this.showHidden = showHidden;

		if (oldValue != showHidden) {
			this.changeSupport.firePropertyChange("showHidden", oldValue, showHidden);
		}
	}
}