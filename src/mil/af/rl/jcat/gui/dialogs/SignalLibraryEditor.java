package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.JColoredIndexList;



public class SignalLibraryEditor extends JDialog implements ActionListener, ListSelectionListener
{
	private Library library;
	private Vector<Signal> signals;
	private JButton delButton;
	private JButton closeButton;
	private JColoredIndexList signalList;
	private AbstractPlan plan;
	private JLabel occurCount;
	private JLabel causeLbl;
	private JLabel inhibLbl;
	private JLabel effectLbl;
	private Frame parent;
	private JButton cleanButton;
	private static Logger logger = Logger.getLogger(SignalLibraryEditor.class);


	public SignalLibraryEditor(Frame parent, AbstractPlan thePlan)
	{
		super(parent, "Edit Signal Library");
		library = thePlan.getLibrary();
		plan = thePlan;
		this.parent = parent;
		
		TreeSet<Signal> sortedSigs = new TreeSet<Signal>(library.getAllSignals());
		signals = new Vector<Signal>(sortedSigs);
		
		init();
		colorizeList();
		
		pack();
		setLocationRelativeTo(parent);
		
		setVisible(true);
	}
	
	private void init()
	{
		JPanel basePanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new BorderLayout());
		GridLayout buttonLayout = new GridLayout(3,1);
		buttonLayout.setVgap(3);
		JPanel buttonPanel = new JPanel(buttonLayout);
		JPanel infoPanel = new JPanel(new GridLayout(4,2));
//		JPanel namePanel = new JPanel(new BorderLayout());
		basePanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.add(infoPanel, BorderLayout.CENTER);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		
		signalList = new JColoredIndexList();
		signalList.setColoredForeground(new Color(170, 0, 0));
		signalList.addListSelectionListener(this);
		signalList.setListData(signals);
		JScrollPane signalListScrl = new JScrollPane(signalList);
		signalListScrl.setPreferredSize(new Dimension(300, 300));
		signalListScrl.setBorder(BorderFactory.createTitledBorder("Available Signals"));
		basePanel.add(signalListScrl, BorderLayout.CENTER);
		
		JLabel rLbl = new JLabel("Red = unused in model");
		rLbl.setForeground(new Color(170,0,0));
		bottomPanel.add(rLbl, BorderLayout.NORTH);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("Signal Info:"));
//		namePanel.add(tfMechName, BorderLayout.CENTER);
		
		infoPanel.add(new JLabel("Occurrances in model:  "));
		infoPanel.add(occurCount = new JLabel());
		infoPanel.add(new JLabel("Used as a cause:  "));
		infoPanel.add(causeLbl = new JLabel());
		infoPanel.add(new JLabel("Used as an inhibitor:  "));
		infoPanel.add(inhibLbl = new JLabel());
		infoPanel.add(new JLabel("Used as an effect:  "));
		infoPanel.add(effectLbl = new JLabel());
		
		delButton = new JButton("Delete");
		delButton.addActionListener(this);
		buttonPanel.add(delButton);
//		
//		btUsePredef = new JButton("Use Predefined");
//		btUsePredef.setActionCommand("predef");
//		btUsePredef.addActionListener(this);
//		buttonPanel.add(btUsePredef);
		cleanButton = new JButton("Clean Up");
		cleanButton.addActionListener(this);
		buttonPanel.add(cleanButton);
//		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		
		/*JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0,2,0,2));
		cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(MainFrm.getInstance().getHelpBroker()));
		buttonPanel.add(cHelpBtn);*/
		
//		mechDialog.addWindowListener(new java.awt.event.WindowAdapter() {
//			public void windowClosing(java.awt.event.WindowEvent e) {
//				mechCreated = false;
//				disposeNow = true;
//				mechDialog.dispose();
//			}
//		});
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setContentPane(basePanel);
		
