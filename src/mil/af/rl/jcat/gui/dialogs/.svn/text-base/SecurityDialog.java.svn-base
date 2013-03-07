package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.swing.*;

import org.apache.log4j.Logger;


import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.util.Encrypter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.FileUtils;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: C3I Associates</p>
 * @author Edward Verenich
 * @version 1.0
 */
public class SecurityDialog extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JButton apply = new JButton("Apply");
	private JButton cancel = new JButton("Exit");
	private JLabel portLabel = new JLabel("Client collaboration port: ");
	private JLabel passLabel = new JLabel("New Pass Phrase:");
	private JLabel confirmLabel = new JLabel("Confirm Phrase:");
	private JPasswordField phrase = new JPasswordField(10);
	private JPasswordField confirm = new JPasswordField(10);
	private JTextField port = new JTextField(10);
	//private JCheckBox eplans = new JCheckBox("Encrypt Plans");
	private JCheckBox change = new JCheckBox("Change Password");
	private String ph;
	private int portnumber;
	private static Logger logger = Logger.getLogger(SecurityDialog.class);
	private boolean copyComplete = false;


	public SecurityDialog(String passphrase, java.awt.Frame parent) throws MissingRequiredFileException, Exception
	{
		super(parent);
		this.setTitle("Security Settings:");
		this.setModal(true);
		this.setResizable(false);

		if(parent != null)
		{
			setLocation((int) parent.getLocationOnScreen().getX() + 50,
					(int) parent.getLocationOnScreen().getY() + 100);
		}
		ph = passphrase;
		jbInit();
	}

	private void jbInit()throws MissingRequiredFileException, Exception
	{
		String content = "";
		File accessfile = new File(EnvUtils.getJCATSettingsHome() + EnvUtils.sep + "access.jc");
		boolean copyStarted = false;
		
		if(!accessfile.exists())
		{
			while(!copyComplete)
			{
				if(!copyStarted)
				{
					// This new way of loading resources worx with a common resource jar for Web Start
					InputStream orig = this.getClass().getClassLoader().getResourceAsStream("access.jc");
					FileUtils.copyFile(orig, accessfile, this);
					copyStarted = true;
				}
				else
				{
					try{
						Thread.sleep(10);
					}catch(InterruptedException exc){   }
				}
			}
		}
		
		if(!accessfile.exists())
		{
			accessfile = null;
			dispose();
			throw new MissingRequiredFileException("A Required access file does not exist, contact your administrator.");
		}
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(accessfile));
			String str;
			while ((str = in.readLine()) != null) 
				content += str;
			
			in.close();
		}
		catch (IOException e) {
			throw new MissingRequiredFileException("A required access file is corrupted, contact your administrator");
		}
		
		Encrypter encrypter = null;
		String dcipher = null;
		// now decrypt it

		encrypter = new Encrypter(ph);
		dcipher = encrypter.decrypt(content);
		if(dcipher == null)
			throw new Exception("Incorrect pass-phrase.");

		// for now just look for port settings
		StringTokenizer st = new StringTokenizer(dcipher,":");
		if(st.countTokens() < 1)
		{
			st = null;
			throw new MissingRequiredFileException("A required access file is corrupted, contact your administrator");
		}
		st.nextToken();
		portnumber = Control.getInstance().getRmiPort(); //Integer.parseInt(st.nextToken());
		port.setText(portnumber+"");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		JPanel bpanel = new JPanel();
		this.getContentPane().setLayout(new BorderLayout());
		bpanel.add(apply);
		apply.addActionListener(this);
		apply.setToolTipText("Apply current settings.");
		bpanel.add(cancel);
		cancel.addActionListener(this);
		cancel.setToolTipText("Cancel current changes and exit.");
		this.getContentPane().add(bpanel,BorderLayout.SOUTH);

		panel.add(portLabel);
		panel.add(port);
		panel.add(passLabel);
		panel.add(phrase);
		panel.add(confirmLabel);
		panel.add(confirm);
		panel.add(change);
		//panel.add(eplans);
		//eplans.setToolTipText("If enrypt plans option is selected, a user will have to use a pass phrase to open plans.");

		this.getContentPane().add(panel,BorderLayout.CENTER);
		this.pack();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(apply))
		{
			int port_number = 1099;
			// check if the port is a valid number
			try{
				port_number = Integer.parseInt(port.getText().trim());
			}catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(this,"Please specify a valid port number.");
				return;
			}
			// make sure it's above 1024
			if(port_number < 1024)
			{
				JOptionPane.showMessageDialog(this,"Ports below 1024 are reserved by the system.");
				return;
			}
			if(this.change.isSelected())
			{
				String pone = new String(phrase.getPassword());
				String ptwo = new String(confirm.getPassword());
				if(!pone.equals(ptwo))
				{
					JOptionPane.showMessageDialog(this,"Make sure the two pass phrases are the same. ");
					return;
				}
				ph = pone;  // get the new pass phrase
			}
			
			Control.getInstance().setRmiPort(port_number);
			String stuff = "port:"+port_number;
			// first change the password
			// now create an encryptor and write the file out
			try{
				FileWriter writer = new FileWriter(new File(EnvUtils.getJCATSettingsHome() + EnvUtils.sep + "access.jc"));
				Encrypter enc = new Encrypter(ph);
				String ecipher = enc.encrypt(stuff);

				writer.write(ecipher);
				writer.flush();
				writer.close();
			}catch(Exception ex)
			{
				logger.error("actionPerformed - Error applying security info:  "+ex.getMessage());
			}

			// write the file that stores the rmi port number
			try{
				FileWriter writer = new FileWriter(new File(EnvUtils.getJCATSettingsHome() + EnvUtils.sep + "prime.jc"));
				Encrypter enc = new Encrypter("jc@tpri#a!e77");
				String ecipher = enc.encrypt(stuff);

				writer.write(ecipher);
				writer.flush();
				writer.close();
			}catch(Exception ex)
			{
				logger.error("actionPerformed(apply) - Error writing prime.jc file:  "+ex.getMessage());
			}


		}
		else if(e.getSource().equals(cancel))
		{
			ph = null;
			portnumber = Integer.MIN_VALUE;;
			this.dispose();
		}
		else if(e.getActionCommand().equals(FileUtils.COPY))
		{
			copyComplete = true;
		}
		
	}

//	public static void main(String args[])
//	{
//	try{
//	FileWriter writer = new FileWriter(new File("resources/config/prime.jc"));
//	String stuff = "port:1099";
//	Encrypter enc = new Encrypter("jc@tpri#a!e77");
//	String ecipher = enc.encrypt(stuff);
//	writer.write(ecipher);
//	writer.flush();
//	writer.close();
//	}catch(Exception ex)
//	{
//	ex.printStackTrace(System.err);
//	}


//	}
}
