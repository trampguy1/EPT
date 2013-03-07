
package mil.af.rl.jcat.plan;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.bayesnet.BayesNet;
import mil.af.rl.jcat.bayesnet.JCatBayesBuilder;
import mil.af.rl.jcat.bayesnet.Policy;
//import mil.af.rl.jcat.control.PlanArgument;
//import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.exceptions.BayesNetException;
import mil.af.rl.jcat.exceptions.GraphLoopException;
import mil.af.rl.jcat.exceptions.SamplerMemoryException;
import mil.af.rl.jcat.exceptions.SignalException;
//import mil.af.rl.jcat.gui.dialogs.SamplingOptions;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitationSet;
import mil.af.rl.jcat.processlibrary.signaldata.ProtocolSet;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MultiMap;

import com.c3i.jwb.JWBUID;


/**
 *
 * <p>
 * </p>
 * <p>
 * AbstractPlan is a logical representation of a model. 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * @author Edward Verenich
 */
public final class AbstractPlan implements PlanInterface
{

	private ResourceManager resourceManager = new ResourceManager();
	private volatile HashMap<Guid, PlanItem> planitems = new HashMap<Guid, PlanItem>();
	private MultiMap<Guid, JWBUID> itemToShape = new MultiMap<Guid, JWBUID>(); // <pi guid : shape uid>
	private HashMap<JWBUID, Guid> shapeToItem = new HashMap<JWBUID, Guid>(); // <shape u
	private Vector<COA> coaList = new Vector<COA>();
	private String name = "";
	private Library library = new Library();
	private BayesNet bayesNet;
	private String filePath;
	private Documentation documentation = new Documentation();
	private boolean modified = false;
	private Guid id = null;
	private ColorScheme cScheme = new ColorScheme("", null);
	private int loadedPlanLength = -1;
	private Font defFont = new Font("Arial", 1, 11);
	// flags and attributes for default probability sets //
	public static final float[] STANDARD_DEFAULTS_SET = new float[] { .75f, .8f, 1.0f, .55f };
	/**
	 * And/Or set contains default probabilities in this order:
	 *   and_cause, and_inhib, and_effect, and_group, or_cause, or_inhib, or_effect, or_group
	 *   	0			1		2				3		4			5		6			7
	 */
	public static final float[] AND_OR_DEFAULTS_SET = new float[] { .0f, .0f, 1.0f, 1.0f, 0.99f, 0.99f, 1.0f, .99f };
	public static float[] USER_DEFINED_DEFAULTS_SET = new float[] {};
	private float[] activeDefaultsSet = STANDARD_DEFAULTS_SET;
	// allow exactly 3 active COAs because there could be 1 for scheduling, 1 for elicited and 1 for resources separately
	private COA[] activeCOAs = new COA[3];
	private boolean isBuilding; //is the plan currently building its BayesNet
	private boolean wasRetreived; //was the plan retreived via collaboration during initial connection
	public ArrayList<Policy> activePolicies = new ArrayList<Policy>();
	private ArrayList<PlanListener> listeners = new ArrayList<PlanListener>();
	private static Logger logger = Logger.getLogger(AbstractPlan.class);

	/**
	 * Constructs a new AbstractPlan.
	 */
	public AbstractPlan(Guid planid)
	{
		id = planid;
	}

	public void addPlanListener(PlanListener lis)
	{
		if(!listeners.contains(lis))
			listeners.add(lis);
	}
	
	/**
	 * removes items from the plan
	 *
	 * @param key
	 *            the key of the shape to remove from the collection
	 */
	public boolean edgeWillMakeLoop(Event edgeTail, Event edgeHead)
	{
		boolean makesLoop = false;
		Guid head = edgeHead.getGuid();
		Guid tail = edgeTail.getGuid();
		//makesLoop = checkForLoop(tail, head);
		makesLoop = checkLoop(head, tail, this.getAllEvents().size());
		return makesLoop;
	}

