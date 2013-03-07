package mil.af.rl.jcat.gui.table.model.base;


import mil.af.rl.jcat.processlibrary.Signal;

import mil.af.rl.jcat.util.*;

/**
 
 * <p>Title: PTSignal.java</p>
 
 * <p>Description: Wrapper for a signal that holds inverse information relating
 
 *                 to it's current model/process.  This is needed because
 
 *                 a Signal cannot keep track of its relation to a process</p>
 
 * <p>Copyright: Copyright (c) 2004</p>
 
 * <p>Company: C3I Associates</p>
 
 * @author Edward Verenich (JCAT Team)
 
 * @version 1.0
 
 */


public class PTSignal
{
	
	private Signal signal;
	private boolean inverse;
	private float alonep;
	private boolean def = true;
	private boolean used = false;
	private boolean ignoreClearDef;
	private String name;
	
	
	public PTSignal(Signal psignal, boolean isinverse)
	{
		signal = psignal;
		inverse = isinverse;
		name = psignal.getSignalName();
	}

	
	public String getSignalName()
	{
		return name;
	}
	
	public void setSignalName(String name)
	{
		this.name = name;
	}
	
	public Guid getSignalGuid()
	{
		return signal.getSignalID();
	}
	
	public void setInverse(boolean inverse)
	{
		this.inverse = inverse;
	}
	
	public boolean isInverse()
	{
		return inverse;
	}
	
	public boolean isDefault()
	{
		return def;
	}
	
	//public void setDefaultProbability(float prob){alonep = prob;}
	
	public void setProbability(float prob)
	{
		//def = false;
		alonep = prob;
	}
	
	public void clearDefault()
	{
		if(!ignoreClearDef)
			def = ignoreClearDef = false;
		else
			ignoreClearDef = false;
	}
	
	public float getProbability()
	{
		return alonep;
	}
	
	public void setUsedByMechanism(boolean u)
	{
		used = u;
	}
	
	public boolean isUsedByMechanism()
	{
		return used;
	}
	
	public String toString()
	{
		if(inverse)
			return name+" [NOT]";
		else
			return name;
	}
	
	public Signal getSignal()
	{
		return signal;
	}
	
	public boolean equals(Object inOb)
	{
		if(inOb instanceof PTSignal)
			return super.equals(inOb);
		else if(inOb instanceof Signal)
		{
			Signal inSignal = (Signal)inOb;
			return this.getSignal().equals(inSignal);
		}
		
		return false;
	}


	
	public void setIgnoreNextClearDef()
	{
		ignoreClearDef = true;
		
	}
	
}

