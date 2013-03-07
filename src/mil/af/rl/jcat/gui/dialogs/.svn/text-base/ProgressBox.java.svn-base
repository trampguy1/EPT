/*
 * Created on Jul 26, 2005
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBox extends JDialog
{

	private static final long serialVersionUID = 1L;
	JLabel fileLbl, groupLbl, funcLbl;
	JProgressBar progress;
	private JPanel mainPanel;

	public ProgressBox(java.awt.Dialog parent, String funcName, String groupName, int max)
	{
		super(parent);
		
		setUndecorated(true);
		setSize(300,100);
		//setLocationRelativeTo(parent);
		setLocation(parent.getLocation().x+(parent.getSize().width/2)-150, parent.getLocation().y+100);
		setContentPane(mainPanel = new JPanel());
		mainPanel.setLayout(new mil.af.rl.jcat.gui.dialogs.XYLayout());
		mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		
		Font theFont = new Font("Arial", 0, 14);
		fileLbl = new JLabel();   fileLbl.setFont(theFont.deriveFont(12f));   //fileLbl.setForeground(Color.WHITE);
		groupLbl = new JLabel(groupName);   groupLbl.setFont(theFont);   //groupLbl.setForeground(Color.WHITE);
		//groupLbl.setHorizontalAlignment(JLabel.RIGHT);
		funcLbl = new JLabel(funcName);   funcLbl.setFont(theFont.deriveFont(1));   //funcLbl.setForeground(Color.WHITE);   
		progress = new JProgressBar(0, max);
		
		//getContentPane().add(funcLbl, new mil.af.rl.jcat.gui.dialogs.XYConstraints(5,5,0,0));
		mainPanel.add(groupLbl, new mil.af.rl.jcat.gui.dialogs.XYConstraints(5,25,230,0));
		//getContentPane().add(fileLbl, new mil.af.rl.jcat.gui.dialogs.XYConstraints(5,25,210,0));
		mainPanel.add(progress, new mil.af.rl.jcat.gui.dialogs.XYConstraints(5,70,290,25));
	}
	
//	public ProgressBox(JFrame parent)
//	{
//		this(parent, "", "", 0);
//	}
	
	public void setProgressMax(int max)
	{
		progress.setMaximum(max);
	}
	
	public void setProgress(int x)
	{
		progress.setValue(x);
	}
	
	public void setFileName(String name)
	{
		fileLbl.setText(name);
	}
	
	public void setGroupName(String name)
	{
		groupLbl.setText(name);
	}
	
	public void setFuncName(String name)
	{
		funcLbl.setText(name);
	}

	public void addToProgress(int i)
	{
		progress.setValue(progress.getValue()+i);
		if(progress.getValue() >= progress.getMaximum())
			progress.setValue(progress.getValue()-2); //temporary hack
	}
	
	public int getProgress()
	{
		return progress.getValue();
	}
	
	public void complete()
	{
		dispose();
	}
}
