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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.github.fritaly.dualcommander.event.ChangeEventSource;
import com.github.fritaly.dualcommander.event.ChangeEventSupport;
import com.github.fritaly.dualcommander.event.ColumnEvent;
import com.github.fritaly.dualcommander.event.ColumnEventHelper;
import com.github.fritaly.dualcommander.event.ColumnEventListener;


public class DirectoryBrowser extends JPanel implements ListSelectionListener, ChangeEventSource, KeyListener, MouseListener,
		HasParentDirectory, FocusListener, ColumnEventListener {

	private static final Color EVEN_ROW = Color.WHITE;

	private static final Color ODD_ROW = Color.decode("#DDDDFF");

	private final class FileNameRenderer extends DefaultTableCellRenderer {

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
			setBorder(Utils.createEmptyBorder(2));

			return component;
		}
	}

	private final class FileTypeRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -1456922668251532841L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			final JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			final File file = (File) value;

			if (file.isDirectory()) {
				component.setIcon(Icons.FOLDER_ICON);
			} else {
				component.setIcon(null);
			}

			component.setText("");

			if (isSelected) {
				setBackground((row % 2 == 0) ? Color.decode("#FFC57A") : Color.decode("#F5AC4C"));
			} else {
				setBackground((row % 2 == 0) ? EVEN_ROW : ODD_ROW);
			}

			setForeground(Color.decode("#555555"));
			setBorder(Utils.createEmptyBorder(2));

			return component;
		}
	}

	private final class FileSizeRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -5094024636812268688L;

		private final DecimalFormat decimalFormat;

		public FileSizeRenderer() {
			final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
			symbols.setGroupingSeparator(' ');

			this.decimalFormat = new DecimalFormat();
			this.decimalFormat.setDecimalFormatSymbols(symbols);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			final JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			final File file = (File) value;

			if (file.isFile()) {
				// Render the file sizes with ' ' as grouping separator
				component.setText(decimalFormat.format(file.length()));
				component.setHorizontalAlignment(JLabel.RIGHT);
			} else {
				// Render the directories with a bold font
				final Font font = component.getFont();

				component.setFont(new Font(font.getName(), Font.BOLD, component.getFont().getSize()));
				component.setText("[DIR]");
			}

			if (isSelected) {
				setBackground((row % 2 == 0) ? Color.decode("#FFC57A") : Color.decode("#F5AC4C"));
			} else {
				setBackground((row % 2 == 0) ? EVEN_ROW : ODD_ROW);
			}

			setForeground(file.isDirectory() ? Color.BLACK : Color.decode("#555555"));
			setBorder(Utils.createEmptyBorder(2));

			return component;
		}
	}

	private final class LastUpdateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -1888924791239159846L;

		private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			final JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			final File file = (File) value;

			if (file.equals(getParentDirectory())) {
				// Don't display the last modified date for the parent directory
				component.setText("");
			} else {
				component.setText(dateFormat.format(new Date(file.lastModified())));
			}

			if (isSelected) {
				setBackground((row % 2 == 0) ? Color.decode("#FFC57A") : Color.decode("#F5AC4C"));
			} else {
				setBackground((row % 2 == 0) ? EVEN_ROW : ODD_ROW);
			}

			setForeground(Color.decode("#555555"));
			setBorder(Utils.createEmptyBorder(2));

			return component;
		}
	}

	private final class TableHeaderRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 596061491019164527L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (tableModel.getSortCriteria().ordinal() == column) {
				setIcon(!tableModel.isSortAscending() ? Icons.TRIANGLE_UP_ICON : Icons.TRIANGLE_DOWN_ICON);
			} else {
				setIcon(null);
			}

			// Display the text on the left and the icon on the right
			setHorizontalTextPosition(SwingConstants.LEFT);
			setText(value.toString());
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

			return this;
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

	private final FileTableModel tableModel;

	private final JTable table;

	private final JButton directoryButton = new JButton(Icons.FOLDER_ICON);

	private final ChangeEventSupport eventSupport = new ChangeEventSupport();

	private final UserPreferences preferences;

	private final JLabel summary;

	public DirectoryBrowser(UserPreferences preferences, File directory) {
		Validate.notNull(preferences, "The given user preferences are null");
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		this.preferences = preferences;

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]", "[]1[grow]1[]"));

		this.tableModel = new FileTableModel(this);

		this.table = new JTable(tableModel);
		this.table.setBackground(Utils.getDefaultBackgroundColor());
		this.table.addKeyListener(this);
		this.table.addMouseListener(this);
		this.table.addFocusListener(this);
		this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.table.getSelectionModel().addListSelectionListener(this);

		// Listen to column event resize events
		final ColumnEventHelper eventHelper = new ColumnEventHelper(this);

		this.table.getColumnModel().addColumnModelListener(eventHelper);
		this.table.getTableHeader().addMouseListener(eventHelper);

		// Render the table headers with a bold font
		this.table.getTableHeader().setFont(Utils.getBoldFont(this.table.getTableHeader().getFont()));
		this.table.getTableHeader().setBackground(Utils.getDefaultBackgroundColor());
		this.table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
		this.table.getTableHeader().addMouseListener(this);

		final TableColumn typeColumn = this.table.getColumn(FileTableModel.COLUMN_TYPE);
		typeColumn.setCellRenderer(new FileTypeRenderer());
		typeColumn.setResizable(false);
		typeColumn.setHeaderValue("");
		typeColumn.setMaxWidth(Icons.FOLDER_ICON.getIconWidth() + 5);

		final TableColumn fileColumn = this.table.getColumn(FileTableModel.COLUMN_NAME);
		fileColumn.setCellRenderer(new FileNameRenderer());
		fileColumn.setResizable(true);

		final TableColumn sizeColumn = this.table.getColumn(FileTableModel.COLUMN_SIZE);
		sizeColumn.setCellRenderer(new FileSizeRenderer());
		sizeColumn.setResizable(true);

		// Dynamically set the column to the correct size
		final TableColumn lastUpdateColumn = this.table.getColumn(FileTableModel.COLUMN_LAST_UPDATE);
		lastUpdateColumn.setCellRenderer(new LastUpdateRenderer());
		lastUpdateColumn.setResizable(false);
		lastUpdateColumn.setMaxWidth(Utils.getTimestampRenderWidth() + 5);
		lastUpdateColumn.setMinWidth(Utils.getTimestampRenderWidth() + 5);

		// Use a square border (not one with rounded corners)
		this.directoryButton.setBorder(Utils.createRaisedBevelBorder());
		this.directoryButton.setFont(Utils.getDefaultFont());
		this.directoryButton.setFocusable(false);
		this.directoryButton.setHorizontalAlignment(SwingConstants.LEFT);

		this.summary = new JLabel(" ");
		this.summary.setBorder(Utils.createRaisedBevelBorder());

		add(directoryButton, "grow, wrap");
		add(new JScrollPane(table), "grow, wrap");
		add(summary, "grow");

		// Set the directory (this will populate the table)
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

	private void updateSummary(Iterable<File> iterable) {
		Validate.notNull(iterable, "The give iterable is null");

		int files = 0, folders = 0;
		long totalSize = 0;

		for (File file : iterable) {
			if (file.isFile()) {
				files++;
				totalSize += file.length();
			} else {
				folders++;
			}
		}

		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setGroupingSeparator(' ');

		final DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setDecimalFormatSymbols(symbols);

		// Set the summary
		if (files > 0) {
			if (folders > 0) {
				summary.setText(String.format("%d folder(s) and %d file(s) [%s Kb]", folders, files, decimalFormat.format(totalSize / 1024)));
			} else {
				summary.setText(String.format("%d file(s) [%s Kb]", files, decimalFormat.format(totalSize / 1024)));
			}
		} else {
			if (folders > 0) {
				summary.setText(String.format("%d folder(s)", folders));
			} else {
				summary.setText(" ");
			}
		}
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

		this.tableModel.clear();

		// Populate the list with the directory's entries
		for (File file : directory.listFiles()) {
			if (!file.isHidden() || preferences.isShowHidden()) {
				tableModel.add(file);
			}
		}

		updateSummary(tableModel.getAll());

		// If there's a parent directory, add an entry rendered as ".."
		final File parentDir = getCanonicalFile(directory).getParentFile();

		if ((parentDir != null) && parentDir.exists()) {
			tableModel.add(parentDir);
		}

		// Sort the entries
		tableModel.sort();

		// Notify the listeners that all the entries changed
		tableModel.fireTableDataChanged();

		if ((oldDir != null) && tableModel.contains(oldDir)) {
			// Auto-select the directory we just left
			final int index = tableModel.indexOf(oldDir);

			table.getSelectionModel().setSelectionInterval(index, index);
		} else {
			// Auto-select the 1st entry (if there's one)
			if (tableModel.size() > 1) {
				table.getSelectionModel().setSelectionInterval(1, 1);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("[%s] Set directory to %s", getComponentLabel(), directory.getAbsolutePath()));
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
		if (e.getSource() == table.getSelectionModel()) {
			// The parent directory can't be selected
			final File parentDir = getParentDirectory();

			if (parentDir != null) {
				if (getSelection().contains(parentDir)) {
					// Unselect the parent directory entry (always the 1st one)
					table.getSelectionModel().removeSelectionInterval(0, 0);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("[%s] Selection changed", getComponentLabel()));
			}

			// Propagate the event
			fireChangeEvent();
		}
	}

	public List<File> getSelection() {
		return tableModel.getFilesAt(table.getSelectedRows());
	}

	private String getComponentLabel() {
		return (getName() == null) ? "N/A" : getName();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() != table) {
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
		if (e.getSource() != table) {
			return;
		}

		// Propagate event to our listeners
		processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
				e.getKeyLocation()));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() != table) {
			return;
		}

		// Propagate event to our listeners
		processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
				e.getKeyLocation()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == table) {
			if (e.getClickCount() == 2) {
				// Only react to double clicks
				final List<File> selection = getSelection();

				if (selection.size() == 1) {
					final File file = selection.iterator().next();

					if (file.isDirectory()) {
						// Change to the clicked directory
						setDirectory(file);
					} else {
						try {
							// View the selected file
							new ProcessBuilder(preferences.getViewFileCommand(), file.getAbsolutePath()).start();
						} catch (IOException e1) {
							logger.error("Error when viewing file", e1);
						}
					}
				}
			}
		} else if (e.getSource() == table.getTableHeader()) {
			final int columnIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));

			if (columnIndex >= 0) {
				// React to clicks on column headers and sort the entries accordingly
				switch(columnIndex) {
				case 0:
					tableModel.setSortCriteria(SortCriteria.TYPE);
					break;
				case 1:
					tableModel.setSortCriteria(SortCriteria.NAME);
					break;
				case 2:
					tableModel.setSortCriteria(SortCriteria.SIZE);
					break;
				case 3:
					tableModel.setSortCriteria(SortCriteria.LAST_UPDATE);
					break;
				default:
					throw new UnsupportedOperationException("Unsupported column index: " + columnIndex);
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
		if (e.getSource() == table) {
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
		if (e.getSource() == table) {
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

	@Override
	public void columnResized(ColumnEvent event) {
		System.err.println(event);
	}
}
