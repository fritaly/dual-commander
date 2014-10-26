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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import org.apache.commons.lang.Validate;

public final class SortedListModel<E> extends AbstractListModel<E> {

	private static final long serialVersionUID = 4813672030693748114L;

	private final SortedSet<E> model;

	public SortedListModel() {
		// Use the natural order
		this.model = new TreeSet<E>();
	}

	public SortedListModel(Comparator<E> comparator) {
		Validate.notNull(comparator, "The given comparator is null");

		this.model = new TreeSet<E>(comparator);
	}

	public int getSize() {
		return model.size();
	}

	@SuppressWarnings("unchecked")
	public E getElementAt(int index) {
		return (E) model.toArray()[index];
	}

	public void add(E element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(E[] elements) {
		final Collection<E> collection = Arrays.asList(elements);

		model.addAll(collection);

		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();

		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(E element) {
		return model.contains(element);
	}

	public E firstElement() {
		return model.first();
	}

	public Iterator<E> iterator() {
		return model.iterator();
	}

	public E lastElement() {
		return model.last();
	}

	public boolean removeElement(E element) {
		final boolean removed = model.remove(element);

		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}

		return removed;
	}
}