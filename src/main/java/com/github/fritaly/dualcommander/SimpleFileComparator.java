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

public final class SimpleFileComparator implements Comparator<File> {

	private final HasParentDirectory delegate;

	public SimpleFileComparator(HasParentDirectory delegate) {
		Validate.notNull(delegate, "The given delegate is null");

		this.delegate = delegate;
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

		// Compare the 2 files by (case-insensive) name
		return f1.getName().compareToIgnoreCase(f2.getName());
	}
}