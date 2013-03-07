package mil.af.rl.jcat.plan;

import mil.af.rl.jcat.util.Guid;

public class PlanResource {

	public static final int THREAT = 0;
	public static final int OPERATIONAL = 1;
	String name;
	int available;
	int type;
	Guid typeId;
	
	/**
	 * 
	 * @param name resoure name
	 * @param available
	 * @param resourceType ex: PlanResource.THREAT
	 */
	PlanResource(String name, int available, int resourceType)
	{
		this.name = name;
		this.available = available;
		this.type = resourceType;
		typeId = new Guid();
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public String getTypeAsString()
	{
		if(this.type == OPERATIONAL)
			return "Operational";
		else if(this.type == THREAT)
			return "Threat";
		else
			return "Undefined Type";
	}

	public void setType(int type) {
		this.type = type;
	}

	public Guid getTypeId() {
		return typeId;
	}

	public void setTypeId(Guid typeId) {
		this.typeId = typeId;
	}
}
