/*
 * Created on Sep 8, 2005
 *
 */
package mil.af.rl.jcat.exceptions;

public class GraphLoopException extends Exception
{
	private static final long serialVersionUID = 1L;

	public GraphLoopException()
    {
        super();
    }

    public GraphLoopException(String message)
    {
        super(message);
    }

    public GraphLoopException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GraphLoopException(Throwable cause)
    {
        super(cause);
    }

}
