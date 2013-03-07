/*
 * Created on Feb 11, 2005
 *  
 */
package mil.af.rl.jcat.gui;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import org.jfree.data.xy.XYSeries;

import mil.af.rl.jcat.exceptions.BayesNetException;
import mil.af.rl.jcat.exceptions.PlotException;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;

public class ProfileDataModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private static ProfileDataModel model = null;
	private String[] columnNames = { "Name", "Label" };
    private HashMap data = new HashMap();
	private boolean plotPredicted = true;
	private boolean plotInferred = true;

	
    public static ProfileDataModel getInstance()
    {
        if (ProfileDataModel.model == null)
            return new ProfileDataModel();
        else
            return ProfileDataModel.model;
    }

    private ProfileDataModel()
    {
        model = this;
    }

    public void addPlot(AbstractPlan plan, PlanItem p) throws PlotException
    {
        if (!data.containsKey(p.getGuid()))
        {
        	try{
				if(plan.getPredictedProbs(p.getGuid()) == null)
					throw new PlotException("The item may be new since the sampler was last started. \nTry re-sampling and plot this item again.");
			}catch(BayesNetException e){
				throw new PlotException("The BayesNet has not been constructed, please start the sampler.");
			}

			addPlot(new Plot(plan, p));
        }
    }

    private void addPlot(Plot p)
    {
        data.put(p.getID(), p);
        ProbabilityProfiles.getInstance().addSeries(p.getPredSeries(), Plot.PREDICTED, plotPredicted);
        ProbabilityProfiles.getInstance().addSeries(p.getInfSeries(), Plot.INFERRED, plotInferred);
        fireTableDataChanged();
    }

    public void clear()
    {
        data.clear();
        ProbabilityProfiles.getInstance().removeAllSeries();
        fireTableDataChanged();
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName(int column)
    {
        return this.columnNames[column];
    }

    /**
     * @return Returns the data.
     */
    public Collection getData()
    {
        return data.values();
    }

    public Plot getPlotAt(int rowIndex)
    {
        return (Plot) data.values().toArray()[rowIndex];
    }

    public Plot getPlotForSeries(XYSeries series)
    {
    	java.util.Iterator plots = data.values().iterator();
    	while(plots.hasNext())
    	{
    		Plot thisPlot = (Plot)plots.next();
    		if(thisPlot.getPredSeries().equals(series) || thisPlot.getInfSeries().equals(series))
    			return thisPlot;
    	}
    	return null;
    }
    
    public int getRowCount()
    {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Plot p = (Plot) data.values().toArray()[rowIndex];
        if (columnIndex == 0)
            return p.getItem().getName();
        else
            return p.getItem().getLabel();
    }

    public void removePlot(int i)
    {
        Plot p = (Plot) data.values().toArray()[i];
        removePlot(p);
    }

    public void removePlot(Plot p)
    {
    	data.remove(p.getID());
        ProbabilityProfiles.getInstance().removeSeries(p.getPredSeries());
        ProbabilityProfiles.getInstance().removeSeries(p.getInfSeries());
        fireTableDataChanged();
    }
    
	public void setPlotPredicted(boolean plot)
	{
		plotPredicted = plot;
		
		ProbabilityProfiles.getInstance().setShowPredicted(plot);
        
		fireTableDataChanged();
	}

	public void setPlotInferred(boolean plot)
	{
		plotInferred = plot;
		
		ProbabilityProfiles.getInstance().setShowInferred(plot);
        
		fireTableDataChanged();
	}

	public boolean isPlotPred()
	{
		return plotPredicted;
	}

	public boolean isPlotInf()
	{
		return plotInferred;
	}
	
	
}