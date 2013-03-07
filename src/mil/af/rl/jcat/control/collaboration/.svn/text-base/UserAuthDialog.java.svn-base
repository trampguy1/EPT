/*
 * Created on Jan 5, 2006
 * Author:  Mike D
 * UserAuthDialog - Dialog shown to clients logging into a collab server that requires authentication
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserAuthDialog extends JDialog implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	private JPasswordField passBox;
	private JButton okButton, cancButton;
	private JTextField uNameBox;
	String user = null;
	String pass = null;

	
	public UserAuthDialog(Frame parent, int authType)
	{
		super(parent, "Authentication Info");
		setModal(true);
		setSize(300,220);
		setResizable(false);
		setLocationRelativeTo(parent);
		setLayout(new GridLayout(0,1));
		
		init(authType);
		
		setVisible(true);
	}
	
	//inits differently based on authType specified
	public void init(int authType)
	{
		JPanel buttonPan = new JPanel(new FlowLayout());
		okButton = new JButton("OK");
		cancButton = new JButton("Cancel");
		buttonPan.add(okButton);
		buttonPan.add(cancButton);
		okButton.addActionListener(this);
		cancButton.addActionListener(this);
		
		JTextArea txt = new JTextArea("Authentication required. \nPlease enter the following information.");
		txt.setFocusable(false);
		txt.setLineWrap(true);
		txt.setWrapStyleWord(true);
		txt.setBackground(Color.RED.darker().darker());
		txt.setForeground(Color.WHITE);
		txt.setEditable(false);
		txt.setFont(new java.awt.Font("Arial", 1, 12));
		getContentPane().add(txt);
		
		if(authType == CollaborationControl.SESSION_PASS_AUTH)
		{
			setSize(300,150);
			JPanel passPan = new JPanel(new FlowLayout());
			passBox = new JPasswordField();
			passBox.setPreferredSize(new java.awt.Dimension(150,25));
			passBox.addKeyListener(this);
			
			passPan.add(new JLabel("Password: "));
			passPan.add(passBox);
			getContentPane().add(passPan);			
		}
		else if(authType == CollaborationControl.USER_LIST_AUTH)
		{
			JPanel passPan = new JPanel(new FlowLayout());
			passBox = new JPasswordField();
			passBox.setPreferredSize(new java.awt.Dimension(150,25));
			passBox.addKeyListener(this);
			
			JPanel namePan = new JPanel(new FlowLayout());
			uNameBox = new JTextField();
			uNameBox.setPreferredSize(new java.awt.Dimension(150,25));
			uNameBox.addKeyListener(this);
			
			namePan.add(new JLabel("Username: "));
			namePan.add(uNameBox);
			passPan.add(new JLabel("Password: "));
			passPan.add(passBox);
			getContentPane().add(namePan);
			getContentPane().add(passPan);
		}
		
		getContentPane().add(buttonPan);
	}
		
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			if(uNameBox != null)
				user = uNameBox.getText();
			if(passBox != null)
				pass = new String(passBox.getPassword());
		}
				
		dispose();
	}

	public String getUsername()
	{
		return user;
	}
	
	public String getPassword()
	{
		return pass;
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
