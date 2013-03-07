/*
 * Created on Jan 23, 2006
 * Author:  Mike D
 * AnnouncmentWindow - Simple tooltip style window for showing server announcments
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JWindow;
import javax.swing.Timer;

public class AnnouncmentWindow extends JWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	Timer disposer = new Timer(3000, this);
	
	public AnnouncmentWindow(Frame parent, String msg, int x, int y)
	{
		super(parent);
		
		getContentPane().setBackground(new Color(255,242,140));
		getContentPane().add(new javax.swing.JLabel(msg));
		disposer.start();
		pack();
		
		setLocation(x+10, y-this.getSize().height);
		
		if(parent.getState() != Frame.ICONIFIED) //don't pop up these things if the parent isn't even showing (minimized)
			setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == disposer)
		{
			disposer.stop();
			disposer = null;
			dispose();
		}
	}

}
