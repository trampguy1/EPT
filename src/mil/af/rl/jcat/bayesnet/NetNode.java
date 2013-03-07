/*
 * Created on May 11, 2006
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.util.Guid;

/**
 * This class is a Data container for th BayesNet and related classes. THERE IS TO BE NO COMPUTATION
 * OCCURING IN THIS CLASS OR ANY DERIVED CLASSES. This class is a data container.
 *
 * @author mcnamacr
 */
public class NetNode implements Serializable, Comparable
{
	//ID used to keep track of NetNodes in Genie
    private static int g = 0;
    protected int depth = 0;
    protected Boolean closesLoop = false;
    protected NodeType type;
    protected ArrayList<NetNode> causes = new ArrayList<NetNode>();
    protected ArrayList<NetNode> effects = new ArrayList<NetNode>();
    protected ArrayList<NetNode> inhibitors = new ArrayList<NetNode>();
    private Guid planID;
    private Guid libID;
    private String name;
    private String description;
    private String genieID = "Node" + g++;
    protected Vector<Float> causalCPT;
    protected Vector<Float> inhibitingCPT;
    protected Vector<Float> effectCPT;
    //Temporal information
    protected float [] schedule;
    protected float [] leak;
    protected int nodeEffectSliceDelay;
    protected float continuationProbability;
    protected int persistence = 1; //Default Persistence
    protected HashMap<Integer, Evidence> evidence;
    protected SimpleResource resources = null;
	protected SimpleResource inferredResources = null;
	private int resourceUseType = HIGHEST_PROB_USE; //defaults to AND
    public static final int RESOURCE_USE_AND = 0;
    public static final int RESOURCE_USE_OR = 1;
	public static final int HIGHEST_PROB_USE = 2;
	public static final int USER_PRIORITY_USE = 3;
	public static final int POLICY_TABLE = 4;
    private static Logger logger = Logger.getLogger(NetNode.class);
    public Policy policy = null;

    enum ResourceSet {Predicted, Inferred};

    public NetNode(Guid libID, NodeType type)
    {
        super();
        this.name = type.name();
        this.libID = libID;
        this.type = type;
    }

    public NetNode(Guid planID, Guid libID, NodeType type)
    {
        super();
        this.planID = planID;
        this.libID = libID;
        this.type = type;
    }

    public void addCause(NetNode node)
    {
        this.causes.add(node);
    }

    public void addInhbitor(NetNode node)
    {
        this.inhibitors.add(node);
    }

    public void addEffect(NetNode node)
    {
        this.effects.add(node);
    }

    public boolean removeCause(NetNode node)
    {
        return this.causes.remove(node);
    }

    public boolean removeInhibitor(NetNode node)
    {
        return this.inhibitors.remove(node);
    }

    public boolean removeEffect(NetNode node)
    {
        return this.effects.remove(node);
    }

    /**
     * @return Returns the causes.
     */
    public ArrayList<NetNode> getCauses()
    {
        return causes;
    }

    /**
     * @param causes The causes to set.
     */
    public void setCauses(ArrayList<NetNode> causes)
    {
        this.causes = causes;
    }

    /**
     * @return Returns the effects.
     */
    public ArrayList<NetNode> getEffects()
    {
        return effects;
    }

    /**
     * @param effects The effects to set.
     */
    public void setEffects(ArrayList<NetNode> effects)
    {
        this.effects = effects;
    }

    /**
     * @return Returns the inhibitors.
     */
    public ArrayList<NetNode> getInhibitors()
    {
        return inhibitors;
    }

    /**
     * @param inhibitors The inhibitors to set.
     */
    public void setInhibitors(ArrayList<NetNode> inhibitors)
    {
        this.inhibitors = inhibitors;
    }
    /**
     * Added to support explaination as a hack. -CM 9/25/06 9:30pm
     *
     * @return a list of NetNodes, child effect first and followed by immediate paternal causes.
     */
    public List<NetNode> getExplainNodes()
    {
    	ArrayList<NetNode> nodes = new ArrayList<NetNode>();
    	nodes.add(this);
    	nodes.addAll(this.causes);
    	nodes.addAll(this.inhibitors);
    	return nodes;
    }

    /**
     * @return Returns the libID.
     */
    public Guid getLibID()
    {
        return libID;
    }

    /**
     * @return Returns the planID.
     */
    public Guid getPlanID()
    {
        return planID;
    }

    /**
     * @return Returns the type.
     */
    public NodeType getType()
    {
        return type;
    }

    /**
     * @return Returns the depth.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * @param depth The depth to set.
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public String getGenieID()
    {
        return this.genieID;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return Returns the causalCPT.
     */
    public Vector<Float> getCausalCPT()
    {
        return causalCPT;
    }

    /**
     * @return Returns the closesLoop.
     */
    public Boolean getClosesLoop()
    {
        return closesLoop;
    }

    /**
     * @return Returns the continuationProbability.
     */
    public float getContinuationProbability()
    {
        return continuationProbability;
    }

    /**
     * @return Returns the effectCPT.
     */
    public Vector<Float> getEffectCPT()
    {
        return effectCPT;
    }

    /**
     * @return Returns the evidence.
     */
    public HashMap<Integer, Evidence> getEvidence()
    {
        return evidence;
    }

    /**
     * @return Returns the inhibitingCPT.
     */
    public Vector<Float> getInhibitingCPT()
    {
        return inhibitingCPT;
    }

    /**
     * @return Returns the leak.
     */
    public float[] getLeak()
    {
        return leak;
    }

    /**
     * @return Returns the nodeEffectSlicenetNodeDelay.
     */
    public int getNodeEffectSliceDelay()
    {
        return nodeEffectSliceDelay;
    }

    /**
     * @return Returns the persistence.
     */
    public int getPersistence()
    {
        return persistence;
    }

    /**
     * @return Returns the schedule.
     */
    public float[] getSchedule()
    {
        return schedule;
    }

	public int getResourceUseType()
	{
		return resourceUseType;
	}

    public void setResourceUseType(int type)
	{
		resourceUseType = type;
	}

    public SimpleResource getResources(ResourceSet set){
    	switch(set){
    		case Predicted:
    			return this.resources;
     		case Inferred:
    			return this.inferredResources;
    		default:
    			return null;
    	}
    }

    public String toString()
    {
        if(name == null && type == NodeType.Adder)
            return "Adder";
        else
        {
        	return this.getName() +((getDescription() != null) ? " ("+ this.getDescription() + ")" : "") +" ["+type.toString()+ "]";
        }
    }

    //allows NetNodes to be sorted in a SortedMap, alphabetically for now, maybe toposort style later
    public int compareTo(Object nd)
	{
    	NetNode cNode = (NetNode)nd;
    	String thisName = getName() + " ("+getDescription()+") " + this.getPlanID();
    	String thatName = cNode.getName() + " ("+cNode.getDescription() + ") " + cNode.getPlanID();

			//sort alphabetically
    	if(thisName.compareTo(thatName) < 0)
    		return -1;
    	else if(thisName.compareTo(thatName) == 0)
    		return 0;
    	else
    		return 1;
	}


    public SimpleResource getResources()
	{
		return resources;
	}


	public void setPolicy(Policy inPolicy) {
		this.policy = inPolicy;
		if(policy != null){
			policy.addMember(this);
		}
	}

}
