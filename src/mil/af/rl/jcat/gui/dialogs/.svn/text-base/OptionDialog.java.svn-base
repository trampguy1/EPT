package mil.af.rl.jcat.gui.dialogs;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JDialog;


public class OptionDialog extends JDialog
{
	private boolean accepted = false;
	
	
	public OptionDialog() throws HeadlessException
	{
		super();
	}


	public OptionDialog(Dialog owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}


	public OptionDialog(Dialog owner, String title) throws HeadlessException
	{
		super(owner, title);
	}


	public OptionDialog(Dialog owner) throws HeadlessException
	{
		super(owner);
	}


	public OptionDialog(Frame owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}


	public OptionDialog(Frame owner, String title) throws HeadlessException
	{
		super(owner, title);
	}


	public OptionDialog(Frame owner) throws HeadlessException
	{
		super(owner);
	}
	
	
	public void setAccepted(boolean accept)
	{
		accepted = accept;
	}
	
	public boolean isAccepted()
	{
		return accepted;
	}

}
