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
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.github.fritaly.dualcommander.event.ChangeEventSource;
import com.github.fritaly.dualcommander.event.ChangeEventSupport;


public class DirectoryBrowser extends JPanel implements ListSelectionListener, ChangeEventSource, KeyListener, MouseListener,
		HasParentDirectory, FocusListener {

	private static final Color EVEN_ROW = Color.WHITE;

	private static final Color ODD_ROW = Color.decode("#DDDDFF");

	private final class FileTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -896199602148007012L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			final JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

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

			if (isSelected) {
				setBackground((row % 2 == 0) ? Color.decode("#FFC57A") : Color.decode("#F5AC4C"));
			} else {
				setBackground((row % 2 == 0) ? EVEN_ROW : ODD_ROW);
			}

			setForeground(file.isDirectory() ? Color.BLACK : Color.decode("#555555"));

			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

			return component;
		}
	}

	private final class FileListCellRenderer extends DefaultListCellRenderer {

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

			if (isSelected) {
				setBackground((index % 2 == 0) ? Color.decode("#FFC57A") : Color.decode("#F5AC4C"));
			} else {
				setBackground((index % 2 == 0) ? EVEN_ROW : ODD_ROW);
			}

			setForeground(file.isDirectory() ? Color.BLACK : Color.decode("#555555"));

			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

			return component;
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

	private final Logger logger = Logger.getLogger(this.getClass());

	private File directory;

	private final SortedListModel<File> listModel;

	// TODO Replace the JList by a sortable JTable
	private final JList<File> list;

	private final JButton directoryButton = new JButton(Icons.FOLDER_ICON);

	private final ChangeEventSupport eventSupport = new ChangeEventSupport();

	private final UserPreferences preferences;

	public DirectoryBrowser(UserPreferences preferences, File directory) {
		Validate.notNull(preferences, "The given user preferences are null");
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		this.preferences = preferences;

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]", "[][grow]"));

		this.listModel = new SortedListModel<File>(new FileComparator(this));

		this.list = new JList<>(listModel);
		this.list.setBackground(Utils.getDefaultBackgroundColor());
		this.list.setCellRenderer(new FileListCellRenderer());
		this.list.addListSelectionListener(this);
		this.list.addKeyListener(this);
		this.list.addMouseListener(this);
		this.list.addFocusListener(this);

		this.directoryButton.setFocusable(false);
		this.directoryButton.setHorizontalAlignment(SwingConstants.LEFT);

		add(directoryButton, "grow, span");
		add(new JScrollPane(list), "grow");

		// Set the directory (this will populate the list)
		setDirectory(directory);
	}

	public File getDirectory() {
		return getCanonicalFile(directory);
	}

	@Override
	public File getParentDirectory() {
		final File parentDir = getCanonicalFile(directory).getParentFile();

		return (parentDir != null) && parentDir.exists() ? parentDir : null;
	}

	public void refresh() {
		setDirectory(getDirectory());
	}

	public void setDirectory(File directory) {
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		final File oldDir = this.directory;

		this.directory = directory;

		// Refresh the UI

		// Display the (normalized) canonical path
		directoryButton.setText(getCanonicalPath(directory));

		this.listModel.clear();

		// Populate the list with the directory's entries
		for (File file : directory.listFiles()) {
			if (!file.isHidden() || preferences.isShowHidden()) {
				listModel.add(file);
			}
		}

		// If there's a parent directory, add an entry rendered as ".."
		final File parentDir = getCanonicalFile(directory).getParentFile();

		if ((parentDir != null) && parentDir.exists()) {
			listModel.add(parentDir);
		}

		if ((oldDir != null) && listModel.contains(oldDir)) {
			// Auto-select the directory we just left
			list.setSelectedValue(oldDir, true);
		} else {
			// Auto-select the 1st entry (if there's one)
			if (listModel.getSize() > 1) {
				list.setSelectedIndex(1);
			}
		}

		// Fire an event to ensure listeners are notified of the directory
		// change
		fireChangeEvent();
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		this.eventSupport.addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		this.eventSupport.removeChangeListener(listener);
	}

	private void fireChangeEvent() {
		this.eventSupport.fireEvent(new ChangeEvent(this));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == list) {
			// The parent directory can't be selected
			final File parentDir = getParentDirectory();

			if (parentDir != null) {
				if (getSelection().contains(parentDir)) {
					// Unselect the parent directory entry (always the 1st one)
					list.removeSelectionInterval(0, 0);
				}
			}

			// Propagate the event
			fireChangeEvent();
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

			if ((parentDir != null) && parentDir.exists()) {
				setDirectory(parentDir);
			}
		} else {
			// Propagate event to our listeners
			processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
					e.getKeyLocation()));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() != list) {
			return;
		}

		// Propagate event to our listeners
		processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
				e.getKeyLocation()));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() != list) {
			return;
		}

		// Propagate event to our listeners
		processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
				e.getKeyLocation()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.list) {
			if (e.getClickCount() == 2) {
				// Only react to double clicks
				final List<File> selection = getSelection();

				if (selection.size() == 1) {
					final File file = selection.iterator().next();

					if (file.isDirectory()) {
						// Change to the clicked directory
						setDirectory(file);
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == list) {
			// Propagate the event
			final FocusListener[] listeners = getListeners(FocusListener.class);

			if (listeners != null) {
				final FocusEvent event = new FocusEvent(this, e.getID(), e.isTemporary(), e.getOppositeComponent());

				for (FocusListener listener : listeners) {
					listener.focusGained(event);
				}
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == list) {
			// Propagate the event
			final FocusListener[] listeners = getListeners(FocusListener.class);

			if (listeners != null) {
				final FocusEvent event = new FocusEvent(this, e.getID(), e.isTemporary(), e.getOppositeComponent());

				for (FocusListener listener : listeners) {
					listener.focusLost(event);
				}
			}
		}
	}
}
