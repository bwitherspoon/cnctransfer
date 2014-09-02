package edu.tntech.cnctransfer;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	private static final String title = "CNC Transfer";

	public static void main(String[] args) {
		// Attempt to use system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't find class for the system look and feel.");
			System.err.println("Using the default look and feel.");
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Can't use the system look and feel on this platform.");
			System.err.println("Using the default look and feel.");
		} catch (Exception e) {
			System.err.println("Couldn't use the system look and feel for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		}

		// Transfer control to the event dispatching thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame(title).setVisible(true);
			}
		});
	}
}

