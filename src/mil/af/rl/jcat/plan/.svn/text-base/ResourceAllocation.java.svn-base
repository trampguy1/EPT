package mil.af.rl.jcat.plan;

import java.io.Serializable;

import mil.af.rl.jcat.util.Guid;

public class ResourceAllocation implements Serializable
{
	Guid typeId;
	int allocated;
	private String name = "";
	private boolean contingent = false;
	
	/**
	 * used as a container for a specific tyoe and quantity of resources
	 * 
	 * @param typeId
	 * @param allocated
	 */
	public ResourceAllocation(Guid typeId, int allocated)
	{
		this.typeId = typeId;
		this.allocated = allocated;
	}
	
	public ResourceAllocation(Guid id, int allocated, String nm)
	{
		this(id, allocated);
		name = nm;
	}
	
	public ResourceAllocation(Guid id, int allocated, String nm, boolean conting)
	{
		this(id, allocated, nm);
		contingent = conting;
	}
	
	public int getAllocated()
	{
		return allocated;
	}

	public String getName()
	{
		return name;
	}
	
	public boolean isContingent()
	{
		return contingent;
	}
	
	public Guid getID()
	{
		return typeId;
	}
}
