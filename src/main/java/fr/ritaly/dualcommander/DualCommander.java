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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import net.miginfocom.swing.MigLayout;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame {

	private static final long serialVersionUID = 5445919782222373150L;

	private final JButton viewButton, editButton, copyButton, moveButton, mkdirButton, deleteButton, quitButton;

	public DualCommander() {
		// TODO Generate a fat jar at build time
		// TODO Insert version number in frame's title & build id
		super("Dual Commander");

		try {
			// Apply the JGoodies L&F
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
			// Not supposed to happen
		}

		// Layout, columns & rows
		setLayout(new MigLayout("insets 5px", "[grow][grow]", "[grow][]"));

		getContentPane().add(new JList<>(), "grow");
		getContentPane().add(new JList<>(), "grow, wrap");

		this.viewButton = new JButton("F3 View");
		this.editButton = new JButton("F4 Edit");
		this.copyButton = new JButton("F5 Copy");
		this.moveButton = new JButton("F6 Move");
		this.mkdirButton = new JButton("F7 Mkdir");
		this.deleteButton = new JButton("F8 Delete");
		this.quitButton = new JButton("Alt+F4 Quit");

		// The 7 buttons must all have the same width (they must belong to the
		// same size group)
		final JPanel buttonPanel = new JPanel(new MigLayout("insets 0px", StringUtils.repeat("[grow, sizegroup g1]", 7), "[grow]"));
		buttonPanel.add(viewButton, "grow");
		buttonPanel.add(editButton, "grow");
		buttonPanel.add(copyButton, "grow");
		buttonPanel.add(moveButton, "grow");
		buttonPanel.add(mkdirButton, "grow");
		buttonPanel.add(deleteButton, "grow");
		buttonPanel.add(quitButton, "grow");

		getContentPane().add(buttonPanel, "grow, span 2");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public static void main(String[] args) {
		final DualCommander commander = new DualCommander();
		commander.setVisible(true);
	}
}