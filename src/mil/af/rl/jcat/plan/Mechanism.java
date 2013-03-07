
package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.util.Guid;


/**
 * Title: Mechanism.java
 * Description: Class represents a mechanism object. As of version 1.1 supports the notion of Consolidators
 * Copyright: Copyright (c) 2004
 * Company: C3I Associates
 * @author Edward Verenich
 * @author Craig McNamara
 * @version 1.1
 */

public class Mechanism extends PlanItem implements Serializable //, JWBAttachment
{

	private static final long serialVersionUID = 3848464579513541696L;
	/**
	 * Mechanism will alway abe an effect at one of its edges, thus we only need
	 * to make a distinction on the destination edge.
	 */

	//    public static final int CAUSE = 0;
	//    public static final int INHIBIT = 1;
	@SuppressWarnings("unused")
	//to emable collaboration of mechanism type information until  collaboration is fixed
	//    private int mechType = 0;
	private boolean feedbackEdge;
	private Guid sigGuid = null;
	private boolean loopCloser;
	private ArrayList<Guid> events = new ArrayList<Guid>(2);

	/**
	 * Creates a new mechanism and uses an existing signal as the underlying signal.
	 *
	 * @param mGuid ID for this mechanism
	 * @param name name for this mechanism
	 * @param to the destination Event this mechanism connects
	 * @param from the source Event this mechanism connects
	 * @param sGuid ID of existing signal
	 */
	public Mechanism(Guid mGuid, String name, Event to, Event from, Guid sGuid)
	{
		this(mGuid, name, to.getGuid(), from.getGuid(), sGuid);
	}

	/**
	 * Creates a new mechanism generating a new signal ID for the underlying signal.
	 *
	 * @param mGuid ID for this mechanism
	 * @param name name for this mechanism
	 * @param to the destination Event this mechanism connects
	 * @param from the source Event this mechanism connects
	 */
	public Mechanism(Guid mGuid, String name, Event to, Event from)
	{
		this(mGuid, name, to, from, new Guid());
	}

	/**
	 * Creates a new mechanism used as a consolidator (connected to multiple destination Events)
	 * 
	 * @param mGuid ID for this mechanism
	 * @param name name for this mechanism
	 * @param to the destination Events this mechanism connects to
	 * @param from the source Event this mechanism connects
	 * @param sGuid ID of existing signal
	 */
	public Mechanism(Guid mGuid, String name, Collection<Event> to, Event from, Guid sGuid)
	{
		super(mGuid, name, "signal", PlanItem.MECHANISM);
		sigGuid = sGuid;

		events.add(from.getGuid());
		for(Event e : to)
			events.add(e.getGuid());
	}

	public Mechanism(Guid mGuid, String name, Guid toEvent, Guid fromEvent)
	{
		this(mGuid, name, toEvent, fromEvent, new Guid());
	}
	
	public Mechanism(Guid mGuid, String name, Guid toEvent, Guid fromEvent, Guid signalID)
	{
		super(mGuid, name, "signal", PlanItem.MECHANISM);
		sigGuid = signalID;

		events.add(fromEvent);
		events.add(toEvent);
	}
	
	/*    private void addCausalSignalsToGroups(Event to, Event from, Library lib) throws SignalModeConflictException
	 //    {
	 //        if (lib.getProcess(to.getProcessGuid()).getInhibitingSignals().contains(this.sigGuid))
	 //            addInhibitor(to, lib);
	 //        else
	 //            addCause(to, lib);
	 //
	 //        // if we succeded in adding either the cause or inhibit then add the effect
	 //        // if there is a problem with that we should remove the cause or inhibit above from the plan before
	 //        // allowing the exception to fall through
	 //        try{
	 //            addEffect(from, lib);
	 //        }catch(SignalModeConflictException exc){
	 //            to.removeCause(getGuid());
	 //            to.removeInhibitor(getGuid());
	 //            throw new SignalModeConflictException(exc.getMessage());
	 //        }
	 }*/

	/**
	 * Gets the events that are Influences by the mechanism. Mechanisms no longer have
	 * a one-to-one relationship with events. Mechanisms may now express a one-to-Many
	 * relationship between events.  Index 0 is always the source/from Event
	 *  
	 * @return collection of Event guids on the leading edge of the mechanism.
	 */
	public Collection<Guid> getInfluencedEvents()
	{
		//Position Zero is the fromEvent Guid so it is excluded -CM
		return events.subList(1, events.size());
	}

	//    /**
	//     *  Gets the mechanism type from whiteboard updates.
	//     * 
	//     * @deprecated pending long term solution involving process library updates
	//     * @return int mechanism type
	//     * 
	//     */
	//    public int getCollaboratedType()
	//    {
	//        return mechType;
	//    }

	public Object clone()
	{
		return this;
	}

	//    public JWBAttachment deepCopy()
	//    {
	//        //Mechanism m = new Mechanism(getName(),isig,mechtype,library);
	//        //m.setDocumentation((Documentation)getDocumentation().clone());
	//        //return m;
	//        return this;
	//    }

	//    public Object getAttachment()
	//    {
	//        return this;
	//    }

