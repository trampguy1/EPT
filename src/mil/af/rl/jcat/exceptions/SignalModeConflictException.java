/*
 * Created on May 20, 2005
 *
 */
package mil.af.rl.jcat.exceptions;

/**
 * @author lemmerj
 * 
 */
public class SignalModeConflictException extends SignalException
{

	private static final long serialVersionUID = 1L;
	private String signalName;
    private String processName;
    private String definedModeSet;
    private String errorModeSet;

    public SignalModeConflictException(String message)
    {
        super(message);
    }
    
    public SignalModeConflictException(String message, String sigName, String procName, String defined, String error)
    {
    	super(message);
    	signalName = sigName;
    	definedModeSet = defined;
    	errorModeSet = error;
    	processName = procName;
    }

    /**
     * @return Returns the errorModeSet.
     */
    public String getErrorModeSet()
    {
        return errorModeSet;
    }

    /**
     * @param modeSetName
     *            The errorModeSet to set.
     */
    public void setErrorModeSet(String modeSetName)
    {
        this.errorModeSet = modeSetName;
    }

    /**
     * @return Returns the definedModeSet.
     */
    public String getDefinedModeSet()
    {
        return definedModeSet;
    }

    /**
     * @param modeSetName
     *            The definedModeSet to set.
     */
    public void setDefinedModeSet(String modeSetName)
    {
        this.definedModeSet = modeSetName;
    }

    /**
     * @return Returns the processName.
     */
    public String getProcessName()
    {
        return processName;
    }

    /**
     * @param processName
     *            The processName to set.
     */
    public void setProcessName(String processName)
    {
        this.processName = processName;
    }

    /**
     * @return Returns the signalName.
     */
    public String getSignalName()
    {
        return signalName;
    }

    /**
     * @param signalName
     *            The signalName to set.
     */
    public void setSignalName(String signalName)
    {
        this.signalName = signalName;
    }
}
