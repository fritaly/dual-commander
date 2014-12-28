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
import java.util.Comparator;

import org.apache.commons.lang.Validate;

public final class ConfigurableFileComparator implements Comparator<File> {

	private final HasParentDirectory delegate;

	private SortCriteria criteria = SortCriteria.NAME;

	private boolean ascending = true;

	public ConfigurableFileComparator(HasParentDirectory delegate) {
		Validate.notNull(delegate, "The given delegate is null");

		this.delegate = delegate;
	}

	public ConfigurableFileComparator(HasParentDirectory delegate, SortCriteria criteria) {
		Validate.notNull(delegate, "The given delegate is null");
		Validate.notNull(criteria, "The given sort criteria is null");

		this.delegate = delegate;
		this.criteria = criteria;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public SortCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SortCriteria criteria) {
		Validate.notNull(criteria, "The given sort criteria is null");

		this.criteria = criteria;
	}

	@Override
	public int compare(File f1, File f2) {
		// The parent directory always comes first
		if (f1.isDirectory() && f1.equals(this.delegate.getParentDirectory())) {
			return -1;
		}
		if (f2.isDirectory() && f2.equals(this.delegate.getParentDirectory())) {
			return +1;
		}

		// Compare the 2 files according to the current sort criteria
		final int result = criteria.compare(f1, f2);

		return ascending ? result : -1 * result;
	}
}