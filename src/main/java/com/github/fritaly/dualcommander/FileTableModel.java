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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.lang.Validate;

/**
 * Simple implementation of {@link TableModel} used for storing the files being
 * browsed.
 *
 * @author francois_ritaly
 */
public class FileTableModel implements TableModel {

	public static final String COLUMN_TYPE = "Type";

	public static final String COLUMN_NAME = "Name";

	public static final String COLUMN_SIZE = "Size";

	public static final String COLUMN_LAST_UPDATE = "Last Update";

	/**
	 * The list containing the files being browsed.
	 */
	private final List<File> list = new ArrayList<>();

	/**
	 * Comparator used for sorting the directories & files.
	 */
	private final ConfigurableFileComparator comparator;

	/**
	 * List of listeners to be notified upon change.
	 */
	private final EventListenerList listeners = new EventListenerList();

	public FileTableModel(HasParentDirectory delegate) {
		Validate.notNull(delegate, "The given object is null");

		this.comparator = new ConfigurableFileComparator(delegate);
	}

	public void clear() {
		list.clear();
	}

	public void add(File file) {
		Validate.notNull(file, "The given file is null");

		list.add(file);
	}

	public void sort() {
		Collections.sort(list, comparator);
	}

	public SortCriteria getSortCriteria() {
		return comparator.getCriteria();
	}

	public void setSortCriteria(SortCriteria criteria) {
		Validate.notNull(criteria, "The given criteria is null");

		if (criteria.equals(comparator.getCriteria())) {
			// Change the sort order
			comparator.setAscending(!comparator.isAscending());
		} else {
			// Change the sort criteria
			comparator.setCriteria(criteria);
		}

		// Sort the entries
		sort();

		// Notify the listeners
		fireTableDataChanged();
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(TableModelListener.class, l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(TableModelListener.class, l);
	}

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
	public void fireTableChanged(TableModelEvent event) {
		// Guaranteed to return a non-null array
		final Object[] listeners = this.listeners.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableModelListener.class) {
				((TableModelListener) listeners[i + 1]).tableChanged(event);
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
		case 2:
		case 3:
			return File.class;
		default:
			throw new IllegalArgumentException(String.format("Invalid column index: %d", columnIndex));
		}
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return COLUMN_TYPE;
		case 1:
			return COLUMN_NAME;
		case 2:
			return COLUMN_SIZE;
		case 3:
			return COLUMN_LAST_UPDATE;
		default:
			throw new IllegalArgumentException(String.format("Invalid column index: %d", columnIndex));
		}
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final File file = list.get(rowIndex);

		switch (columnIndex) {
		case 0:
		case 1:
		case 2:
		case 3:
			return file;
		default:
			throw new IllegalArgumentException(String.format("Invalid column index: %d", columnIndex));
		}
	}

	public File getFileAt(int rowIndex) {
		return list.get(rowIndex);
	}

	public List<File> getFilesAt(int[] rowIndices) {
		final List<File> selection = new ArrayList<>(rowIndices.length);

		for (int rowIndex : rowIndices) {
			selection.add(list.get(rowIndex));
		}

		return selection;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// We don't support that operation
		throw new UnsupportedOperationException();

//		list.set(rowIndex, (File) aValue);
//
//		// Fire an event to notify the change
//		fireTableChanged(new TableModelEvent(this, rowIndex));
	}

	public boolean contains(File element) {
		return list.contains(element);
	}

	public int indexOf(File element) {
		return list.indexOf(element);
	}

	public int size() {
		return list.size();
	}
}
