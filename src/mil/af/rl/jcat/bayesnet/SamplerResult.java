/*
 * Created on Mar 20, 2007
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SamplerResult implements Serializable
{
    protected LikelihoodStatistics stats = new LikelihoodStatistics();
    //Use Tree amp so entries are sorted according to their natural ordering
    protected TreeMap<NetNode, LikelihoodNodeData> dataIndex = null;
    private static Logger logger = Logger.getLogger(SamplerResult.class);
    
    public SamplerResult()
    {
        
    }    
    
    public SamplerResult(LikelihoodStatistics stats, HashMap<NetNode, LikelihoodNodeData> index)
    {
        this.stats = stats;
        this.dataIndex = new TreeMap(index);
    }
    
    public void mergeResult(SamplerResult result)
    {
        stats.mergeStatistics(result.stats);
        mergeDataIndex(result.dataIndex);
    }

    private void mergeDataIndex(TreeMap<NetNode, LikelihoodNodeData> dataIndex2)
    {
        if(dataIndex == null)
            dataIndex = new TreeMap(dataIndex2);
        else
        {
            assert dataIndex.size() != dataIndex2.size();
            
            Iterator di = dataIndex.entrySet().iterator();
            Iterator di2 = dataIndex2.entrySet().iterator();
            while(di2.hasNext())
            {
                Entry<NetNode, LikelihoodNodeData> e1 = (Entry<NetNode, LikelihoodNodeData>) di.next();
                Entry<NetNode, LikelihoodNodeData> e2 = (Entry<NetNode, LikelihoodNodeData>) di2.next();
                if(e1.getKey().getName().compareTo(e2.getKey().getName()) == 0)
                {
                    e1.getValue().mergeNodeData(e2.getValue());
                }
                else
                    logger.info("mergeDataIndex - Invalid NodeMatch");
                
            }
        }        
    }

    /**
     * @return the dataIndex
     */
    public Map<NetNode, LikelihoodNodeData> getDataIndex()
    {
        return dataIndex;
    }

    /**
     * @return the stats
     */
    public LikelihoodStatistics getStats()
    {
        return stats;
    }
}
