package mil.af.rl.jcat.gui;

import java.util.List;
import java.util.Vector;

import org.jfree.data.xy.XYSeries;

import mil.af.rl.jcat.exceptions.PlotException;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.util.Guid;


//like gui.Plot, used to hold info plotted in a prob chart, this one is for COA comparison
public class COAItemPlot
{

	private Vector<XYSeries> plotSeries;
	private Vector<String> coaNames = new Vector<String>();
	private String name = "";

	
	/**
	 * 
	 * @param coas COAs to include for the plot of this item
	 * @param plan Plan used to extract the name of the item
	 * @param itemID
	 * @throws PlotException if item is not found or has not been sampled for any of the COAs specified
	 */
	public COAItemPlot(List<COA> coas, AbstractPlan plan, Guid itemID) throws PlotException
	{
		plotSeries = new Vector<XYSeries>();
		name = itemID.toString();
		if(plan.getItem(itemID) != null)
			name = plan.getItem(itemID).getName();
		
		
		//ensure there are pred probs existing for this item in each coa, otherwise cannot create coaplot
		for(COA thisCOA: coas)
		{
			//check for probs stored in COA (not in a plan)
			if(thisCOA.getPredictedProbs(itemID) == null)
				throw new PlotException("1 or more COAs do not contain probabilities for this item, \n " +
						"the COA must have been sampled and contain info for this item.");
			
			XYSeries newSeries = new XYSeries(name); //+ "("+thisCOA.getName()+")");
			double[] probs = thisCOA.getPredictedProbs(itemID);
			for(int i=0; i < probs.length; i++)
				newSeries.add((Number)new Integer(i), (Number)new Float(probs[i] * 100));
			newSeries.add(new Integer(probs.length), new Integer(0));
			
			plotSeries.add(newSeries);
			
			coaNames.add(thisCOA.getName());
		}
		
	}

	
	public Vector<XYSeries> getAllSeries()
	{
		return plotSeries;
	}
	
	public Object[] getCOANames()
	{
		return coaNames.toArray();
	}

	public String getItemName()
	{
		return name;
	}
}
