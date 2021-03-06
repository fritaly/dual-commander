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

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;

public class Utils {

	public static int getTimestampRenderWidth() {
		final String text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		final Font font = Utils.getDefaultFont();
		final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);

		return (int) font.getStringBounds(text, context).getWidth();
	}

	public static Border createEmptyBorder(int space) {
		return BorderFactory.createEmptyBorder(space, space, space, space);
	}

	public static Border createRaisedBevelBorder() {
		return BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), Utils.createEmptyBorder(1));
	}

	private static class NegatingComparator<T> implements Comparator<T> {

		private final Comparator<T> decorated;

		NegatingComparator(Comparator<T> decorated) {
			Validate.notNull(decorated, "The given comparator is null");

			this.decorated = decorated;
		}

		@Override
		public int compare(T o1, T o2) {
			// Swap the 2 arguments
			return decorated.compare(o2, o1);
		}
	}

	public static <T> Comparator<T> negate(Comparator<T> comparator) {
		Validate.notNull(comparator, "The given comparator is null");

		if (comparator instanceof NegatingComparator) {
			return ((NegatingComparator<T>) comparator).decorated;
		}

		return new NegatingComparator<T>(comparator);
	}

	/**
	 * Recursively deletes the given file or directory and notifies the provided
	 * handler when a file / directory has been deleted.
	 *
	 * @param file
	 *            the file / directory to delete. Can't be null.
	 * @param handler
	 *            a possible listener to be notified of file deletions. Can be
	 *            null.
	 */
	public static void deleteRecursively(File file, FileHandler handler) {
		// The given file can be a file or a directory
		// The given listener can be null
		Validate.notNull(file, "The given file is null");

		if (file.isDirectory()) {
			// Delete the directory recursively
			for (File entry : file.listFiles()) {
				deleteRecursively(entry, handler);
			}
		}

		// Delete the file or the empty directory
		file.delete();

		if (handler != null) {
			handler.handle(file);
		}
	}

	public static Color getDefaultBackgroundColor() {
		return new JLabel().getBackground();
	}

	public static Font getDefaultFont() {
		return new JLabel().getFont();
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

	// --- The following methods were copied from commons-io's FileUtils --- //

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The file copy buffer size (30 MB)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

    public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && srcDir.isDirectory() == false) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }

    public static void copyDirectory(File srcDir, File destDir,
            boolean preserveFileDate) throws IOException {

        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir,
            FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }

    private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
            boolean preserveFileDate, List<String> exclusionList) throws IOException {
        // recurse
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {  // null if abstract pathname does not denote a directory, or if an I/O error occurs
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(srcFile, dstFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }

    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        File destFile = new File(destDir, srcFile.getName());
        copyFile(srcFile, destFile, preserveFileDate);
    }

    public static void copyFile(File srcFile, File destFile,
            boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination '" + parentFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    public static Font getBoldFont(Font font) {
    	Validate.notNull(font, "The given font is null");

    	return new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize());
    }

    public static Scan scan(Collection<File> collection) {
    	Validate.notNull(collection, "The given collection of files is null");

    	final Scan scan = new Scan();

    	for (File element : collection) {
			if (element.isFile()) {
				scan.visitFile(element);
			} else {
				try {
					Files.walkFileTree(element.toPath(), scan);
				} catch (IOException e) {
					throw new RuntimeException("Error when walking directory '" + element + "'", e);
				}
			}
		}

    	return scan;
    }
}