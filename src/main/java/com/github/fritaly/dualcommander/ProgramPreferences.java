package com.github.fritaly.dualcommander;

public final class ProgramPreferences {

	/**
	 * Whether hidden directories and files should be shown.
	 */
	private boolean showHidden = false;

	public ProgramPreferences() {
	}

	public boolean isShowHidden() {
		return showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}
}