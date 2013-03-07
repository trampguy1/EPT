package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.CustomFocusPolicy;
import mil.af.rl.jcat.util.Guid;


/**
 * Title: MechanismDialog.java
 * Copyright: Copyright (c) 2004
 * Company: C3I Associates
 * @author Edward Vereich
 * @version 1.0
 */
public class MechanismDialog implements ActionListener, KeyListener
{
	
	private static final long serialVersionUID = -4830521377737383616L;
	private JButton btCreateNew;
	private JButton btUsePredef;
	private JButton btCancel;
	private ButtonGroup cbgroup = new ButtonGroup();
	private JLabel mechLabel;
	private JList signalList = new JList();
	private JTextField tfMechName;
	private JCheckBox cbAsCause;
	private JCheckBox cbAsInhibit;
	private JPanel basePanel, bottomPanel, buttonPanel, infoPanel, namePanel;
	private JDialog mechDialog = null;
	
	private Event fromEvent;
	private Event toEvent;
	private AbstractPlan ptPlan;
	private Object[] signals = null;
	private Signal signal;
	private int mechanism_type;
	private boolean mechCreated = false;
	private boolean disposeNow = false;
	private Mechanism newMechanism = null;
	private Frame parent = null;
	private static Logger logger = Logger.getLogger(MechanismDialog.class);
	
	
	public boolean createMechanism(Frame frm, Event to, Event from, AbstractPlan plan)
	{
		mechDialog = new JDialog(frm);
		mechDialog.setTitle("Mechanism Type");
		mechDialog.setModal(true);
		parent = frm;
		
		try
		{
			jbInit();
			mechDialog.setSize(new Dimension(400, 300));
			mechDialog.setLocationRelativeTo(parent);
			
		}catch(Exception ex){
			logger.error("createMechanism - error initializing mechanism dialog:  "+ex.getMessage());
		}
		
		toEvent = to;
		fromEvent = from;
		ptPlan = plan;
		init();
		
		mechDialog.setVisible(true);
		
		while(!disposeNow) //prevents a crazy race condition caused by event listener threading
			try{   Thread.sleep(200);   }catch(InterruptedException exc){}
		
		return mechCreated;
	}
	
	private void jbInit() throws Exception
	{
		basePanel = new JPanel(new BorderLayout());
		bottomPanel = new JPanel(new BorderLayout());
		GridLayout buttonLayout = new GridLayout(4,1);
		buttonLayout.setVgap(3);
		buttonPanel = new JPanel(buttonLayout);
		infoPanel = new JPanel(new GridLayout(3,1));
		namePanel = new JPanel(new BorderLayout());
		basePanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.add(infoPanel, BorderLayout.CENTER);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		
		JScrollPane scpTaSignalList = new JScrollPane(signalList);
		scpTaSignalList.setBorder(BorderFactory.createTitledBorder("Available Signals"));
		basePanel.add(scpTaSignalList, BorderLayout.CENTER);
		cbAsCause = new JCheckBox("As Cause");
		cbAsCause.setPreferredSize(new Dimension(0, 20));
		cbAsCause.setSelected(true);
		cbgroup.add(cbAsCause);
		infoPanel.add(cbAsCause);
		cbAsInhibit = new JCheckBox("As Inhibitor");
		cbAsInhibit.setPreferredSize(new Dimension(0, 20));
		cbgroup.add(cbAsInhibit);
		infoPanel.add(cbAsInhibit);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("New Mechanism Info"));
		mechLabel = new JLabel("Mechanism Name:");
		namePanel.add(mechLabel, BorderLayout.WEST);
		tfMechName = new JTextField();
		tfMechName.setPreferredSize(new Dimension(0, 20));
		namePanel.add(tfMechName, BorderLayout.CENTER);
		
		infoPanel.add(namePanel);
		
		btCreateNew = new JButton("Create New");
		btCreateNew.setActionCommand("create");
		btCreateNew.addActionListener(this);
		buttonPanel.add(btCreateNew);
		
		btUsePredef = new JButton("Use Predefined");
		btUsePredef.setActionCommand("predef");
		btUsePredef.addActionListener(this);
		buttonPanel.add(btUsePredef);
		
		btCancel = new JButton("Cancel");
		btCancel.addActionListener(this);
		buttonPanel.add(btCancel);
		
		/*JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0,2,0,2));
		cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(MainFrm.getInstance().getHelpBroker()));
		buttonPanel.add(cHelpBtn);*/
		
		mechDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				mechCreated = false;
				disposeNow = true;
				mechDialog.dispose();
			}
		});
		
		mechDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		mechDialog.setContentPane(basePanel);
		
		tfMechName.addKeyListener(this);
		mechDialog.getContentPane().setFocusTraversalPolicyProvider(true);
		mechDialog.getContentPane().setFocusTraversalPolicy(new CustomFocusPolicy(tfMechName));
	}
	
	private void init()
	{
//		try
//		{
			TreeSet<Signal> sortedSigs = new TreeSet<Signal>(ptPlan.getLibrary().getAllSignals());
			signals = sortedSigs.toArray();
			signalList.setListData(signals);
			if(signals.length < 1)
				btUsePredef.setEnabled(false);
			
//		}catch(Exception e){
//			if (signals == null)
//				btUsePredef.setEnabled(false);
//		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(!validateType())
		{
			JOptionPane.showMessageDialog(parent, "Select CAUSE or INHIBITOR.");
			return;
		}
		mechanism_type = SignalType.CAUSAL; // default
		// CREATE A NEW SIGNAL
		if(e.getActionCommand().equals("create"))
		{
			String name = tfMechName.getText().trim();
			if(name.length() < 1)
			{
				JOptionPane.showMessageDialog(parent, "Name cannot be empty.");
				return;
			}
			if(ptPlan.getLibrary().signalNameExists(name))
			{
				JOptionPane.showMessageDialog(parent, "Signal names must be uniuqe");
				return;
			}
			if(cbAsInhibit.isSelected())
				mechanism_type = SignalType.INHIBITING;
			
			signal = new Signal(new Guid(), name);
			
			boolean loopWillResult = ptPlan.edgeWillMakeLoop(fromEvent, toEvent);
			int res = JOptionPane.YES_OPTION;
			if(loopWillResult)
				res = JOptionPane.showConfirmDialog(parent, "Creating this mechanism will create a causal loop. \nAre you sure you wish to do this?", "Loop Detected", JOptionPane.YES_NO_OPTION);
			
			if(res == JOptionPane.YES_OPTION)
			{
				newMechanism = new Mechanism(new Guid(), name, toEvent, fromEvent, signal.getSignalID());
				newMechanism.setLoopCloser(loopWillResult);
				mechCreated = true;
			}
			
			disposeNow = true;
			mechDialog.dispose();
		}
		else if (e.getActionCommand().equals("predef")) // USE PREDIFINED SIGNAL
		{
			if (signalList.getSelectedIndex() == -1)
			{
				JOptionPane.showMessageDialog(parent, "Please select a predefined signal from the list.");
				return;
			}
			signal = (Signal) signalList.getSelectedValue();
			
			boolean loopWillResult = ptPlan.edgeWillMakeLoop(fromEvent, toEvent);
			int res = JOptionPane.YES_OPTION;
			if(loopWillResult)
				res = JOptionPane.showConfirmDialog(parent, "Creating this mechanism will create a causal loop. \nAre you sure you wish to do this?", "Loop Detected", JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.YES_OPTION)
			{
				if(cbAsInhibit.isSelected())
					mechanism_type = SignalType.INHIBITING;
				
//				try{
                    //Set the mech created flag because i dont know if its read but i want this to work -CM
                    //Check and make a consolidator
                    if(fromEvent.containsSignal(signal.getSignalID(), SignalType.EFFECT, ptPlan))
                    {
                        newMechanism = fromEvent.getMechanismFromSignalID(signal.getSignalID(), ptPlan);
                        newMechanism.addConsolidatedOutput(toEvent);
                        mechCreated = true;
                        logger.debug("actionPerformed(predef) - consolidator: existing mech used");
                    }                        
                    else
                    {
                        newMechanism = new Mechanism(new Guid(), signal.getSignalName(), toEvent, fromEvent, signal.getSignalID());
    					newMechanism.setLoopCloser(loopWillResult);
    					mechCreated = true;
                    }
					
//				}catch(SignalModeConflictException se) //this might only happen with addConsolidatedOutput
//				{
//					String message = "Mechanism " + se.getSignalName() +" could not be created.\n"
//					+ se.getMessage();
//					mechCreated = false;
//					JOptionPane.showMessageDialog(parent, message);
//				}
			}
			
			disposeNow = true;
			mechDialog.dispose();
		}
		else if (e.getActionCommand().equals("Cancel"))
		{
			//cancelMechanismCreation();
			mechCreated = false;
			disposeNow = true;
			mechDialog.dispose();
		}
	}
    
	
	public Signal getSignal()
	{
		return signal;
	}
	
	public Mechanism getMechanism()
	{
		return newMechanism;
	}
	
	public int getType()
	{
		return mechanism_type;
	}
	
	private boolean validateType()
	{
		if (!cbAsCause.isSelected() && !cbAsInhibit.isSelected())
			return false;
		else
			return true;
	}
	
	public void keyPressed(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.VK_ENTER)
			btCreateNew.doClick();
	}
	
	
	//unused key events
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
}
