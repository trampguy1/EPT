/*
 * Created on Jul 6, 2006
 *
 */
package mil.af.rl.jcat.bayesnet;

import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;
import mil.af.rl.jcat.util.Guid;

public interface Sampler
{

    /**
     * Initialize the sampler and start the thread.
     */
    public void sampleDistribution(int timeSpan);

    /**
     * @return Returns the sampleCount.
     */
    public int getSampleCount();

    /**
     * @return Returns the timespan.
     */
    public int getTimespan();

    public boolean killSampler();

    public boolean unpauseSampler();

    public boolean pauseSampler();

    public boolean isSamplerPaused();

    public boolean isSampling();

    public double[] getInferredProbs(NetNode node);

    public double[] getPredictedProbs(NetNode node);

    public ExplanationData getWhyData(NetNode netNode);

    public void initializeExplanation(NetNode netNode, int time);

	public void initializeParentExplaination(NetNode childNode, NetNode parent);
}