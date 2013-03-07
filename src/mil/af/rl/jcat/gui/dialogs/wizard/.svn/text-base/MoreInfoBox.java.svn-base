/*
 * Created on Sep 29, 2005
 * Author: MikeyD
 */
package mil.af.rl.jcat.gui.dialogs.wizard;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

public class MoreInfoBox extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane = null;
	private JEditorPane textPane = null;
	private JButton closeButton = null;
	private Window parent = null;
	private JScrollPane scroll;
	private static Logger logger = Logger.getLogger(MoreInfoBox.class);

	public MoreInfoBox(Dialog par)
	{
		super(par);
		parent = par;
		setSize(300, 370);
		setLocationRelativeTo(par);
		setUndecorated(true);
		setAlwaysOnTop(true);
		initialize();
	}

	private void initialize()
	{
		//this.setTitle("More Information");
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		textPane = new JEditorPane();
		textPane.setText("<html> No addition info found </html>");
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.setBackground(new java.awt.Color(255,254,160));
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setBackground(new java.awt.Color(255,254,154));
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		bottomPanel.add(closeButton);
		scroll = new JScrollPane(textPane);
		scroll.setBorder(null);
		contentPane.add(scroll, java.awt.BorderLayout.CENTER);
		contentPane.add(bottomPanel, java.awt.BorderLayout.SOUTH);
		this.setContentPane(contentPane);
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == closeButton)
			dispose();
		else if(event.getSource() instanceof JButton)
		{
			InputStream pageInput = this.getClass().getClassLoader().getResourceAsStream(event.getActionCommand()+".html");
			if(pageInput != null)
			{
				try{
					StringBuffer buff = new StringBuffer();
					BufferedReader pageInputStream = new BufferedReader(new InputStreamReader(pageInput));
					String line = "";
					while((line = pageInputStream.readLine()) != null)
						buff.append(line);
					
					textPane.setText(buff.toString());
				}catch(java.io.IOException exc){
					logger.warn("actionPerformed - IOExc reading 'more info' document:  "+exc.getMessage());
				}
			}
			else
				textPane.setText("<html> No addition information available </html>");
			
			//setLocationRelativeTo(parent);
			Point butLoc = ((JButton)event.getSource()).getLocationOnScreen();
			setLocation(new Point(butLoc.x - getSize().width, butLoc.y - getSize().height));
			setVisible(true);
			textPane.setCaretPosition(0);
		}
	}
}
