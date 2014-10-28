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
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame implements ChangeListener, KeyListener, WindowListener {

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

	// TODO Encapsulate those JTabbedPane into a widget
	private final JTabbedPane leftTabbedPane, rightTabbedPane;

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

		// Init the buttons
		refreshButtons(getLeftPanel().getSelection());

		// Reload the last configuration
		final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

		final Preferences leftPrefs = prefs.node("left.panel");
		final int leftTabCount = leftPrefs.getInt("tab.count", 1);

		for (int i = 0; i < leftTabCount; i++) {
			if (i > 0) {
				// FIXME Factor this logic
				final FileList fileList = new FileList(new File("."));
				fileList.addChangeListener(this);
				fileList.addKeyListener(this);

				this.leftTabbedPane.addTab(fileList.getDirectory().getName(), fileList);
			}

			final String path = leftPrefs.get(String.format("tab.%d.directory", i), ".");

			// Set the tab to the correct directory
			((FileList) this.leftTabbedPane.getComponentAt(i)).setDirectory(new File(path));
		}

		final Preferences rightPrefs = prefs.node("right.panel");
		final int rightTabCount = rightPrefs.getInt("tab.count", 1);

		for (int i = 0; i < rightTabCount; i++) {
			if (i > 0) {
				// FIXME Factor this logic
				final FileList fileList = new FileList(new File("."));
				fileList.addChangeListener(this);
				fileList.addKeyListener(this);

				this.rightTabbedPane.addTab(fileList.getDirectory().getName(), fileList);
			}

			final String path = rightPrefs.get(String.format("tab.%d.directory", i), ".");

			// Set the tab to the correct directory
			((FileList) this.rightTabbedPane.getComponentAt(i)).setDirectory(new File(path));
		}

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
		JTabbedPane tabbedPane = null;

		if (e.getSource() == getLeftPanel()) {
			tabbedPane = leftTabbedPane;
		} else if (e.getSource() == getRightPanel()) {
			tabbedPane = rightTabbedPane;
		}

		if (tabbedPane != null) {
			final FileList sourcePanel = (FileList) tabbedPane.getSelectedComponent();
			final boolean metaDown = (e.getModifiersEx() | KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK;

			if ((e.getKeyCode() == KeyEvent.VK_T) && metaDown) {
				// Create a new tab
				// TODO Factor out this logic in a TabbedPane class
				final FileList newPanel = new FileList(sourcePanel.getDirectory());
				newPanel.addChangeListener(this);
				newPanel.addKeyListener(this);

				tabbedPane.addTab(newPanel.getDirectory().getName(), newPanel);
			} else if ((e.getKeyCode() == KeyEvent.VK_W) && metaDown) {
				if (tabbedPane.getTabCount() > 1) {
					// Close the current tab (only if not the last one)
					sourcePanel.removeChangeListener(this);
					sourcePanel.removeKeyListener(this);

					tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
				}
			} else if ((e.getKeyCode() >= KeyEvent.VK_1) && (e.getKeyCode() <= KeyEvent.VK_9) && metaDown) {
				final int index = e.getKeyCode() - KeyEvent.VK_1;

				if (index <= tabbedPane.getTabCount() - 1) {
					tabbedPane.setSelectedIndex(index);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
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

			final Preferences leftPrefs = prefs.node("left.panel");
			leftPrefs.putInt("tab.count", this.leftTabbedPane.getTabCount());

			for (int i = 0; i < this.leftTabbedPane.getTabCount(); i++) {
				final FileList component = (FileList) this.leftTabbedPane.getComponentAt(i);

				leftPrefs.put(String.format("tab.%d.directory", i), component.getDirectory().getAbsolutePath());
			}

			final Preferences rightPrefs = prefs.node("right.panel");
			rightPrefs.putInt("tab.count", this.rightTabbedPane.getTabCount());

			for (int i = 0; i < this.rightTabbedPane.getTabCount(); i++) {
				final FileList component = (FileList) this.rightTabbedPane.getComponentAt(i);

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