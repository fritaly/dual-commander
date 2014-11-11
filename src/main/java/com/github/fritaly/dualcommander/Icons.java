package com.github.fritaly.dualcommander;

import javax.swing.ImageIcon;

public final class Icons {

	private static ImageIcon getIcon(String name) {
		return new ImageIcon(Icons.class.getResource(String.format("/com/github/fritaly/dualcommander/icons/%s", name)));
	}

	public static final ImageIcon FOLDER_ICON = getIcon("folder.png");
}