
package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

import mil.af.rl.jcat.bayesnet.Evidence;
import mil.af.rl.jcat.bayesnet.Policy;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;
import mil.af.rl.jcat.util.PIComparable;


/**
 * <p>
 * Title: PlanItem.java
 * </p>
 * <p>
 * Description: Objects extend this class to become a valid plan item.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: C3I Associates
 * </p>
 *
 * @author Edward Verenich
 * @version 1.0
 */

public abstract class PlanItem implements Serializable, PIComparable //JWBAttachment
{

	/**
	 * !!!! READ THIS BEFORE ADDING ANYTHING TO THIS CLASS !!!!
	 * PlanItem Objects are what get serialized via Controller (not just during collaboration)
	 * Thus any structure contained in this class MUST implement Serializable directly or through inheritance.
	 * If this rule is broken, JCAT will be broken and not neccessarily just collaboration.  The effects this would
	 * have may not appear right away or be very obvious so pay attention!
	 */
	public static final int MECHANISM = 0;
	public static final int EVENT = 1;
	private Guid guid = null;
	private String label;
	private String name;
	private int type;
	private int delay = 0;
	private int persistence = 1;
	private float continuation = 0;
	private Documentation documentation = new Documentation();
	private TreeMap<Integer, MaskedFloat> schedule = new TreeMap<Integer, MaskedFloat>();
	private HashMap<Integer, Evidence> evidence = new HashMap<Integer, Evidence>();
	private MultiMap<Integer, ResourceAllocation> threatResources = new MultiMap<Integer, ResourceAllocation>();
	private HashMap<Guid, ResourceAllocation> operationalResources = new HashMap<Guid, ResourceAllocation>();
	public Policy inPolicy = null;

	/**
	 * Creates a new PlanItem, called by other plan items (event, mechanism)
	 * within the same package.
	 *
	 * @param guid
	 *            String
	 * @param name
	 *            String
	 * @param label
	 *            String
	 */
	protected PlanItem(Guid guid, String name, String label, int t)
	{
		this.guid = guid;
		this.name = name;
		this.label = label;
		this.type = t;
	}

	/**
	 * Get persistance of this item in time steps
	 * @return
	 */
	public int getPersistence()
	{
		return persistence;
	}

	/**
	 * Get continuation probability of this item
	 * @return
	 */
	public float getContinuation()
	{
		return continuation;
	}

	/**
	 * Get the name of this item
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the delay of this item in time steps
	 * @return
	 */
	public int getDelay()
	{
		return delay;
	}

	/**
	 * Get the ID of this item
	 * @return
	 */
	public Guid getGuid()
	{
		return guid;
	}

	/**
	 * Get label text for this item 
	 * @return
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Set the persistance value for this item in time steps
	 * @param persistence
	 */
	public void setPersistence(int persistence)
	{
		this.persistence = persistence;
	}

