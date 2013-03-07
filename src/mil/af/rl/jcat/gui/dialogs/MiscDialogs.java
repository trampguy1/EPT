package mil.af.rl.jcat.gui.dialogs;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.util.MultiMap;



public class MiscDialogs
{
	private static JDialog currentDialog = null;
	private static boolean boxAccepted = false;

	
	// was trying to make this a more dynamic/universal input box, din't finish that concept
	// use this class to put misc simple dialogs that don't really warrant an exclusive class
	// sort of like a JOptionPane class with custom dialogs
	public static boolean showCOAOptionsBox(Frame parent, AbstractPlan plan, JComponent name, JComponent track, JComponent untracked)
	{
		InputVerifier inputVerify = new InputVerifier();
		InternalListener listener = new InternalListener(inputVerify);
		currentDialog = new JDialog(parent, "New COA");
		currentDialog.setSize(320, 200);
		currentDialog.setModal(true);
		currentDialog.setLocationRelativeTo(parent);
		currentDialog.setResizable(false);
		Container pane = currentDialog.getContentPane();
		pane.setLayout(null);
		boxAccepted = false;
		
		JLabel untrackLbl = new JLabel("When applying this COA, for untracked items:");
		untrackLbl.setBounds(10, 90, 300, 21);
		JLabel trackLbl = new JLabel("Tracking:");
		trackLbl.setBounds(10, 50, 68, 21);
//		trackLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel nmLbl = new JLabel("Name:");
		nmLbl.setBounds(10, 20, 48, 21);
//		nmLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		JComponent nameBox = name; 
		nameBox.setBounds(80, 20, 201, 21);
		inputVerify.assertNotBlank((JTextComponent)nameBox, "Please enter a name for this COA.");
		java.util.Vector<String> coaNames = new java.util.Vector<String>();
		for(COA c : plan.getCOAList())
			coaNames.add(c.getName());
		inputVerify.assertNotDuplicate((JTextComponent)nameBox, coaNames, "A COA with the specified name already exists, please use another name");
		JComponent trackingBox = track;
		trackingBox.setBounds(80, 45, 201, 50);
		JComponent untrackBox = untracked;
		untrackBox.setBounds(20, 110, 265, 21);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(listener);
		okButton.setBounds(60, 140, 81, 23);
		JButton cancButton = new JButton("Cancel");
		cancButton.addActionListener(listener);
		cancButton.setBounds(170, 140, 81, 23);
		
		pane.add(nmLbl);
		pane.add(nameBox);
		pane.add(trackingBox);
		pane.add(trackLbl);
		pane.add(untrackLbl);
		pane.add(untrackBox);
		pane.add(okButton);
		pane.add(cancButton);
		
		currentDialog.setVisible(true);
		return boxAccepted;
	}
	
	private static class InputVerifier
	{
		private java.util.ArrayList<JTextComponent> notBlank = new java.util.ArrayList<JTextComponent>(); //use err msg 0
		private Hashtable<JTextComponent, List<String>> notDuplicate = new Hashtable<JTextComponent, List<String>>(); //use err msg 1
		private Hashtable<Object, String[]> messages = new Hashtable<Object, String[]>();
		private String lastMsg = "";
		private boolean verified = false;
		
		public void assertNotBlank(JTextComponent comp, String failMsg)
		{
			notBlank.add(comp);
			if(messages.get(comp) == null)
				messages.put(comp, new String[2]); //2 diff types avail now: not blank and not duplicate
			messages.get(comp)[0] = failMsg;
		}
		
		public void assertNotDuplicate(JTextComponent comp, java.util.List<String> list, String failMsg)
		{
			notDuplicate.put(comp, list);
			if(messages.get(comp) == null)
				messages.put(comp, new String[2]);
			messages.get(comp)[1] = failMsg;
		}
		
		public boolean verify()
		{
			for(JTextComponent obj : notBlank)
			{
				JTextComponent txtComp = (JTextComponent)obj;
				if(txtComp.getText().equals(""))
				{
					lastMsg = messages.get(obj)[0];
					return false;
				}
			}
			
			for(JTextComponent obj : notDuplicate.keySet())
			{
				JTextComponent txtComp = (JTextComponent)obj;
				for(String s : notDuplicate.get(txtComp))
				{
					if(txtComp.getText().equalsIgnoreCase(s))
					{
						lastMsg = messages.get(obj)[1];
						return false;
					}
				}
			}
			
			verified = true;
			return true;
		}
		
		public String getMessage(Object obj)
		{
			if(obj == null)
				return lastMsg;
			else
				return (verified) ? "" : "Invalid input specified";
		}
		
	}
	
	private static class InternalListener implements ActionListener
	{
		InputVerifier iv = null;
		
		public InternalListener(){ }
		
		public InternalListener(InputVerifier iv)
		{
			this.iv = iv;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("OK"))
			{
				if(iv == null || iv.verify())
				{
					boxAccepted = true;
					currentDialog.dispose();
				}
				else
					JOptionPane.showMessageDialog(currentDialog, iv.getMessage(null));
			}
			else if(e.getActionCommand().equals("Cancel"))
				currentDialog.dispose();
		}
		
	}
}
