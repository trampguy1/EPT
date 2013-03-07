/*
 * Created on Dec 20, 2004
 *
 */
package mil.af.rl.jcat.gui;

import mil.af.rl.jcat.exceptions.BayesNetException;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.jfree.data.xy.XYSeries;


public class Plot
{
	public static final int PREDICTED = 0;
	public static final int INFERRED = 1;
	public static final int BOTH = 2;
	PlanItem item;
    XYSeries predSeries;
    XYSeries infSeries;
    AbstractPlan plan;
    private static Logger logger = Logger.getLogger(Plot.class);
    

    public Plot(AbstractPlan p, PlanItem i)
    {
        item = i;
        plan = p;
        predSeries = new XYSeries(item.getName() + " [" + item.getLabel() + "] ");
        infSeries = new XYSeries(item.getName() + " (inferred)");
    }

    protected void updatePlot() throws NullPointerException
    {
    	// create a series for predicted probability
    	double[] probs = null;
    	try{
    		probs = plan.getPredictedProbs(item.getGuid());
    	}catch(BayesNetException exc){  //this should never happen
    		logger.info("updatePlot - trying to plot on chart but net isn't built!");
    	}
                
        predSeries.setNotify(false);
        if(probs == null)
            throw new NullPointerException("Predicted probabilities for this plot ("+item.getName()+") are null");
        predSeries.clear();
        
		for (int i=0; i < probs.length; i++) 
		{
		    if(i == probs.length-1) 
		        predSeries.setNotify(true);
		    predSeries.add((Number) new Integer(i), (Number) new Float(probs[i] * 100));
		}
		predSeries.add(new Integer(probs.length), new Integer(0));
	
		// create a series for inferred probability
		double[] infProbs = null;
		try{
			infProbs = plan.getInferredProbs(item.getGuid());
	    }catch(BayesNetException exc){  //this should never happen
	    	logger.info("updatePlot - trying to plot on chart but net isn't built");
		}
		
		infSeries.setNotify(false);
		if(infProbs == null)
			throw new NullPointerException("Inferred probabilities for this plot ("+item.getName()+") are null");
		infSeries.clear();
		
		for(int i=0; i < infProbs.length; i++)
		{
			if(i == infProbs.length-1)
				infSeries.setNotify(true);
			infSeries.add((Number) new Integer(i), (Number) new Float(infProbs[i] * 100));
		}
		infSeries.add(new Integer(infProbs.length), new Integer(0));
    	
    }

    /**
     * @return Returns the predSeries.
     */
    public XYSeries getPredSeries()
    {
        return predSeries;
    }
    
    public XYSeries getInfSeries()
    {
    	return infSeries;
    }

    public void setPredSeries(XYSeries predSeries)
    {
        this.predSeries = predSeries;
    }

    public Guid getID()
    {
        return item.getGuid();
    }
    /**
     * @return Returns the item.
     */
    public PlanItem getItem()
    {
        return item;
    }
    /**
     * @return Returns the plan.
     */
    public AbstractPlan getPlan()
    {
        return plan;
    }
}
