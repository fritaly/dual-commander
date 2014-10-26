package fr.ritaly.dualcommander;

import javax.swing.JFrame;

public class DualCommander extends JFrame {

	private static final long serialVersionUID = 5445919782222373150L;

	public DualCommander() {
		// TODO Insert version number in frame's title & build id
		super("Dual Commander");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public static void main(String[] args) {
		final DualCommander commander = new DualCommander();
		commander.setVisible(true);
	}
}