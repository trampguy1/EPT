/*
 * Created on 24-Jul-06
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

public class LikelihoodStatistics implements Serializable
{
	public static final long maxSampleSeries = 1000;

    ArrayList<Double> predLikelihoods = new ArrayList<Double>();
    ArrayList<Double> priorLikelihoods = new ArrayList<Double>();
	protected double totalLikelihood = 0.0;
    protected double predLikelihood = 1.0;
    protected double priorLikelihood = 1.0;
    private int sampleCount = 0;
    private double plsq = 0.0; // sum of (sample predLikelihoods of various prior evidence) squared
    private double pl = 0.0; // sum of (sample predLikelihoods of various prior evidence), like NOT squared
    private double olsq = 0.0;// of the actual observations, that is where the 'o' came from
    private double ol = 0.0; // actual observations, NOT squared
    //Dataset variables
	private double dsMax = 0;
	private double dsMin = 1;
	private static Logger logger = Logger.getLogger(LikelihoodStatistics.class);

    public LikelihoodStatistics()
    {
        super();
    }

    public void mergeStatistics(LikelihoodStatistics stats)
    {
        this.plsq += stats.plsq;
        this.pl += stats.priorLikelihood;
        this.ol += stats.predLikelihood;
        this.olsq += stats.olsq;
        this.sampleCount += stats.sampleCount;
        this.totalLikelihood += stats.totalLikelihood;

    }

    public void updateModelStatistics(){// called after as sample is completed; usually just before resetting the predLikelihoods to begin taking a new sample
        sampleCount += 1;
        plsq += (priorLikelihood * priorLikelihood);
        pl += priorLikelihood;
        ol += this.predLikelihood;
        olsq += (predLikelihood * predLikelihood);
        totalLikelihood += predLikelihood;
//      logger.debug("priorLikelihood = " + priorLikelihood + "predLikelihood = " + predLikelihood);
        if(predLikelihoods.size()< maxSampleSeries){
	        predLikelihoods.add(new Double(predLikelihood));
	        priorLikelihoods.add(new Double(priorLikelihood));
        }
        if(predLikelihood > dsMax) //do this in the 'toDoubleArray' method to increase efficiency perhaps
        	dsMax = predLikelihood;
        if(priorLikelihood < dsMin)
        	dsMin = priorLikelihood;
    }
    public void setPriorLikelihood(double priorLikelihood) {
        this.priorLikelihood = priorLikelihood;
    }
    public void resetLikelihood(){
        setPredLikelihood(1.0);
        setPriorLikelihood(1.0);
    }
    /**
     * @param theLikelihood The theLikelihood to set.
     */
    public void setPredLikelihood(double likelihood) {
        this.predLikelihood = likelihood;
    }

    /**
     * @return Returns the predLikelihood.
     */
    public double getPredLikelihood()
    {
        return predLikelihood;
    }

    public void updatePredictedLikelihood(double updater){// called after each time slice
        setPredLikelihood(this.predLikelihood * updater);
    }
    public void updatePriorLikelihood(double updater){// called after each time slice
        priorLikelihood *= updater;
    }
    public double getPriorLikelihood(){
        return priorLikelihood;
    }

    public double variance(double sumRVSq, double mean, int n){
        return (Math.sqrt(sumRVSq - (mean * mean)))  / n;
    }


    public double getDSMin()
    {
    	//return dsMin;
    	return 0.0;
    }

    public double getDSMax()
    {
    	//return dsMax;
    	return 1.0;
    }

    /**
     * @return the predLikelihoods
     */
    public Object[] getPredLikelihoods()
    {
        //TODO make this shit work -CM
    	//I DID -JFL
    	Object[] rtn = predLikelihoods.toArray();
    	predLikelihoods.clear();
        return rtn;
    }

    /**
     * @return the priorLikelihoods
     */
    public Object[] getPriorLikelihoods()
    {
        //TODO make this shit work -CM
    	//I DID -JFL
    	Object[] rtn = priorLikelihoods.toArray();
    	//priorLikelihoods.clear();
        return rtn;
    }

    /**
     * @return the sampleCount
     */
    public int getSampleCount()
    {
        return sampleCount;
    }

    public double getTotalLikelihood()
    {
        return this.totalLikelihood;
    }
}