	/**
	 * Checks for loops in the plan
	 * @param tail
	 * @param head
	 * @return
	 */
	/*
	 private boolean checkForLoop(Guid tail, Guid head)
	 {
	 boolean retVal = true;
	 Event headEvent = (Event) getItem(head);
	 if(!tail.equals(head))
	 {
	 retVal = false;
	 Iterator i = headEvent.getEffects().iterator();
	 for(; i.hasNext();)
	 {
	 Guid nextMechID = (Guid) i.next();
	 Mechanism mech = (Mechanism) getItem(nextMechID);
	 if(mech == null)
	 continue;
	 if(!mech.isLoopCloser())
	 {
	 boolean anyCausedLoop = false;
	 for(int x=0; x<mech.getNumToEvents(); x++)
	 {
	 anyCausedLoop = checkForLoop(tail, mech.getToEvent(x)); //WHAT ABOUT THE OTHER ONE, ur not gona check that?
	 if(anyCausedLoop)
	 break;
	 }
	 
	 retVal = anyCausedLoop;
	 }
	 if(retVal)
	 break;
	 }
	 }
	 return retVal;
	 }*/
	// tail never changes (ED)  
	// 
	private boolean checkLoop(Guid head, Guid tail, int elements)
	{
		boolean loop = false;

		if(tail.equals(head))
		{

			return true;
		}
		Event ehead = (Event) getItem(head);
		for(Guid effect : ehead.getEffects())
		{

			Mechanism m = (Mechanism) getItem(effect);
			if(m == null)
				continue;
			for(Guid influence : m.getInfluencedEvents())
			{
				elements--;
				if(elements < 1)
					break;
				loop = checkLoop(influence, tail, elements);
			}
		}
		return loop;
	}

	/**
	 * Method removes a plan item using its container shape UID, it also removes
	 * the corresponding mappings in the plan.
	 * @param shape JWBUID
	 * @return item PlanItem that was removed
	 */
	public synchronized PlanItem removeItem(JWBUID shape, List linkedShapes)
	{
		PlanItem item;
		item = planitems.get(this.shapeToItem.get(shape));
		if(item == null)
			return null;

		//techically you may not be deleting a mechanism here if its a consolidator, but just one of the pieces(shapes)
		//that are part of the mechanism/consolidator
		if(item.getItemType() == PlanItem.MECHANISM)
		{
			JWBUID startKey = (JWBUID) linkedShapes.get(0);
			JWBUID endKey = (JWBUID) linkedShapes.get(1);
			Mechanism mech = (Mechanism) item;
			if(mech.isConsolidator())
			{
				//this will be null when the shape with an incoming consol is deleted first cause its no longer in shapeToItem
				//these 'linkedShapes' do not solve this problem as it seems they were intended to
				//possible solution is to: mark an event as having an incoming consol and 
				//if the shape gets deleted the flag will indicate that it should not be deleted from
				//shapeToItem until its consolidator does it which should happen right afterwords

				Guid toId = this.shapeToItem.get(endKey);
				mech.removeConsolidatedOutput(toId);
				//do a shapeToItem.remove(endKey) only if the item returned is no longer in planitems (indicating 
				//it was deleted moments ago, make sure this is done after the removeConsolidatedOuput tho
				if(planitems.get(toId) == null)//removes the toEvents shape in cause it wasn't removed before hand **
					shapeToItem.remove(endKey);

				//now for cause or inhibit
				Event to = (Event) planitems.get(toId); //can u cast a null w/o nullpointer..test
				if(to != null)// just do both
				{
					to.removeCause(item.getGuid());
					to.removeInhibitor(item.getGuid());
				}
				//remove the single shape from the list of shapes that are part of the consolidator mech mapping
				if(remShapeMapping(mech.getGuid(), shape))
					planitems.remove(mech.getGuid());
				shapeToItem.remove(shape);
			}
			// make sure we take care of all the related events
			else
			{
				Event from = (Event) planitems.get(((Mechanism) item).getFromEvent());
				if(from != null)
					from.removeEffect(item.getGuid());
				// now for cause or inhibit
				Event to = (Event) planitems.get(((Mechanism) item).getToEvent(0));
				if(to != null)// just do both
				{
					to.removeCause(item.getGuid());
					to.removeInhibitor(item.getGuid());
				}
				remShapeMapping(shapeToItem.get(shape));
				item = planitems.remove(shapeToItem.remove(shape));
			}
		}
		else
		{
			remShapeMapping(shapeToItem.get(shape));
			//use flag mentioned above to determine if the shapeToItem.remove should happen now **
			if(!((Event) item).hasConsolidator(this))
				shapeToItem.remove(shape);
			item = planitems.remove(item.getGuid());
		}
		
		for(PlanListener lis : listeners)
			lis.itemListChanged(new PlanEvent(this, PlanEvent.REMOVED, item.getGuid()));

		return item;
	}

