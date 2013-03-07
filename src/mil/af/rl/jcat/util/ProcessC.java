package mil.af.rl.jcat.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ProcessC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7836900354492495047L;
	
    private ArrayList<SignalC> causes = new ArrayList<SignalC>();
    private ArrayList<SignalC> effects = new ArrayList<SignalC>();
    private ArrayList<SignalC> inhibits = new ArrayList<SignalC>();
    private ArrayList<ElicitationC> elicitations = new ArrayList<ElicitationC>();
    private String name;
    
	public ProcessC(String n) {
		super();
		name = n;
	}
	public String getName()
	{
		return name;
	}
	public void addCause(SignalC c)
	{
		causes.add(c);
	}
	public void addEffect(SignalC e)
	{
		effects.add(e);
	}
	public void addInhibit(SignalC i)
	{
		inhibits.add(i);
	}
	public void addElicitation(ElicitationC ec)
	{
		this.elicitations.add(ec);
	}
	
	public ArrayList<SignalC> getCauses() {
		return causes;
	}

	public ArrayList<SignalC> getEffects() {
		return effects;
	}

	public ArrayList<ElicitationC> getElicitations() {
		return elicitations;
	}

	public ArrayList<SignalC> getInhibits() {
		return inhibits;
	}

}
