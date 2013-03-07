/*
 * Created on Jun 14, 2006
 * Author: Mike D
 * MissingRequiredFileException.java - Exception thrown when a required JCat config file is missing
 */
package mil.af.rl.jcat.exceptions;

public class MissingRequiredFileException extends Exception
{

	private static final long serialVersionUID = 1L;
	
	public MissingRequiredFileException(String msg)
	{
		super(msg);
	}
	
	public MissingRequiredFileException(String msg, Throwable e)
	{
		super(msg, e);
	}
	
}
