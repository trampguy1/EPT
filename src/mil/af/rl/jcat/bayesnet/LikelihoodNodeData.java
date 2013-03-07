/*
 * Created on Jun 9, 2006
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;

import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;

/**
 * All data that is generated and used only by the sampler is contained here
 * 
 * @author craig
 *
 *
 */
public class LikelihoodNodeData implements Serializable, SamplerData
{
    public String nodeName;
	protected int [] state;
    protected int [] inferredState;
    protected double [] accumulatedLikelihood;
    protected int occurrenceCount[] = {0};
    protected ExplanationData whyData;
    
    public LikelihoodNodeData()
    {
        super();
    }
    
    public String toString(){
    		return nodeName;
    }
    
    
    public void zeroState()
    {
        for (int j = 0; j < state.length; j++)
        {
            state[j] = 0;
            inferredState[j] = 0;
        }
    }
    
    /* (non-Javadoc)
     * @see mil.af.rl.jcat.bayesnet.SamplerData#computePredictedProbs(int)
     */
    public double [] computePredictedProbs(int count)
    {
        synchronized(this.occurrenceCount)
        {
            double [] predicted = new double[occurrenceCount.length];
            for(int i = 0; i < predicted.length; i++)
            {
                predicted[i] = ((double) this.occurrenceCount[i] / count);
            }        
            return predicted;
        }
    }
    
    /* (non-Javadoc)
     * @see mil.af.rl.jcat.bayesnet.SamplerData#computeInferrenceProbs(double)
     */
    public double [] computeInferrenceProbs(double totalLikelihood)
    {
        synchronized(this.accumulatedLikelihood)
        {
            double [] inferred = new double[this.accumulatedLikelihood.length];
            for(int i = 0; i < inferred.length; i++)
            {
                inferred[i] = ((double)this.accumulatedLikelihood[i] / totalLikelihood);
            }
            return inferred;
        }
    }

    /**
     * @return Returns the accumulatedLikelihood.
     */
    public double[] getAccumulatedLikelihood()
    {
        return accumulatedLikelihood;
    }

    /**
     * @return Returns the inferredState.
     */
    public int[] getInferredState()
    {
        return inferredState;
    }

    /**
     * @return Returns the occurrenceCount.
     */
    public int[] getOccurrenceCount()
    {
        return occurrenceCount;
    }

    /**
     * @return Returns the state.
     */
    public int[] getState()
    {
        return state;
    }

    /**
     * @return Returns the whyData.
     */
    public ExplanationData getWhyData()
    {
        if(whyData == null)
            whyData = new ExplanationData();
        return whyData;
    }

    /**
     * @param whyData The whyData to set.
     */
    public void setWhyData(ExplanationData whyData)
    {
        this.whyData = whyData;
    }
    
    public void mergeNodeData(LikelihoodNodeData data)
    {
        mergeOccuranceCount(data.occurrenceCount);
        mergerAccumulatedLikelihood(data.accumulatedLikelihood);
    }

    private void mergerAccumulatedLikelihood(double[] accumulatedLikelihood2)
    {
        for(int i = 0; i < this.accumulatedLikelihood.length; i++)
        {
            this.accumulatedLikelihood[i] += accumulatedLikelihood2[i];
        }        
    }

    private void mergeOccuranceCount(int[] occurrenceCount2)
    {
        for(int i = 0; i < this.occurrenceCount.length; i++)
        {
            this.occurrenceCount[i] += occurrenceCount2[i];
        }
    }
    
    
}
