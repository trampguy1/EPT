package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitationSet;
import mil.af.rl.jcat.processlibrary.signaldata.SignalSet;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;

import org.apache.log4j.Logger;
import org.dom4j.Element;



/**
 * COAState - state of an item as it pertains to a COA
 * keep track of timing elements, schedule, resources... maybe evidence later
 */
public class COAState implements Serializable
{

	//timing elements
	protected int delay = -1;
	protected int persistance = -1;
	protected float continuation = -1f;
	//schedule
	protected TreeMap<Integer, MaskedFloat> schedule = null;
	//resources
	protected HashMap<Guid, ResourceAllocation> resources = null;
	private Guid ID = null;
	private String name = "";
	protected MultiMap<Integer, ResourceAllocation> tResources = null;
	protected ElicitationSet cElicit = null; //cause elicitations
	protected ElicitationSet iElicit = null; //inhibit elicitations
	protected ElicitationSet eElicit = null; //effect elicitations
	private double[] predProbs = null;
	private static Logger logger = Logger.getLogger(COAState.class);
	
	
	/**
	 * Create a new COAState containing elements we're interested in tracking
	 * @param itemID  PlanItem id for item tracked by this state object
	 * @param dly  Timing element - delay
	 * @param persist  Timing element - persistance
	 * @param contin  Timing element - continuation
	 * @param sched  The item's schedule map (a copy will be made to ensure object separation)
	 * @param res  The item's resource map (a copy will be made to ensure object separation)
	 * @param tRes  The item's threat resource map (a copy will be made to ensure object separation)
	 * @param eSets  An array containing the eliciation sets for each of the 3 mode sets. Should be in order C,I,E
	 */
	public COAState(Guid itemID, int dly, int persist, float contin, TreeMap<Integer, MaskedFloat> sched, 
		HashMap<Guid, ResourceAllocation> res, MultiMap<Integer, ResourceAllocation> tRes, ElicitationSet[] eSets)
	{
		ID = itemID;
		delay = dly;
		persistance = persist;
		continuation = contin;
		if(sched != null)
			schedule = (TreeMap<Integer, MaskedFloat>)sched.clone(); //get a copy of the schedule, not the reference
		if(res != null)
			resources = (HashMap<Guid, ResourceAllocation>)res.clone();
		if(tRes != null)
			tResources = (MultiMap<Integer, ResourceAllocation>)tRes.clone();
		if(eSets != null && eSets.length == 3)
		{
			if(eSets[0] != null) cElicit = (ElicitationSet)eSets[0].clone();
			if(eSets[1] != null) iElicit = (ElicitationSet)eSets[1].clone();
			if(eSets[2] != null) eElicit = (ElicitationSet)eSets[2].clone();
		}
	}

	public COAState(Element e)
	{
		schedule = new TreeMap<Integer, MaskedFloat>();
		resources = new HashMap<Guid, ResourceAllocation>();
		tResources = new MultiMap<Integer, ResourceAllocation>();
		parseDocument(e);
	}
	
	

	public Guid getItemID()
	{
		return ID;
	}
	
	
	public MultiMap<Integer, ResourceAllocation> getThreatResources()
	{
		return tResources;
	}

	
	public ElicitationSet getCauseElicit()
	{
		return cElicit;
	}
	
