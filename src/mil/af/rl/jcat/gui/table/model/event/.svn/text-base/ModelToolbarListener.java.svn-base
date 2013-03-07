package mil.af.rl.jcat.gui.table.model.event;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;


import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.dialogs.EventDialog;
import mil.af.rl.jcat.gui.dialogs.GroupDialog;
import mil.af.rl.jcat.gui.dialogs.SelectSignalDialog;
import mil.af.rl.jcat.gui.table.model.base.PTModel;
import mil.af.rl.jcat.processlibrary.Signal;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class ModelToolbarListener implements ActionListener {
	
	private PTModel tablemodel;
	private EventDialog parent;
	private static Logger logger = Logger.getLogger(ModelToolbarListener.class);
	
	
	public ModelToolbarListener(EventDialog dlg, PTModel model) {
		
		tablemodel = model;
		parent = dlg;
	}
	
	
	/**
	 
	 * Invoked when an action occurs.
	 
	 *
	 
	 * @param e ActionEvent
	 
	 * @todo Implement this java.awt.event.ActionListener method
	 
	 */
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("nsig"))
		{
			addSignal();
		}
		else if(e.getActionCommand().equals("agrp"))
		{
			addGroup();
		}
	}
	
	
	private void addSignal()
	{
		//LinkedList signalList = new LinkedList();
		TreeSet<Signal> signalList = new TreeSet<Signal>();
		signalList.addAll(parent.getLibrary().getAllSignals());
		signalList.addAll(tablemodel.getPendingSignals());
		
		new SelectSignalDialog(parent, signalList).setVisible(true);
		
		// add the signal via the model, it will do the rest
		if(parent.theNewSignal != null) //null if user closes signal box
		{
			tablemodel.addSignal(parent.theNewSignal);
		}
	}
	
	private void addGroup()
	{
		new GroupDialog(parent, tablemodel).setVisible(true);
	}
	
	
}

