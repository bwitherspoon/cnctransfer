package edu.tntech.cnctransfer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import jssc.SerialPort;
import jssc.SerialPortException;

class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 9043866045393340503L;
	
	private static String prefsNodeName = "/edu/tntech/cnctransfer";
	
	private JTextArea messageTextArea;
	private JButton sendButton;

	public MainFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		setContentPane(contentPane);

		createMessagePane();
		createButtonPane();
		createMenu();

		pack();
		setLocationRelativeTo(null);
	}
	
	public void printMessage(final String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				messageTextArea.append(msg + "\n");
			}
		});
	}

	private void createMessagePane() {
		messageTextArea = new JTextArea(5, 35);
		messageTextArea.setMargin(new Insets(0, 5, 0, 5));
		messageTextArea.setEditable(false);

		JScrollPane scroll_pane = new JScrollPane(messageTextArea);
		scroll_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll_pane.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

		getContentPane().add(scroll_pane);
	}

	private void createButtonPane() {
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(100, 50));
		sendButton.setEnabled(false);
		sendButton.setActionCommand("send");
		sendButton.addActionListener(this);

		JButton open_button = new JButton("Settings");
		open_button.setPreferredSize(new Dimension(100, 50));
		open_button.setActionCommand("open");
		open_button.addActionListener(this);

		JPanel button_pane = new JPanel();
		button_pane.setLayout(new BoxLayout(button_pane, BoxLayout.LINE_AXIS));
		button_pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
		button_pane.add(Box.createHorizontalGlue());
		button_pane.add(sendButton);

		getContentPane().add(button_pane);
	}

	private void createMenu() {
		JMenu menu;
		JMenuItem menu_item;
		JMenuBar menu_bar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu_bar.add(menu);

		menu_item = new JMenuItem("Settings");
		menu_item.setMnemonic(KeyEvent.VK_S);
		menu_item.setActionCommand("settings");
		menu_item.addActionListener(this);
		menu.add(menu_item);

		menu.addSeparator();

		menu_item = new JMenuItem("Exit");
		menu_item.setMnemonic(KeyEvent.VK_X);
		menu_item.setActionCommand("exit");
		menu_item.addActionListener(this);
		menu.add(menu_item);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu_bar.add(menu);

		menu_item = new JMenuItem("About");
		menu_item.setMnemonic(KeyEvent.VK_A);
		menu_item.setActionCommand("about");
		menu_item.addActionListener(this);
		menu.add(menu_item);

		setJMenuBar(menu_bar);
	}

	private void handleAboutAction() {
		JOptionPane.showMessageDialog(
				this,
				"Created by C. Brett Witherspoon",
				"About",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private void handleSettingsAction() {
		SettingsDialog dialog = new SettingsDialog(this);
		dialog.setVisible(true);
		sendButton.setEnabled(true);
	}

	private void handleSendAction() {
		final File file;
		
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(MainFrame.this);

		if (ret == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				public Void doInBackground() {
					Preferences prefs;
					byte[] buffer;
					SerialPort port;
					
					// Read bytes from file
					if (!file.canRead()) {
						printMessage("Unable to read " + file.getAbsolutePath());
					} else {
						try {
							buffer = Files.readAllBytes(file.toPath());
						} catch (IOException e) {
							printMessage("Exception occured while reading " + file.getAbsolutePath());
							printMessage("Review stderr for stack trace");
							e.printStackTrace();
							return null;
						}
						
						printMessage("Read " + buffer.length + " bytes from " + file.getAbsolutePath());
						
						prefs = Preferences.userRoot().node(prefsNodeName);
						String portPref = prefs.get(SettingsDialog.PORT_PREF, SettingsDialog.PORT_DEFAULT);
						int rate = prefs.getInt(SettingsDialog.RATE_PREF, SettingsDialog.RATE_DEFAULT);
						int databits = prefs.getInt(SettingsDialog.DATABITS_PREF, SettingsDialog.DATABITS_DEFAULT);
						int stopbits = prefs.getInt(SettingsDialog.STOPBITS_PREF, SettingsDialog.STOPBITS_DEFAULT);
						// FIXME
						int parity = SerialPort.PARITY_NONE;
						
						port = new SerialPort(portPref);
						
						try {
							port.openPort();
							port.setParams(rate, databits, stopbits, parity);
							port.writeBytes(buffer);
							port.closePort();
						} catch (SerialPortException e) {
							printMessage("Exception occured while configuring serial port");
							printMessage("Review stderr for stack trace");
							e.printStackTrace();
							return null;
						}
						
						printMessage("Wrote " + buffer.length + " bytes to " + portPref);
					}
					
					return null;
				}
			};
			
			worker.execute();
		}
	}

	private void handleExitAction() {
		for (Frame frame : Frame.getFrames()) {
			if (frame.isActive()) {
				WindowEvent wc = new WindowEvent(
						frame, WindowEvent.WINDOW_CLOSING);
				frame.dispatchEvent(wc);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch(cmd) {
			case "send":
				handleSendAction();
				break;
			case "exit":
				handleExitAction();
				break;
			case "about":
				handleAboutAction();
				break;
			case "settings":
				handleSettingsAction();
				break;
		}
	}
}
	
