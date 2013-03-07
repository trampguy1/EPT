package mil.af.rl.jcat.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ElicitationC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 500972497423628789L;
	private float prob;
	private int protocol;
	private String name;
	private ArrayList<SignalC> signals;
	private ArrayList<Guid> guids;
	
	public ElicitationC(String name,float probability,int protocol, ArrayList<SignalC> sigs) {
		super();
		this.name = name;
		prob = probability;
		this.protocol = protocol;
		signals = sigs;
		guids = new ArrayList<Guid>();
		for(SignalC s : signals)
		{
			guids.add(s.getGuid());
		}
	}
	public float getProbability()
	{
		return prob;
	}
	public int getProtocol()
	{
		return protocol;
	}
	public String getName()
	{
		return name;
		
	}
	public ArrayList<SignalC> getSignalSet()
	{
		return signals;
	}
	public ArrayList<Guid> getGuidSet()
	{
		return guids;
	}

}
