package mil.af.rl.jcat.plan;

import java.awt.AWTEvent;
import java.util.List;

import mil.af.rl.jcat.util.Guid;



public class PlanEvent extends AWTEvent
{
	public static final int ADDED = 1000;
	public static final int REMOVED = 1001;
	
	private List<Guid> itemIDs;
	private AbstractPlan sourcePlan;

	
	public PlanEvent(AbstractPlan source)
	{
		this(source, -1);
	}
	
	public PlanEvent(AbstractPlan source, int id)
	{
		super(source, id);
		sourcePlan = source;
	}
	
	public PlanEvent(AbstractPlan source, int id, List<Guid> itemIDs)
	{
		this(source, id);
		this.itemIDs = new java.util.Vector<Guid>(itemIDs);
	}
	
	public PlanEvent(AbstractPlan source, int id, Guid itemID)
	{
		this(source, id);
		itemIDs = new java.util.Vector<Guid>();
		itemIDs.add(itemID);
	}
	
	
	public AbstractPlan getPlan()
	{
		return sourcePlan;
	}
	
	public List<Guid> getItemIDs()
	{
		return itemIDs;
	}
	
}
