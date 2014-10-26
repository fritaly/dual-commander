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

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.Validate;

public class FileList extends JPanel {

	private static final long serialVersionUID = 411590029543053088L;

	private File directory;

	private final DefaultListModel<File> listModel;

	private final JList<File> list;

	public FileList(File directory) {
		Validate.notNull(directory, "The given directory is null");
		Validate.isTrue(directory.exists(), String.format("The given directory '%s' doesn't exist", directory.getAbsolutePath()));
		Validate.isTrue(directory.isDirectory(), String.format("The given path '%s' doesn't denote a directory", directory.getAbsolutePath()));

		this.directory = directory;

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]", "[grow]"));

		this.listModel = new DefaultListModel<>();

		// Populate the list with the directory's entries
		for (File file : directory.listFiles()) {
			if (!file.isHidden()) {
				// TODO Define an option to list hidden entries
				listModel.addElement(file);
			}
		}

		this.list = new JList<>(listModel);

		add(new JScrollPane(list), "grow");
	}

	public File getDirectory() {
		// TODO Add method setDirectory(File)
		return directory;
	}
}
