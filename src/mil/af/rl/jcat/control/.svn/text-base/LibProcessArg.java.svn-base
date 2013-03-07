package mil.af.rl.jcat.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitationSet;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitedProbability;
import mil.af.rl.jcat.processlibrary.signaldata.ModeSet;
import mil.af.rl.jcat.processlibrary.signaldata.ProtocolSet;
import mil.af.rl.jcat.util.ElicitationC;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.ProcessC;
import mil.af.rl.jcat.util.SignalC;


public class LibProcessArg implements Serializable{

    private static final long serialVersionUID = -6823696676455247289L;
    public static final int ADD_PROCESS = 0;
    public static final int REMOVE_PROCESS = 1;
    public static final int COPY_SIGNAL_DATA = 2;
    public static final int COPY_NEW_INSTANCE = 3;
    
    private int defSubType = -1;
    private int op;
    private String pname;
    private Guid proc;
    private Guid fromp;//used for copying signal data
    private HashMap<Guid,SignalC> signalmaping = new HashMap<Guid,SignalC>();
    private transient AbstractPlan plan;
    private ProcessC copy;
	private float[] defProbs = null;
    
    
    /**
     * Creates a process argument when a new process needs to be created.
     * @param operation
     * @param process
     * @param name
     */
    public LibProcessArg(int operation, Guid process, String name, float[] defaults, int defaultsSubType)
    {
        proc = process;
        op = operation;
        pname = name;
        defSubType = defaultsSubType;
        defProbs = defaults;
    }

    /**
     * Creates a process arg when signal data needs to be copied from one process to another
     * @param operation int to perform
     * @param toprocess Guid target process
     * @param fromprocess Guid source process
     */
    public LibProcessArg(int operation, Guid toprocess, Guid originalprocess, AbstractPlan plan, int defaultsSubType)
    {
        op = operation;
        proc = toprocess;
        fromp = originalprocess;
        this.plan = plan;
        defProbs = plan.getDefaultProbSet();
        defSubType = defaultsSubType;
        //this.initProcessCopy();
    }
    
    public LibProcessArg(int op, String name, Guid originalprocess, AbstractPlan plan, int defaultsSubType)
    {
    	this.op = op;
    	fromp = originalprocess;
    	this.plan = plan;
    	pname = name;
    	defProbs = plan.getDefaultProbSet();
        defSubType = defaultsSubType;
    	//proc = new Guid();//to be retrieved later
    	this.initProcessCopy();
    }
    
    public void setNewGuid(Guid guid)
    {
    	proc = guid;
    }
    
    public ProcessC getTransferCopy()
    {
    	return copy;
    }
    
    public String getName()
    {
        return pname;
    }
    
    /**
     * Method returns the type of operation
     * @return
     */
    public int getOperation()
    {
        return op;
    }
    
    /**
     * Method returns the id of the process involved
     * @return
     */
    public Guid getProcessGuid()
    {
        return proc;
    }
    
    public Guid fromProcess()
    {
        return fromp;
    }
    
    private void initProcessCopy()
    {
    	copy = new ProcessC(pname);
    	SignalC scopy;
    	String n;
    	for(Object g : plan.getLibrary().getCausalSignals(fromp))
    	{
    		
    		n = plan.getLibrary().getSignalName(((Signal)g).getSignalID());
    		scopy = new SignalC(n,((Signal)g).getSignalID());
    		this.signalmaping.put(((Signal)g).getSignalID(),scopy);
    		copy.addCause(scopy);
    		
    	}
    	for(Object g : plan.getLibrary().getEffectSignals(fromp))
    	{
    		n = plan.getLibrary().getSignalName(((Signal)g).getSignalID());
    		scopy = new SignalC(n,((Signal)g).getSignalID());
    		this.signalmaping.put(((Signal)g).getSignalID(),scopy);
    		copy.addEffect(scopy);
    	}
    	for(Object g : plan.getLibrary().getInhibitingSignals(fromp))
    	{
    		n = plan.getLibrary().getSignalName(((Signal)g).getSignalID());
    		scopy = new SignalC(n,((Signal)g).getSignalID());
    		this.signalmaping.put(((Signal)g).getSignalID(),scopy);
    		copy.addInhibit(scopy);
    	}
    	// now lets copy all the elicited values (single and groups), don't forget the mappings
    	List<ModeSet> modes = plan.getLibrary().getProcess(fromp).getModeSets();
        for(ModeSet ms : modes)
        {
            for(Object ps : ms.getProtocols())
            {
                ElicitationSet eset = ((ProtocolSet)ps).getElicitations();
                for(Object ep : eset.toArray())
                {
                    ElicitedProbability eprob = (ElicitedProbability)ep;
                    float prob = eprob.getProbability();
                    String name = eprob.getGroupName();
                    ArrayList<Guid> guids = new ArrayList<Guid>(eprob.getSignalSet());
                    ArrayList<SignalC> sigs = new ArrayList<SignalC>();
                    for(Guid g : guids)
                    {
                    	sigs.add(this.signalmaping.get(g));
                    }
                    copy.addElicitation(new ElicitationC(name,prob,((ProtocolSet)ps).getProtocol(),sigs));
                }
            }
        } 
    }
	
    //used for default probability sets (AND/OR) and such
    public int getDefaultsSubType()
	{
		return defSubType;
	}

    public float[] getDefaults()
    {
    	return defProbs;
    }

	
    public void setDefaultsSubType(int defaultsSubType)
	{
		defSubType = defaultsSubType;
	}

}
