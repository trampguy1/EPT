/*
 * Created on Oct 19, 2005
 *
 */
package mil.af.rl.jcat.bayesnet.explaination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import mil.af.rl.jcat.bayesnet.LikelihoodNodeData;
import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.explaination.Counts;

@SuppressWarnings("serial")
public class ExplanationData implements Serializable
{
    public static double explainThreshold = .8;

    HashMap<Integer, Counts> slices = new HashMap<Integer, Counts>();

    /**
     * 
     */
    public ExplanationData()
    {
    }

    public void addTime(int slice, int sizeCauses, int sizeInhibit)
    {
        // decided to work with causes and inhibitions separately (
        // slices.put(new Integer(slice), new Counts((1<< (causalNodes.length +
        // inhibitingNodes.length + 1))));
        // the below and related stuff is to support explanation about
        // inhibition
        slices.put(new Integer(slice), new Counts(sizeCauses, sizeInhibit));
    }

    public int bitValue(int sliceState)
    {
        return sliceState != 0 ? 1 : 0;
    }

    public int getCount(int slice)
    {
        int retVal = 0;
        Iterator i = ((Set) slices.entrySet()).iterator();
        if (i.hasNext())
        {
            Map.Entry o = (Map.Entry) i.next();
            Counts c = (Counts) o.getValue();
            retVal = c.getCount();
        }
        return retVal;
    }

    public ArrayList<Float> getJointProbs(int slice)
    {
        Counts theCounts = (Counts) slices.get(new Integer(slice));
        if (theCounts != null)
        {
            return theCounts.getJointProb();
        } else
        {
            // System.out.println("Slice, " + slice + ", for event, " +
            // toString() + " not found.");
        }
        return null;
    }

    public ArrayList getMargProbs(int slice)
    {

        ArrayList<Float> theJoints = getJointProbs(slice);
        ArrayList<Float> theMargs = null;
        if (theJoints != null)
        {
            int vecLen = theJoints.size();
            theMargs = new ArrayList<Float>(vecLen);
            for (int j = 0; j < vecLen; j++)
            {
                float margVal = 0.0f;
                for (int k = j; k < vecLen; k++)
                {
                    if ((k & j) == j)
                    {
                        // theMargs[k] += theJoints[j];
                        // WOW! templates and auto-(un)boxing!!!
                        margVal += theJoints.get(k);
                    }
                }
                theMargs.add(j, margVal);
            }
        }
        return theMargs;

    }

    public ArrayList<Float> getPercentJointContriubtion(int slice)
    {
        Counts sliceCounts = ((Counts) slices.get(new Integer(slice)));
        if (true /* sliceCounts.percentJointContribution != null */)
        {// lets always compute it for now to catch better values
            ArrayList<Float> joint = getJointProbs(slice);
            int len = joint.size();
            int halfSize = len / 2;
            sliceCounts.percentJointContribution = new ArrayList<Float>(halfSize);
            float totalEventProb = 0;
            for (int j = 1; j < len; j += 2)
            {
                totalEventProb += joint.get(j);
            }
            for (int k = 0; k < halfSize; k++)
            {
                float contribution = 0.0f;
                if (totalEventProb > 0.0f)
                {
                    contribution = joint.get((k << 1) + 1) / totalEventProb;
                }
                sliceCounts.percentJointContribution.add(k, contribution);
            }
        }
        return sliceCounts.percentJointContribution;
    }

    public ArrayList getSliceList()
    {
        TreeSet<Integer> sortedSlices = new TreeSet<Integer>(slices.keySet());
        int sliceCnt = sortedSlices.size();
        ArrayList<Integer> rtnVal = new ArrayList<Integer>(sliceCnt);
        for (Iterator<Integer> i = sortedSlices.iterator(); i.hasNext();)
        {
            int k =  i.next();
            rtnVal.add(k);
        }
        return rtnVal;
    }
    
    public HashMap<Integer, Counts> getSliceMap()
    {
    	return this.slices;
    }

    public void recordSample(NetNode node, HashMap<NetNode, LikelihoodNodeData> data, int slice)
    {
        Counts thisCount = (Counts) slices.get(slice);
        if (thisCount != null)
        {
            thisCount.recordSample(node, data, slice);
        }
    }

    public SortedMap<Float, Integer> sortEffectTrueProbs(
            ArrayList<Float> indexedList)
    {
        TreeMap<Float, Integer> retVal = new TreeMap<Float, Integer>();
        int lim = indexedList.size();
        for (int j = 1; j < lim; j += 2)
        { // just the ones where the effect occurred
            retVal.put(indexedList.get(j), j);
        }
        /*
         * float[] prob = new float[lim >> 1]; // don't need room for the event
         * non-occurrences int [] index = new int[lim >> 2]; for(int j = 1; j <
         * lim; j += 2){// thus only the probs where the effect 'happens' are
         * sorted prob[j] = ((Float)(indexedList.elementAt(j))).floatValue();
         * index[j] = j; }
         */
        return retVal;
    }

    public static double getExplainThreshold()
    {
        return explainThreshold;
    }
    
    public static void setExplainThreshold(double threshold)
    {
        explainThreshold = threshold;        
    }
}