	/**
	 * Method removes a plan item and takes care of all cleaning as far as
	 * connected items go.
	 * @param item PlanItem
	 */
	public void removeItem(PlanItem item)
	{
		planitems.remove(item.getGuid());
		
		for(PlanListener lis : listeners)
			lis.itemListChanged(new PlanEvent(this, PlanEvent.REMOVED, item.getGuid()));
	}
	
	/**
	 * Method used to add a new plan item and set the corresponding mappings
	 * @param item PlanItem to add
	 * @param uid JWBUID shape id
	 */
	public void addItem(PlanItem item, JWBUID uid)
	{
		planitems.put(item.getGuid(), item);
		setShapeMapping(item.getGuid(), uid);

		for(PlanListener lis : listeners)
			lis.itemListChanged(new PlanEvent(this, PlanEvent.ADDED, item.getGuid()));
	}

	public void addItem(PlanItem item, List<JWBUID> uids)
	{
		for(JWBUID id : uids)
			addItem(item, id);
	}

	/**
	 * Adds an item to the AbstractPlan without mapping the item to a shape.
	 * -CM temporary support for consolidators.
	 * @param item
	 */
	public void addUnmappedItem(PlanItem item)
	{
		planitems.put(item.getGuid(), item);
	}

	/**
	 * Returns the item specified by a given key.
	 * @param key the item guid
	 * @return the item
	 */
	public PlanItem getItem(Guid key)
	{
		return planitems.get(key);
	}

	/**
	 * Method returns all items
	 *
	 * @return HashMap
	 */
	public synchronized HashMap<Guid, PlanItem> getItems()
	{
		return planitems;
	}


	/**
	 * Set plan item to shape mapping
	 * @param pitem Guid
	 * @param shapeUID String
	 */
	public void setShapeMapping(Guid pitem, com.c3i.jwb.JWBUID shapeUID)
	{
		itemToShape.put(pitem, shapeUID);
		shapeToItem.put(shapeUID, pitem);
	}

	/**
	 * Remove plan item to shape mapping
	 * @param pitem Guid
	 */
	public void remShapeMapping(Guid pitem)
	{
		itemToShape.remove(pitem);
		//shapeToItem.remove(key)
	}

	/**
	 * Because there can now be multiple shapes mapped to 1 planitem (consolidator) this will
	 * remove a specific shape from the items list of mapped shapes
	 * This will only remove the item itself from the map if it has no more shapes mapped 
	 * @param pItem PlanItem ID to remove the shape ID from
	 * @param shapeID shape ID to remove
	 * @return true if the items shape list is now empty and the item iteself has been removed
	 */
	private boolean remShapeMapping(Guid pItem, JWBUID shapeID)
	{
		itemToShape.get(pItem).remove(shapeID);
		if(itemToShape.get(pItem).size() < 1)
		{
			itemToShape.remove(pItem);
			return true;
		}
		else
			return false;
	}

	/**
	 * Method returns 1st level items that caused, inhibited or effected a given event
	 * @param pitem Guid event in question
	 * @param mode int CAUSE,EFFECT, INHIBITOR
	 * @return List event guids list
	 * @throws Exception no such item exists in the plan
	 */
	public List getFirstLevelItems(Guid pitem, int mode) throws Exception
	{
		List<Guid> items = new LinkedList<Guid>();
		// add the item itself
		items.add(pitem);
		Event e = (Event) planitems.get(pitem);
		if(e == null)
			throw new Exception("No such plan item exists.");
		else
		{
			Iterator i = null;
			if(mode == SignalType.CAUSAL)
				i = e.getCauses().iterator();
			else if(mode == SignalType.INHIBITING)
				i = e.getInhibitors().iterator();
			else if(mode == SignalType.EFFECT)
				i = e.getEffects().iterator();

			Mechanism m = null;
			for(; i.hasNext();)
			{
				m = (Mechanism) planitems.get(i.next());
				items.add(m.getGuid());
				if(mode == SignalType.EFFECT)
				{
					Iterator<Guid> toEvents = m.getInfluencedEvents().iterator();
					while(toEvents.hasNext())
					{
						Guid toEvent = toEvents.next();
						if(!items.contains(toEvent))
							items.add(toEvent);
					}
				}
				else
				{
					if(!items.contains(m.getFromEvent()))
						items.add(m.getFromEvent());
				}
			}
			return items;
		}
	}

