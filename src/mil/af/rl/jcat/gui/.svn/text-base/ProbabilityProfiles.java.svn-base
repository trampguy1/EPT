/*
 * Created on Aug 19, 2004
 *
 * Author Craig McNamara
 */

package mil.af.rl.jcat.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import mil.af.rl.jcat.gui.dialogs.WhyDiegoWhy;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.EnvUtils;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;


/**
 * @author mcnamacr
 *  
 */
public class ProbabilityProfiles extends DockableFrame implements Runnable, TableModelListener, DockableFrameListener, ActionListener, MouseListener
{

	private static final long serialVersionUID = 1L;
	static int imagenum;
	BasicStroke infStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, 0, 1f, new float[] { 5f, 5f }, 0f);
	BasicStroke normStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, 0, 1f, new float[] { 5f, 0f }, 0f);
	public static ProbabilityProfiles probProfile = null;
	private JFreeChart chart;
	private JPopupMenu chartMenu = null;
	private ChartPanel cp;
	ProfileDataModel model = null;
	private XYSeriesCollection plots = new XYSeriesCollection();
	private NumberAxis x = new NumberAxis();
	private NumberAxis y = new NumberAxis();
	private XYStepRenderer plotRend = new XYStepRenderer();
	private Color lastNormColor = Color.GREEN;
	private JCheckBoxMenuItem plotPred;
	private JCheckBoxMenuItem plotInf;
	private double bound = 1.0;
	private int time1;
	private Plot selectedPlot;
	private static Logger logger = Logger.getLogger(ProbabilityProfiles.class);

	public static ProbabilityProfiles getInstance()
	{
		if(probProfile == null)
			return new ProbabilityProfiles();
		else
			return ProbabilityProfiles.probProfile;
	}

	private ProbabilityProfiles()
	{
		super("Probability Profiles", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_chart.png")));
		setupChartAxis();
		setupPopupMenu();
		probProfile = this;
		javax.help.CSH.setHelpIDString(this, "Probability_Profiles");
		model = ProfileDataModel.getInstance();
		model.addTableModelListener(this);
		new Thread(this, "ProbabilityProfile-Updater").start();
	}

	/**
	 * @param arg0
	 */
	public void addSeries(XYSeries thisSeries, int type, boolean visible)
	{
		plots.addSeries(thisSeries);
		int thisIndex = plots.getSeriesCount() - 1;

		if(type == Plot.INFERRED) //this plot is a dotted line for inferred probs 
		{
			plotRend.setSeriesStroke(thisIndex, infStroke);
			// if inferred is plotted along with its predicted then 
			// dont add to legend make it same color as last predicted plot
			//if(model.getPlotPred())
			//{
			plotRend.setSeriesVisibleInLegend(thisIndex, Boolean.valueOf(false));
			plotRend.setSeriesPaint(thisIndex, lastNormColor);
			//}

			//plotRend.setSeriesVisible(thisIndex, Boolean.valueOf(model.getPlotInf()));
			plotRend.setSeriesVisible(thisIndex, Boolean.valueOf(visible));
		}
		else if(type == Plot.PREDICTED) //this plot is a solid line for predicted probs
		{
			plotRend.setSeriesStroke(thisIndex, normStroke);
			lastNormColor = (Color) (plotRend.getSeriesPaint(thisIndex));
			plotRend.setSeriesVisibleInLegend(thisIndex, Boolean.valueOf(true));

			//plotRend.setSeriesVisible(thisIndex, Boolean.valueOf(model.getPlotPred()));
			plotRend.setSeriesVisible(thisIndex, Boolean.valueOf(visible));
		}
	}

	public String getImage() throws IOException
	{
		//MPG Modified to handle folder not existing
		boolean dirCreated = (new File(EnvUtils.getUserHome() + "/.JCAT")).mkdir();

		byte[] png = ChartUtilities.encodeAsPNG(chart.createBufferedImage(480, 240));

		File image = new File(EnvUtils.getUserHome() + "/.JCAT/" + new Random().nextInt(99999999) + ".png");
		image.deleteOnExit();
		FileOutputStream out = new FileOutputStream(image);
		out.write(png);
		out.flush();
		out.close();
		return image.getName();
	}

	/**
	 *  
	 */
	public void removeAllSeries()
	{
		plots.removeAllSeries();
	}

	public void hideSeries(int index)
	{
		plotRend.setSeriesVisible(index, Boolean.valueOf(false));
	}

	public void showSeries(int index)
	{
		plotRend.setSeriesVisible(index, Boolean.valueOf(true));
	}

	public void removeSeries(XYSeries arg0)
	{
		plots.removeSeries(arg0);
	}

	public int getNumSeries()
	{
		return plots.getSeriesCount();
	}

	public XYSeries getSelectedSeries()
	{
		try
		{
			return plots.getSeries(plotRend.getSelectedSeries());
		}catch(Exception exc)
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		while(true)
		{
			updateGraph();
			try
			{
				Thread.sleep(5000);
			}catch(Exception e)
			{
				logger.error("run - error during graph update thread sleep");
			}
		}

	}

	/**
	 *  
	 */
	private void setupChartAxis()
	{
		chart = ChartFactory.createXYStepChart(null, "Time", "Probability", plots, PlotOrientation.VERTICAL, true, true, false);
		chart.getLegend().setItemFont(new java.awt.Font("Arial", 0, 10));
		cp = new ChartPanel(chart, true);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		Rectangle2D shape = new Rectangle2D.Double(0.0, 0.0, 12.0, 10.0);
		plotRend.setShape(shape);
		plotRend.setShapesVisible(true);
		plot.setRenderer(plotRend);
		cp.setDisableMouseDrag(true); //added to chart by mikeD to disable windowed zooming on chart
		cp.updateUI();

		y.setLowerBound(0.0);
		y.setUpperBound(102.0);
		y.setLowerMargin(1.5);
		y.setUpperMargin(2.5);
		chart.getXYPlot().setRangeAxis(y);

		NumberTickUnit t = new NumberTickUnit(1.0);
		TickUnits tu = new TickUnits();
		tu.add(t);
		x.setLowerBound(0.0);
		x.setUpperBound(1.0);
		x.setStandardTickUnits(tu);
		x.setAutoTickUnitSelection(true);
		chart.getXYPlot().setDomainAxis(x);

		this.getContentPane().add(cp);
		//panel.add("Center", cp);
	}

	public void resetChartAxis()
	{
		ValueAxis va = chart.getXYPlot().getRangeAxis();
		va.setLowerBound(0.0);
		va.setUpperBound(102.0);
		va.setLowerMargin(1.5);
		va.setUpperMargin(2.5);

		va = chart.getXYPlot().getDomainAxis();
		va.setLowerBound(0.0);
		va.setUpperBound(bound);
	}

	/**
	 *  
	 */
	private void setupPopupMenu()
	{
		chartMenu = cp.getPopupMenu();
		JMenuItem resetZoom = new JMenuItem("Reset Zoom");
		plotPred = new JCheckBoxMenuItem("Plot Predicted", true);
		plotInf = new JCheckBoxMenuItem("Plot Inferred", true);
		JMenuItem removePlot = new JMenuItem("Remove plot");
		JMenuItem clear = new JMenuItem("Clear All");
		JMenuItem explain = new JMenuItem("Explain");
		JMenuItem betaExplain = new JMenuItem("Beta Explain");
		JMenuItem betaExplainComp = new JMenuItem("Beta Explain (Compare)");
		//remove auto ranging option which somehow is always the last
		chartMenu.remove(chartMenu.getComponentCount() - 1);
		chartMenu.add(resetZoom, chartMenu.getComponentCount() - 1);
		chartMenu.add(plotPred);
		chartMenu.add(plotInf);
		chartMenu.addSeparator();
		chartMenu.add(removePlot);
		chartMenu.add(clear);
		chartMenu.addSeparator();
		chartMenu.add(explain);
		chartMenu.add(betaExplain);
		chartMenu.add(betaExplainComp);

		resetZoom.addActionListener(this);
		removePlot.addActionListener(this);
		clear.addActionListener(this);
		explain.addActionListener(this);
		betaExplain.addActionListener(this);
		betaExplainComp.addActionListener(this);
		plotPred.addActionListener(this);
		plotInf.addActionListener(this);
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals("Clear All"))
			model.clear();
		else if(event.getActionCommand().equals("Remove plot"))
		{
			Plot selectedPlot = model.getPlotForSeries(getSelectedSeries());
			if(selectedPlot != null)
				model.removePlot(selectedPlot);
		}
		else if(event.getActionCommand().equals("Plot Predicted"))
			model.setPlotPredicted(plotPred.isSelected());
		else if(event.getActionCommand().equals("Plot Inferred"))
			model.setPlotInferred(plotInf.isSelected());
		else if(event.getActionCommand().equals("Reset Zoom"))
			resetChartAxis();
		else if(event.getActionCommand().equals("Explain"))
		{
			Plot selectedPlot = model.getPlotForSeries(getSelectedSeries());
			int time = (int) ((XYPlot) chart.getPlot()).getDomainCrosshairValue();

			if(selectedPlot != null)
				explain(selectedPlot.getItem(), time);
		}
		else if(event.getActionCommand().equals("Beta Explain"))
		{
			Plot selectedPlot = model.getPlotForSeries(getSelectedSeries());
			int time = (int) ((XYPlot) chart.getPlot()).getDomainCrosshairValue();

			if(selectedPlot != null)
				betaExplain(selectedPlot.getItem(), time);
		}
		else if(event.getActionCommand().equals("Beta Explain (Compare)"))
		{
			selectedPlot = model.getPlotForSeries(getSelectedSeries());
			time1 = (int) ((XYPlot) chart.getPlot()).getDomainCrosshairValue();
			int time2 = time1 + 1;

			if(selectedPlot != null)
			{
				try
				{
					time2 = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter second time to compare to:"));
				}catch(NumberFormatException exc)
				{
					JOptionPane.showMessageDialog(this, "Invalid input, defaulting to time " + time1 + 1);
				}

				betaExplainCompare(selectedPlot.getItem(), time1, time2);
			}
		}
	}

	//<<<< JOHN SHOULD CALL HIS EXPLAIN STUFF FROM HERE >>>>
	public void explain(PlanItem item, int time)
	{
		mil.af.rl.jcat.plan.AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		WhyDiegoWhy explanation = null;
		if(plan != null && plan.getBayesNet() != null)
		{
			explanation = new WhyDiegoWhy(item.getGuid(), plan, time);
			if(!explanation.isVisible())
			{
				// display alert that says "No explanations currently available
				logger.info("explain - No explanations currently available");
			}
		}
		else
		{
			logger.info("explain - You must first start the sampler");
		}
		logger.info("explain - <<<<<  item: " + item.getName() + "  time: " + time);
	}

	public void betaExplain(PlanItem item, int time)
	{
		/*mil.af.rl.jcat.plan.AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		 
		 if(plan != null && plan.getBayesNet() != null)
		 new ExplainDialog(MainFrm.getInstance(), plan, item.getGuid(), time);
		 else
		 System.out.println("The plan has not been compiled into a Bayes Net.");

		 System.out.println("<<<<<  item: "+item.getName()+"  time: "+time);*/
	}

	public void betaExplainCompare(PlanItem item, int time, int time2)
	{
		/*mil.af.rl.jcat.plan.AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		 
		 if(plan != null && plan.getBayesNet() != null)
		 new ExplainDialog(MainFrm.getInstance(), plan, item.getGuid(), time, time2);
		 else
		 logger.info("explain - You must first start the sampler");
		 
		 logger.info("explain - "+item+"  selected from "+time+" to "+(time2)); //temporary, use time2 l8r
		 */
	}

	public void mouseReleased(MouseEvent event)
	{
		if(event.getButton() == MouseEvent.BUTTON1)
		{
			int time2 = (int) ((XYPlot) chart.getPlot()).getDomainCrosshairValue();
			betaExplainCompare(selectedPlot.getItem(), time1, time2);
			cp.removeMouseListener(this);
		}
	}

	public void tableChanged(TableModelEvent e)
	{
		updateGraph();
	}

	private void updateGraph()
	{
		java.util.Vector plots = new java.util.Vector();
		plots.addAll(model.getData());
		for(Iterator i = plots.iterator(); i.hasNext();)
		{
			Plot p = (Plot) i.next();
			try
			{
				p.updatePlot();
				//if (bound < (double) p.getPredSeries().getItemCount())
				bound = (double) p.getPredSeries().getItemCount() - 1;
			}catch(NullPointerException exc)
			{
				//user must have closed the plan after sampling was started on it
				//or user removed a planitem from the plan that was plotted
				logger.info("updateGraph - Tried to plot a null PlanItem (a sampling plan was closed or plotted item removed); plot removed");
				model.removePlot(p);
			}
		}
		// divide because there are twice as many things graphed with inferred probs added
		x.setUpperBound(bound);
	}

	public void addDockListener()
	{
		addDockableFrameListener(this);
	}

	// listener for hiding a docked frame
	public void dockableFrameHidden(DockableFrameEvent arg0)
	{
		MainFrm.getInstance().getCatMenuBar().uncheckViewItem(getTitle());
	}

	/* 
	 * Unimplemented methods of DockableFrame listener
	 */
	public void dockableFrameAdded(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameRemoved(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameShown(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameDocked(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameFloating(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameAutohidden(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameAutohideShowing(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameActivated(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameDeactivated(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameTabShown(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameMaximized(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameRestored(DockableFrameEvent arg0)
	{
	}

	public void dockableFrameTabHidden(DockableFrameEvent arg0)
	{
	}

	//unused mouselistener methods
	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	
	public void setShowPredicted(boolean plot)
	{
		for(int x=0; x<getNumSeries(); x++)
		{
			if(x % 2 == 0) //if the plot is odd its an inferred
			{
				if(!plot)
					hideSeries(x);
				else
					showSeries(x);
			}
		}
		
//		if(!plot) //legend items will be removed show ones for inferred
//		{
			for(int x=0; x<getNumSeries(); x++)
				if(x % 2 != 0) //if the plot is odd its an inferred
					plotRend.setSeriesVisibleInLegend(x, Boolean.valueOf(!plot));
//		}
	}

	public void setShowInferred(boolean plot)
	{
		for(int x=0; x<getNumSeries(); x++)
		{
			if(x % 2 != 0) //if the plot is odd its an inferred
			{
				if(!plot)
					hideSeries(x);
				else
					showSeries(x);
			}
		}
	}
	
	

}
