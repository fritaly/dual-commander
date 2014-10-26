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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame {

	private static final long serialVersionUID = 5445919782222373150L;

	private final JButton viewButton, editButton, copyButton, moveButton, mkdirButton, deleteButton, quitButton;

	// TODO Add icons for each action
	private final class ViewAction extends AbstractAction {
		public ViewAction() {
			super("F3 View");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class EditAction extends AbstractAction {
		public EditAction() {
			super("F4 Edit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class CopyAction extends AbstractAction {
		public CopyAction() {
			super("F5 Copy");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class MoveAction extends AbstractAction {
		public MoveAction() {
			super("F6 Move");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class MkdirAction extends AbstractAction {
		public MkdirAction() {
			super("F7 Mkdir");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class DeleteAction extends AbstractAction {
		public DeleteAction() {
			super("F8 Delete");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class QuitAction extends AbstractAction {
		public QuitAction() {
			super("Alt+F4 Quit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Ask a user confirm before exiting
			DualCommander.this.dispose();
		}
	}

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

		this.viewButton = new JButton(new ViewAction());
		this.editButton = new JButton(new EditAction());
		this.copyButton = new JButton(new CopyAction());
		this.moveButton = new JButton(new MoveAction());
		this.mkdirButton = new JButton(new MkdirAction());
		this.deleteButton = new JButton(new DeleteAction());
		this.quitButton = new JButton(new QuitAction());

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