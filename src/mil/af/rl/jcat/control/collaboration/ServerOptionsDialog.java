/*
 * Created on Jan 5, 2006
 * Author:  MikeD
 * ServerOptionsDialog - Dialog for user options and configuration when starting a collab server
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.MainFrm;

public class ServerOptionsDialog extends JDialog implements ActionListener, ItemListener
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ServerOptionsDialog.class);
	private JRadioButton sessionPassOpt, userListOpt, addressListOpt;
	private JButton authConfig, cancButton, startButton;
	private JCheckBox chatOpt, logOpt, authOption;
	private boolean start = false;
	private Hashtable options = null;
	private JTextField portNum;
	private JTextField servName;
	private JComboBox interfacesBox;
	private JLabel hostNameBox;

	
	public ServerOptionsDialog(Frame parent, Hashtable opts)
	{
		super(parent, "Collaboration Host Options");
		setModal(true);
		setSize(355, 440);
		setResizable(false);
		setLocationRelativeTo(parent);
		setLayout(null);
		options = opts;
		
		init();
		
		setVisible(true);
	}
		
	public void init()
	{
		JPanel hostInfPanel = new JPanel(new GridLayout(0,2));
		hostInfPanel.setBorder(BorderFactory.createTitledBorder("Host Info"));
		hostInfPanel.setBounds(5,5,335,100);
		
		hostInfPanel.add(new JLabel("   Hosting plan:"));
		hostInfPanel.add(new JLabel(MainFrm.getInstance().getActiveView().getPlan().getPlanName()));
		hostInfPanel.add(new JLabel("   Host address:"));
		//the rest of host stuff is done below...
		
		JPanel authPanel = new JPanel(null);
		authPanel.setBorder(BorderFactory.createTitledBorder("Authentication"));
		authPanel.setBounds(5,110,335,110);
		
		authOption = new JCheckBox("Enable Authentication", ((Boolean)options.get("auth")).booleanValue());
		authOption.setBounds(10,15,200,25);
		authOption.addItemListener(this);
		ButtonGroup authGrp = new ButtonGroup();
		int type = ((Integer)options.get("auth-type")).intValue();
		sessionPassOpt = new JRadioButton("Session password", type==CollaborationControl.SESSION_PASS_AUTH?true:false);
		authGrp.add(sessionPassOpt);
		sessionPassOpt.setBounds(25,40,150,25);
		userListOpt = new JRadioButton("User list", type==CollaborationControl.USER_LIST_AUTH?true:false);
		authGrp.add(userListOpt);
		userListOpt.setBounds(25,60,150,25);
		addressListOpt = new JRadioButton("IP Address list", type==CollaborationControl.IP_LIST_AUTH?true:false);
		authGrp.add(addressListOpt);
		addressListOpt.setBounds(25,80,180,25);
		authConfig = new JButton("Configure");
		authConfig.setBounds(210,45,100,23);
		authConfig.addActionListener(this);
		authPanel.add(authOption);
		authPanel.add(sessionPassOpt);
		authPanel.add(userListOpt);
		authPanel.add(addressListOpt);
		authPanel.add(authConfig);
		
		enableAuthOptions(authOption.isSelected());
		
		JPanel otherPanel = new JPanel(null);
		otherPanel.setBorder(BorderFactory.createTitledBorder(""));
		otherPanel.setBounds(5,230,335,140);
		
		chatOpt = new JCheckBox("Enable user chat", ((Boolean)options.get("chat")).booleanValue());
		chatOpt.setBounds(10,5,150,20);
		chatOpt.setEnabled(false);
		logOpt = new JCheckBox("Log session events", ((Boolean)options.get("log")).booleanValue());
		logOpt.setBounds(10,30,200,20);
		JLabel portLbl = new JLabel("Port:");
		portLbl.setBounds(15,55,35,20);
		portNum = new JTextField(((Integer)options.get("port"))+"", 5);
		portNum.setBounds(45,55,50,20);
		JLabel servNameLbl = new JLabel("Your user name:");
		servNameLbl.setHorizontalAlignment(JLabel.RIGHT);
		servNameLbl.setBounds(120,55,110,20);
		servName = new JTextField("server/host");
		servName.setBounds(235,55,90,20);
		JTextArea portNote = new JTextArea("Note:  This port, 1 port lower and 1 port higher will be used.  If your system " +
				"is behind a router or firewall, be sure to configure it to allow these 3 ports or your " +
				"collaboration session may not work properly.");
		portNote.setForeground(java.awt.Color.RED);
		portNote.setEditable(false);
		portNote.setOpaque(false);
		portNote.setLineWrap(true);
		portNote.setWrapStyleWord(true);
		portNote.setBounds(10,80,320,55);
		portNote.setFont(portLbl.getFont().deriveFont(11f));
		otherPanel.add(chatOpt);
		otherPanel.add(logOpt);
		otherPanel.add(servNameLbl);
		otherPanel.add(servName);
		otherPanel.add(portLbl);
		otherPanel.add(portNum);
		otherPanel.add(portNote);
		
		startButton = new JButton("Start");
		startButton.setBounds(80,375,80,25);
		startButton.setFont(startButton.getFont().deriveFont(1));
		startButton.addActionListener(this);
		cancButton = new JButton("Cancel");
		cancButton.setBounds(200,375,80,25);
		cancButton.addActionListener(this);
		
		try{
			Vector<InetAddress> addrs = getInterfaces((InetAddress)options.get("interface"));
			
			interfacesBox = new JComboBox(addrs);
			interfacesBox.addItemListener(this);
			interfacesBox.setRenderer(new InetAddressComboBoxRenderer());
			hostInfPanel.add(interfacesBox);//new JLabel(addrs.get(0).getHostAddress()));
			hostInfPanel.add(new JLabel("   Host name:"));
			hostInfPanel.add(hostNameBox = new JLabel(addrs.get(0).getHostName()));
			
		}catch(UnknownHostException exc)
		{
			hostInfPanel.add(new JLabel("Unable to determine network interface!"));
			startButton.setEnabled(false);
		}
		
		add(hostInfPanel);
		add(authPanel);
		add(otherPanel);
		add(startButton);
		add(cancButton);
	}
	
	public boolean startWasPressed()
	{
		return start;
	}
	
	/**
	 * Generate a list of available network interfaces, the list will always contain at least
	 * 1 InetAddress unless the exception is throw
	 * @param defInt The address to be default(first in the list) if its in the list, null is allowed
	 * @return list of available InetAddresses
	 * @throws UnknownHostException
	 */
	public Vector<InetAddress> getInterfaces(InetAddress defInt) throws UnknownHostException
	{
		
		Vector<InetAddress> choices = new Vector<InetAddress>();
		try{
			Enumeration<NetworkInterface> intfs = java.net.NetworkInterface.getNetworkInterfaces();
			while(intfs.hasMoreElements())
			{
				NetworkInterface intf = intfs.nextElement();
				
				Enumeration<InetAddress> addrs = intf.getInetAddresses();
				while(addrs.hasMoreElements())
				{
					InetAddress addr = addrs.nextElement();
					
					if(!addr.isAnyLocalAddress() && !addr.isLinkLocalAddress() && !addr.isLoopbackAddress())
						choices.add(addr);
				}
			}
		}catch(SocketException exc)
		{
			logger.error("getInterfaces - Unable to determine available network interfaces: ", exc);
		}
		
		if(choices.size() < 1)
			choices.add(InetAddress.getLocalHost());
		else if(defInt != null && choices.indexOf(defInt) > -1) //move the default to the top of the list if it exists
			choices.add(0, choices.remove(choices.indexOf(defInt)));
		
		return choices;
	}
	
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == startButton)
		{
			//set the current options
			options.put("auth", new Boolean(authOption.isSelected()));
			int type = 0;
			if(sessionPassOpt.isSelected())
				type = CollaborationControl.SESSION_PASS_AUTH;
			else if(userListOpt.isSelected())
				type = CollaborationControl.USER_LIST_AUTH;
			else
				type = CollaborationControl.IP_LIST_AUTH;
			options.put("auth-type", new Integer(type));
			options.put("chat", new Boolean(chatOpt.isSelected()));
			options.put("log", new Boolean(logOpt.isSelected()));
			options.put("port", new Integer(portNum.getText()));
			options.put("name", servName.getText());
			options.put("interface", interfacesBox.getSelectedItem());
									
			start = true;
		}
		else if(event.getSource() == authConfig)
		{
			int type = 0;
			if(sessionPassOpt.isSelected())
				type = CollaborationControl.SESSION_PASS_AUTH;
			else if(userListOpt.isSelected())
				type = CollaborationControl.USER_LIST_AUTH;
			else
				type = CollaborationControl.IP_LIST_AUTH;
				
			new AuthOptionsDialog(this, type, options);
		}
		
		if(event.getSource() == startButton || event.getSource() == cancButton)
			dispose();
	}
	
	public void itemStateChanged(ItemEvent event)
	{
		if(event.getSource() == authOption)
		{
			if(event.getStateChange() == ItemEvent.SELECTED)
				enableAuthOptions(true);
			else
				enableAuthOptions(false);
		}
		else if(event.getSource() == interfacesBox)
		{
			if(event.getStateChange() == ItemEvent.SELECTED)
				hostNameBox.setText(((InetAddress)interfacesBox.getSelectedItem()).getHostName());
		}
	}
	
	public void enableAuthOptions(boolean enab)
	{
		sessionPassOpt.setEnabled(enab);
		userListOpt.setEnabled(enab);
		addressListOpt.setEnabled(enab);
		authConfig.setEnabled(enab);
	}

	private class InetAddressComboBoxRenderer extends BasicComboBoxRenderer
	{

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if(value instanceof InetAddress)
				setText(((InetAddress)value).getHostAddress());
			
			return comp;
		}
		
	}
}