	/**
	 * Set the continuation probability for this item
	 * @param continuation
	 */
	public void setContinuation(float continuation)
	{
		this.continuation = continuation;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Set the delay value for this item in time steps
	 * @param delay
	 */
	public void setDelay(int delay)
	{
		this.delay = delay;
	}

	public void setGuid(Guid guid)
	{
		this.guid = guid;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Resources map currently contains a key (String) of the resources name with an object (ie Integer) value
	 * @return resources map
	 */
	public HashMap<Guid, ResourceAllocation> getResources()
	{
		return operationalResources;
	}

	public void setResources(HashMap<Guid, ResourceAllocation> res)
	{
		operationalResources = res;
	}

	/**
	 * Get the type of item this is (Event or Mechanism)
	 * @return
	 */
	public int getItemType()
	{
		return this.type;
	}

	public void setDocumentation(Documentation doc)
	{
		documentation = doc;
	}

	public Documentation getDocumentation()
	{
		return documentation;
	}

	public abstract Object clone();

	/**
	 * @return Returns the schedule.
	 */
	public TreeMap<Integer, MaskedFloat> getSchedule()
	{
		return schedule;
	}

	/**
	 * Set the schedule for this item
	 * @param schedule The schedule to set.
	 */
	public void setSchedule(TreeMap<Integer, MaskedFloat> schedule)
	{
		this.schedule = schedule;
	}

	/**
	 * Add a time to this items schedule list
	 *
	 * @param time The time the event occurs
	 * @param prob The probability the event occurs at the time
	 */
	public void scheduleEvent(int time, float prob)
	{
		schedule.put(new Integer(time), MaskedFloat.getMaskedValue(prob));
	}

	/**
	 * Remove the schedule item at the given time
	 * @param time
	 */
	public void removeScheduledTime(Integer time)
	{
		this.schedule.remove(time);
	}

	/**
	 * Add a piece of Evidence to this item
	 * @param time
	 * @param prob
	 */
	public void addObservation(int time, Evidence prob)
	{
		evidence.put(new Integer(time), prob);
	}

	/**
	 * Get the evidence that has been added to this item
	 * @return
	 */
	public HashMap getEvidence()
	{
		return evidence;
	}

	/**
	 * @param evidence The evidence to set.
	 */
	public void setEvidence(HashMap<Integer, Evidence> evidence)
	{
		this.evidence = evidence;
	}

	public void addThreatResource(int time, ResourceAllocation allocation)
	{
		threatResources.put(time, allocation);
	}

	public MultiMap<Integer, ResourceAllocation> getThreatResources()
	{
		return threatResources;
	}

	public void setThreatResources(MultiMap<Integer, ResourceAllocation> threat)
	{
		this.threatResources = threat;
	}

	public String toString()
	{
		return getName() + ((!getLabel().equals("")) ? " (" + getLabel() + ")" : "");
	}

	// allows plan items to be sorted alphabetically based on the name, currently used for nav tree
	//ascending
	public int compareTo(Object item)
	{
		try
		{
			PlanItem pItem = (PlanItem) item;

			if(toString().toLowerCase().compareTo(pItem.toString().toLowerCase()) < 0)
				return -1;
			else if(toString().toLowerCase().compareTo(pItem.toString().toLowerCase()) == 0)
				return 0;
			else
				return 1;
		}catch(ClassCastException exc)
		{
			return 0;
		}
	}

	//descending
	public int descendCompareTo(Object item)
	{
		try
		{
			PlanItem pItem = (PlanItem) item;

			if(toString().toLowerCase().compareTo(pItem.toString().toLowerCase()) < 0)
				return 1;
			else if(toString().toLowerCase().compareTo(pItem.toString().toLowerCase()) == 0)
				return 0;
			else
				return -1;
		}catch(ClassCastException exc)
		{
			return 0;
		}
	}

	//allows sorting based on number of causes
	public int causeCompareTo(Object item)
	{
		try
		{
			PlanItem pItem = (PlanItem) item;
			if(!(pItem instanceof Event))
				return 0;
			Event event = (Event) pItem;
			if(((Event) this).getCauses().size() < event.getCauses().size())
				return 1;
			else if(((Event) this).getCauses().size() == event.getCauses().size())
				return 0;
			else
				return -1;
		}catch(ClassCastException exc)
		{
			return 0;
		}
	}

	//allows sorting based on number of inhibitors
	public int inhibitCompareTo(Object item)
	{
		try
		{
			PlanItem pItem = (PlanItem) item;
			if(!(pItem instanceof Event))
				return 0;
			Event event = (Event) pItem;
			if(((Event) this).getInhibitors().size() < event.getInhibitors().size())
				return 1;
			else if(((Event) this).getInhibitors().size() == event.getInhibitors().size())
				return 0;
			else
				return -1;
		}catch(ClassCastException exc)
		{
			return 0;
		}
	}

	//allows sorting based on number of effects
	public int effectCompareTo(Object item)
	{
		try
		{
			PlanItem pItem = (PlanItem) item;
			if(!(pItem instanceof Event))
				return 0;
			Event event = (Event) pItem;
			if(((Event) this).getEffects().size() < event.getEffects().size())
				return 1;
			else if(((Event) this).getEffects().size() == event.getEffects().size())
				return 0;
			else
				return -1;
		}catch(ClassCastException exc)
		{
			return 0;
		}
	}

	public void setGroup(Policy policyGroup)
	{
		inPolicy = policyGroup;

	}
}
