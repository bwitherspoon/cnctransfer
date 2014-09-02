package edu.tntech.cnctransfer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import jssc.SerialPortList;

public class SettingsDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 4627108043773590972L;
	
	public final static String RATE_PREF = "serial/rate";
	public final static String PORT_PREF = "serial/port";
	public final static String PARITY_PREF = "serial/parity";
	public final static String DATABITS_PREF = "serial/databits";
	public final static String STOPBITS_PREF = "serial/stopbits";
	public final static String FLOWCONTROL_PREF = "serial/flowcontrol";
	
	public final static int RATE_DEFAULT = 9600;
	public final static String PORT_DEFAULT = "";
	public final static String PARITY_DEFAULT = "none";
	public final static int DATABITS_DEFAULT = 8;
	public final static int STOPBITS_DEFAULT = 1;
	public final static String FLOWCONTROL_DEFAULT = "none";
	
	// FIXME only use Integer wrappers in generics
	public static final Integer[] RATES = {110, 300, 600, 1200, 4800, 9600,
										   14400, 19200, 38400, 57600, 115200,
	  		  					 	       128000, 256000};
	public static final Integer[] DATABITS = {5, 6, 7, 8};
	public static final Integer[] STOPBITS = {1, 2};
	
	public static String prefsNodeName = "/edu/tntech/cnctransfer";
	
	private final Preferences prefs;
	
	private final JComboBox<String> portList;
	private final JComboBox<Integer> rateList;
	private final JComboBox<String> parityList;
	private final JComboBox<Integer> bitsList;
	private final JComboBox<Integer> stopbitsList;
	private final JComboBox<String> flowControlList;
	
	protected final static String OK_CMD = "ok";
	protected final static String CANCEL_CMD = "cancel";
	
	public SettingsDialog(Frame owner) {
		super(owner, "Settings", true);
		
		// Obtain a preferences object
		prefs = Preferences.userRoot().node(prefsNodeName);
		
		// Create settings panel
		String portPref = prefs.get(PORT_PREF, PORT_DEFAULT);
		// FIXME do in SwingWorker
		String[] ports = SerialPortList.getPortNames();
		portList = new JComboBox<>(ports);
		portList.setPreferredSize(new Dimension(150, 25));
		portList.setSelectedItem(portPref);
		
		int ratePref = prefs.getInt(RATE_PREF, RATE_DEFAULT);
		rateList = new JComboBox<>(RATES);
		rateList.setPreferredSize(new Dimension(100, 35));
		rateList.setSelectedItem(ratePref);
		
		String[] parity = {"none", "even", "odd"};
		String parityPref = prefs.get(PARITY_PREF, PARITY_DEFAULT);
		parityList = new JComboBox<>(parity);
		parityList.setPreferredSize(new Dimension(100, 35));
		parityList.setSelectedItem(parityPref);
		
		int bitsPref = prefs.getInt(DATABITS_PREF, 8);
		bitsList = new JComboBox<>(DATABITS);
		bitsList.setPreferredSize(new Dimension(100, 35));
		bitsList.setSelectedItem(bitsPref);
		
		int stopbitsPref = prefs.getInt(STOPBITS_PREF, 1);
		stopbitsList = new JComboBox<>(STOPBITS);
		stopbitsList.setPreferredSize(new Dimension(100, 35));
		stopbitsList.setSelectedItem(stopbitsPref);
		
		// FIXME
		String[] flowControl = {"none"};
		flowControlList = new JComboBox<>(flowControl);
		flowControlList.setPreferredSize(new Dimension(100, 35));
		
		JPanel settingsPane = new JPanel();
		settingsPane.setLayout(new BoxLayout(settingsPane, BoxLayout.LINE_AXIS));
		settingsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
		settingsPane.add(portList);
		settingsPane.add(rateList);
		settingsPane.add(parityList);
		settingsPane.add(bitsList);
		settingsPane.add(stopbitsList);
		settingsPane.add(flowControlList);
		
		// Create button panel
		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(100, 50));
		okButton.setActionCommand(OK_CMD);
		okButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 50));
		cancelButton.setActionCommand(CANCEL_CMD);
		cancelButton.addActionListener(this);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(15, 0)));
		buttonPane.add(okButton);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.add(settingsPane);
		contentPane.add(buttonPane);
		
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	private void handleOKAction() {
		String port = (String)portList.getSelectedItem();
		prefs.put(PORT_PREF, (port != null) ? port : ""); 
		prefs.putInt(RATE_PREF, (int)rateList.getSelectedItem());
		prefs.put(PARITY_PREF, (String)parityList.getSelectedItem());
		prefs.putInt(DATABITS_PREF, (int)bitsList.getSelectedItem());
		prefs.putInt(STOPBITS_PREF, (int)stopbitsList.getSelectedItem());
		// TODO flow control
		setVisible(false);
		dispose();
	}
	
	private void handleCancelAction() {
		setVisible(false);
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch(cmd) {
			case OK_CMD:
				handleOKAction();
				break;
			case CANCEL_CMD:
				handleCancelAction();
				break;
		}
	}
}
