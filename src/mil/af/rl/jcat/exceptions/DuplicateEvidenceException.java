/*
 * Created on Jul 19, 2005
 *
 */
package mil.af.rl.jcat.exceptions;

public class DuplicateEvidenceException extends Exception
{

	private static final long serialVersionUID = 1L;

	public DuplicateEvidenceException()
    {
        super();
    }

    public DuplicateEvidenceException(String message)
    {
        super(message);
    }

    public DuplicateEvidenceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DuplicateEvidenceException(Throwable cause)
    {
        super(cause);
    }

}
