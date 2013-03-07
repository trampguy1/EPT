/*
 * Created on 25-Jul-06
 *
 */
package mil.af.rl.jcat.bayesnet.parallel;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.tiling.computefarm.Task;

import mil.af.rl.jcat.bayesnet.LikelihoodNodeData;
import mil.af.rl.jcat.bayesnet.LikelihoodSampler;
import mil.af.rl.jcat.bayesnet.LikelihoodStatistics;
import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.SamplerData;

public class LikelihoodSamplingTask implements Serializable, Task
{
    LikelihoodSampler sampler;
    int timespan;
    private ArrayList<NetNode> sortedNodes;
    public SamplerData data = new LikelihoodNodeData();
    public LikelihoodStatistics stats = new LikelihoodStatistics();
    
    public LikelihoodSamplingTask(ArrayList<NetNode> sortedNodes, int timespan)
    {
        super();
        this.timespan = timespan;
        this.sortedNodes = sortedNodes;
        sampler = new LikelihoodSampler(sortedNodes);
        sampler.initializeSamplerData(timespan);
        
    }

    public Object execute()
    {
//        System.out.println("Starting Job");       
        sampler.sampleDistribution(timespan, 5000);
//        System.out.println("Job completed");
        return sampler.getBulkSamplerdata();
    }

    /**
     * @param netNode
     * @param time
     * @see mil.af.rl.jcat.bayesnet.LikelihoodSampler#initializeExplanation(mil.af.rl.jcat.bayesnet.NetNode, int)
     */
    public void initializeExplanation(NetNode netNode, int time)
    {
        sampler.initializeExplanation(netNode, time);
    }

    /**
     * @param childNode
     * @param parent
     * @see mil.af.rl.jcat.bayesnet.LikelihoodSampler#initializeParentExplaination(mil.af.rl.jcat.bayesnet.NetNode, mil.af.rl.jcat.bayesnet.NetNode)
     */
    public void initializeParentExplaination(NetNode childNode, NetNode parent)
    {
        sampler.initializeParentExplaination(childNode, parent);
    }
}
