/*
 * Created on May 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary;

/**
 * @author John Lemmer Contains enums which whose values MUST NOT be changed.
 *         These values are serialized and changing them would obsolete saved
 *         files.
 * 
 */
public final class SignalType
{
	public static int notSet = 0;
    public static final int CAUSAL = 1;
    public static final int INHIBITING = 2;
    public static final int EFFECT = 3;
    public static final int GROUP = 10; //added by miked - doing custom defaults sets
    public static float probProblem = (float) -1;
    public static int RNOR = 4; //TODO:  this isn't really a signal type
    public static int ELICITED_VALUE = 5;
    public static int GAND = 6; //TODO:  this isn't really a signal type

    public static String getEnumName(int val)
    {
        String retVal = val == 1 ? "CAUSAL" : val == 2 ? "INHIBITING"
                : val == 3 ? "EFFECT" : val == 4 ? "RNOR"
                        : val == 5 ? "ELICITED_VALUE" : val == 6 ? "GAND"
                                : "Not Set";
        return retVal;
    }
}