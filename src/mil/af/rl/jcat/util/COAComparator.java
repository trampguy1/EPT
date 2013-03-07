package mil.af.rl.jcat.util;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.exceptions.GraphLoopException;
import mil.af.rl.jcat.exceptions.PlotException;
import mil.af.rl.jcat.exceptions.SamplerMemoryException;
import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.gui.COAItemPlot;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.PlanItem;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.renderer.xy.CustomStepRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class COAComparator implements Runnable
{
	//private HashMap data = new HashMap();
	private static Logger logger = Logger.getLogger(COAComparator.class);
	BasicStroke infStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, 0, 1f, new float[]{5f,5f}, 0f);
	BasicStroke normStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, 0, 1f, new float[]{5f,0f}, 0f);
	private XYSeriesCollection series = new XYSeriesCollection();
	private Vector<COAItemPlot> plots = new Vector<COAItemPlot>();
	private CustomStepRenderer plotRend = new CustomStepRenderer();
	private Vector<ChangeListener> listeners = new Vector<ChangeListener>();
//	private DefaultXYItemRenderer plotRend = new DefaultXYItemRenderer();
	private AbstractPlan plan = null;
	private int coaColorIndex, itemColorIndex, numCOAs;
	private Rectangle2D rect = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
	private Paint[] colorArray = ChartColor.createDefaultPaintArray();
	private List<COA> coaList;
	private List<Guid> itemList;
	private ProgressMonitor progMonitor;
	boolean ready = false; //the comparator is ready to be used/displayed
	boolean reverseColors = false; //make line color coa instead of item
	private int defLength;


	/**
	 * Create a comparator without automatically creating plots and checking them for sampling data or sampling then
	 * @param plan
	 */
	public COAComparator(AbstractPlan plan, int defLength)
	{
		this.plan = plan;
		this.defLength = defLength;

		plotRend.setShapesVisible(true);
	}

	/**
	 * Create a comparator using the specified COAs and PlanItems, also ensures that
	 * all COAs have been sampled and contain some predicted probabilities
	 * @param plan
	 * @param coaList
	 * @param itemList
	 * @param defLength default plan length to sample with if plan has no previous or loaded length
	 * @param cListen Listener to be notified when the comparators state changes (ex: its ready for use)
	 * @param pm ProgressMonitor can be null if not needed
	 */
	public COAComparator(AbstractPlan plan, List<COA> coaList, List<Guid> itemList, int defLength, ChangeListener cListen, ProgressMonitor pm, boolean reverseClrs)
	{
		this(plan, defLength);

		this.coaList = coaList;
		this.itemList = itemList;
		listeners.add(cListen);

		if(pm != null)
		{
			pm.setMillisToDecideToPopup(0);
			pm.setMillisToPopup(0);
		}
		progMonitor = pm;
		reverseColors = reverseClrs;

		new Thread(this, "COAComparator-Builder").start();
	}

	/**
	 * Used by second constructor
	 */
	public void run()
	{
		if(coaList != null && itemList != null && coaList.size() > 0 && itemList.size() > 0)
		{
			// ensure each specified coa has sampler data (pred probs)
			// a coa might need to be applied, so save the current state (might not be a coa)
			COA[] oldStates = (plan.hasActiveCOAs()) ? plan.getActiveCOA().clone() : new COA[]{plan.createCOA("TEMP", true, true, true, false, false)};

			boolean modified = false;
			int progIncrem, progress = 0;

			progIncrem = 90 / coaList.size();
			if(progMonitor != null) progMonitor.setNote("Collecting Samples...");

			for(COA thisCOA : coaList)
			{
				if(!thisCOA.hasSampled()) //if it has not sampled, apply the COA and run it
				{
					Control.getInstance().applyCOA(thisCOA, plan, false);
					try{
						int length = defLength;
						if(plan.getBayesNet() != null)
							length = plan.getBayesNet().getTimespan();
						else if(plan.getLoadedPlanLength() > 0)
							length = plan.getLoadedPlanLength();

						plan.buildBayesNet(length);

						while(!thisCOA.hasSampled()) // buildbayesnet will return before any amount of sampling
							Thread.sleep(500);

						if(progMonitor != null) progMonitor.setProgress(progress += progIncrem);
						plan.getBayesNet().killSampler();
					}catch(SignalException e){
						logger.error("run - SignalException sampling COA for compare:  ", e);
					}catch(GraphLoopException e){
						logger.error("run - GraphLoopException sampling COA for compare:  ", e);
					}catch(InterruptedException e){
						logger.error("run - InterruptedException sampling COA for compare:  ", e);
					}catch(SamplerMemoryException e)					{
						logger.error("run - out of memory error, sampler not started:  ", e);
					}
					modified = true;
				}
			}
			if(modified) // restore to the state before this junk
			{
				for(COA oldState : oldStates)
					Control.getInstance().applyCOA(oldState, plan, false);
			}

			if(progMonitor != null) progMonitor.setNote("Plotting data...");
			progIncrem = 10 / itemList.size();
			//create the plots for each item
			for(Guid itemID : itemList)
			{
				try{
					COAItemPlot aPlot = new COAItemPlot(coaList, plan, itemID);
					addPlot(aPlot);
				}catch(PlotException e){
					logger.error("run - PlotException plotting COA compare item:  " + e);
				}
				if(progMonitor != null)
					progMonitor.setProgress(progress += progIncrem);
			}
		}

		ready = true;
		notifyListeners();

		if(progMonitor != null) progMonitor.close();

	}


	public void addPlot(COAItemPlot p)
	{
		boolean inLegend = true;
		Vector<XYSeries> allSeries = p.getAllSeries();
		Paint sClr = getNextItemColor(); 

		for(XYSeries thisSeries : allSeries)
		{
			addSeries(thisSeries, true, inLegend, sClr);
			inLegend = false;
		}

		numCOAs = allSeries.size(); //keep track of how many coas are plotted
		coaColorIndex = 0;

		plots.add(p);
	}


	private void addSeries(XYSeries thisSeries, boolean visible, boolean inLegend, Paint seriesColor)
	{
		series.addSeries(thisSeries);
		int thisIndex = series.getSeriesCount()-1;

		plotRend.setSeriesStroke(thisIndex, normStroke);

		plotRend.setSeriesVisibleInLegend(thisIndex, Boolean.valueOf(inLegend));
		plotRend.setSeriesPaint(thisIndex, (reverseColors) ? getNextCOAColor() : seriesColor);


		plotRend.setSeriesShape(thisIndex, rect);
		plotRend.setSeriesFillPaint(thisIndex, (reverseColors) ? seriesColor : getNextCOAColor());

		plotRend.setSeriesVisible(thisIndex, Boolean.valueOf(visible));

	}


	private Paint getNextCOAColor()
	{
		if(coaColorIndex - 1 >= 0)
			return colorArray[--coaColorIndex];
		else
			return colorArray[coaColorIndex = colorArray.length-1];
	}

	private Paint getNextItemColor()
	{
		if(itemColorIndex + 1 < colorArray.length-1)
			return colorArray[itemColorIndex++];
		else
			return colorArray[itemColorIndex = 0];

	}

	public Vector<COAItemPlot> getPlots()
	{
		return plots;
	}

	public int getCOACount()
	{
		return numCOAs;
	}

	public Paint getCOAColor(int index)
	{
		return colorArray[colorArray.length-1-index];
	}

	public Paint getItemColor(COAItemPlot plot)
	{
		return colorArray[plots.indexOf(plot)];
	}

	public XYSeriesCollection getDataset()
	{
		return series;
	}


	public XYLineAndShapeRenderer getRenderer()
	{
		return plotRend;
	}

	public AbstractPlan getPlan()
	{
		return plan;
	}

	public boolean isReady()
	{
		return ready;
	}

	public void addChangeListener(ChangeListener l)
	{
		listeners.add(l);
	}

	private void notifyListeners()
	{
		for(ChangeListener l : listeners)
			l.stateChanged(new ChangeEvent(this));
	}



	public boolean isReverseColors()
	{
		return reverseColors;
	}

	public List<COA> getCoaList()
	{
		return coaList;
	}

	public List<PlanItem> getItemList()
	{
		Vector<PlanItem> items = new Vector<PlanItem>(itemList.size());

		for(Guid id : itemList)
			items.add(plan.getItem(id));

		return items;
	}


	public int getItemCount()
	{
		return getItemList().size();
	}


	public int getTime()
	{
		if(series.getSeriesCount() > 0)
			return series.getItemCount(0);
		else
			return 0;
	}

}