	/**
	 * Method returns a list of shapes that correspond to given guids.
	 * @param guids
	 * @return List<JWBUID> shape ids
	 */
	public List<JWBUID> getShapeMapping(List guids)
	{
		List<JWBUID> shapes = new LinkedList<JWBUID>();
		Iterator i = guids.iterator();
		for(; i.hasNext();)
		{
			shapes.addAll(itemToShape.get((Guid) i.next()));
		}
		return shapes;
	}

	/**
	 * Method returns a PlanItem to JWBShape mapping
	 * @param id
	 * @return
	 */
	public List<JWBUID> getShapeMapping(Guid id)
	{
		return itemToShape.get(id);
	}

	/**
	 * Method returns a JWBShape to PlanItem mapping
	 * @param suid JWBUID
	 * @return Guid plan item id
	 */
	public Guid getGuidMapping(JWBUID suid)
	{
		return shapeToItem.get(suid);
	}

	/**
	 * Returns true if a given guid corresponds to a PlanItem in the plan
	 * @param guid
	 * @return
	 */
	public boolean containsItem(Guid guid)
	{
		return planitems.containsKey(guid);
	}

	/**
	 * Method returns all events in the plan
	 */
	public synchronized Collection<Event> getAllEvents()
	{
		ArrayList<Event> events = new ArrayList<Event>();

		for(PlanItem item : planitems.values())
		{
			if(item.getItemType() == PlanItem.EVENT)
				events.add((Event) item);
		}
		return events;
	}

	/**
	 * Method returns all mechanisms in the plan
	 * @return Collection<Mechanism> mechs
	 */
	public Collection<Mechanism> getAllMechanisms()
	{
		ArrayList<Mechanism> mechs = new ArrayList<Mechanism>();
		for(PlanItem item : planitems.values())
		{
			if(item.getItemType() == PlanItem.MECHANISM)
				mechs.add((Mechanism) item);
		}
		return mechs;
	}

	public int getInputSize(Guid eventGuid)
	{
		Event e = (Event) planitems.get(eventGuid);
		return e.getCauses().size() + e.getInhibitors().size();

	}

	public String getPlanName()
	{
		return this.name;
	}

	public void setPlanName(String newName)
	{
		int strtInd = (newName.lastIndexOf(EnvUtils.sep) >= 0) ? newName.lastIndexOf(EnvUtils.sep)+1 : 0;
		int endInd = (newName.toLowerCase().endsWith(".jcat")) ? newName.toLowerCase().lastIndexOf(".jcat") : newName.length();
		
		name = newName.substring(strtInd, endInd);
	}

	public String getPlanItemName(Guid planID)
	{
		return ((PlanItem) planitems.get(planID)).getName();
	}

	public String getEventObjectName(Guid planID)
	{
		return ((PlanItem) planitems.get(planID)).getLabel();
	}

	public java.util.TreeMap getSchedule(Guid planID)
	{
		return ((PlanItem) planitems.get(planID)).getSchedule();
	}

//	public void buildBayesNet()
//	{
//		SamplingOptions dlg = new SamplingOptions(this);
//	}

	/**
	 * Starts the sampler for this plan (called by sampling options dialog)
	 */
	public void buildBayesNet(int timeSpan) throws SignalException, GraphLoopException, SamplerMemoryException
	{
		if(this.planitems.isEmpty())
		{
			this.bayesNet = null;
		}
		else
		{
			// if there is a sampler running, kill it before creating a new one (replacing the bayesNet variable
			// does not destory the existing sampling thread within the BayesNet)
			if(this.bayesNet != null)
				bayesNet.killSampler();
			this.bayesNet = new JCatBayesBuilder(this, this.library).buildBayesNet(timeSpan);
			if(bayesNet != null)
			{
				try{
					startSampler(timeSpan);
				}catch(OutOfMemoryError exc){
					throw new SamplerMemoryException(exc);
				}

				for(PlanListener lis : listeners)
					lis.bayesNetBuilt(new PlanEvent(this));
				
				if(hasActiveCOAs())
					startCOASampleWait(1000); //TODO:  LOW-PRIORITY  this number should be changed based on plan maybe
				isBuilding = false;
			}
			else
			{
				logger.error("buildBayesNet - BayesNet construction failed; sampler is not running");
			}
		}
	}

