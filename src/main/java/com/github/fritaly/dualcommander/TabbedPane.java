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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public class TabbedPane extends JTabbedPane implements KeyListener, ChangeListener, FocusListener {

	private static final long serialVersionUID = 8522448669013461274L;

	private final Logger logger = Logger.getLogger(this.getClass());

	private final UserPreferences preferences;

	public TabbedPane(UserPreferences preferences) {
		// Put the tabs at the top
		super(JTabbedPane.TOP);

		Validate.notNull(preferences, "The given user preferences are null");

		this.preferences = preferences;
	}

	public DirectoryBrowser addBrowserTab() {
		return addBrowserTab(new File(System.getProperty("user.home")));
	}

	public DirectoryBrowser addBrowserTab(File directory) {
		// The called constructor will validate the parameter
		final DirectoryBrowser browser = new DirectoryBrowser(preferences, directory);
		browser.addChangeListener(this);
		browser.addKeyListener(this);
		browser.addFocusListener(this);

		super.addTab(browser.getDirectory().getName(), browser);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("[%s] Added new browser tab at index %d", getComponentLabel(), getTabCount() - 1));
		}

		return browser;
	}

	public void closeActiveBrowserTab() {
		final DirectoryBrowser browser = getActiveBrowser();
		browser.removeChangeListener(this);
		browser.removeKeyListener(this);
		browser.removeFocusListener(this);

		final int index = getSelectedIndex();

		removeTabAt(index);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("[%s] Removed browser tab at index %d", getComponentLabel(), index));
		}
	}

	public DirectoryBrowser getActiveBrowser() {
		return (DirectoryBrowser) getSelectedComponent();
	}

	public DirectoryBrowser getBrowserAt(int index) {
		return (DirectoryBrowser) getComponentAt(index);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			final boolean metaDown = (e.getModifiersEx() | KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK;

			if ((e.getKeyCode() == KeyEvent.VK_T) && metaDown) {
				// Create a new tab and set to focus on it
				setSelectedComponent(addBrowserTab(getActiveBrowser().getDirectory()));
			} else if ((e.getKeyCode() == KeyEvent.VK_W) && metaDown) {
				if (getTabCount() > 1) {
					// Close the current tab (only if not the last one)
					closeActiveBrowserTab();
				}
			} else if ((e.getKeyCode() >= KeyEvent.VK_1) && (e.getKeyCode() <= KeyEvent.VK_9) && metaDown) {
				final int index = e.getKeyCode() - KeyEvent.VK_1;

				if (index <= getTabCount() - 1) {
					setSelectedIndex(index);
				}
			} else {
				// Propagate event to our listeners
				processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
						e.getKeyLocation()));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			// Propagate the event to our listeners
			processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
					e.getKeyLocation()));
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			// Propagate the event to our listeners
			processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(),
					e.getKeyLocation()));
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			// Update the current tab's title
			this.setTitleAt(getSelectedIndex(), getActiveBrowser().getDirectory().getName());

			// Propagate the event
			fireStateChanged();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Propagate the event
		final FocusListener[] listeners = getListeners(FocusListener.class);

		if (listeners != null) {
			final FocusEvent event = new FocusEvent(this, e.getID(), e.isTemporary(), e.getOppositeComponent());

			for (FocusListener listener : listeners) {
				listener.focusGained(event);
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Propagate the event
		final FocusListener[] listeners = getListeners(FocusListener.class);

		if (listeners != null) {
			final FocusEvent event = new FocusEvent(this, e.getID(), e.isTemporary(), e.getOppositeComponent());

			for (FocusListener listener : listeners) {
				listener.focusLost(event);
			}
		}
	}

	public void init(Preferences preferences) {
		Validate.notNull(preferences, "The given preferences is null");

		final int tabCount = preferences.getInt("tab.count", 1);

		for (int i = 0; i < tabCount; i++) {
			// Create a tab set to the correct directory
			addBrowserTab(new File(preferences.get(String.format("tab.%d.directory", i), ".")));
		}

		// Ensure the tabbed pane has at least one tab
		if (getTabCount() == 0) {
			addBrowserTab();
		}
	}

	public void saveState(Preferences preferences) {
		Validate.notNull(preferences, "The given preferences is null");

		preferences.putInt("tab.count", getTabCount());

		for (int i = 0; i < getTabCount(); i++) {
			preferences.put(String.format("tab.%d.directory", i), getBrowserAt(i).getDirectory().getAbsolutePath());
		}
	}

	private String getComponentLabel() {
		return (getName() == null) ? "N/A" : getName();
	}
}