	/**
	 * Returns the ID of the origin event
	 *
	 * @return origin/from Event ID
	 */
	public Guid getFromEvent()
	{
		//As long as the mechanism is valid this will never be null
		return events.get(0);
	}

	/**
	 * Get the ID for the internal signal used in this Mechanism
	 * @return signal ID
	 */
	public Guid getSignalGuid()
	{
		return sigGuid;
	}

	/**
	 * Returns the guid of the destination event
	 *
	 * @param index - the index of the effect you want (mechanisms may connect to multiple destination/to Events
	 * @return ID destination event
	 */
	public Guid getToEvent(int index)
	{
		return events.get(index + 1);
	}

	/**
	 * Get the number of destination/to Events this Mechanism connects to 
	 * @return number of Events
	 */
	public int getNumToEvents()
	{
		return events.size() - 1;
	}

	/**
	 * Used to determine the number of edges combined in to a consolidator.
	 * 
	 * @return int the size of the consolidator 
	 */
	public int getConsolidatorSize()
	{
		//Size minus one for the origin 
		return this.events.size() - 2;
	}

	/**
	 * Returns the feedback flag value
	 *
	 * @return boolean
	 */
	public boolean isFeedbackEdge()
	{
		return feedbackEdge;
	}

	/**
	 * Returns the loopCloser flag value
	 * @return boolean
	 */
	public boolean isLoopCloser()
	{
		return loopCloser;
	}

	/**
	 * Sets the feedback edge flag
	 *
	 * @param feedbackEdge
	 */
	public void setFeedbackEdge(boolean feedbackEdge)
	{
		this.feedbackEdge = feedbackEdge;
	}

	/**
	 * Sets the mechanism origin event
	 *
	 * @param fromEvent ID of the origin/from Event
	 */
	public void setFromEvent(Guid fromEvent)
	{
		events.set(0, fromEvent);
	}

	/**
	 * Sets the destination Event of the mechanism
	 *
	 * @param toEvent
	 */
	public void setToEvent(Guid toEvent)
	{
		events.set(1, toEvent);
	}

	/**
	 * Adds another event to the consolidated mechanism
	 * 
	 * @param toEvent
	 */
	public void addConsolidatedOutput(Event toEvent)
	{
		//        this.mechType = mechType;
		events.add(toEvent.getGuid());
		//addCausalSignalsToGroups(toEvent, fromEvent, lib);
		return;
	}

	/**
	 * Removes a consolidated output from the mechanism
	 * @param event
	 */
	public void removeConsolidatedOutput(Guid event)
	{
		if(events.contains(event))
			events.remove(event);
	}

	/**
	 * Checks to see if this mechanism was turned in to a consolidator
	 * 
	 * @return boolean value indicating whether the mechanism is a Consolidator 
	 */
	public boolean isConsolidator()
	{
		if(this.events.size() > 2)
			return true;
		else
			return false;
	}

	/**
	 * Sets loopCloser flag
	 * @param loopCloser
	 */
	public void setLoopCloser(boolean loopCloser)
	{
		this.loopCloser = loopCloser;
	}

	/*// performs a check before performing the actual Event.addCause
	 private void addCause(Event toEvent, Library lib) throws SignalModeConflictException
	 {
	 Process proc = lib.getProcess(toEvent.getProcessGuid());
	 Guid sigGuid = this.getSignalGuid();
	 
	 // if proc is null then your probably pasting (guid changed) and so there would be no need to perform this check
	 if(proc != null)
	 {
	 if(proc.getInhibitingSignals().contains(sigGuid))
	 throw new SignalModeConflictException("The signal("+ lib.getSignalName(sigGuid) +") is already used by "+proc.getProcessName()+" as a inhibitor", this.getName(), proc.getProcessName(), "inhibitor", "cause");
	 }
	 
	 toEvent.addCause(this);
	 }
	 
	 // performs a check before performing the actual Event.addCause
	 private void addInhibitor(Event toEvent, Library lib) throws SignalModeConflictException
	 {
	 Process proc = lib.getProcess(toEvent.getProcessGuid());
	 Guid sigGuid = this.getSignalGuid();
	 
	 // if proc is null then your probably pasting (guid changed) and so there would be no need to perform this check
	 if(proc != null)
	 {
	 if(proc.getCausalSignals().contains(sigGuid))
	 throw new SignalModeConflictException("The signal("+ lib.getSignalName(sigGuid) +") is already used by "+proc.getProcessName()+" as a cause", this.getName(), proc.getProcessName(), "cause", "effect");
	 }
	 
	 toEvent.addInhibitor(this);
	 }
	 
	 // performs a check before performing the actual Event.addCause
	 private void addEffect(Event toEvent, Library lib) throws SignalModeConflictException
	 {
	 Process proc = lib.getProcess(toEvent.getProcessGuid());
	 Guid sigGuid = this.getSignalGuid();    	
	 toEvent.addEffect(this);
	 } */

	public String getLabel()
	{
		if(!this.isConsolidator())
			return "";
		else
			return " - (Consolidator)";
	}
}
