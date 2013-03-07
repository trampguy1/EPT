/*
 * Created on Jul 22, 2004
 *
 */
package mil.af.rl.jcat.plan;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import mil.af.rl.jcat.util.Guid;

/**
 * @author John Lemmer
 * 
 */
public interface PlanInterface
{
    /**
     * @return a collection of Guid's, one Guid for each event in the plan
     */
    public Collection getAllEvents();

    public int getInputSize(Guid eventGuid);

    public String getPlanName();

    public String getPlanItemName(Guid planID);

    public String getEventObjectName(Guid planID);
    
    public TreeMap getSchedule(Guid planID);

    public float getLeak(Guid planID);

    public int getPersistence(Guid planID);

    /**
     * @param planID
     * @return
     */
    public float getContinuationProbability(Guid planID);

    /**
     * @param planID
     * @return
     */
    public int getDelay(Guid planID);

    /**
     * @param planID
     * @return
     */
    public HashMap getEvidence(Guid planID);
}