/*
 * Created on Jan 5, 2006
 * Author:  Mike D
 * ClientOptionsDialog - Dialog for user to change options for thier collab session
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ClientOptionsDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton okButton, cancButton;
	private JCheckBox announceOpt;
	private boolean showAnnounce = true;
	private Hashtable options;
	
	public ClientOptionsDialog(Frame parent, Hashtable opts)
	{
		super(parent, "Collaboration Options");
		setModal(true);
		//setSize(200,100);
		setResizable(false);
		setLayout(new GridLayout(0,1));
		options = opts;
		
		init();
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	//inits differently based on authType specified
	public void init()
	{
		JPanel buttonPan = new JPanel(new FlowLayout());
		okButton = new JButton("OK");
		cancButton = new JButton("Cancel");
		buttonPan.add(okButton);
		buttonPan.add(cancButton);
		okButton.addActionListener(this);
		cancButton.addActionListener(this);
		
		announceOpt = new JCheckBox("Show user action announcments", ((Boolean)options.get("announce")).booleanValue());
		
		getContentPane().add(announceOpt);
		getContentPane().add(buttonPan);
	}
		
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			showAnnounce = announceOpt.isSelected();
			options.put("announce", new Boolean(showAnnounce));
		}
				
		dispose();
	}
	
}
