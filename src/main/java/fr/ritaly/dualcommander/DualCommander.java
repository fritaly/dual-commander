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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame implements ChangeListener, KeyListener {

	private static final long serialVersionUID = 5445919782222373150L;

	// TODO Add icons for each action
	private final class ViewAction extends AbstractAction {

		private static final long serialVersionUID = -3885021368508066211L;

		public ViewAction() {
			super("F3 View");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class EditAction extends AbstractAction {

		private static final long serialVersionUID = -4570243231693650751L;

		public EditAction() {
			super("F4 Edit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class CopyAction extends AbstractAction {

		private static final long serialVersionUID = -4681732083985135686L;

		public CopyAction() {
			super("F5 Copy");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class MoveAction extends AbstractAction {

		private static final long serialVersionUID = -3452333607164390841L;

		public MoveAction() {
			super("F6 Move");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class MkdirAction extends AbstractAction {

		private static final long serialVersionUID = -6354029847164675819L;

		public MkdirAction() {
			super("F7 Mkdir");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = -4059922323563836824L;

		public DeleteAction() {
			super("F8 Delete");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class QuitAction extends AbstractAction {

		private static final long serialVersionUID = 388422465301643805L;

		public QuitAction() {
			super("Alt+F4 Quit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Ask a user confirm before exiting
			DualCommander.this.dispose();
		}
	}

	private final JButton viewButton = new JButton(new ViewAction());

	private final JButton editButton = new JButton(new EditAction());

	private final JButton copyButton = new JButton(new CopyAction());

	private final JButton moveButton = new JButton(new MoveAction());

	private final JButton mkdirButton = new JButton(new MkdirAction());

	private final JButton deleteButton = new JButton(new DeleteAction());

	private final JButton quitButton = new JButton(new QuitAction());

	private final JTabbedPane leftTabbedPane, rightTabbedPane;

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
		setLayout(new MigLayout("insets 0px", "[grow]0px[grow]", "[grow][]"));

		// TODO Retrieve the previous directory displayed
		final FileList leftList = new FileList(new File("."));
		leftList.addChangeListener(this);
		leftList.addKeyListener(this);

		this.leftTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.leftTabbedPane.addTab(leftList.getDirectory().getName(), leftList);

		final FileList rightList = new FileList(new File("."));
		rightList.addChangeListener(this);
		rightList.addKeyListener(this);

		this.rightTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.rightTabbedPane.addTab(rightList.getDirectory().getName(), rightList);

		// Adding the 2 components to the same sizegroup ensures they always
		// keep the same width
		getContentPane().add(leftTabbedPane, "grow, sizegroup g1");
		getContentPane().add(rightTabbedPane, "grow, sizegroup g1, wrap");

		// The 7 buttons must all have the same width (they must belong to the
		// same size group)
		final JPanel buttonPanel = new JPanel(
				new MigLayout("insets 0px", StringUtils.repeat("[grow, sizegroup g1]", 7), "[grow]"));
		buttonPanel.add(viewButton, "grow");
		buttonPanel.add(editButton, "grow");
		buttonPanel.add(copyButton, "grow");
		buttonPanel.add(moveButton, "grow");
		buttonPanel.add(mkdirButton, "grow");
		buttonPanel.add(deleteButton, "grow");
		buttonPanel.add(quitButton, "grow");

		getContentPane().add(buttonPanel, "grow, span 2");

		// Register shortcuts at a global level (not on every component)
		final InputMap inputMap = this.leftTabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actionMap = this.leftTabbedPane.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "view");
		actionMap.put("view", viewButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "edit");
		actionMap.put("edit", editButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "copy");
		actionMap.put("copy", copyButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "move");
		actionMap.put("move", moveButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "mkdir");
		actionMap.put("mkdir", mkdirButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "delete");
		actionMap.put("delete", deleteButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK), "quit");
		actionMap.put("quit", quitButton.getAction());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void refreshButtons(List<File> selection) {
		final int size = selection.size();

		// Only enabled if only one entry selected
		this.viewButton.setEnabled(size == 1);
		this.editButton.setEnabled(size == 1);

		// Only enabled if selection isn't empty
		this.copyButton.setEnabled(size > 0);
		this.moveButton.setEnabled(size > 0);
		this.deleteButton.setEnabled(size > 0);

		// Always enabled
		this.mkdirButton.setEnabled(true);
		this.quitButton.setEnabled(true);
	}

	private FileList getLeftPanel() {
		return (FileList) this.leftTabbedPane.getSelectedComponent();
	}

	private FileList getRightPanel() {
		return (FileList) this.rightTabbedPane.getSelectedComponent();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == getLeftPanel()) {
			// Update the buttons based on the current selection
			refreshButtons(getLeftPanel().getSelection());

			// Update the tab's title
			this.leftTabbedPane.setTitleAt(this.leftTabbedPane.getSelectedIndex(), getLeftPanel().getDirectory().getName());
		} else if (e.getSource() == getRightPanel()) {
			// Update the buttons based on the current selection
			refreshButtons(getRightPanel().getSelection());

			// Update the tab's title
			this.rightTabbedPane.setTitleAt(this.rightTabbedPane.getSelectedIndex(), getRightPanel().getDirectory().getName());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		final boolean metaDown = (e.getModifiersEx() | KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK;

		if (e.getSource() == getLeftPanel()) {
			if ((e.getKeyCode() == KeyEvent.VK_T) && metaDown) {
				// TODO Factor out this logic in a TabbedPane class
				final FileList newPanel = new FileList(getLeftPanel().getDirectory());
				newPanel.addChangeListener(this);
				newPanel.addKeyListener(this);

				leftTabbedPane.addTab(newPanel.getDirectory().getName(), newPanel);
			}
		} else if (e.getSource() == getRightPanel()) {
			if ((e.getKeyCode() == KeyEvent.VK_T) && metaDown) {
				final FileList newPanel = new FileList(getRightPanel().getDirectory());
				newPanel.addChangeListener(this);
				newPanel.addKeyListener(this);

				rightTabbedPane.addTab(newPanel.getDirectory().getName(), newPanel);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new DualCommander().setVisible(true);
			}
		});
	}
}