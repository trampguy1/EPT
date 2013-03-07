package mil.af.rl.jcat.util;

import java.io.Serializable;

public class SignalC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4445398504845136334L;
	private String name;
	private Guid guid;
	public SignalC(String name, Guid guid) {
		super();
		this.name = name;
		this.guid = guid;
	}
	public String getName()
	{
		return name;
	}
	public Guid getGuid()
	{
		return guid;
	}
}
