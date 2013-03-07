/*
 * Created on Jul 13, 2006
 *
 */
package mil.af.rl.jcat.bayesnet.explaination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import mil.af.rl.jcat.bayesnet.LikelihoodNodeData;
import mil.af.rl.jcat.bayesnet.NetNode;

public class Counts implements Serializable
{
    private class PersistenceStatistics implements Serializable
    {
        float averagePersistence = 0.0f;
        int longestPersistence = 0;
        int shortestPersistence = 100000;

        public void updateStatistics(int persistenceLength)
        {
            if (persistenceLength >= 0)
            {// the event actually happened
                if (persistenceLength < shortestPersistence)
                {
                    shortestPersistence = persistenceLength;
                }
                if (longestPersistence < persistenceLength)
                {
                    longestPersistence = persistenceLength;
                }
                // this acutally depends on when sampleCount is updated
                // but this won't bomb and will converge to the correct answer
                averagePersistence = (averagePersistence * sampleCount + persistenceLength)
                        / (sampleCount + 1);
            }
        }
    }
    int causeCount[] = null;
    int eventCount[] = null;
    int inhibitCount[] = null;
    ArrayList<Float> percentJointContribution = null;
    PersistenceStatistics persistence = new PersistenceStatistics();
    int sampleCount = 0;;

    Counts(int size)
    {
        eventCount = new int[size];
        for (int j = 0; j < size; j++)
        {
            eventCount[j] = 0;
        }
    }

    Counts(int causeSize, int inhibitSize)
    {
        this(1 << (causeSize + inhibitSize + 1));// + 1 because the event
                                                    // itself will be counted as
                                                    // well as all combinations
                                                    // of causes and inhibitors
        causeCount = new int[1 << (causeSize + 1)];
        inhibitCount = new int[1 << (inhibitSize + 1)];
        for (int j = 0; j < causeCount.length; j++)
        {
            causeCount[j] = 0;
        }
        for (int j = 0; j < inhibitCount.length; j++)
        {
            inhibitCount[j] = 0;
        }
    }

    public int getAveragePersistence()
    {
        int retVal = ((int) (persistence.averagePersistence + 0.5f)); // rounding
                                                                        // it
        return retVal;
    }

    public int getCount()
    {
        return sampleCount;
    }

    public synchronized ArrayList<Float> getJointProb()
    {
        while (getCount() < 100)
        {
            try
            {
                // System.out.println("Waiting for samples before getting joint
                // probabilities:" + node.toString() + "Current Count = " +
                // getCount() );
                wait();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        int probCount = eventCount.length;
        ArrayList<Float> probVector = new ArrayList<Float>();
        float denom = (float) getCount();
        // System.out.println("Sample size for " + node.toString() + " is " +
        // denom);
        probVector = new ArrayList<Float>(probCount);
        for (int j = 0; j < probCount; j++)
        {
            probVector.add(j, eventCount[j] / denom);
        }
        return probVector;
    }

    public PersistenceStatistics getPersistence()
    {
        return persistence;
    }

    public synchronized void recordSample(NetNode node, HashMap<NetNode, LikelihoodNodeData> data, int slice)
    {
        // here comes the hard work!
        int thisEventBit = bitValue(data.get(node).getState()[slice]);
        getPersistence().updateStatistics(data.get(node).getState()[slice] - 1);
        int eventIndex = 0;// this will also be the index for inhibitIndex
        int causeIndex = 0;
        int inhibitIndex = 0;
        int causingSlice = slice - node.getNodeEffectSliceDelay();
        if (causingSlice >= 0)
        {

            // the following code depends on how the causal and inhibiting CPT's
            // are merged; merging happens somewhere else (in the sampler, I
            // think)
            // (not very robust code, eh?)
            for (int k = 0; k < node.getInhibitors().size(); k++)
            {
                // make sure delay is considered is it now?
                int thisInhibitor = bitValue(data.get(node.getInhibitors().get(k)).getState()[causingSlice]);
                eventIndex = eventIndex | (thisInhibitor << k);
                inhibitIndex = inhibitIndex | (thisInhibitor << k);
            }
            inhibitCount[(inhibitIndex << 1) | thisEventBit] += 1;

            eventIndex = (eventIndex << node.getCauses().size()); // to make
                                                                    // room for
                                                                    // the
                                                                    // causes
            for (int j = 0; j < node.getCauses().size(); j++)
            {
                int thisCause = bitValue(data.get(node.getCauses().get(j)).getState()[causingSlice]);
                eventIndex = eventIndex | (thisCause << j);
                causeIndex = causeIndex | (thisCause << j);
            }
            causeCount[causeIndex] += 1;

            eventIndex = (eventIndex << 1) | thisEventBit; // put in this
                                                            // node's event's
                                                            // state
            eventCount[eventIndex] += 1;
            sampleCount += 1;
        }
        if (getCount() >= 100)
        {
            this.notifyAll();
        }
    }
    
    public int bitValue(int sliceState)
    {
        return sliceState != 0 ? 1 : 0;
    }
}
