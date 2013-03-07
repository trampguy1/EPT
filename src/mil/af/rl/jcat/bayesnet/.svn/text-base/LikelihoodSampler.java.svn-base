/*
 * Created on May 11, 2006
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.bayesnet.LikelihoodStatistics;
import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;

public class LikelihoodSampler implements Runnable, Sampler, Serializable
{
    //Timing and node data for the sampler
	protected boolean keepSampling = true;
	protected boolean isPaused = false;
	protected static Random random = new Random();
	protected int timespan;
    protected int sampleCount;
    protected NetNode[] sortedNodes;
    protected LikelihoodNodeData[] nodeData;
    protected HashMap<NetNode, LikelihoodNodeData> dataIndex = null;
    //Statistical Data for the sampler
    LikelihoodStatistics stats = new LikelihoodStatistics();
    //Work thread for the sampler
    protected transient Thread sampler;
    private int stopAfter = -1;
    private static Logger logger = Logger.getLogger(LikelihoodSampler.class);

    public LikelihoodSampler(ArrayList<NetNode> sortedNodes)
    {
        super();
        dataIndex = new HashMap<NetNode, LikelihoodNodeData>(sortedNodes.size());
        convertNodesToArray(sortedNodes.toArray());

    }
    private void convertNodesToArray(Object[] objects)
    {
        this.sortedNodes = new NetNode[objects.length];
        for(int i = 0; i < objects.length; i++)
        {
            sortedNodes[i] = (NetNode)objects[i];
        }
    }
    /* (non-Javadoc)
     * @see mil.af.rl.jcat.bayesnet.Sampler#sampleDistribution(int, int)
     */
    public void sampleDistribution(int timeSpan)
    {        
        this.timespan = timeSpan;
        this.initializeSamplerData();
        sampler = new Thread(this, "Likelihood-Sampler");
        sampler.setPriority(Thread.MIN_PRIORITY);
        sampler.start();
    }
    /**
     * Run the sampler without a seperate thread for a specified amount of samples
     * and return upon completion. For parallel computing threading issues.
     * @param timeSpan
     * @param blockSize
     */
    public void sampleDistribution(int timeSpan, int blockSize)
    {            
        this.timespan = timeSpan;
        stopAfter = blockSize;
        run();
    }

    public void initializeSamplerData()
    {
    	this.sampleCount = 0;
    	this.nodeData = new LikelihoodNodeData [sortedNodes.length];
    	for(int i = 0; i < sortedNodes.length; i ++){   		
    		LikelihoodNodeData data = new LikelihoodNodeData();
    		data.nodeName = sortedNodes[i].toString();
    		data.accumulatedLikelihood = new double[timespan];
    		data.occurrenceCount = new int[timespan];
    		data.inferredState = new int[timespan];
    		data.state = new int[timespan];    		
    		nodeData[i] = data;
    		dataIndex.put(sortedNodes[i], data);
    	}        
    }
    /**
     * Call to make parallel stuff work with loading explaination 
     * 
     * @param timespan
     */
    public void initializeSamplerData(int timespan)
    {
        this.sampleCount = 0;
        this.nodeData = new LikelihoodNodeData [sortedNodes.length];
        for(int i = 0; i < sortedNodes.length; i ++){           
            LikelihoodNodeData data = new LikelihoodNodeData();
            data.nodeName = sortedNodes[i].toString();
            data.accumulatedLikelihood = new double[timespan];
            data.occurrenceCount = new int[timespan];
            data.inferredState = new int[timespan];
            data.state = new int[timespan];         
            nodeData[i] = data;
            dataIndex.put(sortedNodes[i], data);
        }        
    }
    
    public void run()
    {   //If you keep sampling keep sampling
        //If stop after is not set(ex -1) keep sampling
        //If stopAfter is set sample to a ceiling and stop
        while(keepSampling == true && this.stopAfter == -1 || this.sampleCount < this.stopAfter)
        {
            stats.resetLikelihood();
            for(int s  = 0; s < this.timespan; s++)
            {
                for(int i = 0; i < sortedNodes.length; i++)
                {
                    this.takeSliceSample(sortedNodes[i], nodeData[i], s, this.timespan);
                }
            }
            for(int k = 0; k < nodeData.length; k++)
            {
                LikelihoodNodeData data = nodeData[k];
                this.accumulateSliceResults(data);
                data.zeroState();
            }
            sampleCount++;
            stats.updateModelStatistics();
            while(this.isPaused)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Causes the calling method to wait for a specified number of samples to be collected.
     * Used for various data collection methods involving explain, but its use is not limited to that.
     * 
     * @param numToWait
     * @throws InterruptedException
     */
    public synchronized void waitForSamples(int numToWait) throws InterruptedException
    {
        int start = this.getSampleCount();
        while((this.getSampleCount() - start)< numToWait)
            wait();       
    }

    synchronized public void takeSliceSample(NetNode node, LikelihoodNodeData nodeData, int slice, int sliceLimit)
    {
        if(nodeData == null)
            logger.error("takeSliceSample - nodeData is null!");
        double occurrenceProb = 0.0;
        double inferredProb = 0.0;
        if (node.schedule != null && node.schedule[slice] > 0.0f)
        { // if it is 0.0 then this not is not scheduled at this time slice
            // scheduling the node is like setting its prior.
            // the block deals only with scheduled nodes
            inferredProb = occurrenceProb = node.schedule[slice]; 
            // this is Pearle's notion of intervention rather than the
            // pure concept of simply scheduling
            
         } else
        {
            // this block deals with UNscheduled nodes, nodes that depend on
            // their causes and leak for their occurrence
            float currentLeak = node.leak == null ? (float) 0.0 : node.leak[slice];
            float [] causalProbs = getSampleEventProbabilityFactor(slice, node, node.causalCPT, node.causes);
            float [] inhibitingProbs = getSampleEventProbabilityFactor(slice, node, node.inhibitingCPT, node.inhibitors);
            
            // make computations for prediction
            float notInhibitingProb = ((float) 1.0) - inhibitingProbs[0];
            occurrenceProb = (1.0 - (1.0 - currentLeak) * (1.0 - causalProbs[0]))* notInhibitingProb;
            
            // make computations for inference
            float inferredNotInhibitingProb = ((float) 1.0)- inhibitingProbs[1];
            inferredProb = (1.0 - (1.0 - currentLeak) * (1.0 - causalProbs[1])) * inferredNotInhibitingProb;
        }

        // now we are set up to actually draw the sample
        double draw = random.nextDouble();
        int newEventSlice = slice + node.nodeEffectSliceDelay;
        /***********************************************************************
         * Semantic Bug!!**************** this condition should not be tested if
         * there is evidence!!!!!!!!!!********
         **********************************************************************/
        if (newEventSlice < sliceLimit)// this introduces a bug in late stage
                                        // evidence!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        {
            int originalPriorState = nodeData.state[newEventSlice]; // for use below in
                                                            // computation of
                                                            // prior evidence
                                                            // likelihood
            int extendedPersistence = extendMinimumPersistence(node.persistence, node.continuationProbability);
            if (occurrenceProb >= draw)
            {
                updateNodeState(nodeData.state, newEventSlice, extendedPersistence, sliceLimit);
            } else
            {
                // lets assume all the slice states are set to 0 before beginning sampling state[slice] = 0 or that they have some entry due to persistence: so do nothing here;
                // but we do need to release the resources allocated for this event
             }

            double iDraw = random.nextDouble();
            Evidence theEvidence = null;
            if (node.evidence != null /* && inferredProb > 0.0 */)
            {
                theEvidence = searchForEvidenceDuringPersistence(node, newEventSlice, node.persistence);
            }
            if (theEvidence != null)
            {
                // there may be some evidence for this node during the extended
                // persistence starting at this time slice
                // therfore we are at an 'observable' event, an event for which
                // there may evidence
                if (originalPriorState == 0)
                {// if != 0 the event persited from a previous slice and I do
                    // no think it should further effect the likelihood of this
                    // sample
                    stats.updatePriorLikelihood(occurrenceProb >= draw ? occurrenceProb
                                    : 1.0 - occurrenceProb);// needs to be
                                                            // thought more
                                                            // about!
                    if (processEvidence(theEvidence, inferredProb, nodeData.accumulatedLikelihood[slice]))
                    {
                        updateNodeState(nodeData.inferredState, newEventSlice,
                                extendedPersistence, sliceLimit);
                    } else
                    {
                        nodeData.inferredState[slice] = 0;
                        // it should have been initialized to zero, but we are
                        // belt ans suspender people
                    }
                }
            } else{
            // there is no evidence at all for this node, so certainly none at
            // this slice!
	            if (inferredProb > iDraw)
	            {
	                updateNodeState(nodeData.inferredState, newEventSlice, extendedPersistence, sliceLimit);
	            } else
	            {
	                // the inferredState should be all set up either from
	                // initialization or from persistence.
	            }
            }
        }
        if (nodeData.whyData != null)
        {
            nodeData.getWhyData().recordSample(node, this.dataIndex, slice);
        }
    }
    
    public void accumulateSliceResults(LikelihoodNodeData data){
        // for prediction we always accumulate '1'
        for(int j = 0; j < this.timespan; j++){
            if(data.state[j] > 0){
                data.occurrenceCount[j] += 1;
            }
            if(data.inferredState[j] > 0){
                data.accumulatedLikelihood[j] += stats.predLikelihood;
            }else{
                data.accumulatedLikelihood[j] += 0.0;// for debuggins
            }
        }
    }
    
    public boolean processEvidence(Evidence ev, double causalProb, double accumulatedLikelihood){
        boolean occurred = false;
        switch(ev.type){
            case Evidence.SENSOR:
                if(causalProb >= random.nextFloat()){
                    occurred = true;
                    stats.updatePredictedLikelihood(ev.report ? (1 - ev.MDR) : ev.MDR);
                }else{
                    occurred = false;
                    stats.updatePredictedLikelihood(ev.report ? ev.FAR : (1 -  ev.FAR));
                }
            break;
            case Evidence.ABSOLUTE:
                double currentProbEstimate = stats.totalLikelihood > 0.00 ? accumulatedLikelihood / stats.totalLikelihood :0.0;
                    if(stats.totalLikelihood ==  0.0 || ev.probability > currentProbEstimate){
                        // if probability == 1.0, this is the equiv of having a 'true' sensor report for a sensor with MDR == FAR == 0.0; so SENSOR and ABSOLUTE are equivalent in this case.
                        occurred = true;
                        stats.updatePredictedLikelihood(causalProb);
                    }
                    else{
                        // likewise if probability == 0.0 this is like the perfect sensor reporting false.
                        stats.updatePredictedLikelihood(1.0 - causalProb);
                        //occurred was initialized to false
                    }
            break;
        }
        return occurred;
    }
    
    public Evidence searchForEvidenceDuringPersistence(NetNode node, int slice, int sliceSpan){
        Evidence retVal = null;
        if(node.evidence != null){
            for(int j = slice; j < (slice + sliceSpan); j++){
                Evidence next = node.evidence.get(new Integer(j));
                if(next != null){
                    if(retVal == null){
                        retVal = next;
                    }else{
                        // we must have found a second piece of evidence during the persistence period
                        logger.warn("searchForEvidenceDuringPersistance - Don't know how to process evidence  '" + node.toString() + "': because multiple evidence was found during this event's persistence period");
                    }
                }
            }
        }
        return retVal;
    }
    public Evidence summarizeAbsoluteEvidence(NetNode node, int slice, int sliceSpan) {
        /*
         * There seem to be two issues in treating a persisting event as one monolithic thing (either entirely does occur or does not occur) to which multiple pieces of evidence can be assigned, possibly or different types
         * 1) under what circumstatnces does the event occur in the particular sample?
         * 2) what likelihood should be associated with this (non) occurrence?
         * The method 'serachForEvidenceDuring Persistence' is a hack around this: only accept one piece of evidence or else log an error.
         */
        if(node.evidence != null){
            int lim = slice + sliceSpan;
            double sumProbs = 0.0;
            int evidenceCount = 0;
            for(int j = slice; j < lim; j++){
                Evidence curEv = ((Evidence)node.evidence.get(new Integer(j)));
                if(curEv != null) 
                    if(curEv.type == Evidence.ABSOLUTE){
                        sumProbs += curEv.probability;
                        evidenceCount += 1;
                    }else{
                        return curEv;
                    }
            }
            if(evidenceCount > 0 ){
                return new Evidence(sumProbs/ evidenceCount);
            }
        }
        return null;
    }
    
    protected void updateNodeState(int[] state, int newEventSlice, int extendedPersistence, int sliceLimit){
        for (int k = 0; k < extendedPersistence; k++)
        {
            int persistedEventSlice = newEventSlice + k;
            if (persistedEventSlice < sliceLimit)
            {
                state[persistedEventSlice] = k + 1; // + 1 because 0 means it didn't happen
             }
        }
    }
    /**
     * 
     * @param slice
     * @param node
     * @param CPT
     * @param modeNodes
     * @return float [] element 0 contains the predicted prob element 1 contains inference prob
     */
    protected float [] getSampleEventProbabilityFactor(int slice, NetNode node, Vector CPT, ArrayList<NetNode> modeNodes)
    {
        float [] eventProbs = {0.0f, 0.0f};
        if (modeNodes != null && modeNodes.size() > 0)
        {
            int cptIndex = 1; // we are interested in the probability of this
            int infCptIndex = 1; // ditto
            // event OCCURRING
            for (int j = 0; j < modeNodes.size(); j++)
            {
                if (dataIndex.get(modeNodes.get(j)).state[slice] != 0)
                {// we know (thanks to the topo sort driving this that the jth cause has
                 // already been sampled
                    cptIndex |= (1 << (j + 1));// the "+ 1" is because the 0th position is for the event itself
                }
                if(this.dataIndex.get(((NetNode)modeNodes.get(j))).inferredState[slice] != 0){
                    infCptIndex |= (1 << (j + 1)); // "+ 1" for j is because 0th position for the event itself
                }
            }
            // now the cptIndex points to the CPT entry we want, since the bits
            // in it which are one tell us which of the causes have occurred
            eventProbs[0] = ((Float)(CPT.get(cptIndex))).floatValue();
            eventProbs[1] = ((Float)CPT.get(infCptIndex)).floatValue();
            return eventProbs;
        }
        return eventProbs;
    }
 
    protected int extendMinimumPersistence(int persistence, float continuationProbability)
    {
        int retVal = persistence;
        if(continuationProbability > 0.0f){
            double draw = 0.0;
            boolean cont = false;
            /* on the first iteration we just want to know if the event continues (at all) into the next slice
             then we want to know if it continues past the whole slice
             */ 
            do
            {
                draw = random.nextDouble();
                if (draw < continuationProbability)
                {
                    if(retVal + 1 == timespan){
                        break;
                    }else{
                        retVal += 1;
                        cont = true;
                    }
                } else
                {
                    cont = false;
                }
    
            } while (cont);
        }
        return retVal;
    }
    
    

    /* (non-Javadoc)
     * @see mil.af.rl.jcat.bayesnet.Sampler#getSampleCount()
     */
    public int getSampleCount()
    {
        return sampleCount;
    }
    /* (non-Javadoc)
     * @see mil.af.rl.jcat.bayesnet.Sampler#getTimespan()
     */
    public int getTimespan()
    {
        return timespan;
    }
    
    public boolean killSampler()
    {
        this.keepSampling = false;
        return true;
    }
    
    public boolean unpauseSampler()
    {
        this.isPaused = false;
        sampler.notify();
        return false;
    }
    
    public boolean pauseSampler()
    {
        this.isPaused = true;
        return false;
    }
    
    public boolean isSamplerPaused()
    {
        return isPaused;
    }
    
    public boolean isSampling()
    {
        return keepSampling;
    }
    
    public synchronized double[] getInferredProbs(NetNode node)
    {
        SamplerData data = this.dataIndex.get(node);
        double [] sliceProbs;
        synchronized(data)
        {
            sliceProbs = data.computeInferrenceProbs(stats.totalLikelihood);
        }
        return sliceProbs;        
    }
    public synchronized double[] getPredictedProbs(NetNode node)
    {
        SamplerData data = this.dataIndex.get(node);
        double [] sliceProbs = null;
        if(data != null)
        {
	        synchronized(data)
	        {
	            sliceProbs = data.computePredictedProbs(this.sampleCount);
	        }
        }
        return sliceProbs;
    }
    
    public SamplerResult getBulkSamplerdata()
    {
        return new SamplerResult(stats, dataIndex);
    }

    public void initializeExplanation(NetNode netNode, int time)
    {
        synchronized(dataIndex)
        {
            dataIndex.get(netNode).getWhyData().addTime(time, netNode.getCauses().size(), netNode.getInhibitors().size());
        }
    }
    
    public void initializeParentExplaination(NetNode childNode, NetNode parent) 
    {
		synchronized(dataIndex)
		{
			LikelihoodNodeData childData = dataIndex.get(childNode);
			LikelihoodNodeData parentData = dataIndex.get(parent);
//			if(childData.whyData == null)
//				childData.whyData = new ExplanationData();
			//get the parent times and add explain points in the child
			for(int slice : parentData.getWhyData().getSliceMap().keySet())
			{
				int currentParentSlice = slice;
				int currentParentPersistence = parentData.getWhyData().getSliceMap().get(slice).getAveragePersistence();
				int childSlice = currentParentSlice - parent.nodeEffectSliceDelay - currentParentPersistence;
				if(childSlice >= 0)
					childData.getWhyData().addTime(childSlice, childNode.getCauses().size(), childNode.getInhibitors().size());
			}			
		}
		
	}
    
    public ExplanationData getWhyData(NetNode netNode)
    {
        return dataIndex.get(netNode).getWhyData();        
    }
	
    public LikelihoodStatistics getStatistics()
	{
		return stats;
	}
	
}