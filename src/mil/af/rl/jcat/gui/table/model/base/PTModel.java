package mil.af.rl.jcat.gui.table.model.base;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.dialogs.GroupDialog;
import mil.af.rl.jcat.gui.table.CellEditorModel;
import mil.af.rl.jcat.gui.table.model.CheckBoxRenderer;
import mil.af.rl.jcat.gui.table.model.JComboEditor;
import mil.af.rl.jcat.gui.table.model.JComboRenderer;
import mil.af.rl.jcat.gui.table.model.SpinnerEditor;
import mil.af.rl.jcat.gui.table.model.SpinnerRenderer;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.ProcessUtil;
import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.plan.*;
/**
 * <p>Title: PTModel.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Edward Verenich
 * @version 1.0
 */


public class PTModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private Object[][] data;
	private Object[] columns;
	private CellEditorModel cellmodel = new CellEditorModel();
	//static so all PTModels can see what pending arguments there are
	private static ArrayList<RemSignalArg> pendingArgs = new ArrayList<RemSignalArg>();
	private int protocol;
	public int mode;
	private Element modeset;
	private Hashtable<String, JPGroup> groups;
	private Hashtable signals; // contains PTSignal wrappers
	private Hashtable singles;
	private Hashtable smap = new Hashtable();
	private float defprob;  // default probability value
	private AbstractPlan plan = null;
	private Event pevent;
	private static Logger logger = Logger.getLogger(PTModel.class);
	
	
	public PTModel(Element mset, int tmode, int tprotocol, AbstractPlan plan, Event parentevent)
	{
		
		pevent = parentevent;
		modeset = mset;
		mode = tmode;
		protocol = tprotocol;
		this.plan = plan;
		
		pendingArgs.clear();
		initdata();
		buildTable();
	}
	
	
	public void setValueAt(Object value,int row, int col)
	{
		if(col == 0 && row < signals.size())
		{
			PTSignal psignal = (PTSignal)data[row][col];
			// rename, invert or remove
			if(value.toString().equals("rename"))
			{
				// prompt for the name
				String newname = JOptionPane.showInputDialog(mil.af.rl.jcat.gui.MainFrm.getInstance(), "Enter new signal name:", psignal);
				if(newname != null)
				{
					// change in the signal wrapper for the table, renames the underlying signal locally too
					psignal.setSignalName(newname);
						
					//have control rename the signal
					pendingArgs.add(new RemSignalArg(RemSignalArg.RENAME, psignal.getSignalGuid(), newname));
					
					this.fireTableCellUpdated(row,col);
				}
				else
				{
					return;
				}
			}
			else if(value.toString().equals("invert"))
			{
				if(psignal.isInverse())
				{
					psignal.setInverse(false);
					pendingArgs.add(new RemSignalArg(RemSignalArg.INVERT, psignal.getSignal(), pevent.getProcessGuid(), false));
				}
				else
				{
					psignal.setInverse(true);
					pendingArgs.add(new RemSignalArg(RemSignalArg.INVERT, psignal.getSignal(), pevent.getProcessGuid(), true));
				}
				this.fireTableCellUpdated(row, col);
			}
			else if(value.toString().equals("remove"))
			{
				// check to see if its connected with a mechanism, dont allow deletion if it is
				if(psignal.isUsedByMechanism())
					JOptionPane.showMessageDialog(MainFrm.getInstance(), "This signal is connected in your model using a mechanism. \nDelete the mechanism in the model before removing this signal.");
				// check if the signal is part of any group
				else if(isGroupMember(psignal))
				{
					int res = JOptionPane.showConfirmDialog(MainFrm.getInstance(), "This signal is part of one or more groups. \nRemoving it will also modify certain groups. \n" +
							"Are you sure you wish to delete this signal?", "Delete", JOptionPane.YES_NO_OPTION);
					if(res == JOptionPane.YES_OPTION)
						removeSignal(psignal);
				}
				else
				{
					int res = JOptionPane.showConfirmDialog(MainFrm.getInstance(), "Are you sure you wish to delete the signal below? \n"+psignal, "Delete", JOptionPane.YES_NO_OPTION);
					if(res == JOptionPane.YES_OPTION)
						removeSignal(psignal);
					else
						return;
				}
			}
		}
		else if(row < signals.size() && col == 1)
		{
			PTSignal ptSig = ((PTSignal)data[row][0]);
			MaskedFloat nvalue = (MaskedFloat)value;
			
			if(data[row][col] != nvalue) //if value has changed
				ptSig.clearDefault();
			
			singles.put(ptSig.getSignalGuid(), new Float(nvalue.floatValue()));
			data[row][col] = nvalue;
			ptSig.setProbability(nvalue.floatValue());
		}
		else if(row == signals.size() && col > 1)
		{
			MaskedFloat nvalue = (MaskedFloat)value;
			data[row][col] = nvalue;
			((JPGroup)columns[col]).setProbability(nvalue.floatValue());
		}else if(row < signals.size() && col > 1)
		{
			if(value.toString().equals("remgroup"))
			{
				JPGroup g = (JPGroup) columns[col];
				int r = JOptionPane.showConfirmDialog(mil.af.rl.jcat.gui.MainFrm.getInstance(),
						"Are you sure you want to delete group - "+g.getName(),
						"Delete group",
						JOptionPane.YES_NO_OPTION);
				if(r == JOptionPane.YES_OPTION)
				{
					removeGroup(g);
				}
			}
			else if(value.toString().equals("edtgroup"))
			{
				JPGroup g = (JPGroup) columns[col];
								
				//show group dialog for editing
				new GroupDialog(MainFrm.getInstance(), this, g).setVisible(true);
			}
		}
		
	}
	
	
	public String getColumnName(int col){return columns[col].toString();}
	
	public boolean isCellEditable(int row, int col){
		
		
		
		if(col == 0)
			
		{
			
			return false;
			
		}else if(mode == SignalType.EFFECT && col == 1)
			
		{
			return false;
			
		}else if(col > 1 && row < signals.size())
		{
			return false;
		}
		
		return true;
		
	}
	
	public Object getValueAt(int row, int col) {return data[row][col];}
	
	public int getRowCount(){return data.length;}
	
	public int getColumnCount()
	{
		return columns.length;
		//return data[0].length;
	}
	
	
	public void setDefaultProbability(float dp)
	{
		this.defprob = dp;
	}
	
	public Hashtable returnGroups()
	{
		Hashtable groups = new Hashtable<String, JPGroup>();
		// find all elicited probabilities
		List epset = modeset.selectNodes(".//ProtocolSet[@protocol='"+protocol+"']/ElicitationSet/ElicitedProbability");
		Iterator pi = epset.iterator();
		for(int x=1;pi.hasNext();)
		{
			Element e = (Element)pi.next();
			List slist = e.selectNodes(".//Signal");
			if(slist != null && slist.size() > 1)
			{
				float prob = Float.parseFloat(e.attributeValue("causalprobability"));
				String grpName = e.attributeValue("group-name");
				
				JPGroup group = new JPGroup((grpName.equals("")) ? "grp "+x : grpName, prob, pevent);
				
				Iterator si = slist.iterator();
				
				for(;si.hasNext();)
				{
					Guid guid = new Guid( ((Element)si.next()).attributeValue("guid"));
					group.addSignal(guid, plan.getLibrary());
				}

				// now add the group to the groups map
				groups.put(group.getName(), group);
				
				x++;
			}
		}
		return groups;
	}
	
	public void resetDefaultValues(float prob)
	{
		defprob = prob;
		Iterator si = signals.values().iterator();
		for(;si.hasNext();)
		{
			PTSignal s = (PTSignal)si.next();
			if(s.isDefault())
			{
				s.setProbability(defprob);
			}
		}
		//System.out.println("fireTableDataChanged()");
		//this.refresh();
		refreshIgnoreNextClearDef();
		fireTableDataChanged();
		
	}
	
	public Hashtable returnSingleProbabilities()
	{
		Hashtable singles = new Hashtable();
		// find all elicited probabilities
		List epset = modeset.selectNodes(".//ProtocolSet[@protocol='"+protocol+"']/ElicitationSet/ElicitedProbability");
		Iterator pi = epset.iterator();
		for(int x=1;pi.hasNext();x++)
			
		{
			Element e = (Element)pi.next();
			List slist = e.selectNodes(".//Signal");
			if(slist != null && slist.size() == 1)
			{
				float prob = Float.parseFloat(e.attributeValue("causalprobability"));
				Element es = (Element)slist.get(0); // will always be the first element
				singles.put(new Guid(es.attributeValue("guid")),new Float(prob));
			}
		}
		return singles;
	}
	
	public Hashtable returnSignals()
	{
		List inversions = new LinkedList();
		Hashtable sigs = new Hashtable();
		// first get a subset of inversions, so we can distinguish inversions
		// note that inversions are universal to a ModeSet, also SignalSets for
		// different protocols are disjoint, thus we must make sure that the same
		// signal does not appear in the same protocol.
		List invlist = modeset.selectNodes(".//Inversions/SignalSet/Signal");
		// we're just going to get a collection of guids in the inversions set
		if(invlist != null && invlist.size() > 0)
			
		{
			Iterator ii = invlist.iterator();
			for(;ii.hasNext();)
			{
				Element s = (Element)ii.next();
				inversions.add(new Guid(s.attributeValue("guid")));
			}
		}
		// now populate the signals using the PTSignal wrapper and check
		// if the signal is contained in the inversion set, if so flag it
		List slist = modeset.selectNodes(".//ProtocolSet[@protocol='"+protocol+"']/SignalSet/Signal");
		if(slist != null && slist.size() > 0)
		{
			Iterator si = slist.iterator();
			for(;si.hasNext();)
			{
				Element e = (Element)si.next();
				Guid guid = new Guid(e.attributeValue("guid"));
				Signal sig = plan.getLibrary().getSignal(guid);
				
				PTSignal ptsig = null;
				if(inversions.contains(guid))
				{
					// signal is inverted, set the flag
					ptsig = new PTSignal(sig,true);
				}else{
					ptsig = new PTSignal(sig,false);
				}
				ptsig.setProbability(defprob);
				if(pevent.containsSignal(guid,this.mode,plan))
				{
					ptsig.setUsedByMechanism(true);
				}
				sigs.put(guid,ptsig);
			}
		}
		return sigs;
	}
	
	public TableCellRenderer getCellRenderer(int row, int col)
	{
		return cellmodel.getRenderer(row,col);
	}
	
	public TableCellEditor getCellEditor(int row, int col)
	{
		return cellmodel.getEditor(row,col);
	}
	
	public Event getEvent()
	{
		return pevent;
	}
	
	/**
	 * Method used to persist elicited probabilities specified in this model.
	 * 10.27.2005 ev
	 */
	public void persistElicitedProbabilities()
	{
		//first apply any pending remote arguments
		try{
			for(RemSignalArg arg : pendingArgs)
				Control.getInstance().getController(plan.getId()).foreignUpdate(arg);
			pendingArgs.clear();
			
		}catch(java.rmi.RemoteException e)
		{
			logger.error("persistElicitedProbabilities - RemoteExc applying pending arguments:  "+e.getMessage());
		}
		
		if(groups.size() > 0)
		{
			Iterator git = groups.values().iterator();
			try{
				for (; git.hasNext(); ) {
					
					JPGroup grp = (JPGroup)git.next();
					Control.getInstance().getController(plan.getId()).
						foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, grp.getSignalGuids(), pevent.getProcessGuid(),
							grp.getProbability(plan.getLibrary()), this.protocol, grp.getName()));
					
				}
			}catch(RemoteException e)
			{
				logger.error("persistElicitedProbabilities - RemExc saving elicited probabilities:  "+e.getMessage());
			}
		}
		
		// all PTSignals that do not have the default flag set, should be eps
		
		if(signals.size() > 0)
		{
			Iterator sig = signals.values().iterator();
			try{
				for(;sig.hasNext();)
				{
					PTSignal signal = (PTSignal)sig.next();
					List<Guid> elicited = new ArrayList<Guid>(1);
					if(!signal.isDefault()) 
					{
						//RemSignalArg(int op, List<Guid> sigs, Guid prcss, float prob, int prot)
						elicited.add(signal.getSignalGuid());
						Control.getInstance().getController(plan.getId()).
							foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, elicited, pevent.getProcessGuid(),
								signal.getProbability(), this.protocol));
					}
				}
			}catch(RemoteException e)
			{
				logger.error("persistElicitedProbs - RemoteExc creating elicited probabilities:  "+e.getMessage());
			}
		}
		
	}
	/**
	 
	 * Method saves current model settings into a ModeSet element and returns
	 
	 * it to the caller, so that all modes and protocolsand elements may be merged to
	 
	 * produce one unified mode set.
	 
	 * @return Element
	 
	 */
	/*public Element saveModeProtocol()
	 
	 {
	 
	 List inlist = new LinkedList();
	 Element ms = DocumentFactory.getInstance().createElement("ModeSet").
	 addAttribute("mode",""+mode).addAttribute("defaultSingleSignal",""+defprob);
	 Element mainsigset = ms.addElement("SignalSet");
	 Element inversions = ms.addElement("Inversions");
	 Element insigset = inversions.addElement("SignalSet");
	 if(signals.size() > 0)
	 
	 {
	 Iterator si = signals.values().iterator();
	 
	 for (; si.hasNext(); ) {
	 PTSignal psig = (PTSignal) si.next();
	 Element s = mainsigset.addElement("Signal");
	 s.addAttribute("guid", psig.getSignalGuid().toString());
	 s.addAttribute("name", psig.getSignalName());
	 if (psig.isInverse()) {
	 inlist.add(psig);
	 Element i = insigset.addElement("Signal");
	 i.addAttribute("guid", psig.getSignalGuid().toString());
	 i.addAttribute("name", psig.getSignalName());
	 
	 }
	 
	 }
	 
	 }
	 
	 // add the ProtocolSet element
	  
	  Element protocolset = ms.addElement("ProtocolSet").addAttribute("protocol",""+protocol);
	  
	  Element protsigset = mainsigset.createCopy(); // same for protocol specific models
	  
	  protocolset.add(protsigset);
	  
	  Element elicitationset = protocolset.addElement("ElicitationSet");
	  
	  // get all groups, for each one create elicited probabilities (multiple sigs)
	   
	   if(groups.size() > 0)
	   
	   {
	   
	   Iterator git = groups.values().iterator();
	   
	   for (; git.hasNext(); ) {
	   
	   JPGroup grp = (JPGroup)git.next();
	   
	   Element elicitedprob = elicitationset.addElement("ElicitedProbability").
	   
	   addAttribute("causalprobability",grp.getProbability()+"");
	   
	   Element epsigset = elicitedprob.addElement("SignalSet");
	   
	   Iterator gsi = grp.getSignals().iterator();
	   
	   for(;gsi.hasNext();)
	   
	   {
	   
	   Signal sngl = (Signal)gsi.next();
	   
	   epsigset.addElement("Signal").addAttribute("guid",sngl.getSignalID().toString()).
	   
	   addAttribute("name",sngl.getSignalName());
	   }
	   }
	   }
	   
	   // all PTSignals that do not have the default flag set, should be eps
	    
	    if(signals.size() > 0)
	    {
	    Iterator sig = signals.values().iterator();
	    for(;sig.hasNext();)
	    {
	    PTSignal signal = (PTSignal)sig.next();
	    if(!signal.isDefault())
	    
	    {
	    Element elicitedprob = elicitationset.addElement("ElicitedProbability").
	    addAttribute("causalprobability",signal.getProbability()+"");
	    Element epsigset = elicitedprob.addElement("SignalSet");
	    epsigset.addElement("Signal").addAttribute("guid",signal.getSignalGuid().toString()).
	    addAttribute("name",signal.getSignalName());
	    }
	    }
	    }
	    return ms;
	    
	    }*/
	
	public void addSignal(Signal signal)
	{
		PTSignal ps = new PTSignal(signal, false);
		ps.setProbability(defprob);
		
		List<Guid> sigs = new java.util.ArrayList<Guid>();
		sigs.add(signal.getSignalID());
		RemSignalArg sArg = null;
		try{
			switch(mode)
			{
				case SignalType.CAUSAL:
					sArg = ProcessUtil.createCauseArg(pevent.getProcessGuid(), sigs, plan.getLibrary());
					checkForPendingSigArg(SignalType.INHIBITING, signal);
					break;
				case SignalType.EFFECT:
					sArg = ProcessUtil.createEffectArg(pevent.getProcessGuid(), sigs, plan.getLibrary());
					break;
				case SignalType.INHIBITING:
					sArg = ProcessUtil.createInhibArg(pevent.getProcessGuid(), sigs, plan.getLibrary());
					checkForPendingSigArg(SignalType.CAUSAL, signal);
					break;
			}
			
			signals.put(ps.getSignalGuid(), ps);
			
			Runnable r = new Runnable()
			{
				public void run()
				{
//					setIgnoreNextOnAll();
					buildTable();
//					fireTableDataChanged();
					fireTableStructureChanged();
				}
			};
			
			new Thread(r, "ProbTable-Updater").run();
			
			// do the controller stuff
			pendingArgs.add(new RemSignalArg(RemSignalArg.ADD, signal));
			pendingArgs.add(sArg);
			
		}catch(SignalModeConflictException exc){
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "Error adding signal. \n" + exc.getMessage());
		}

	}
	
	private void checkForPendingSigArg(int mode, Signal signal) throws SignalModeConflictException
	{
		for(RemSignalArg rsa : pendingArgs)
		{
			if(!rsa.getArgument().contains(signal.getSignalID()))
				continue;
			switch(mode)
			{
				case SignalType.CAUSAL:
					if(rsa.getOperation() == RemSignalArg.ADD_CAUSE)
						throw new SignalModeConflictException("The signal is already used by"+pevent.getName()+" as a cause");
					break;
				case SignalType.EFFECT:
					break;
				case SignalType.INHIBITING:
					if(rsa.getOperation() == RemSignalArg.ADD_INHIBITOR)
						throw new SignalModeConflictException("The signal is already used by "+pevent.getName()+" as an inhibitor");
					break;
			}
		}
	}


	public void removeSignal(PTSignal signal)
	{
		//if signal is part of any groups, remove it from them first
		Iterator<JPGroup> grps = groups.values().iterator();
		ArrayList<JPGroup> reAddGroups = new ArrayList<JPGroup>(); //needed to avoid concurrent modification
		while(grps.hasNext())
		{
			JPGroup grp = grps.next();
			if(grp.getSignalGuids().contains(signal.getSignalGuid()))
			{
				grps.remove();
				grp.removeSignal(signal.getSignal());
				if(grp.getSignals().size() > 1 && !reAddGroups.contains(grp))
					reAddGroups.add(grp);
			}
			else//remove it and re-add it anyway, this will ensure no duplicate groups
			{
				grps.remove();
				if(!reAddGroups.contains(grp))
					reAddGroups.add(grp);
			}
		}
		for(JPGroup grp : reAddGroups)
			groups.put(grp.getName(), grp);

		//remove the signal itself from the model and prepare the remote arg that will do it in the
		// library if the dialog is saved, the arg should take care of groups indirectly
		signals.remove(signal.getSignalGuid());
		
		ArrayList<Guid> toRemove = new ArrayList<Guid>();
		toRemove.add(signal.getSignalGuid());
		pendingArgs.add(new RemSignalArg(RemSignalArg.REMOVE, toRemove, pevent.getProcessGuid()));
		
		Runnable r = new Runnable()
		{
			public void run()
			{
//				setIgnoreNextOnAll();
				buildTable();
				fireTableDataChanged();
				fireTableStructureChanged();
			}
		};
		new Thread(r, "ProbTable-Updater").run();
	}
	
	public void removeGroup(JPGroup group)
	{
		pendingArgs.add(new RemSignalArg(RemSignalArg.REM_ELICITED, group.getSignalGuids(), pevent.getProcessGuid()));
			
		groups.remove(group.getName());
		Runnable r = new Runnable()
		{
			public void run()
			{
//				setIgnoreNextOnAll();
				buildTable();
				fireTableStructureChanged();
				fireTableDataChanged();
			}
		};
		new Thread(r, "ProbTable-Updater").run();
	}
	
	public void addGroup(JPGroup group)	
	{	
		if(group.getSignals().size() > 1)
		{
			pendingArgs.add(new RemSignalArg(RemSignalArg.ADD_ELICITED, group.getSignalGuids(), pevent.getProcessGuid()));

			groups.put(group.getName(), group);
			
			Runnable r = new Runnable()		
			{			
				public void run()
				{				
//					setIgnoreNextOnAll();
					buildTable();
					tableStructureChanged();				
				}			
			};		
			new Thread(r, "ProbTable-Updater").run();
		}
	}
	
	public void tableStructureChanged()
	{		
		this.fireTableStructureChanged();		
	}
	
	public boolean containsGroup(JPGroup group)	
	{
		Iterator gi = groups.values().iterator();
		for(;gi.hasNext();)			
		{			
			if(group.equals((JPGroup)gi.next()))
			{
				return true;
			}			
		}
		return false;
	}
	
	public boolean isGroupMember(PTSignal signal)
	{
		boolean ism = false;
		Iterator gi = groups.values().iterator();
		for(;gi.hasNext();)
		{
			JPGroup gr = (JPGroup)gi.next();
			Iterator si = gr.getSignals().iterator();
			for(;si.hasNext();)
			{
				if( ((Signal)si.next()).getSignalID().equals(signal.getSignalGuid()))
				{
					ism = true;
					break;
				}
			}
			if(ism)
				break;
		}
		
		return ism;
	}
	
	public Collection getSignals()	
	{		
		return signals.values();		
	}
	
	public List<Signal> getPendingSignals()
	{
		ArrayList<Signal> pendSigs = new ArrayList<Signal>();
		
		for(RemSignalArg rsa : pendingArgs)
			if(rsa.getOperation() == RemSignalArg.ADD)
				pendSigs.add(rsa.getNewSignal());
		
		return pendSigs;
	}
	
	public int groupNumber()
	{		
		return groups.size();		
	}
	
	
	private synchronized void refreshIgnoreNextClearDef()
	{
		// create a data vector with [rows or signals + 1][col or grps + 2]
		//data = new Object[signals.size() + 1][columns.length];
		Iterator si = signals.values().iterator();
		for(int drow = 0; si.hasNext(); drow++)
		{
			PTSignal s = (PTSignal)si.next();
			// dont allow the isDefault flag to be reset when the next spinner change event is fired
			s.setIgnoreNextClearDef();
			data[drow][0] = s;
			data[drow][1] = MaskedFloat.getMaskedValue(s.getProbability());
		}
	}
	
	private void setIgnoreNextOnAll()
	{
		Iterator si = signals.values().iterator();
		for(int drow = 0; si.hasNext(); drow++)
		{
			PTSignal s = (PTSignal)si.next();
				s.setIgnoreNextClearDef();
		}
	}
	
	private void initdata()
	{
		// get the default single signal probability for the current mode
		defprob = plan.getLibrary().getProcess(pevent.getProcessGuid()).getDefault((Integer.parseInt(modeset.attributeValue("mode"))));
		groups = returnGroups();
		columns = new Object[groups.size()+2];// + names and alone prob columns
		columns[0] = "signals";
		columns[1] = "alone";

		signals = returnSignals();
		singles = returnSingleProbabilities();
		data = new Object[0][columns.length];
		
		Iterator si = signals.values().iterator();
		for(int drow = 0; si.hasNext(); drow++)
		{
			PTSignal s = (PTSignal)si.next();
			if(singles.containsKey(s.getSignalGuid()))
			{
				s.clearDefault();
			}
		}
		
//		setIgnoreNextOnAll();
	}
	
	public synchronized void buildTable()	
	{
		smap = new Hashtable();
		cellmodel = new CellEditorModel();
		columns = new Object[groups.size() + 2];// + names and alone prob columns
		columns[0] = "signals";
		columns[1] = "alone";
		Iterator gi = groups.values().iterator();
		for(int x=2; gi.hasNext(); x++)
		{
			columns[x] = gi.next();
		}
		
		// create a data vector with [rows or signals + 1][col or grps + 2]
		data = new Object[signals.size() + 1][columns.length];
		Iterator si = signals.values().iterator();
		//test
		//smap.clear();
		for(int drow = 0; si.hasNext(); drow++)
		{
			PTSignal s = (PTSignal)si.next();
			
			data[drow][0] = s;
			// add to the map
			smap.put(s.getSignalGuid(), new Integer(drow));
			// make sure proper renderer and editor for the cell are set
			if(MainFrm.getInstance().isSimpleProbMode())
			{
				cellmodel.addRenderer(drow, 1, new JComboRenderer());
				cellmodel.addEditor(drow, 1, new JComboEditor(this, drow, 1));
			}
			else
			{
				cellmodel.addRenderer(drow, 1, new SpinnerRenderer());
				cellmodel.addEditor(drow, 1, new SpinnerEditor(this, drow, 1, s));
			}
			
//			we just use whatever is in there
//			data[drow][1] = MaskedFloat.getMaskedValue(s.getProbability());
			
			if(singles.containsKey(s.getSignalGuid()))
			{
				float sigVal = ((Float)singles.get(s.getSignalGuid())).floatValue();
				MaskedFloat p = MaskedFloat.getMaskedValue(sigVal);
				s.setProbability(p.floatValue());
				data[drow][1] = p;
//				s.clearDefault();
			}
			else
				data[drow][1] = MaskedFloat.getMaskedValue(defprob);
			
			//this causes the ignore for the clearDefault triggered by setValueAt
//			s.setIgnoreNextClearDef(); //do this down here in case we do the s.clearDefault() a few lines up
			
		}
		
		// finally add the combined row tag
		data[signals.size()][0] = "combined";
		// initialize the group editor cells
		if(groups.size() > 0)
		{
			for(int tcol = 2; tcol < (groups.size() + 2); tcol++)
			{
				for(int trow = 0; trow < signals.size(); trow++)
				{
					cellmodel.addEditor(trow, tcol, new DefaultCellEditor(new JCheckBox()));
					cellmodel.addRenderer(trow, tcol, new CheckBoxRenderer());
				}
				// add a spinner for combined probabilities
				if(MainFrm.getInstance().isSimpleProbMode())
				{
					cellmodel.addRenderer(signals.size(), tcol, new JComboRenderer());
					cellmodel.addEditor(signals.size(), tcol, new JComboEditor(this, signals.size(), tcol));
				}
				else
				{
					cellmodel.addRenderer(signals.size(), tcol, new SpinnerRenderer());
					cellmodel.addEditor(signals.size(), tcol, new SpinnerEditor(this, signals.size(), tcol));
				}
			}
		}
		
		// now we add groups that exist
		Iterator git = groups.values().iterator();
		for(int tcol=2;git.hasNext();tcol++)
		{
			JPGroup group = (JPGroup)git.next();
			Iterator gsi = group.getSignals().iterator();
			for(;gsi.hasNext();)
			{
				Signal signal = (Signal)gsi.next();
				int sigrow = ((Integer)smap.get(signal.getSignalID())).intValue();
				data[sigrow][tcol] = new Boolean(true);
			}
			// add the group probability
			data[signals.size()][tcol] = MaskedFloat.getMaskedValue(group.getProbability(plan.getLibrary()));
		}
		// table data changed
		//this.fireTableDataChanged();
	}

}