	//wait for specified number of samples, then store pred probs in COA for later comparison
	private void startCOASampleWait(int samples)
	{
		if(bayesNet == null || !bayesNet.isSampling())
			return;

		final int stopSamples = samples;
		new Thread(new Runnable()
		{

			public void run()
			{
				while(bayesNet.getSampleCount() < stopSamples && bayesNet.isSampling())
				{
					try
					{
						Thread.sleep(1000);
					}catch(InterruptedException e)
					{
					}
				}

				//now with enough samples, store the probs for each tracked node
				try
				{
					for(int x = 0; x < activeCOAs.length; x++)
					{
						if(activeCOAs[x] == null)
							continue;
						for(Guid itemID : activeCOAs[x].getItemGuids())
						{
							COAState coaSt = activeCOAs[x].get(itemID);
							coaSt.setPredictedProbs(getPredictedProbs(itemID));
						}
						activeCOAs[x].setHasSampled(true);
					}
				}catch(BayesNetException exc)
				{
					logger.error("startCOASampleWait - bayesnet error! \n" + exc.getMessage());
				}
			}

		}, "COA-Sampler-wait").start();

	}

	public void startSampler(int timeSpan)
	{
		this.bayesNet.sampleDistribution(timeSpan);
	}

	public String toString()
	{
		return planitems.toString();
	}

	/**
	 * @param planID
	 * @return
	 */
	public double[] getInferredProbs(Guid planID) throws BayesNetException
	{
		if(bayesNet == null)
			throw new BayesNetException("The BayesNet has not been constructed.");
		return bayesNet.getInferredProbs(planID);
	}

