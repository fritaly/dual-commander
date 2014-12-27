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
package com.github.fritaly.dualcommander.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.Validate;

// See http://stackoverflow.com/questions/8752694/java-jtable-detect-column-re-sized-by-user
public final class ColumnEventHelper extends MouseAdapter implements TableColumnModelListener {

	private final ColumnEventListener listener;

	private boolean resizing = false;

	private int resizingColumn = -1;

	private int oldWidth = -1;

	public ColumnEventHelper(ColumnEventListener listener) {
		Validate.notNull(listener, "The given listener is null");

		this.listener = listener;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// capture start of resize
		if (e.getSource() instanceof JTableHeader) {
			final TableColumn column = ((JTableHeader) e.getSource()).getResizingColumn();

			if (column != null) {
				resizing = true;
				resizingColumn = column.getModelIndex();
				oldWidth = column.getPreferredWidth();
			} else {
				resizingColumn = -1;
				oldWidth = -1;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (resizing) {
			if (e.getSource() instanceof JTableHeader) {
				final TableColumn column = ((JTableHeader) e.getSource()).getColumnModel().getColumn(resizingColumn);

				if (column != null) {
					int newWidth = column.getPreferredWidth();

					if (newWidth != oldWidth) {
						listener.columnResized(new ColumnEvent(column));
					}
				}
			}
		}

		resizing = false;
		resizingColumn = -1;
		oldWidth = -1;
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
	}
}