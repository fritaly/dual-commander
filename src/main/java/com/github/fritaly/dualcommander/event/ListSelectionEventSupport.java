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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.Validate;

public final class ListSelectionEventSupport implements ListSelectionEventSource {

	private final List<ListSelectionListener> listeners = new ArrayList<>();

	public ListSelectionEventSupport() {
	}

	@Override
	public void addListSelectionListener(ListSelectionListener listener) {
		Validate.notNull(listener, "The given listener is null");

		this.listeners.add(listener);
	}

	@Override
	public void removeListSelectionListener(ListSelectionListener listener) {
		Validate.notNull(listener, "The given listener is null");

		this.listeners.remove(listener);
	}

	public void fireEvent(ListSelectionEvent event) {
		Validate.notNull(event, "The given event is null");

		for (ListSelectionListener listener : listeners) {
			listener.valueChanged(event);
		}
	}
}
