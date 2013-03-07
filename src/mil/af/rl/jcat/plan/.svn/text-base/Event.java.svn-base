
package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mil.af.rl.jcat.control.LibProcessArg;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;


/**
 * <p>
 * Title: Event.java
 * </p>
 * <p>
 * Description: Object reprecends an event in a causal model.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: C3I Associates
 * </p>
 * @author Edward Verenich
 * @version 1.0
 */

public class Event extends PlanItem implements Serializable //, JWBAttachment
{

	private static final long serialVersionUID = -1695406177817404355L;
	private LinkedList<Guid> causes = new LinkedList<Guid>();
	private LinkedList<Guid> effects = new LinkedList<Guid>();
	private LinkedList<Guid> inhibits = new LinkedList<Guid>();

	//private float singleCausalProb = -1.0f;//SignalData.getDefaultSingleSignalCausalProbability();
	//private float singleInhibitingProb = -1.0f;//SignalData.getDefaultSingleSignalInhibitProbability();
	//private float singleEffectProb = -1.0f;//SignalData.getDefaultSingleSignalEffectProbability();
	private float leak = 0.0f;
	private Guid processguid = null;
	private String colorAttribName = "";
	private ArrayList copiedCauseSignals = new ArrayList();
	private ArrayList copiedEffectSignals = new ArrayList();
	private ArrayList copiedInhibitSignals = new ArrayList();
	private LibProcessArg parg; // used for pasting to different instances
	private int resourceUseType = mil.af.rl.jcat.bayesnet.NetNode.HIGHEST_PROB_USE;

	
	/**
	 * Construct a new Event
	 * @param guid ID for this Event
	 * @param name Name for this Event
	 * @param label Label for this Event
	 * @param pguid ID for the associated "Process"
	 */
	public Event(Guid guid, String name, String label, Guid pguid)
	{
		super(guid, name, label, PlanItem.EVENT);
		processguid = pguid;
	}
	
	/**
	 * Construct a new Event automatically generating a "Process" ID
	 * @param guid ID for this Event
	 * @param name Name for this Event
	 * @param label Label for this Event
	 */
	public Event(Guid guid, String name, String label)
	{
		this(guid, name, label, new Guid());
	}

	public void setProcessCArg(LibProcessArg a)
	{
		parg = a;
	}

	public LibProcessArg getPArgument()
	{
		return parg;
	}

