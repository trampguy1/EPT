package mil.af.rl.jcat.control;

import java.io.Serializable;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: C3I Associates</p>
 *
 * @author Edward Verenich
 * @version 1.0
 */
public class LibArgument implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final int SERVER_RESPONSE = 0;
    public static final int CLIENT_REQUEST = 1;
    private int originator;
    private Serializable argument;

    public LibArgument(int org, Serializable arg) {
        originator = org;
        argument = arg;
    }

    public int getOriginator()
    {
        return originator;
    }
    public Serializable getArgument()
    {
        return argument;
    }
}
