/*
 * Created on 18-Aug-06
 *
 */
package mil.af.rl.jcat.bayesnet;

public interface SamplerData
{

    public double[] computePredictedProbs(int count);

    public double[] computeInferrenceProbs(double totalLikelihood);

}