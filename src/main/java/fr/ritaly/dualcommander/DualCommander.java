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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame implements ChangeListener, WindowListener {

	private static final long serialVersionUID = 5445919782222373150L;

	// TODO Add icons for each action
	// TODO Implement the actions F3 -> F8
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

	private static JButton createButton(Action action) {
		Validate.notNull(action, "The given action is null");

		final JButton button = new JButton(action);
		button.setFocusable(false);

		return button;
	}

	// --- Actions --- //

	private final ViewAction viewAction = new ViewAction();

	private final EditAction editAction = new EditAction();

	private final CopyAction copyAction = new CopyAction();

	private final MoveAction moveAction = new MoveAction();

	private final MkdirAction mkdirAction = new MkdirAction();

	private final DeleteAction deleteAction = new DeleteAction();

	private final QuitAction quitAction = new QuitAction();

	// --- Buttons --- //

	private final JButton viewButton = createButton(viewAction);

	private final JButton editButton = createButton(editAction);

	private final JButton copyButton = createButton(copyAction);

	private final JButton moveButton = createButton(moveAction);

	private final JButton mkdirButton = createButton(mkdirAction);

	private final JButton deleteButton = createButton(deleteAction);

	private final JButton quitButton = createButton(quitAction);

	private final TabbedPane leftPane, rightPane;

	public DualCommander() {
		// TODO Generate a fat jar at build time
		super(String.format("Dual Commander %s", Utils.getApplicationVersion()));

		try {
			// Apply the JGoodies L&F
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
			// Not supposed to happen
		}

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]0px[grow]", "[grow][]"));

		this.leftPane = new TabbedPane();
		this.rightPane = new TabbedPane();

		// Adding the 2 components to the same sizegroup ensures they always
		// keep the same width
		getContentPane().add(leftPane, "grow, sizegroup g1");
		getContentPane().add(rightPane, "grow, sizegroup g1, wrap");

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
		final InputMap inputMap = this.leftPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actionMap = this.leftPane.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, true), "view");
		actionMap.put("view", viewButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true), "edit");
		actionMap.put("edit", editButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true), "copy");
		actionMap.put("copy", copyButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, true), "move");
		actionMap.put("move", moveButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0, true), "mkdir");
		actionMap.put("mkdir", mkdirButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, true), "delete");
		actionMap.put("delete", deleteButton.getAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK), "quit");
		actionMap.put("quit", quitButton.getAction());

		addWindowListener(this);

		// Reload the last configuration
		final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

		final Preferences leftPrefs = prefs.node("left.panel");
		final int leftTabCount = leftPrefs.getInt("tab.count", 1);

		for (int i = 0; i < leftTabCount; i++) {
			// Set the tab to the correct directory
			this.leftPane.addFileTab(new File(leftPrefs.get(String.format("tab.%d.directory", i), ".")));
		}

		// Ensure the tabbed pane has at least tab
		if (this.leftPane.getTabCount() == 0) {
			this.leftPane.addFileTab();
		}

		final Preferences rightPrefs = prefs.node("right.panel");
		final int rightTabCount = rightPrefs.getInt("tab.count", 1);

		for (int i = 0; i < rightTabCount; i++) {
			// Set the tab to the correct directory
			this.rightPane.addFileTab(new File(rightPrefs.get(String.format("tab.%d.directory", i), ".")));
		}

		// Ensure the tabbed pane has at least tab
		if (this.rightPane.getTabCount() == 0) {
			this.rightPane.addFileTab();
		}

		// Init the buttons
		refreshButtons(getLeftPanel().getSelection());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void refreshButtons(List<File> selection) {
		final int size = selection.size();

		// Enable / disable the actions so that key shortcuts and buttons have a
		// consistent behavior

		// Only enabled if only one entry selected
		this.viewAction.setEnabled(size == 1 && selection.iterator().next().isFile());
		this.editButton.setEnabled(size == 1 && selection.iterator().next().isFile());

		// Only enabled if selection isn't empty
		this.copyAction.setEnabled(size > 0);
		this.moveAction.setEnabled(size > 0);
		this.deleteAction.setEnabled(size > 0);

		// Always enabled
		this.mkdirAction.setEnabled(true);
		this.quitAction.setEnabled(true);
	}

	private FileList getLeftPanel() {
		return this.leftPane.getActiveComponent();
	}

	private FileList getRightPanel() {
		return this.rightPane.getActiveComponent();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == getLeftPanel()) {
			// Update the buttons based on the current selection
			refreshButtons(getLeftPanel().getSelection());
		} else if (e.getSource() == getRightPanel()) {
			// Update the buttons based on the current selection
			refreshButtons(getRightPanel().getSelection());
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			// Save the program state
			final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

			// FIXME Move this logic to class TabbedPane itself
			final Preferences leftPrefs = prefs.node("left.panel");
			leftPrefs.putInt("tab.count", this.leftPane.getTabCount());

			for (int i = 0; i < this.leftPane.getTabCount(); i++) {
				final FileList component = (FileList) this.leftPane.getComponentAt(i);

				leftPrefs.put(String.format("tab.%d.directory", i), component.getDirectory().getAbsolutePath());
			}

			final Preferences rightPrefs = prefs.node("right.panel");
			rightPrefs.putInt("tab.count", this.rightPane.getTabCount());

			for (int i = 0; i < this.rightPane.getTabCount(); i++) {
				final FileList component = (FileList) this.rightPane.getComponentAt(i);

				rightPrefs.put(String.format("tab.%d.directory", i), component.getDirectory().getAbsolutePath());
			}

			prefs.sync();
		} catch (BackingStoreException e1) {
			// Not a big deal
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
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