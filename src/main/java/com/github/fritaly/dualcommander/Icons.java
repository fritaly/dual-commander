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

	public static final ImageIcon FOLDER_ICON = getIcon("famfamfam/folder.png");

	public static final ImageIcon ACCEPT_ICON = getIcon("famfamfam/accept.png");

	public static final ImageIcon CANCEL_ICON = getIcon("famfamfam/cancel.png");

	public static final ImageIcon COG_ICON = getIcon("famfamfam/cog.png");

	public static final ImageIcon HELP_ICON = getIcon("famfamfam/help.png");

	public static final ImageIcon COPY_ICON = getIcon("famfamfam/page_copy.png");

	public static final ImageIcon DELETE_ICON = getIcon("famfamfam/delete.png");

	// TODO Test icon "pencil.png"
	public static final ImageIcon EDIT_ICON = getIcon("famfamfam/page_edit.png");

	public static final ImageIcon TICK_ICON = getIcon("famfamfam/tick.png");

	public static final ImageIcon FIRST_ICON = getIcon("famfamfam/resultset_first.png");

	public static final ImageIcon LAST_ICON = getIcon("famfamfam/resultset_last.png");

	public static final ImageIcon NEXT_ICON = getIcon("famfamfam/resultset_next.png");

	public static final ImageIcon PREVIOUS_ICON = getIcon("famfamfam/resultset_previous.png");

	public static final ImageIcon INFORMATION_ICON = getIcon("famfamfam/information.png");

	public static final ImageIcon EXCLAMATION_ICON = getIcon("famfamfam/exclamation.png");

	public static final ImageIcon ERROR_ICON = getIcon("famfamfam/error.png");

	public static final ImageIcon DRIVE_ICON = getIcon("famfamfam/drive.png");

	public static final ImageIcon CUT_ICON = getIcon("famfamfam/cut.png");

	public static final ImageIcon ADD_ICON = getIcon("famfamfam/add.png");

	public static final ImageIcon DOOR_OPEN_ICON = getIcon("famfamfam/door_open.png");

    public static final ImageIcon TRIANGLE_DOWN_ICON = getIcon("google/drop25.png");

    public static final ImageIcon TRIANGLE_UP_ICON = getIcon("google/drop27.png");
}