package fr.ritaly.dualcommander;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JLabel;

public class Utils {

	public static Color getDefaultBackgroundColor() {
		return new JLabel().getBackground();
	}

	private static Properties getApplicationProperties() {
		try {
			final Properties properties = new Properties();
			properties.load(Utils.class.getResourceAsStream("/application.properties"));

			return properties;
		} catch (IOException e) {
			// Should never happen
			throw new RuntimeException("Unable to load application properties from resource file 'application.properties'", e);
		}
	}

	public static String getApplicationVersion() {
		return getApplicationProperties().getProperty("version");
	}

	public static Date getApplicationReleaseDate() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm.ss (Z)").parse(getApplicationProperties().getProperty("release.date"));
		} catch (ParseException e) {
			// Return null (happens when the value hasn't been replaced and is
			// set to @RELEASE_DATE@)
			return null;
		}
	}
}