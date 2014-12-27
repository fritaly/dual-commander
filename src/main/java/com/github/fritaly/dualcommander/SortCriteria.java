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

/**
 * Enumerates the possible criteria to sort files.
 *
 * @author francois_ritaly
 */
public enum SortCriteria implements Comparator<File> {
	TYPE {
		@Override
		public int compare(File f1, File f2) {
			if (f1.isFile()) {
				if (f2.isFile()) {
					// f1 & f2 are files
					return 0;
				}

				// f1 (file) > f2 (folder)
				return +1;
			} else {
				if (f2.isDirectory()) {
					// f1 & f2 are folders
					return 0;
				}

				// f1 (folder) < f2 (file)
				return -1;
			}
		}
	},
	NAME {
		@Override
		public int compare(File f1, File f2) {
			return f1.getName().compareToIgnoreCase(f2.getName());
		}
	},
	SIZE {
		@Override
		public int compare(File f1, File f2) {
			if (f1.length() < f2.length()) {
				return -1;
			} else if (f1.length() > f2.length()) {
				return +1;
			}

			return 0;
		}
	},
	LAST_UPDATE {
		@Override
		public int compare(File f1, File f2) {
			if (f1.lastModified() < f2.lastModified()) {
				return -1;
			} else if (f1.lastModified() > f2.lastModified()) {
				return +1;
			}

			return 0;
		}
	};
}