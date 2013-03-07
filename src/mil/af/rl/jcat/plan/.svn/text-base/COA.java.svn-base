package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.util.FormattedDocument;
import mil.af.rl.jcat.util.Guid;



public class COA implements Serializable
{
	private Hashtable<Guid, COAState> itemStates = null;
	private FormattedDocument coaSummary = null;
	private String coaName = null;
	private boolean clearUntracked = false; // clear sched, resources etc for items not tracked by this COA
	private boolean trackSchedule = false; // track items that have scheduling
	private boolean trackResources = false; // track items that have res
	private boolean trackElicit = false; //track elicited probabilities
	private boolean hasSampled = false;
	transient private static Logger logger = Logger.getLogger(COA.class);
	
	
	public COA(String name)
	{
		coaName = name;
		itemStates = new Hashtable<Guid, COAState>();
	}
	
	public COA(Element e) // reconstruct a COA from a dom element (loaded from file)
	{
		itemStates = new Hashtable<Guid, COAState>();
		parseDocument(e);
	}
	
	
	public Set<Guid> getItemGuids()
	{
		return itemStates.keySet();
	}
	
	public COAState get(Guid key)
	{
		return itemStates.get(key);
	}
	
	public void put(Guid key, COAState val)
	{
		itemStates.put(key, val);
	}
	
	public String getName()
	{
		return coaName;
	}
	
	public FormattedDocument getSummary()
	{
		return coaSummary;
	}
	
	public boolean isClearUntracked()
	{
		return clearUntracked;
	}
	
	public boolean isTrackSchedule()
	{
		return trackSchedule;
	}
	
	public boolean isTrackResources()
	{
		return trackResources;
	}
	
	public boolean isTrackElicited()
	{
		return trackElicit;
	}
	
	public void setClearUntracked(boolean val)
	{
		clearUntracked = val;
	}
	
	public void setTrackSchedule(boolean val)
	{
		trackSchedule = val;
	}
	
	public void setTrackResources(boolean val)
	{
		trackResources = val;
	}
	
	public void setTrackElicited(boolean val)
	{
		trackElicit = val;
	}

	public void setSummary(FormattedDocument doc)
	{
		coaSummary = doc;		
	}
	
	public boolean containsItem(Guid itemID)
	{
		return itemStates.containsKey(itemID);
	}
	
	public String toString()
	{
		return coaName;
	}


	public Document getDocument(Library lib)
	{
		Document doc = DocumentHelper.createDocument();
        org.dom4j.Element rootEl = doc.addElement("COA").addAttribute("name", getName()).
        	addAttribute("clearUntracked", clearUntracked+"").addAttribute("trackSched", trackSchedule+"")
        	.addAttribute("trackRes", trackResources+"").addAttribute("trackElicit", trackElicit+"");
        
        if(coaSummary != null)
        	rootEl.addElement("COASummary").add(coaSummary.getXML().getRootElement());
        
        for(Guid key : itemStates.keySet())
        {
        	COAState state = itemStates.get(key);
        	state.addToDocument(rootEl, lib);
        }
        
        return doc;
	}
	
	public void parseDocument(Element e)
	{
		try{
			coaName = e.attributeValue("name");
			clearUntracked = Boolean.parseBoolean(e.attributeValue("clearUntracked"));
			trackSchedule = Boolean.parseBoolean(e.attributeValue("trackSched"));
			trackResources = Boolean.parseBoolean(e.attributeValue("trackRes"));
			trackElicit = Boolean.parseBoolean(e.attributeValue("trackElicit"));
		}catch(Exception exc){
			logger.error("parseDocument - error parsing COA parameters from document:  "+  exc.getMessage());
		}
		
		Element sumEl = (Element)e.selectSingleNode("COASummary");
		if(sumEl != null)
			coaSummary = new FormattedDocument(sumEl);
		
		Iterator subEls = e.selectNodes("PlanItem").iterator();
		while(subEls.hasNext())
		{
			Element stateEl = (Element)subEls.next();
			COAState thisState = new COAState(stateEl);
			itemStates.put(thisState.getItemID(), thisState);
		}
	}

	
	
	public double[] getPredictedProbs(Guid itemID)
	{
		if(itemStates.containsKey(itemID))
			return itemStates.get(itemID).getPredictedProbs();
		else
			return null;
	}

	
	public void setHasSampled(boolean val)
	{
		hasSampled = val;
	}
	
	public boolean hasSampled()
	{
		return hasSampled;
	}
}
