/*
 * Created on Jun 29, 2005
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;

/**
 * @author lemmerj
 *
 */
public class Evidence implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	public static final int SENSOR = 1;
	public static final int ABSOLUTE = 2;
	int type = 0;

    double FAR = 0.0f;
	double MDR = 0.0f;
    double probability = 0.0;
	
    boolean report = true;
		
	/**
	 * @param theReport: whether the sensor has reported true (target detected) or false (no target detected)
	 * @param theFAR the false alarm rate
	 * @param theMDR the missed detection rate
	 * This is to add evidence derived from a sensor which is characterized by a false alarm rate (FAR)
	 * and a missed detection rate (MDR). The 'type', set to SENSOR, tells the sampler how to interpret this evidence
	 * as sensor evidence
	 */
	public Evidence(boolean theReport, double theFAR, double theMDR){
		FAR = theFAR;
		MDR = theMDR; 
		report = theReport;
		type = SENSOR;
	}
	
	/**
	 * @param evidence The absolute posterior probability for this event at this time. 
	 * 
	 * There is no
	 * perscriptive model of how the evidence provider arrives at this value
	 */
	public Evidence(double evidence){
		probability = evidence;
		type = ABSOLUTE;
	}
	
	/**
     * @return Returns the fAR.
     */
    public double getFAR()
    {
        return FAR;
    }
    /**
     * @return Returns the mDR.
     */
    public double getMDR()
    {
        return MDR;
    }
    /**
     * @return Returns the report.
     */
    public boolean isReport()
    {
        return report;
    }

    /**
     * @return Returns the type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return Returns the probability.
     */
    public double getProbability()
    {
        return probability;
    }


}