	public double[] getPredictedProbs(Guid planID) throws BayesNetException
	{
		if(bayesNet == null)
			throw new BayesNetException("The BayesNet has not been constructed.");
		return bayesNet.getPredictedProbs(planID);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see mil.af.rl.jcat.plan.PlanInterface#getLeak(mil.af.rl.jcat.util.Guid)
	 */
	public float getLeak(Guid planID)
	{
		return ((Event) planitems.get(planID)).getLeak();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see mil.af.rl.jcat.plan.PlanInterface#getLeak(mil.af.rl.jcat.util.Guid)
	 */
	public int getDelay(Guid planID)
	{
		if(planID == null)
			return 0;
		PlanItem i = (PlanItem) planitems.get(planID);
		return i.getDelay();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see mil.af.rl.jcat.plan.PlanInterface#getPersistence(mil.af.rl.jcat.util.Guid)
	 */
	public int getPersistence(Guid planID)
	{
		// modified by JFL 14 Dec 04
		int retVal = 1;
		try
		{
			PlanItem item = ((PlanItem) planitems.get(planID));
			if(item != null)
			{// the problem here is that sometimes the planID guid is "none
				// assigned" so that nothing gets found in the hashtable
				retVal = item.getPersistence();
			}
		}catch(NullPointerException e)
		{
			logger.warn("getPersistance - NullPointerExc getting persistance from item:  " + e.getMessage());
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see mil.af.rl.jcat.plan.PlanInterface#getContinuationProbability(mil.af.rl.jcat.util.Guid)
	 */
	public float getContinuationProbability(Guid planID)
	{
		PlanItem item = ((PlanItem) planitems.get(planID));
		if(item == null)
		{
			return 0.0f;
		}
		else
		{
			return ((PlanItem) planitems.get(planID)).getContinuation();
		}
	}

	public Event getEventFromProcess(Guid procID)
	{
		Iterator allEvents = this.getAllEvents().iterator();
		while(allEvents.hasNext())
		{
			Event thisItem = (Event) allEvents.next();
			if(thisItem.getProcessGuid().equals(procID))
				return thisItem;
		}
		return null;
	}

	/**
	 * Gets the signals for the event
	 *
	 * @param toEvent
	 * @return
	 * @throws Exception
	 */
	public Collection getSignalsForEvent(Event toEvent)
	{
		Collection sigs = new LinkedList();
		sigs.addAll(library.getCausalSignals(toEvent.getProcessGuid()));
		sigs.addAll(library.getInhibitingSignals(toEvent.getProcessGuid()));
		sigs.addAll(library.getEffectSignals(toEvent.getProcessGuid()));
		if(sigs.size() > 0)
			return sigs;
		return null;
	}

	/**
	 * @param toEvent
	 * @return
	 */
	public Collection getSignalNamesForEvent(Event toEvent)
	{
		Collection sigs = new LinkedList();
		Collection names = new LinkedList();
		sigs.addAll(toEvent.getCauses());
		sigs.addAll(toEvent.getInhibitors());
		if(sigs.size() > 0)
		{
			Object[] ary = sigs.toArray();
			for(int i = 0; i < sigs.size(); i++)
				names.add(library.getSignal(((Mechanism) planitems.get((Guid) ary[i])).getSignalGuid()));
			return names;
		}
		return null;
	}

	/**
	 * @return
	 */
	public Library getLibrary()
	{
		return this.library;
	}

	/**
	 * @return Returns the bayesNet.
	 */
	public BayesNet getBayesNet()
	{
		return bayesNet;
	}

	/**
	 * @return Returns the filePath.
	 */
	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * @param filePath The filePath to set.
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public void fireWasOpened()
	{
		setModified(false);
		
		for(PlanListener lis : listeners)
			lis.opened(new PlanEvent(this));
	}
	
	public void fireWasSaved()
	{
		setModified(false);
		setPlanName(filePath);
		
		for(PlanListener lis : listeners)
			lis.saved(new PlanEvent(this));
	}
	
	public void fireActiveCOAChanged()
	{
		
	}
	
	public void setDocumentation(Documentation doc)
	{
		documentation = doc;
		
		for(PlanListener lis : listeners)
			lis.documentationChanged(new PlanEvent(this));
	}

	public Documentation getDocumentation()
	{
		return documentation;
	}

	public void cleanup()
	{
		if(bayesNet != null)
		{
			bayesNet.killSampler();
			bayesNet = null;
		}
		this.library = null;
		this.planitems = null;
		this.documentation = null;
		this.filePath = null;
		this.name = null;

	}

	/**
	 * @return Returns the modified.
	 */
	public boolean isModified()
	{
		return modified;
	}

	public boolean isBuilding()
	{
		return isBuilding;
	}

	public void setIsBuilding(boolean val)
	{
		isBuilding = val;
	}

	/**
	 * @param modified The modified to set.
	 */
	public void setModified(boolean modified)
	{
		boolean firstMod = (!this.modified && modified); //first modification since save
		this.modified = modified;
		
		if(firstMod)
			for(PlanListener lis : listeners)
				lis.wasModified(new PlanEvent(this));
	}

	/* (non-Javadoc)
	 * @see mil.af.rl.jcat.plan.PlanInterface#getEvidence(mil.af.rl.jcat.util.Guid)
	 */
	public HashMap getEvidence(Guid planID)
	{
		return ((PlanItem) planitems.get(planID)).getEvidence();
	}

	/**
	 * @return Returns the id.
	 */
	public Guid getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Guid id)
	{
		this.id = id;
	}

	//the colorScheme is stored in the plan for sole purpose of changing the scheme
	//to fit the proper plan when multiple plans are open and being jumped between
	public void setColorScheme(ColorScheme inputScheme)
	{
		cScheme = inputScheme;
		
		for(PlanListener lis : listeners)
			lis.colorSchemeChanged(new PlanEvent(this));
	}

	public ColorScheme getColorScheme()
	{
		return cScheme;
	}

	public void setDefaultFont(java.awt.Font newFont)
	{
		defFont = newFont;
	}

	public java.awt.Font getDefaultFont()
	{
		return defFont;
	}

	public void loadProbabilites(Object[] items)
	{
		/*for(int i = 0; i < items.length; i++)
		 ((PlanItem)items[i]).setPriorProbs(this.bayesNet.getPredictedProbs(((PlanItem)items[i]).getGuid()));   */
	}

	public void setLoadedPlanLength(int readLength)
	{
		loadedPlanLength = readLength;
	}

	public int getLoadedPlanLength()
	{
		return loadedPlanLength;
	}

	public float[] getDefaultProbSet()
	{
		return activeDefaultsSet;
	}

	public void setDefaultProbSet(float[] set)
	{
		if(Arrays.equals(set, STANDARD_DEFAULTS_SET)) //must be a user defined one
			activeDefaultsSet = STANDARD_DEFAULTS_SET;
		else if(Arrays.equals(set, AND_OR_DEFAULTS_SET))
			activeDefaultsSet = AND_OR_DEFAULTS_SET;
		else
		{
			USER_DEFINED_DEFAULTS_SET = set;
			activeDefaultsSet = USER_DEFINED_DEFAULTS_SET;
		}

	}

	public ResourceManager getResourceManager()
	{
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager)
	{
		this.resourceManager = resourceManager;
	}

	/**
	 * Apply a COA onto this plan.  For changes to be made, applyCOA should be called in Control which will
	 * call this method upon completion
	 * @param theCOA
	 */
	public void applyCOA(COA theCOA)
	{
		setCoaActive(theCOA);
		
		for(PlanListener lis : listeners)
			lis.activeCOAChanged(new PlanEvent(this));
	}
	
	/**
	 * Performs checks before setting a coa as active
	 * @param theCOA
	 */
	private void setCoaActive(COA theCOA)
	{
		// remember which COAs are active (allowing a possible 3 COAs to be 'active' if they dont track any of the same info)
		// if this COA and the one previously applied [x] are tracking any of the same info, replace [x] otherwise check current [1]
		// to see if it contains any same info replace [x+1] otherwise put in [x+2]
		if(checkTrackSameParameters(theCOA, activeCOAs[0]) || activeCOAs[0] == null)
			activeCOAs[0] = theCOA;
		else if(!checkTrackSameParameters(theCOA, activeCOAs[0]) && occursMultiple(activeCOAs[0], activeCOAs))
			activeCOAs[0] = theCOA;
		if(checkTrackSameParameters(theCOA, activeCOAs[1]) || activeCOAs[1] == null)
			activeCOAs[1] = theCOA;
		else if(!checkTrackSameParameters(theCOA, activeCOAs[1]) && occursMultiple(activeCOAs[1], activeCOAs))
			activeCOAs[1] = theCOA;
		if(checkTrackSameParameters(theCOA, activeCOAs[2]) || activeCOAs[2] == null)
			activeCOAs[2] = theCOA;
		else if(!checkTrackSameParameters(theCOA, activeCOAs[2]) && occursMultiple(activeCOAs[2], activeCOAs))
			activeCOAs[2] = theCOA;
		
	}

	public void clearCOA()
	{
		activeCOAs[0] = null;
		activeCOAs[1] = null;
		activeCOAs[2] = null;
		
		for(PlanListener lis : listeners)
			lis.activeCOAChanged(new PlanEvent(this));
	}

	//check to see if 2 specified COAs are tracking any of the same parameters (schedule, resources, elicited probs)
	private boolean checkTrackSameParameters(COA firstCOA, COA secondCOA)
	{
		if(firstCOA == null || secondCOA == null)
			return false;
		if(firstCOA.isTrackSchedule() && secondCOA.isTrackSchedule())
			return true;
		if(firstCOA.isTrackResources() && secondCOA.isTrackResources())
			return true;
		if(firstCOA.isTrackElicited() && secondCOA.isTrackElicited())
			return true;

		return false;
	}

	private boolean occursMultiple(Object inputObj, Object[] array)
	{
		int occurs = 0;
		for(Object thisObj : array)
			if(thisObj.equals(inputObj))
				occurs++;
		return(occurs > 1);
	}

	public COA createCOA(String name, boolean trackSched, boolean trackRes, boolean trackElicit, boolean clearUntracked, boolean addToList)
	{
		COA newCOA = new COA(name);

		newCOA.setTrackSchedule(trackSched);
		newCOA.setTrackResources(trackRes);
		newCOA.setTrackElicited(trackElicit);
		newCOA.setClearUntracked(clearUntracked);

		HashMap items = getItems();
		for(Object key : items.keySet())
		{
			PlanItem thisItem = (PlanItem) items.get(key);
			ElicitationSet[] eSets = null;

			// extract elicited values to use in the coa
			if(thisItem.getItemType() == PlanItem.EVENT)
			{
				Process proc = getLibrary().getProcess(((Event) thisItem).getProcessGuid());
				eSets = new ElicitationSet[3];

				try
				{
					ProtocolSet prot = proc.getModeSet(SignalType.CAUSAL).findProtocol(SignalType.RNOR);
					if(prot != null) //there might not be any protocol set in there (no elicitations in that event)
						eSets[0] = (ElicitationSet) prot.getElicitations().clone(); //mode field values are 1,2,3(C,I,E)
				}catch(SignalException exc)
				{
					logger.warn("actionPerformed(creatCOA) - No rnor protocolset found in modeset " + 1 + " while creating COA");
				}
				try
				{
					ProtocolSet prot = proc.getModeSet(SignalType.INHIBITING).findProtocol(SignalType.RNOR);
					if(prot != null)
						eSets[1] = (ElicitationSet) prot.getElicitations().clone();
				}catch(SignalException exc)
				{
					logger.warn("actionPerformed(creatCOA) - No rnor protocolset found in modeset " + 2 + " while creating COA");
				}
				try
				{
					ProtocolSet prot = proc.getModeSet(SignalType.EFFECT).findProtocol(SignalType.RNOR);
					if(prot != null)
						eSets[2] = (ElicitationSet) prot.getElicitations().clone();
				}catch(SignalException exc)
				{
					logger.warn("actionPerformed(creatCOA) - No rnor protocolset found in modeset " + 3 + " while creating COA");
				}
			}

			//create a coa-state for this item
			COAState thisItemState = new COAState(thisItem.getGuid(), thisItem.getDelay(), thisItem.getPersistence(), thisItem.getContinuation(), thisItem.getSchedule(), thisItem
					.getResources(), thisItem.getThreatResources(), eSets);
			newCOA.put(thisItem.getGuid(), thisItemState);
		}

		if(addToList)
			coaList.add(newCOA);
		
		for(PlanListener lis : listeners)
			lis.coaListChanged(new PlanEvent(this, PlanEvent.ADDED));

		return newCOA;
	}

	public Vector<COA> getCOAList()
	{
		return coaList;
	}

	public void setCOAList(Vector<COA> newCoaList)
	{
		coaList = newCoaList;
		
		for(PlanListener lis : listeners)
			lis.coaListChanged(new PlanEvent(this));
	}

	public COA[] getActiveCOA()
	{
		return activeCOAs;
	}
	
	public int[] getActiveCOAIndicies()
	{
		int[] coaInd = new int[activeCOAs.length];
		for(int x=0; x < activeCOAs.length; x++)
			coaInd[x] = coaList.indexOf(activeCOAs[x]);
		
		return coaInd;
	}
	
	public void setActiveCOAs(int[] indicies)
	{
		for(int x : indicies)
			if(x > 0 && x < coaList.size())
				setCoaActive(coaList.get(x));
		
		for(PlanListener lis : listeners)
			lis.activeCOAChanged(new PlanEvent(this));
	}

	public boolean hasActiveCOAs()
	{
		for(COA aCoa : activeCOAs)
			if(aCoa != null)
				return true;

		return false;
	}

	public boolean isEmpty()
	{
		return planitems.isEmpty();
	}

	public MultiMap<Guid, JWBUID> getItemMap()
	{
		return itemToShape;
	}

	/**
	 * get a sub-section of the item map (only map info for requested items
	 */
	public MultiMap<Guid, JWBUID> getItemMap(List<PlanItem> items)
	{
		MultiMap<Guid, JWBUID> partial = new MultiMap<Guid, JWBUID>();

		for(PlanItem item : items)
			for(JWBUID uid : itemToShape.get(item.getGuid()))
				partial.put(item.getGuid(), uid);

		return partial;
	}

	public void setLoadComplete(boolean val)
	{
		wasRetreived = val;
	}

	public boolean getLoadComplete()
	{
		return wasRetreived;
	}

	public void addPolicy(Policy policy)
	{
		activePolicies.add(policy);

	}

}
