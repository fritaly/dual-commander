package fr.ritaly.dualcommander;

import javax.swing.ImageIcon;

public final class Icons {

	private static ImageIcon getIcon(String name) {
		return new ImageIcon(Icons.class.getResource(String.format("/fr/ritaly/dualcommander/icons/%s", name)));
	}

	public static final ImageIcon FOLDER_ICON = getIcon("folder.png");
}