	/**
	 * Used to check if a given signal is contained in one of the mechanisms
	 * used by the event.
	 * @param guid signal identifier
	 * @param mode signal mode, CAUSE, EFFECT, or INHIBITOR
	 * @param plan AbstractPlan containing the mechanisms
	 * @return
	 */
	public boolean containsSignal(Guid guid, int mode, AbstractPlan plan)
	{
		boolean contains = false;
		Mechanism m;
		if(mode == SignalType.CAUSAL)
		{
			Iterator ci = causes.iterator();
			for(; ci.hasNext();)
			{
				m = (Mechanism) plan.getItem((Guid) ci.next());
				if(m != null && m.getSignalGuid().equals(guid))
				{
					contains = true;
					break;
				}
			}
		}
		else if(mode == SignalType.INHIBITING)
		{
			Iterator ci = inhibits.iterator();
			for(; ci.hasNext();)
			{
				m = (Mechanism) plan.getItem((Guid) ci.next());
				if(m != null && m.getSignalGuid().equals(guid))
				{
					contains = true;
					break;
				}
			}

		}
		else
		{
			Iterator ei = effects.iterator();
			for(; ei.hasNext();)
			{
				m = (Mechanism) plan.getItem((Guid) ei.next());
				if(m != null && m.getSignalGuid().equals(guid))
				{
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
    
    /**
	 * Returns the mechanism given a signalId
	 * 
	 * @param signalId
	 * @param plan
	 * @return
	 */
	public Mechanism getMechanismFromSignalID(Guid signalId, AbstractPlan plan)
	{
		Mechanism m;
		Iterator ci = causes.iterator();
		for(; ci.hasNext();)
		{
			m = (Mechanism) plan.getItem((Guid) ci.next());
			if(m != null && m.getSignalGuid().equals(signalId))
			{
				return m;
			}
		}
		ci = inhibits.iterator();
		for(; ci.hasNext();)
		{
			m = (Mechanism) plan.getItem((Guid) ci.next());
			if(m != null && m.getSignalGuid().equals(signalId))
			{
				return m;
			}
		}

		ci = effects.iterator();
		for(; ci.hasNext();)
		{
			m = (Mechanism) plan.getItem((Guid) ci.next());
			if(m != null && m.getSignalGuid().equals(signalId))
			{
				return m;
			}
		}

		return null;
	}

	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places. Instead of setting default
	 * probability values in the Event, you should get the Event's Process from the Library using the process guid
	 * in Event and get/set the values there, this method here simply makes that a bit eaiser.
	 * 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public void setDefEffectProb(float value, Library lib)
	{
		lib.getProcess(processguid).setDefault(SignalType.EFFECT, value);
	}

	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places.  Instead of setting 
	 * default probability values in the Event, you should get the Event's Process from the Library using 
	 * the process guid in Event and get/set the values there, this method simply makes that a bit eaiser. 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public void setDefCausalProb(float value, Library lib)
	{
		lib.getProcess(processguid).setDefault(SignalType.CAUSAL, value);
	}

	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places.  Instead of setting 
	 * default probability values in the Event, you should get the Event's Process from the Library using 
	 * the process guid in Event and get/set the values there, this method simply makes that a bit eaiser. 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public void setDefInhibitingProb(float value, Library lib)
	{
		lib.getProcess(processguid).setDefault(SignalType.INHIBITING, value);
	}

	/**
	 * Set the leak value of this Event
	 * @param probability leak probability
	 */
	public void setLeak(float probability)
	{
		leak = probability;
	}

	public Object clone()
	{
		return this;
	}

	public Guid getProcessGuid()
	{
		return processguid;
	}

	/**
	 * Method adds a cause to the event.  
	 * 
	 * @param mech Mechanism to add
	 */
	public void addCause(Mechanism mech)
	{
		if(!causes.contains(mech.getGuid()))
			causes.add(mech.getGuid());
	}

	/**
	 * Method adds a effect to the event.  
	 * 
	 * @param mech Mechanism to add
	 */
	public void addEffect(Mechanism mech)
	{
		if(!effects.contains(mech.getGuid()))
			effects.add(mech.getGuid());
	}

	/**
	 * Method adds an inhibitor to the event.  
	 * 
	 * @param mech Mechanism to add
	 */
	public void addInhibitor(Mechanism mech)
	{
		if(!inhibits.contains(mech.getGuid()))
			inhibits.add(mech.getGuid());
	}

	public void pasteCause(Guid mech)
	{
		if(!causes.contains(mech))
		{
			causes.add(mech);
		}
	}

	/**
	 * Method removes a cause from the current list
	 *
	 * @param mech ID of Mechanism to remove
	 */
	public synchronized void removeCause(Guid mech)
	{
		causes.remove(mech);
	}

	public void pasteEffect(Guid mech)
	{
		if(!effects.contains(mech))
		{
			effects.add(mech);
		}
	}

	/**
	 * Method removes a Effect from the current list
	 *
	 * @param mech ID of Mechanism to remove
	 */
	public synchronized void removeEffect(Guid mech)
	{
		effects.remove(mech);
	}

	public void pasteInhibitor(Guid mech)
	{
		if(!inhibits.contains(mech))
		{
			inhibits.add(mech);
		}
	}

	/**
	 * Method removes a Inhibitor from the current list
	 *
	 * @param mech ID of Mechanism to remove
	 */
	public synchronized void removeInhibitor(Guid mech)
	{
		inhibits.remove(mech);

	}

	/**
	 * Method returns the list of causes
	 *
	 * @return List
	 */
	public List<Guid> getCauses()
	{
		return causes;
	}

	/**
	 * Method returns the list of effects
	 *
	 * @return List
	 */
	public List<Guid> getEffects()
	{
		return new ArrayList<Guid>(effects);
	}

	/**
	 * Method returns the list of inhibitors.
	 *
	 * @return List
	 */
	public List<Guid> getInhibitors()
	{
		return inhibits;
	}

	/**
	 * Method sets the causes collection, note that the old collection is no
	 * longer valid
	 *
	 * @param c
	 *            List collection of causes
	 */
	public void setCauses(List<Guid> c)
	{
		causes = new LinkedList<Guid>(c);
	}

	/**
	 * Method sets the effects collection, the old collection is replaced.
	 *
	 * @param e
	 *            List
	 */
	public void setEffects(List<Guid> e)
	{
		effects = new LinkedList<Guid>(e);
	}

	/**
	 * Method sets the inhibs collection, the old collection is replaced.
	 *
	 * @param i
	 *            List
	 */
	public void setInhibitors(List<Guid> i)
	{
		inhibits = new LinkedList<Guid>(i);
	}

	//TODO: Get rid of these 3 methods
	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places.  Instead of setting 
	 * default probability values in the Event, you should get the Event's Process from the Library using 
	 * the process guid in Event and get/set the values there, this method simply makes that a bit eaiser. 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public double getSingleCausalProb(Library lib)
	{
		return lib.getProcess(processguid).getDefault(SignalType.CAUSAL);
	}

	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places.  Instead of setting 
	 * default probability values in the Event, you should get the Event's Process from the Library using 
	 * the process guid in Event and get/set the values there, this method simply makes that a bit eaiser. 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public double getSingleInhibitingProb(Library lib)
	{
		return lib.getProcess(processguid).getDefault(SignalType.INHIBITING);
	}

	/**
	 * Convienience method provided to minimize code changes after removing default probability numbers from Event.
	 * This was done in effort to keep the numbers from being duplicated in 2 places.  Instead of setting 
	 * default probability values in the Event, you should get the Event's Process from the Library using 
	 * the process guid in Event and get/set the values there, this method simply makes that a bit eaiser. 
	 * @param value New value
	 * @param lib Reference to ProcessLibary so this Event can look up its process for you
	 */
	public double getSingleEffectProb(Library lib)
	{
		return lib.getProcess(processguid).getDefault(SignalType.EFFECT);
	}

	/**
	 * Get the leak probability for this Event
	 * @return leak probability
	 */
	public float getLeak()
	{
		return leak;
	}

	//scheme attribute is used to update the shape with new color scheme data
	public void setSchemeAttrib(String attrib)
	{
		colorAttribName = (attrib == null) ? "" : attrib;
	}

	public String getSchemeAttrib()
	{
		return colorAttribName;
	}

//	public JWBAttachment deepCopy()
//	{
//		//Process cprocess = this.process.safeClone();
//		//Event nevent = new Event(new Guid(),super.getName(),super.getLabel(),cprocess.getProcessID(),l);
//		//nevent.setDocumentation((Documentation)getDocumentation().clone());
//		//return nevent;
//		return this;
//	}

	public void initCopiedSignals(Process p, Library l)
	{
		//copiedSignals = new ArrayList();

		Iterator i = p.getCausalSignals().iterator();
		for(; i.hasNext();)
		{
			copiedCauseSignals.add(l.getSignal((Guid) i.next()));
		}
		i = p.getEffectSignals().iterator();
		for(; i.hasNext();)
		{
			copiedEffectSignals.add(l.getSignal((Guid) i.next()));
		}
		i = p.getInhibitingSignals().iterator();
		for(; i.hasNext();)
		{
			copiedInhibitSignals.add(l.getSignal((Guid) i.next()));
		}
	}

	public ArrayList getCopiedCausalSignals()
	{
		return copiedCauseSignals;
	}

	public ArrayList getCopiedEffectSignals()
	{
		return copiedEffectSignals;
	}

	public ArrayList getCopiedInhibitingSignals()
	{
		return copiedInhibitSignals;
	}

	
	/**
	 * resourceUseType - Determines how incoming resources will be used together 
	 */
	public int getResourceUseType()
	{
		return resourceUseType;
	}
	
	public void setResourceUseType(int type)
	{
		resourceUseType = type;
	}


	/**
	 * True if one of its incoming mechanisms is a consolidator
	 * @return
	 */
	public boolean hasConsolidator(AbstractPlan plan)
	{
		boolean hasConsolidator = false;
		
		Iterator<Guid> cMechs = causes.iterator();
		while(cMechs.hasNext())
		{
			if(((Mechanism)plan.getItem(cMechs.next())).isConsolidator())
				hasConsolidator = true;
		}
		
		Iterator<Guid> iMechs = inhibits.iterator();
		while(iMechs.hasNext())
		{
			if(((Mechanism)plan.getItem(iMechs.next())).isConsolidator())
				hasConsolidator = true;
		}
		
		return hasConsolidator;
	}

	/*
	   //inserted by mikeyd - used for 'apply default probabilities' fix
		public float getDefaultProb(int mode)
		{
			if(mode == SignalType.CAUSAL)
				return (float)getSingleCausalProb();
			else if(mode == SignalType.INHIBITING)
				return (float)getSingleInhibitingProb();
			else
				return (float)getSingleEffectProb();
		}

		public int getDefaultsSubType()
		{
			return defaultsSubType;
		}
	*/

}
