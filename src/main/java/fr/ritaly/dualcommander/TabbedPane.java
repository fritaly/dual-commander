package fr.ritaly.dualcommander;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.Validate;

public class TabbedPane extends JTabbedPane implements KeyListener, ChangeListener {

	private static final long serialVersionUID = 8522448669013461274L;

	public TabbedPane() {
		// Put the tabs at the top
		super(JTabbedPane.TOP);
	}

	public DirectoryBrowser addBrowserTab() {
		return addBrowserTab(new File(System.getProperty("user.home")));
	}

	public DirectoryBrowser addBrowserTab(File directory) {
		// The called constructor will validate the parameter
		final DirectoryBrowser browser = new DirectoryBrowser(directory);
		browser.addChangeListener(this);
		browser.addKeyListener(this);

		super.addTab(browser.getDirectory().getName(), browser);

		return browser;
	}

	public void closeActiveBrowserTab() {
		final DirectoryBrowser browser = getActiveBrowser();
		browser.removeChangeListener(this);
		browser.removeKeyListener(this);

		removeTabAt(getSelectedIndex());
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

	public void init(Preferences preferences) {
		Validate.notNull(preferences, "The given preferences is null");

		final int tabCount = preferences.getInt("tab.count", 1);

		for (int i = 0; i < tabCount; i++) {
			// Create a tab set to the correct directory
			addBrowserTab(new File(preferences.get(String.format("tab.%d.directory", i), ".")));
		}

		// Ensure the tabbed pane has at least tab
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
}