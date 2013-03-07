/*
 * Created on June 14, 2005
 * Author:  Mike D
 * CollabDock - Docking window displayed during a collaboration session
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.util.TextLog;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

public class CollabDock extends DockableFrame implements DockableFrameListener, ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	private TextLog log;
	private JScrollPane logScroll;
	private JTextField userMsg;
	private JButton sendButton, optButton;
	
	public CollabDock() 
	{
		super("Collaboration", new ImageIcon());
		this.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("starts_sm.png")));
		getContext().setInitMode(DockContext.STATE_HIDDEN);
		getContext().setInitSide(DockContext.DOCK_SIDE_EAST);
		getContext().setInitIndex(0);
		setPreferredSize(new Dimension(150, 300));
		setDockedWidth(150);
		
		JPanel lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(userMsg = new JTextField(), BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(sendButton = new JButton("Send Message"));
		sendButton.addActionListener(this);
		buttonPanel.add(optButton = new JButton("Options"));
		optButton.addActionListener(this);
		lowerPanel.add(buttonPanel, BorderLayout.CENTER);
		
		getContentPane().add(logScroll = new JScrollPane(log = new TextLog()));
		getContentPane().add(lowerPanel, BorderLayout.SOUTH);
		userMsg.addKeyListener(this);
		
		javax.help.CSH.setHelpIDString(this, "Collaboration");
	}
	
	public void log(String txt)
	{
		log.append(txt, java.awt.Color.BLACK, false);
	}
	
	public void log(String txt, java.awt.Color clr, boolean bold)
	{
		log.append(txt, clr, bold);
	}
	
	//inserts a line in the log the same width as the log
	public void insertLine()
	{
		int dashWidth = log.getFontMetrics(log.getFont()).stringWidth("_");
		String line = "___________________________________________________________________________________________" +
				"___________________________________________________________________________";
		int lineWidth = (this.getDockedWidth()/dashWidth)-3;
		log(line.substring(0, (line.length()>=lineWidth)?lineWidth:line.length()));
	}
	
	public void clear()
	{
		log.setText("");
	}
	
	public void addDockListener()
	{
		addDockableFrameListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == sendButton)
		{
			//send the message in userMsg box
			CollaborationControl.getInstance().send(new CCDataPacket(CCDataPacket.LOG_INFO, userMsg.getText()));
			userMsg.setText("");
		}
		else if(event.getSource() == optButton)
		{
			new ClientOptionsDialog(MainFrm.getInstance(), CollaborationControl.getInstance().getClientOptions());
		}
	}
							
	public void keyPressed(KeyEvent event)
	{
		if(event.getSource() == userMsg && event.getKeyCode() == KeyEvent.VK_ENTER)
		{
			//send the message in userMsg box
			CollaborationControl.getInstance().send(new CCDataPacket(CCDataPacket.LOG_INFO, userMsg.getText()));
			userMsg.setText("");
		}
	}
	
	
	public void dockableFrameHidden(DockableFrameEvent arg0)
	{
		MainFrm.getInstance().getCatMenuBar().uncheckViewItem(getTitle());
	}
		
	//unused dock events
	public void dockableFrameAdded(DockableFrameEvent arg0){}
	public void dockableFrameRemoved(DockableFrameEvent arg0){}
	public void dockableFrameShown(DockableFrameEvent arg0){}
	public void dockableFrameDocked(DockableFrameEvent arg0){}
	public void dockableFrameFloating(DockableFrameEvent arg0){}
	public void dockableFrameAutohidden(DockableFrameEvent arg0){}
	public void dockableFrameAutohideShowing(DockableFrameEvent arg0){}
	public void dockableFrameActivated(DockableFrameEvent arg0){}
	public void dockableFrameDeactivated(DockableFrameEvent arg0){}
	public void dockableFrameTabShown(DockableFrameEvent arg0){}
	public void dockableFrameTabHidden(DockableFrameEvent arg0){}
	public void dockableFrameMaximized(DockableFrameEvent arg0){}
	public void dockableFrameRestored(DockableFrameEvent arg0){}
	//unused key events
	public void keyTyped(KeyEvent event){}
	public void keyReleased(KeyEvent e){}
}