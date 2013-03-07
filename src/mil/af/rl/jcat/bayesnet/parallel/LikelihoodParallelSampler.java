/*
 * Created on Mar 20, 2007
 *
 */
package mil.af.rl.jcat.bayesnet.parallel;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.tiling.computefarm.JobRunner;
import org.tiling.computefarm.JobRunnerFactory;
import org.tiling.computefarm.impl.javaspaces.util.ClassServer;
import org.tiling.computefarm.impl.javaspaces.util.ClasspathServer;

import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.Sampler;
import mil.af.rl.jcat.bayesnet.SamplerData;
import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;
import mil.af.rl.jcat.util.MultiMap;

public class LikelihoodParallelSampler implements Sampler
{
    private static Logger logger = Logger.getLogger(LikelihoodParallelSampler.class);
    ClasspathServer server = null;
    JobRunnerFactory factory;
    JobRunner runner;
    SamplerJob job;
    ArrayList<NetNode> nodes;
    
    public LikelihoodParallelSampler(ArrayList<NetNode> sortedNodes)
    {
        try
        {
            server = new ClasspathServer();
            server.start();
            
            this.nodes = sortedNodes;
            factory = JobRunnerFactory.newInstance();
        } catch (IOException e)
        {
            logger.error(e.getMessage());
        }
        
    }
    
    public void sampleDistribution(int timeSpan)
    {
        job = new SamplerJob(nodes, timeSpan);
        runner = factory.newJobRunner(job);
        Thread th = new Thread(job, "SampleCollector");
        th.start();
        new Thread(runner, "JobRunner").start();       
    }

    public double[] getInferredProbs(NetNode node)
    {
        SamplerData data = this.job.result.getDataIndex().get(node);
        double [] sliceProbs;
        synchronized(data)
        {
            sliceProbs = data.computeInferrenceProbs(this.job.result.getStats().getTotalLikelihood());
        }        
        return sliceProbs;
    }

    public double[] getPredictedProbs(NetNode node)
    {
        SamplerData data = this.job.result.getDataIndex().get(node);
        double [] sliceProbs;
        synchronized(data)
        {
            sliceProbs = data.computePredictedProbs(this.getSampleCount());
        }
        return sliceProbs;
    }

    public int getSampleCount()
    {
        return job.result.getStats().getSampleCount();
    }

    public int getTimespan()
    {
        return job.timespan;
    }

    public ExplanationData getWhyData(NetNode netNode)
    {
        return job.result.getDataIndex().get(netNode).getWhyData();
        
    }
    /**
     * Delegate to samplerJob 
     */
    public void initializeExplanation(NetNode netNode, int time)
    {
        job.initializeExplaination(netNode, time);
    }

    public void initializeParentExplaination(NetNode childNode, NetNode parent)
    {
        job.initializeParentExplaination(childNode, parent);
    }

    public boolean isSamplerPaused()
    {
        return false;
    }

    public boolean isSampling()
    {
        return job.keepSampling;
    }

    public boolean killSampler()
    {
        return job.keepSampling = false;
    }

    public boolean pauseSampler()
    {
        return false;
    }

    public boolean unpauseSampler()
    {
        job.isPaused = false;
        job.notify();
        return false;
    }

}