//		tfMechName.addKeyListener(this);
//		mechDialog.getContentPane().setFocusTraversalPolicyProvider(true);
//		mechDialog.getContentPane().setFocusTraversalPolicy(new CustomFocusPolicy(tfMechName));
	}
	
	private void colorizeList()
	{
		Vector<Integer> indexList = new Vector<Integer>();
		
		for(Signal sig : signals)
		{
			if(countOccurances(sig)[0] < 1)
				indexList.add(signals.indexOf(sig)); //color this item in the list dark red
		}
		
		signalList.setColoredIndices(toIntArray(indexList));
	}
	
	private int[] toIntArray(Vector<Integer> indexList)
	{
		int[] ints = new int[indexList.size()];
		
		for(int x=0; x<indexList.size(); x++)
			ints[x] = indexList.get(x);
		
		return ints;
	}

	private void updateSignalInfo(Signal signal)
	{
		//count occurances of this signal in the plan
		int[] result = countOccurances(signal);
		
		
		occurCount.setText(result[0]+"");
		causeLbl.setText((result[1] == 1) ? "yes" : "no");		
		inhibLbl.setText((result[2] == 1) ? "yes" : "no");
		effectLbl.setText((result[3] == 1) ? "yes" : "no");
	}
	
	private int[] countOccurances(Signal sig)
	{
		int[] result = new int[4];
		
		for(Event thisEvent : plan.getAllEvents())
		{
			if(thisEvent.containsSignal(sig.getSignalID(), SignalType.CAUSAL, plan))
			{
				result[1] = 1;
				result[0]++;
			}
			if(thisEvent.containsSignal(sig.getSignalID(), SignalType.INHIBITING, plan))
			{
				result[2] = 1;
				result[0]++;
			}
			if(thisEvent.containsSignal(sig.getSignalID(), SignalType.EFFECT, plan))
			{
				result[3] = 1;
				//result[0]++;
			}
		}
		
		return result;
	}
	

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == closeButton)
			dispose();
		else if(e.getSource() == delButton)
		{
			int index = signalList.getSelectedIndex();
			if(index >= 0 && index < signals.size())
			{
				Vector<Signal> sigs = new Vector<Signal>();
				sigs.add(signals.get(index));
				deleteSignal(sigs);
			}
		}
		else if(e.getSource() == cleanButton)
		{
			if(JOptionPane.showConfirmDialog(parent, "Are you sure you would like to delete all unused signals from the library?", "Clean Up", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				Vector<Signal> sigs = new Vector<Signal>();
				int[] indices = signalList.getColoredIndices();
				for(int x : indices)
					sigs.add(signals.get(x));
				deleteSignal(sigs);
			}
		}
	}

	private void deleteSignal(List<Signal> delSignals)
	{
		if(delSignals.isEmpty())
			return;
		
		JWBController jwbCont = Control.getInstance().getController(plan.getId());
		
		for(Signal sig : delSignals)
		{
			// if signal is used at all, warn user, remove it from all necessary processes
			// then remove the shape (which will do the planitem removals)
			if(countOccurances(sig)[0] > 0)
			{
				String msg = "This signal is currently used in your model.  If you choose to delete it, \n" +
						"the model will be modified in order to eliminate this signal.  This may \n" +
						"include deleting mechanisms and modifying events. \n\n" +
						"Are you sure you would like to completely delete this signal?";
				if(sig.equals(delSignals.get(0)) && JOptionPane.showConfirmDialog(parent, msg, "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return;
				
				// for all the mechanisms that use this signal, remove the shape (which will take care of plan item stuff)
				for(Mechanism mech : plan.getAllMechanisms())
				{
					if(mech.getSignalGuid().equals(sig.getSignalID()))
					{
						try{
							jwbCont.removeShapes(new ArrayList(plan.getShapeMapping(mech.getGuid())));
						}catch(RemoteException exc){
							logger.error("deleteSignal - RemoteException removing shapes: ", exc);
						}
					}
				}
			}
			else if(delSignals.size() == 1 && JOptionPane.showConfirmDialog(parent, "Are you sure you would like to completely delete this signal?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;
			
			ArrayList<Guid> updSigs = new ArrayList<Guid>();
			updSigs.add(sig.getSignalID());
			// for all the processes that contain this signal, remove the signal
			for(Process proc : library.getAllProcesses())
			{
				if(proc.getSignalData().containsSignal(sig.getSignalID()))
				{
					try{
						jwbCont.foreignUpdate(new RemSignalArg(RemSignalArg.REMOVE, updSigs, proc.getProcessID()));
					}catch(RemoteException exc){
						logger.error("deleteSignal - RemoteException signal from processes: ", exc);
					}
				}
			}
			
			// if signal is unused then simply remove it from the library
			try{
				jwbCont.foreignUpdate(new RemSignalArg(RemSignalArg.REMOVE, sig));
			}catch(RemoteException exc){
				logger.error("deleteSignal - RemoteException removing signal from library: ", exc);
			}
			
			// if all succeeds, remove from the actual list
			signals.remove(sig);
			colorizeList();
			signalList.updateUI();
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if(!e.getValueIsAdjusting() && e.getSource() == signalList)
		{
			updateSignalInfo(signals.get(signalList.getSelectedIndex()));
		}
	}

}
