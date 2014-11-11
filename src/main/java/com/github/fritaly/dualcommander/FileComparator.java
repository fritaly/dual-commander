package com.github.fritaly.dualcommander;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.lang.Validate;

public final class FileComparator implements Comparator<File> {

	private final HasParentDirectory delegate;

	public FileComparator(HasParentDirectory delegate) {
		Validate.notNull(delegate, "The given delegate is null");

		this.delegate = delegate;
	}

	@Override
	public int compare(File f1, File f2) {
		// Directories come first
		if (f1.isDirectory()) {
			if (f1.equals(this.delegate.getParentDirectory())) {
				// The parent directory always comes first
				return -1;
			}
			if (f2.isDirectory()) {
				if (f2.equals(this.delegate.getParentDirectory())) {
					// The parent directory always comes first
					return -1;
				}

				// Compare the 2 directories by (case-insensive) name
				return f1.getName().compareToIgnoreCase(f2.getName());
			} else {
				// The directories come first
				return -1;
			}
		} else {
			if (f2.isDirectory()) {
				// The directories come first
				return +1;
			} else {
				// Compare the 2 files by (case-insensive) name
				return f1.getName().compareToIgnoreCase(f2.getName());
			}
		}
	}
}