	public float getContinuation()
	{
		return continuation;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public ElicitationSet getEffectElicit()
	{
		return eElicit;
	}
	
	public ElicitationSet getInhibElicit()
	{
		return iElicit;
	}
	
	public int getPersistance()
	{
		return persistance;
	}
	
	public HashMap<Guid, ResourceAllocation> getResources()
	{
		return resources;
	}
	
	public TreeMap<Integer, MaskedFloat> getSchedule()
	{
		return schedule;
	}
	
			
	public void addToDocument(Element rootEl, Library lib)
	{
		Element subEl = rootEl.addElement("PlanItem").addAttribute("guid", ID.getValue()).addAttribute("delay", delay+"").
			addAttribute("persistance", persistance+"").addAttribute("continuation", continuation+"");
		//add schedule to subEl
		Element schedEl = subEl.addElement("schedule");
		for(Integer time : schedule.keySet())
		{
			schedEl.addElement("probability").addAttribute("time", time.toString()).setText(schedule.get(time).floatValue()+"");
		}
		
		Element resEl = subEl.addElement("Resources");
		for(Guid id : resources.keySet())
		{
			ResourceAllocation allocation = resources.get(id);
			resEl.addElement("allocation").addAttribute("guid", id.getValue()).addAttribute("name", allocation.getName()).addAttribute("allocated", allocation.getAllocated()+"").addAttribute("contingent", allocation.isContingent()+"");
		}
		
		//add threat resources
		Element tResEl = subEl.addElement("ThreatResources");
		for(Object key : tResources.keySet())
		{
			Element timeEl = tResEl.addElement("resource").addAttribute("time", ((Integer)key).intValue()+"");
			
			Iterator valuesForKey = tResources.get((Integer)key).iterator();
			while(valuesForKey.hasNext())
			{
				ResourceAllocation ra = (ResourceAllocation)valuesForKey.next();
				timeEl.addElement("allocation").addAttribute("guid", ra.getID().getValue()).addAttribute("allocated", ra.getAllocated()+"");
			}
		}
		
		//add elicited probablities (requires library ref)
		if(cElicit != null) cElicit.addToDocument(null, subEl.addElement("CauseElicitations"), lib);
		if(iElicit != null) iElicit.addToDocument(null, subEl.addElement("InhibitElicitations"), lib);
		if(eElicit != null) eElicit.addToDocument(null, subEl.addElement("EffectElicitations"), lib);
			
	}
	
	private void parseDocument(Element e)
	{
		ID = new Guid(e.attributeValue("guid"));
		delay = new Integer(e.attributeValue("delay")).intValue();
		persistance = new Integer(e.attributeValue("persistance")).intValue();
		continuation = new Float(e.attributeValue("continuation")).floatValue();
		
		Element schedEl = (Element)e.selectSingleNode("./schedule"); //might need ./ instead
		if(schedEl != null)
		{
			Iterator schedEls = schedEl.selectNodes("probability").iterator();
			while(schedEls.hasNext())
			{
				try{
					Element thisSched = (Element)schedEls.next();
					schedule.put(new Integer(thisSched.attributeValue("time")), MaskedFloat.getMaskedValue(Float.parseFloat(thisSched.getText())));
				}catch(NumberFormatException exc){
					logger.error("parseDocument - Invalid value parsing COA schedule element:  "+exc.getMessage());
				}
			}
		}
		
		Element resEl = (Element)e.selectSingleNode("./Resources"); //might need ./ instead
		if(resEl != null)
		{
			Iterator allocations = resEl.selectNodes("allocation").iterator();
			while(allocations.hasNext())
			{
				try{
					Element thisRes = (Element)allocations.next();
					Guid theGuid = new Guid(thisRes.attributeValue("guid"));
					resources.put(theGuid, new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated")), thisRes.attributeValue("name"), new Boolean(thisRes.attributeValue("contingent"))));
				}catch(NumberFormatException exc){
					logger.error("parseDocument - Invalid value parsing COA resource element:  "+exc.getMessage());
				}
			}
		}
		
		Element tResEl = (Element)e.selectSingleNode("./ThreatResources"); //might need ./ instead
		if(tResEl != null)
		{
			Iterator timeEls = tResEl.selectNodes("resource").iterator();
			while(timeEls.hasNext())
			{
				Element timeEl = (Element)timeEls.next();
				String time = timeEl.attributeValue("time");
				Iterator allocations = timeEl.selectNodes("allocation").iterator();
				while(allocations.hasNext())
				{
					try{
						Element thisRes = (Element)allocations.next();
						Guid theGuid = new Guid(thisRes.attributeValue("guid"));
						tResources.put(new Integer(time), new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated"))));
					}catch(NumberFormatException exc){
						logger.error("parseDocument - Invalid value parsing COA t-resource element:  "+exc.getMessage());
					}
				}
			}
		}
		
		//restore elicited probabilities
		Element cElicitEl = (Element)e.selectSingleNode("./CauseElicitations");
		if(cElicitEl != null)
		{
			ElicitationSet elicited = new ElicitationSet(new SignalSet());
			elicited.restoreFromEl((Element)cElicitEl.selectSingleNode("ElicitationSet"));
			cElicit = elicited;
		}
		
		Element iElicitEl = (Element)e.selectSingleNode("./InhibitElicitations");
		if(iElicitEl != null)
		{
			ElicitationSet elicited = new ElicitationSet(new SignalSet());
			elicited.restoreFromEl((Element)iElicitEl.selectSingleNode("ElicitationSet"));
			iElicit = elicited;
		}
		
		Element eElicitEl = (Element)e.selectSingleNode("./EffectElicitations");
		if(eElicitEl != null)
		{
			ElicitationSet elicited = new ElicitationSet(new SignalSet());
			elicited.restoreFromEl((Element)eElicitEl.selectSingleNode("ElicitationSet"));
			eElicit = elicited;
		}
	}

	
	
	public void setPredictedProbs(double[] predicted)
	{
		predProbs = predicted;
	}
	
	public double[] getPredictedProbs()
	{
		return predProbs;
	}
}
