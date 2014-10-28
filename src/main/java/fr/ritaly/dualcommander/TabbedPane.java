package fr.ritaly.dualcommander;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabbedPane extends JTabbedPane implements KeyListener, ChangeListener {

	private static final long serialVersionUID = 8522448669013461274L;

	public TabbedPane() {
		// Put the tabs at the top
		super(JTabbedPane.TOP);
	}

	public FileList addFileTab() {
		return addFileTab(new File("."));
	}

	public FileList addFileTab(File directory) {
		// The constructor called will validate the parameter
		final FileList fileList = new FileList(directory);
		fileList.addChangeListener(this);
		fileList.addKeyListener(this);

		super.addTab(fileList.getDirectory().getName(), fileList);

		return fileList;
	}

	public void closeActiveFileTab() {
		final FileList fileList = getActiveComponent();
		fileList.removeChangeListener(this);
		fileList.removeKeyListener(this);

		removeTabAt(getSelectedIndex());
	}

	public FileList getActiveComponent() {
		return (FileList) getSelectedComponent();
	}

	public FileList getFileTabAt(int index) {
		return (FileList) getComponentAt(index);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			final boolean metaDown = (e.getModifiersEx() | KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK;

			if ((e.getKeyCode() == KeyEvent.VK_T) && metaDown) {
				// Create a new tab and set to focus on it
				setSelectedComponent(addFileTab(getActiveComponent().getDirectory()));
			} else if ((e.getKeyCode() == KeyEvent.VK_W) && metaDown) {
				if (getTabCount() > 1) {
					// Close the current tab (only if not the last one)
					closeActiveFileTab();
				}
			} else if ((e.getKeyCode() >= KeyEvent.VK_1) && (e.getKeyCode() <= KeyEvent.VK_9) && metaDown) {
				final int index = e.getKeyCode() - KeyEvent.VK_1;

				if (index <= getTabCount() - 1) {
					setSelectedIndex(index);
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
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == getSelectedComponent()) {
			// Update the current tab's title
			this.setTitleAt(getSelectedIndex(), getActiveComponent().getDirectory().getName());
		}
	}
}