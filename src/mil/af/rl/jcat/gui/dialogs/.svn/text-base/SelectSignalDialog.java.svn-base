package mil.af.rl.jcat.gui.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.util.CustomFocusPolicy;
import mil.af.rl.jcat.util.Guid;

public class SelectSignalDialog extends JDialog implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2736289305612282208L;
	private static Logger logger = Logger.getLogger(SelectSignalDialog.class);
	private JScrollPane sigScrollPane = null;
	private JPanel sigInfoPanel = new JPanel();
	private JLabel sigTextFieldLabel = new JLabel();
	private JTextField sigNameField = new JTextField();
	private JButton btnCreateNew = new JButton();
	private JButton btnUsePredef = new JButton();
	private GridBagLayout layout1 = new GridBagLayout();
	private GridBagLayout layout2 = new GridBagLayout();

	private JList signalDisplayList = new JList();
	private Vector<Signal> signals;
	private EventDialog parent ;
	

	public SelectSignalDialog(EventDialog prnt, java.util.Set<Signal> signals)
	{
		super(prnt);
		setModal(true);
		signalDisplayList.setListData(this.signals = new Vector<Signal>(signals));
		parent = prnt ;
		parent.theNewSignal = null ;

		try
		{
			init();
			setSize(350, 300);
			setLocationRelativeTo(prnt);
		}
		catch (Exception e)
		{
			logger.error("Constructor - Error initializing dialog: ", e);
		}

	}

	private void init()
	{
		this.getContentPane().setLayout(layout1);
		this.setTitle("Add Signal");
		sigScrollPane = new JScrollPane(signalDisplayList);

		sigInfoPanel.setBorder(BorderFactory.createTitledBorder("New Signal Information"));
		sigInfoPanel.setLayout(layout2);

		sigTextFieldLabel.setText("Signal Name:");

		sigNameField.setText("");
		sigNameField.setPreferredSize(new Dimension(200,20));

		btnCreateNew.setText("Create New");
		btnCreateNew.setPreferredSize(new Dimension(105,23));
		btnCreateNew.addActionListener(this);

		btnUsePredef.setText("Use Predefined");
		btnUsePredef.setPreferredSize(new Dimension(105,23));
		btnUsePredef.addActionListener(this);

		sigInfoPanel.add(sigTextFieldLabel,new GridBagConstraints(0,0,1,1,0,0,
				GridBagConstraints.CENTER,GridBagConstraints.NONE,
				new Insets(0,0,0,0),0,0));

		sigInfoPanel.add(sigNameField,new GridBagConstraints(1,0,1,1,0,0,
				GridBagConstraints.CENTER,GridBagConstraints.NONE,
				new Insets(0,0,0,0),0,0));

		this.getContentPane().add(sigScrollPane,new GridBagConstraints(0,0,20,14,1,1,
				GridBagConstraints.NORTH,GridBagConstraints.BOTH,
				new Insets(0,0,0,0),0,0));

		this.getContentPane().add(sigInfoPanel,new GridBagConstraints(0,14,20,1,0,0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
				new Insets(0,0,0,0),0,0));

		this.getContentPane().add(btnCreateNew,new GridBagConstraints(0,15,20,1,0,0,
				GridBagConstraints.NORTH,GridBagConstraints.NONE,
				new Insets(0,0,0,110),0,0));
		this.getContentPane().add(btnUsePredef,new GridBagConstraints(0,15,20,1,0,0,
				GridBagConstraints.NORTH,GridBagConstraints.NONE,
				new Insets(0,110,0,0),0,0));

		sigNameField.addKeyListener(this);
		this.getContentPane().setFocusTraversalPolicyProvider(true);
		this.getContentPane().setFocusTraversalPolicy(new CustomFocusPolicy(sigNameField));
	}

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("Create New"))
		{
			String name = sigNameField.getText();

			if(listContainsName(name) || name.trim().length() < 1)
			{
				JOptionPane.showMessageDialog(this, "Name cannot be empty, or already exist.");
				return;
			}

			parent.theNewSignal =  new Signal(new Guid(),name);
			dispose();
		}
		else if(e.getActionCommand().equals("Use Predefined"))
		{
			if (signalDisplayList.getSelectedIndex() == -1)
			{
				JOptionPane.showMessageDialog(this, "Select a predefined signal please.");
				return;
			}
			parent.theNewSignal = (Signal) signalDisplayList.getSelectedValue();
			dispose();
		}
	}
	
	private boolean listContainsName(String name)
	{
		for(Signal s : signals)
			if(s.getSignalName().equals(name))
				return true;
		
		return false;
	}

	public void keyPressed(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.VK_ENTER)
			btnCreateNew.doClick();
	}

	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
}
