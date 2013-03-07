/*
 * Created on Jan 5, 2006
 * Author:  Mike D
 * AuthOptionsDialog - Dialog for setting up the authentication chosen for a collab server
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

public class AuthOptionsDialog extends JDialog implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	private JPasswordField passBox;
	private JButton okButton, cancButton, addButton, remButton;
	private Hashtable options = null;
	private Vector users = new Vector();
	private int authType;
	private JList userList;

	
	public AuthOptionsDialog(ServerOptionsDialog parent, int type, Hashtable opts)
	{
		super(parent, "Authentication Configuration");
		setModal(true);
		setSize(255,300);
		setResizable(false);
		setLocationRelativeTo(parent);
		setLayout(new java.awt.BorderLayout());
		options = opts;
		authType = type;
		
		init();
		
		setVisible(true);
	}
	
	//configures differently based on auth-type input
	public void init()
	{
		okButton = new JButton("OK");
		cancButton = new JButton("Cancel");
		okButton.addActionListener(this);
		cancButton.addActionListener(this);
		
		if(authType == CollaborationControl.SESSION_PASS_AUTH)
		{
			setSize(255,100);
			JPanel passPanel = new JPanel(new FlowLayout());
			passBox = new JPasswordField((String)options.get("auth-pass"));
			passBox.setPreferredSize(new Dimension(150,25));
			passBox.addKeyListener(this);
			
			passPanel.add(new JLabel("Password: "));
			passPanel.add(passBox);
			getContentPane().add(passPanel);
		}
		else if(authType == CollaborationControl.USER_LIST_AUTH)
		{
			setSize(255, 300);
			users.addAll((Vector)options.get("auth-ulist"));
			userList = new JList(users);
			JScrollPane userScroll = new JScrollPane(userList);
			userScroll.setBorder(BorderFactory.createTitledBorder("Allowed Users (username : password)"));
			
			getContentPane().add(userScroll, BorderLayout.CENTER);
			
			JPanel edtPanel = new JPanel(new FlowLayout());
			addButton = new JButton("Add User");
			addButton.addActionListener(this);
			remButton = new JButton("Remove User");
			remButton.addActionListener(this);
			
			edtPanel.add(addButton);
			edtPanel.add(remButton);
			getContentPane().add(edtPanel, BorderLayout.NORTH);
		}
		else //ip list
		{
			setSize(255, 300);
			users.addAll((Vector)options.get("auth-iplist"));
			userList = new JList(users);
			JScrollPane userScroll = new JScrollPane(userList);
			userScroll.setBorder(BorderFactory.createTitledBorder("Allowed IP Addresses"));

			getContentPane().add(userScroll, BorderLayout.CENTER);
			
			JPanel edtPanel = new JPanel(new FlowLayout());
			addButton = new JButton("Add Address");
			addButton.addActionListener(this);
			remButton = new JButton("Remove Address");
			remButton.addActionListener(this);
			
			edtPanel.add(addButton);
			edtPanel.add(remButton);
			getContentPane().add(edtPanel, BorderLayout.NORTH);
		}
		
		JPanel butPanel = new JPanel(new FlowLayout());
		butPanel.add(okButton);
		butPanel.add(cancButton);
		getContentPane().add(butPanel, BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			if(authType == CollaborationControl.SESSION_PASS_AUTH)
					options.put("auth-pass", new String(passBox.getPassword()));
			else if(authType == CollaborationControl.USER_LIST_AUTH)
				options.put("auth-ulist", users);
			else //ip list
				options.put("auth-iplist", users);
			
			dispose();
		}
		else if(event.getSource() == addButton)
		{
			Object newItem = getInput();
			if(newItem != null)
				users.add(newItem);
			userList.updateUI();  //this might be a bad thing to use but prolly not
		}
		else if(event.getSource() == remButton)
		{
			users.remove(userList.getSelectedValue());
			userList.updateUI();
		}
		else if(event.getSource() == cancButton)
			dispose();
	}

	private Object getInput()
	{
		if(authType == CollaborationControl.USER_LIST_AUTH)
		{
			String user = JOptionPane.showInputDialog(this, "Enter a username:");
			
			if(user != null && verifyInput(user))
			{
				String pass = JOptionPane.showInputDialog(this, "Enter a password for user "+user+" (case-sensative):");
				if(pass != null && verifyInput(pass))
					return user+" : "+pass;
				else
					return null;
			}
			else 
				return null;
		}
		else //ip list
		{
			String ip = JOptionPane.showInputDialog(this, "Enter an IP address:");
			if(verifyInput(ip))
				return ip; //even if its null
			else
				return null;
		}
	}
	
	public boolean verifyInput(String input)
	{
		if(input == null || input.trim().equals(""))
		{
			JOptionPane.showMessageDialog(this, "Username and password cannot be empty");
			return false;
		}
		else
		{
			if(authType == CollaborationControl.USER_LIST_AUTH)
			{
				if(input.indexOf(":") == -1)
					return true;
				else
					JOptionPane.showMessageDialog(this, "Username and password may not contain a colon (:).");
			}
			else if(authType == CollaborationControl.IP_LIST_AUTH)
			{
				//verify the input is a valid IP address
				StringTokenizer st = new StringTokenizer(input, ".");
				if(st.countTokens() == 4)
				{
					try{
						int int1 = Integer.parseInt(st.nextToken());
						int int2 = Integer.parseInt(st.nextToken());
						int int3 = Integer.parseInt(st.nextToken());
						int int4 = Integer.parseInt(st.nextToken());
						//make sure each quartet is in 0 to 254 range
						if(int1 < 0 || int2 < 0 || int3 < 0 || int4 < 0 || int1 > 254 || int2 > 254 || int3 > 254 || int4 > 254)
							throw new NumberFormatException();
						return true;
					}catch(NumberFormatException exc)
					{
						JOptionPane.showMessageDialog(this, "Your input was not in the proper format of an IP address. \n" +
						"A valid address should be similar to the following format:  xxx.xxx.xxx.xxx \n" +
						"Note that besides the periods all characters should be numbers between 0 and 254.");
					}
				}
				else
					JOptionPane.showMessageDialog(this, "Your input was not in the proper format of an IP address. \n" +
							"A valid address should be similar to the following format:  xxx.xxx.xxx.xxx \n" +
							"Note there are 4 sections (3 periods) in the address.");
			}
			
			input = null;
			return false;
		}
	}
	
	public void keyPressed(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.VK_ENTER)
			okButton.doClick();
	}

	//unused key-events
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
}
