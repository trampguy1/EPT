/*
 * Created on Mar 20, 2007
 *
 */
package mil.af.rl.jcat.bayesnet.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.Sampler;
import mil.af.rl.jcat.bayesnet.SamplerResult;
import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;
import mil.af.rl.jcat.util.MultiMap;
import net.jini.core.lease.Lease;

import org.apache.log4j.Logger;
import org.tiling.computefarm.CancelledException;
import org.tiling.computefarm.CannotTakeResultException;
import org.tiling.computefarm.CannotWriteTaskException;
import org.tiling.computefarm.ComputeSpace;
import org.tiling.computefarm.Job;

public class SamplerJob implements Job, Runnable
{
    private static final Logger logger = Logger.getLogger(SamplerJob.class.getName());
    private static int MAX_WORKERS = 6;
    private static int ACTIVE_WORKERS = 0;
    ArrayList<NetNode> sortedNodes;
    boolean keepSampling = true;
    boolean isPaused = false;
    int timespan;
    SamplerResult result = new SamplerResult();
    Queue<SamplerResult> results = new ConcurrentLinkedQueue<SamplerResult>();
    MultiMap<Integer, NetNode> explainTimes = new  MultiMap<Integer, NetNode>();
    /**
     * Map of Parent Explainations in <Parent, Child> Relation
     */
    Map<NetNode, NetNode> parentExplains = new HashMap<NetNode, NetNode>();

    public SamplerJob(ArrayList<NetNode> sortedNodes, int timespan)
    {
        this.sortedNodes = sortedNodes;
        this.timespan = timespan;
    }
    
    public void collectResults(ComputeSpace space)
    {
        while(keepSampling || ACTIVE_WORKERS > 0)
        {            
            try
            {                
                result = (SamplerResult)space.take();
                ACTIVE_WORKERS--;
                resultRecieved(result);               
                logger.info("Job Returned Correctly");
            } catch (CannotTakeResultException e)
            {
                logger.error("Can't take result: " + e.getMessage());
            } catch (CancelledException e)
            {
                logger.error("Compute Task Cancelled");
            }
        }
    }

    public void generateTasks(ComputeSpace space)
    {
        while(keepSampling)
        {
            try
            {
                space.write(generateSamplingTask());
                ACTIVE_WORKERS++;
            } catch (CannotWriteTaskException e)
            {
                logger.error("Can't write out task");
            } catch (CancelledException e)
            {
                logger.error("Cancelled: generateTasks(ComputeSpace space)" + e.getMessage());
            }
            synchronized(results)
            {
                if(this.isPaused || ACTIVE_WORKERS == MAX_WORKERS)
                {
                    try
                    {
                        results.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private LikelihoodSamplingTask generateSamplingTask()
    {
        LikelihoodSamplingTask task = new LikelihoodSamplingTask(sortedNodes, timespan);
        //Load explaination data to the task
        for(Integer i : explainTimes)
        {
            List<NetNode> nodes = explainTimes.get(i);
            for(NetNode node : nodes)
            {
                task.initializeExplanation(node, i);
            }            
        }
        //Load Parent Explanations
        for(NetNode parent: parentExplains.keySet())
        {
            task.initializeParentExplaination(parentExplains.get(parent), parent);
        }
        return task;
    }
    
    
    /**
     * Places results in to result queue to merge and genereate answers.
     * Prevents collectResults from blocking.
     * 
     * @param result the result of the sampling task
     */
    public synchronized void resultRecieved(SamplerResult result)
    {
        synchronized(results)
        {
            results.offer(result);
            results.notifyAll();
        }
    }
    
    public void run()
    {
        while(this.keepSampling)
        {
            synchronized(results)
            {
                if(results.isEmpty())
                {
                    try
                    {
                        results.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                    result.mergeResult(results.remove());
            }
        }
    }

    public void initializeExplaination(NetNode netNode, int time)
    {
        explainTimes.put(time, netNode);        
    }
    
    public void initializeParentExplaination(NetNode childNode, NetNode parent)
    {
        parentExplains.put(parent, childNode);

    }
}
