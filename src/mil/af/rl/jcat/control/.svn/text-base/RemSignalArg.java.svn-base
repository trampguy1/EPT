package mil.af.rl.jcat.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.util.Guid;

/**
 *Title:
 *Copyright: Copyright (c) 2005
 *Company: C3I Associates
 * @author Edward Verenich
 * @version 1.0
 */
public class RemSignalArg implements Serializable{

	private static final long serialVersionUID = 197336913187215748L;
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int RENAME = 2;
	public static final int ADD_ELICITED = 3;
	public static final int REM_ELICITED = 4;
	public static final int CHANGE_ELICITED = 5;
	public static final int ADD_CAUSE = 6;
	public static final int ADD_INHIBITOR = 7;
	public static final int ADD_EFFECT = 8;
	public static final int INVERT = 9;

	private int operation;
	private float value;
	private List<Guid> signals = new ArrayList<Guid>();
	private Signal signal; // used to add individual signals over distributed libraries
	private Guid process;
	private int protocol; // used for elicited probs
	private boolean inv;
	private String groupName = "";
	private String newSigName = "";

	/** Should probably only be used for adding new signal
	 */
	public RemSignalArg(int op, Signal sig)
	{
		operation = op;
		signal = sig;
		signals.add(sig.getSignalID());
	}

	/** Should probably only used for a signal rename operation
	 */
	public RemSignalArg(int op, Guid sigID, String newName)
	{
		operation = op;
		signals.add(sigID);
		newSigName = newName;
	}

	public RemSignalArg(int op, List<Guid> sigs,Guid prcss) {
		operation = op;
		signals.addAll(sigs);
		process = prcss;
	}

	public RemSignalArg(int op, Signal sig, Guid proc, boolean invert)
	{
		operation = op;
		signal = sig;
		process = proc;
		inv = invert;
	}

	public RemSignalArg(int op, List<Guid> sigs, Guid prcss, float prob, int prot)
	{
		operation = op;
		signals.addAll(sigs);
		process = prcss;
		value = prob;
		protocol = prot;
	}

	public RemSignalArg(int op, List<Guid> sigs, Guid prcss, float prob, int prot, String grpName)
	{
		this(op, sigs, prcss, prob, prot);
		groupName = grpName;
	}

	public int getOperation()
	{
		return operation;
	}
	public List<Guid> getArgument()
	{
		return signals;
	}
	public int getProtocol()
	{
		return protocol;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public String getNewName()
	{
		return newSigName;
	}
	
	public Guid getProcess()
	{
		return process;
	}

	public float getValue()
	{
		return value;
	}

	public Signal getNewSignal()
	{
		return signal;
	}

	public boolean invert()
	{
		return inv;
	}
}
