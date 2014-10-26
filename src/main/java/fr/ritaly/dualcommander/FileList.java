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
package fr.ritaly.dualcommander;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.Validate;

import fr.ritaly.dualcommander.event.ChangeEventSource;
import fr.ritaly.dualcommander.event.ChangeEventSupport;

public class FileList extends JPanel implements ListSelectionListener, ChangeEventSource, KeyListener {

	private static final Color EVEN_ROW = Color.WHITE;

	private static final Color ODD_ROW = Color.decode("#DDDDFF");

	private final class FileRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -8630518399718717693L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			final JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			final File file = (File) value;

			if (file.isDirectory()) {
				// Render the directories with a bold font
				final Font font = component.getFont();

				component.setFont(new Font(font.getName(), Font.BOLD, component.getFont().getSize()));

				if (file.equals(getParentDirectory())) {
					// Render the parent directory entry as ".."
					component.setText("[..]");
				} else {
					component.setText(String.format("[%s]", file.getName()));
				}
			} else {
				component.setText(file.getName());
			}

			setBackground((index % 2 == 0) ? EVEN_ROW : ODD_ROW);

			if (isSelected) {
				setForeground(Color.RED);
			} else {
				setForeground(Color.BLACK);
			}

			return component;
		}
	}

	private final class FileComparator implements Comparator<File> {
		@Override
		public int compare(File f1, File f2) {
			// Directories come first
			if (f1.isDirectory()) {
				if (f1.equals(getParentDirectory())) {
					// The parent directory always comes first
					return -1;
				}
				if (f2.isDirectory()) {
					if (f2.equals(getParentDirectory())) {
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

	private static String getCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static File getCanonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final long serialVersionUID = 411590029543053088L;

	private File directory;

	private final SortedListModel<File> listModel;

	private final JList<File> list;

	private final JLabel directoryLabel = new JLabel();

	private final ChangeEventSupport eventSupport = new ChangeEventSupport();

	public FileList(File directory) {
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]", "[][grow]"));

		this.listModel = new SortedListModel<File>(new FileComparator());

		this.list = new JList<>(listModel);
		this.list.setCellRenderer(new FileRenderer());
		this.list.addListSelectionListener(this);
		this.list.addKeyListener(this);

		this.directoryLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		add(directoryLabel, "grow, span");
		add(new JScrollPane(list), "grow");

		// Set the directory (this will populate the list)
		setDirectory(directory);
	}

	public File getDirectory() {
		return getCanonicalFile(directory);
	}

	private File getParentDirectory() {
		final File parentDir = getCanonicalFile(directory).getParentFile();

		return (parentDir != null) && parentDir.exists() ? parentDir : null;
	}

	public void setDirectory(File directory) {
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		this.directory = directory;

		// Refresh the UI

		// Display the (normalized) canonical path
		directoryLabel.setText(getCanonicalPath(directory));

		this.listModel.clear();

		// Populate the list with the directory's entries
		for (File file : directory.listFiles()) {
			if (!file.isHidden()) {
				// TODO Define an option to list hidden entries
				listModel.add(file);
			}
		}

		// If there's a parent directory, add an entry rendered as ".."
		final File parentDir = getCanonicalFile(directory).getParentFile();

		if ((parentDir != null) && parentDir.exists()) {
			listModel.add(parentDir);
		}

		// Reset the selection when the directory changes
		list.clearSelection();
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		this.eventSupport.addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		this.eventSupport.removeChangeListener(listener);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == list) {
			// Propagate the event
			this.eventSupport.fireEvent(new ChangeEvent(this));
		}
	}

	public List<File> getSelection() {
		return this.list.getSelectedValuesList();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() != list) {
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// What's the current selection ?
			final List<File> selection = getSelection();

			if (selection.size() == 1) {
				final File selectedFile = selection.iterator().next();

				if (selectedFile.isDirectory()) {
					// Change to the selected directory
					setDirectory(selectedFile);
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			// Return to the parent directory (if any)
			final File parentDir = getParentDirectory();

			if (parentDir.exists()) {
				setDirectory(parentDir);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
