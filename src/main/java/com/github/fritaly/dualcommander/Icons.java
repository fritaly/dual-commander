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

import javax.swing.ImageIcon;

public final class Icons {

	private static ImageIcon getIcon(String name) {
		return new ImageIcon(Icons.class.getResource(String.format("/com/github/fritaly/dualcommander/icons/%s", name)));
	}

	public static final ImageIcon FOLDER_ICON = getIcon("folder.png");

	public static final ImageIcon ACCEPT_ICON = getIcon("accept.png");

	public static final ImageIcon CANCEL_ICON = getIcon("cancel.png");

	public static final ImageIcon COG_ICON = getIcon("cog.png");

	public static final ImageIcon HELP_ICON = getIcon("help.png");

	public static final ImageIcon COPY_ICON = getIcon("page_copy.png");

	public static final ImageIcon DELETE_ICON = getIcon("delete.png");

	// TODO Test icon "pencil.png"
	public static final ImageIcon EDIT_ICON = getIcon("page_edit.png");

	public static final ImageIcon TICK_ICON = getIcon("tick.png");

	public static final ImageIcon FIRST_ICON = getIcon("resultset_first.png");

	public static final ImageIcon LAST_ICON = getIcon("resultset_last.png");

	public static final ImageIcon NEXT_ICON = getIcon("resultset_next.png");

	public static final ImageIcon PREVIOUS_ICON = getIcon("resultset_previous.png");

	public static final ImageIcon INFORMATION_ICON = getIcon("information.png");

	public static final ImageIcon EXCLAMATION_ICON = getIcon("exclamation.png");

	public static final ImageIcon ERROR_ICON = getIcon("error.png");

	public static final ImageIcon DRIVE_ICON = getIcon("drive.png");

	public static final ImageIcon CUT_ICON = getIcon("cut.png");

	public static final ImageIcon ADD_ICON = getIcon("add.png");

	public static final ImageIcon DOOR_OPEN_ICON = getIcon("door_open.png");
}