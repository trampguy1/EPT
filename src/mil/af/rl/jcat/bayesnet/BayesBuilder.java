/*
 * Created on May 16, 2006
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;

import mil.af.rl.jcat.exceptions.GraphLoopException;
import mil.af.rl.jcat.exceptions.SignalException;

public interface BayesBuilder
{
    public BayesNet buildBayesNet(int timeSpan) throws GraphLoopException, SignalException;
    public String getNetworkName();
    public void buildNetNodes();
    public ArrayList<NetNode> topoSortNetNodes() throws GraphLoopException;    
}
