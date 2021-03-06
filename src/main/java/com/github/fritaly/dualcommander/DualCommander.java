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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class DualCommander extends JFrame implements ChangeListener, WindowListener, KeyListener, PropertyChangeListener,
		FocusListener {

	private static final long serialVersionUID = 5445919782222373150L;

	// TODO Add support for Ctrl+C / Ctrl-X / Ctrl-V with the system clipboard
	// TODO Add support for drag'n'drop between file panes
	// TODO Add a contextual menu to file entries
	// TODO Use the new FileSystem API from Java 7 to introspect archive (zip, jar, war, etc) files
	// TODO Add icons for each action
	// TODO Implement the actions F3 -> F8
	private final class AboutAction extends AbstractAction {

		private static final long serialVersionUID = -8972233122891491665L;

		public AboutAction() {
			super("About", Icons.HELP_ICON);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DualCommander.this, "Not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private final class PreferencesAction extends AbstractAction {

		private static final long serialVersionUID = -4073587940746786910L;

		public PreferencesAction() {
			super("Preferences", Icons.COG_ICON);
		}

		public JButton getButton(final JOptionPane optionPane, String text, Icon icon) {
			final JButton button = new JButton(text, icon);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					optionPane.setValue(button.getText());
				}
			});

			return button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final UserPreferencesPanel panel = new UserPreferencesPanel(getPreferences());

			final JOptionPane optionPane = new JOptionPane();
		    optionPane.setMessage(panel);
		    optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		    optionPane.setOptionType(JOptionPane.YES_NO_OPTION);

			final JButton applyButton = getButton(optionPane, "Apply", Icons.ACCEPT_ICON);
			final JButton cancelButton = getButton(optionPane, "Cancel", Icons.CANCEL_ICON);

		    optionPane.setOptions(new Object[] { applyButton, cancelButton });

		    final JDialog dialog = optionPane.createDialog(DualCommander.this, "Preferences");
		    dialog.setVisible(true);

			final Object value = optionPane.getValue();

			if ("Apply".equals(value)) {
				// User clicked on "Apply"
				getPreferences().apply(panel.getPreferences());
			} else {
				// User clicked "Cancel" or closed the dialog
			}
		}
	}

	private final class ViewAction extends AbstractAction {

		private static final long serialVersionUID = -3885021368508066211L;

		public ViewAction() {
			super("F3 View");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// What's the active pane's file selection ?
				final List<File> selection = activePane.getActiveBrowser().getSelection();

				if (!selection.isEmpty() && (selection.size() == 1)) {
					final File file = selection.iterator().next();

					if (file.isFile()) {
						new ProcessBuilder(preferences.getViewFileCommand(), file.getAbsolutePath()).start();
					}
				}
			} catch (IOException e1) {
				logger.error("Error when viewing file", e1);
			}
		}
	}

	private final class EditAction extends AbstractAction {

		private static final long serialVersionUID = -4570243231693650751L;

		public EditAction() {
			super("F4 Edit", Icons.EDIT_ICON);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// What's the active pane's file selection ?
				final List<File> selection = activePane.getActiveBrowser().getSelection();

				if (!selection.isEmpty() && (selection.size() == 1)) {
					final File file = selection.iterator().next();

					if (file.isFile()) {
						new ProcessBuilder(preferences.getEditFileCommand(), file.getAbsolutePath()).start();
					}
				}
			} catch (IOException e1) {
				logger.error("Error when editing file", e1);
			}
		}
	}

	private final class CopyAction extends AbstractAction {

		private static final long serialVersionUID = -4681732083985135686L;

		public CopyAction() {
			super("F5 Copy", Icons.COPY_ICON);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// What's the active pane's file selection ?
			final List<File> selection = activePane.getActiveBrowser().getSelection();

			if (selection.isEmpty()) {
				return;
			}

			// Count the # of files / folders to copy
			final Scan scan = Utils.scan(selection);

			final String message;

			if (scan.getFiles() > 0) {
				if (scan.getDirectories() > 0) {
					message = String.format("Do you really want to copy %d file(s) & %d folder(s)", scan.getFiles(),
							scan.getDirectories());
				} else {
					message = String.format("Do you really want to copy %d file(s)", scan.getFiles());
				}
			} else {
				message = String.format("Do you really want to copy %d folders(s)", scan.getDirectories());
			}

			// TODO Set icon on dialog boxes
			final int reply = JOptionPane.showConfirmDialog(DualCommander.this, message, "Please confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (reply == JOptionPane.YES_OPTION) {
				// Store the inactive pane before the active one loses the focus
				final TabbedPane inactivePane = getInactivePane();

				// Copy the file(s) in a background task
				final SwingWorker<Void, Void> task = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						// TODO Use a progress bar (to notify the progress)
						final File targetDir = inactivePane.getActiveBrowser().getDirectory();

						setWaitCursor();

						try {
							for (File file : selection) {
								// TODO Check whether the target file already exists or
								// not

								if (file.isFile()) {
									FileUtils.copyFileToDirectory(file, targetDir, true);

									if (logger.isInfoEnabled()) {
										logger.info(String.format("Copied file %s to directory %s", file.getAbsolutePath(),
												targetDir.getAbsolutePath()));
									}
								} else if (file.isDirectory()) {
									FileUtils.copyDirectoryToDirectory(file, targetDir);

									if (logger.isInfoEnabled()) {
										logger.info(String.format("Copied directory %s to directory %s", file.getAbsolutePath(),
												targetDir.getAbsolutePath()));
									}
								}
							}

							if (logger.isInfoEnabled()) {
								logger.info(String.format("Copied %d file(s)", selection.size()));
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(DualCommander.this, "An error occured when copying the file(s)", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

						return null;
					}

					@Override
					protected void done() {
						// Refresh the target panel (the inactive one)
						inactivePane.getActiveBrowser().refresh();

						setDefaultCursor();
					}
				};

				task.execute();
			}
		}
	}

	private final class MoveAction extends AbstractAction {

		private static final long serialVersionUID = -3452333607164390841L;

		public MoveAction() {
			super("F6 Move");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// What's the active pane's file selection ?
			final List<File> selection = activePane.getActiveBrowser().getSelection();

			if (selection.isEmpty()) {
				return;
			}

			// Count the # of files / folders to move
			final Scan scan = Utils.scan(selection);

			final String message;

			if (scan.getFiles() > 0) {
				if (scan.getDirectories() > 0) {
					message = String.format("Do you really want to move %d file(s) & %d folder(s)", scan.getFiles(),
							scan.getDirectories());
				} else {
					message = String.format("Do you really want to move %d file(s)", scan.getFiles());
				}
			} else {
				message = String.format("Do you really want to move %d folders(s)", scan.getDirectories());
			}

			// TODO Set icon on dialog boxes
			final int reply = JOptionPane.showConfirmDialog(DualCommander.this,
					message, "Please confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (reply == JOptionPane.YES_OPTION) {
				// Store the inactive pane before the active one loses the focus
				final TabbedPane inactivePane = getInactivePane();

				final SwingWorker<Void, Void> task = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						// Move the file(s)
						setWaitCursor();

						// TODO Use a progress bar (if necessary)
						final File targetDir = inactivePane.getActiveBrowser().getDirectory();

						for (File file : selection) {
							final String initialPath = file.getAbsolutePath();

							// TODO Check whether the target file already exists or
							// not
							final File targetFile = new File(targetDir, file.getName());

							file.renameTo(targetFile);

							if (logger.isInfoEnabled()) {
								logger.info(String.format("Moved file %s to directory %s", initialPath, targetDir.getAbsolutePath()));
							}
						}

						if (logger.isInfoEnabled()) {
							logger.info(String.format("Moved %d file(s)", selection.size()));
						}

						return null;
					}

					@Override
					protected void done() {
						// Refresh the 2 panes
						leftPane.getActiveBrowser().refresh();
						rightPane.getActiveBrowser().refresh();

						setDefaultCursor();
					}
				};

				task.execute();
			}
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
			super("F8 Delete", Icons.DELETE_ICON);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// What's the active pane's file selection ?
			final List<File> selection = activePane.getActiveBrowser().getSelection();

			if (selection.isEmpty()) {
				return;
			}

			// Count the # of files / folders to delete
			final Scan scan = Utils.scan(selection);

			final String message;

			if (scan.getFiles() > 0) {
				if (scan.getDirectories() > 0) {
					message = String.format("Do you really want to delete %d file(s) & %d folder(s)", scan.getFiles(),
							scan.getDirectories());
				} else {
					message = String.format("Do you really want to delete %d file(s)", scan.getFiles());
				}
			} else {
				message = String.format("Do you really want to delete %d folders(s)", scan.getDirectories());
			}

			// TODO Set icon on dialog boxes
			final int reply = JOptionPane.showConfirmDialog(DualCommander.this, message, "Please confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (reply == JOptionPane.YES_OPTION) {
				// Store the active pane before it loses the focus
				final TabbedPane activePane = getActivePane();

				// Delete the file(s) in a background thread
				final SwingWorker<Void, Void> task = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						// TODO Use a progress bar (if necessary)
						setWaitCursor();

						for (File file : selection) {
							Utils.deleteRecursively(file, null);
						}

						if (logger.isInfoEnabled()) {
							logger.info(String.format("Deleted %d file(s)", selection.size()));
						}

						return null;
					}

					@Override
					protected void done() {
						// Refresh the source panel (the active one)
						activePane.getActiveBrowser().refresh();

						setDefaultCursor();
					}
				};

				task.execute();
			}
		}
	}

	private final class QuitAction extends AbstractAction {

		private static final long serialVersionUID = 388422465301643805L;

		public QuitAction() {
			super("Alt+F4 Quit", Icons.DOOR_OPEN_ICON);
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

	private final Logger logger = Logger.getLogger(this.getClass());

	// --- Actions --- //

	private final AboutAction aboutAction = new AboutAction();

	private final PreferencesAction preferencesAction = new PreferencesAction();

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

	private final TabbedPane leftPane;

	private final TabbedPane rightPane;

	private TabbedPane activePane;

	/**
	 * Whether the "shift" key is currently pressed.
	 */
	private volatile boolean shiftPressed;

	/**
	 * Whether the "meta" key (aka "command") is currently pressed.
	 */
	private volatile boolean metaPressed;

	private final UserPreferences preferences = new UserPreferences();

	public DualCommander() {
		// TODO Generate a fat jar at build time
		super(String.format("Dual Commander %s", Utils.getApplicationVersion()));

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Dual Commander %s", Utils.getApplicationVersion()));
		}

		try {
			// Apply the JGoodies L&F
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
			// Not supposed to happen
		}

		// Layout, columns & rows
		setLayout(new MigLayout("insets 0px", "[grow]0px[grow]", "[grow]0px[]"));

		// Create a menu bar
		final JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(preferencesAction));
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(quitAction));

		final JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem(aboutAction));

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		this.leftPane = new TabbedPane(preferences);
		this.leftPane.setName("Left");
		this.leftPane.addChangeListener(this);
		this.leftPane.addKeyListener(this);
		this.leftPane.addFocusListener(this);

		this.rightPane = new TabbedPane(preferences);
		this.rightPane.setName("Right");
		this.rightPane.addChangeListener(this);
		this.rightPane.addKeyListener(this);
		this.rightPane.addFocusListener(this);

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
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, true), "view");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true), "edit");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true), "copy");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, true), "move");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0, true), "mkdir");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, true), "delete");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK), "quit");

		final ActionMap actionMap = this.leftPane.getActionMap();
		actionMap.put("view", viewAction);
		actionMap.put("edit", editAction);
		actionMap.put("copy", copyAction);
		actionMap.put("move", moveAction);
		actionMap.put("mkdir", mkdirAction);
		actionMap.put("delete", deleteAction);
		actionMap.put("quit", quitAction);

		addWindowListener(this);
		addKeyListener(this);

		// Listen to preference change events
		this.preferences.addPropertyChangeListener(this);

		// Reload the last configuration and init the left & right panels
		// accordingly
		final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

		// The user preferences must be loaded first because they're needed to
		// init the UI
		this.preferences.init(prefs.node("user.preferences"));
		this.leftPane.init(prefs.node("left.panel"));
		this.rightPane.init(prefs.node("right.panel"));

		if (logger.isInfoEnabled()) {
			logger.info("Loaded preferences");
		}

		// Init the buttons
		refreshButtons(this.leftPane.getActiveBrowser().getSelection());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		if (logger.isInfoEnabled()) {
			logger.info("UI initialized");
		}
	}

	public TabbedPane getActivePane() {
		return activePane;
	}

	public TabbedPane getInactivePane() {
		if (getActivePane() == leftPane) {
			return rightPane;
		}
		if (getActivePane() == rightPane) {
			return leftPane;
		}

		return null;
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

	public UserPreferences getPreferences() {
		return preferences;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// Event fired when the pane's user selection changes
		if (e.getSource() == this.leftPane) {
			// Update the buttons based on the current selection
			refreshButtons(this.leftPane.getActiveBrowser().getSelection());
		} else if (e.getSource() == this.rightPane) {
			// Update the buttons based on the current selection
			refreshButtons(this.rightPane.getActiveBrowser().getSelection());
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
			if (logger.isDebugEnabled()) {
				logger.debug("Saving preferences ...");
			}

			// Save the program state
			final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

			this.leftPane.saveState(prefs.node("left.panel"));
			this.rightPane.saveState(prefs.node("right.panel"));
			this.preferences.saveState(prefs.node("user.preferences"));

			prefs.sync();

			if (logger.isInfoEnabled()) {
				logger.info("Saved preferences");
			}
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

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getModifiers() | KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK) {
			shiftPressed = true;

			if (logger.isDebugEnabled()) {
				logger.debug("[Shift] key pressed");
			}
		}
		if ((e.getModifiers() | KeyEvent.META_MASK) == KeyEvent.META_MASK) {
			metaPressed = true;

			if (logger.isDebugEnabled()) {
				logger.debug("[Meta] key pressed");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((e.getModifiers() | KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) {
			shiftPressed = false;

			if (logger.isDebugEnabled()) {
				logger.debug("[Shift] key released");
			}
		}
		if ((e.getModifiers() | KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK) {
			metaPressed = false;

			if (logger.isDebugEnabled()) {
				logger.debug("[Meta] key released");
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == leftPane) {
			this.activePane = leftPane;

			if (logger.isDebugEnabled()) {
				logger.debug("Active pane is now left");
			}
		} else if (e.getSource() == rightPane) {
			this.activePane = rightPane;

			if (logger.isDebugEnabled()) {
				logger.debug("Active pane is now right");
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == leftPane) {
			this.activePane = null;

			if (logger.isDebugEnabled()) {
				logger.debug("Active pane is now null");
			}
		} else if (e.getSource() == rightPane) {
			this.activePane = null;

			if (logger.isDebugEnabled()) {
				logger.debug("Active pane is now null");
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() == preferences) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("User preference '%s' changed: '%s' -> '%s'", e.getPropertyName(), e.getOldValue(),
						e.getNewValue()));
			}

			// The 'show hidden' property changed, need to refresh the active
			// directory browsers
			// TODO Set a flag for the inactive directory browsers to refresh
			// when they get the focus
			this.leftPane.getActiveBrowser().refresh();
			this.rightPane.getActiveBrowser().refresh();
		}
	}

	public void setDefaultCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void setWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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