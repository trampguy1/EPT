/*
 * Created on Oct 19, 2005
 */
package mil.af.rl.jcat.util;

import java.util.Iterator;

import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;

public class PlanStats
{

	public static String getName()
	{
		return MainFrm.getInstance().getActiveView().getPlan().getPlanName();
	}
	
	public static String getPath()
	{
		return MainFrm.getInstance().getActiveView().getPlan().getFilePath();
	}
	
	public static String getSize()
	{
		try{
			long size = new java.io.File(MainFrm.getInstance().getActiveView().getPlan().getFilePath()).length();
			return size+" bytes";
		}catch(NullPointerException exc){   return "";   } //plan not saved yet
		
	}
	
	public static String getEventCount()
	{
		return MainFrm.getInstance().getActiveView().getPlan().getAllEvents().size()+"";
	}
	
	public static String getMechanismCount()
	{
		return MainFrm.getInstance().getActiveView().getPlan().getAllMechanisms().size()+"";
	}
	
	public static String getSignalCount()
	{
		return MainFrm.getInstance().getActiveView().getPlan().getLibrary().getAllSignals().size()+"";
	}
	
	public static String getDetatchedEvents()
	{
		java.util.Iterator events = MainFrm.getInstance().getActiveView().getPlan().getAllEvents().iterator();
		int count = 0;
		while(events.hasNext())
		{
			Event thisEvent = (Event)events.next();
			if(thisEvent.getCauses().size() + thisEvent.getInhibitors().size() + thisEvent.getEffects().size() < 1)
				count++;
		}
		return count+"";
	}
	
	public static String getUnusedSignals()
	{
		AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		Iterator events = MainFrm.getInstance().getActiveView().getPlan().getAllEvents().iterator();
		Iterator signals = MainFrm.getInstance().getActiveView().getPlan().getLibrary().getAllSignals().iterator();
		int count = 0;
		
		while(signals.hasNext())
		{
			boolean used = false;
			Signal thisSig = (Signal)signals.next();
			while(events.hasNext())
			{
				Event thisEvent  = (Event)events.next();
				if(thisEvent.containsSignal(thisSig.getSignalID(), SignalType.CAUSAL, plan) ||
					thisEvent.containsSignal(thisSig.getSignalID(), SignalType.INHIBITING, plan) ||
					thisEvent.containsSignal(thisSig.getSignalID(), SignalType.EFFECT, plan))
						used = true;
			}
			
			if(!used)
				count++;
			events = MainFrm.getInstance().getActiveView().getPlan().getAllEvents().iterator();
		}
		
		return count+"";
	}
	
	public static String getDocumentedItems()
	{
		Iterator events = MainFrm.getInstance().getActiveView().getPlan().getAllEvents().iterator();
		int count = 0;
		while(events.hasNext())
			if(((Event)events.next()).getDocumentation().isDocumented())
				count++;
		
		return count+"";
	}
	
	public static String getActiveScheme()
	{
		return ColorScheme.getInstance().getName();
	}
}
