package mil.af.rl.jcat.exceptions;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class NoSuchNameException extends Exception
{
	private static final long serialVersionUID = 1L;

	public NoSuchNameException()
	{
		super("Name provided does not exist");
	}

	public NoSuchNameException(String m)
	{
		super(m);
	}

}