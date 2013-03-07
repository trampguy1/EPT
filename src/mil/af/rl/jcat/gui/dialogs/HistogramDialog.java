/*
 * Author: MikeD
 * Beta bastardization of a Histogram for measuring likelihood and priorlikelyhood from BayesNet
 * -for use with model acceptance/rejection
 */

package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


import org.apache.log4j.Logger;
import mil.af.rl.jcat.bayesnet.LikelihoodSampler;
import mil.af.rl.jcat.bayesnet.LikelihoodStatistics;
import mil.af.rl.jcat.bayesnet.Sampler;
import mil.af.rl.jcat.plan.AbstractPlan;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;


public class HistogramDialog extends JDialog implements Runnable, ActionListener
{
	private static final long serialVersionUID = 1L;
	public static final long maxSeriesSize = 1000;
	static int imagenum;
	private JFreeChart chart;
	private ChartPanel cp;
	private HistogramDataset plots = new HistogramDataset();
	private NumberAxis x = new NumberAxis();
	private NumberAxis y = new NumberAxis();
	private XYPlot plot;
	private Color firstSeriesColor;
	private JToggleButton pauseButton;
	private boolean collectData = true;
	private boolean visible = true;
	private JButton clearButton;
	private HistogramDataset ds = null;
	private static Logger logger = Logger.getLogger(HistogramDialog.class);
	private LikelihoodStatistics stats = null;
	private AbstractPlan thePlan;


	public HistogramDialog(java.awt.Frame parent, AbstractPlan plan)
	{
		super(parent, "Model Acceptance/Rejection");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(500,400);
		setLocation(300,300);
		setLocationRelativeTo(parent);

		firstSeriesColor = new Color(100, 100, 255, 220);

        //setup histogram dataset to hold model accept/reject data
        ds = new HistogramDataset();
        ds.setType(HistogramType.RELATIVE_FREQUENCY);

		thePlan = plan;
		Sampler samp = plan.getBayesNet().getSampler();
		if(samp instanceof LikelihoodSampler)
		{
			stats = ((LikelihoodSampler)samp).getStatistics();
			updateDataset();
		}
		else
			logger.warn("Constructor - Not a likelihood sampler running, no histogram for you.");

		setupChart();
		//setVisible(true);

	}

	private void updateDataset()
    {
        if(ds.getSeriesCount() == 2)
        {
            if(ds.getItemCount(1)<LikelihoodStatistics.maxSampleSeries && ds.getItemCount(0) < LikelihoodStatistics.maxSampleSeries){
	            ds.addToSeries(1, toDoubleArray(stats.getPriorLikelihoods()));
	        	ds.addToSeries(0, toDoubleArray(stats.getPredLikelihoods()));
	            // ds.addToSeries(1, toDoubleArray(stats.getPriorLikelihoods()));
            }
        }
        else
        {
            ds.addSeries("Likelihood", new double[0], (int)(stats.getDSMax() * 100), stats.getDSMin(), stats.getDSMax());
            ds.addSeries("Prior Likelihood", new double[0], (int)(stats.getDSMax() * 100), stats.getDSMin(), stats.getDSMax());
        }

    }


    //converts Object[] (should really be a Double[]) to double[]
    public double[] toDoubleArray(Object[] data)
    {
        double[] converted = new double[data.length];

        for(int x=0; x<data.length; x++)
        {
            if(!(data[x] instanceof Double))
            {
                logger.warn("toDoubleArray - not a Double in given Object[]");
                return null;
            }
            converted[x] = ((Double)data[x]).doubleValue();
        }

        return converted;
    }

    public void setVisible(boolean vis)
	{
		try{
			if(visible = vis && stats != null)
				new Thread(this, "Histogram-Updater").start();
			super.setVisible(vis);
		}catch(Exception exc){
			logger.error("setVisible - Could not fire up hist graph thread:  "+exc.getMessage());
		}
	}

	public void run()
	{
		while(visible)
		{
			if(collectData)
			{
				// if the sampler is not equal to the plans current sampler, its been resampled and the likelihood statistics object
				// is no longer valid, so we need to get the new one and start displaying the stats from that
				if(stats != ((LikelihoodSampler)thePlan.getBayesNet().getSampler()).getStatistics())
				{
					stats = ((LikelihoodSampler)thePlan.getBayesNet().getSampler()).getStatistics();
					updateDataset();
					plot.setDataset(ds);
				}
				updateGraph();

				updateDataset();// added by John in deperation
				plot.setDataset(ds);

			}
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){
				logger.warn("run - Error sleeping thread in graph update loop:  "+e);
			}
		}
	}

	private void setupChart()
	{
		chart = ChartFactory.createHistogram("Model Acceptance/Rejection", "", "", plots, PlotOrientation.VERTICAL, true, false, false);

		cp = new ChartPanel(chart, true);
		plot = (XYPlot) chart.getPlot();
		plot.setDataset(ds);  //new

		y.setLabel("Frequency");
//		y.setUpperBound(1.0);
		chart.getXYPlot().setRangeAxis(y);
		chart.getXYPlot().setForegroundAlpha(0.65f);
		chart.getXYPlot().getDomainAxis().setAutoRange(false);

		plot.setForegroundAlpha(0.65f);
		//plot.setRangeAxis(y);
		//plot.getDomainAxis().setAutoRange(false);

		JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
		buttonPanel.add(pauseButton = new JToggleButton("Pause"));
		buttonPanel.add(clearButton = new JButton("Clear Data"));
		pauseButton.addActionListener(this);
		clearButton.addActionListener(this);

		this.getContentPane().add(cp, BorderLayout.CENTER);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == pauseButton)
			collectData = !pauseButton.isSelected();
		else if(event.getSource() == clearButton)
		{
			if(ds != null)
			{
//				testData1 = new double[0];
//				testData2 = new double[0];

				ds.emptyBins();
				chart.fireChartChanged();
			}
		}
	}


	private void updateGraph()
	{

		((XYBarRenderer)plot.getRenderer()).setSeriesPaint(1, Color.BLUE);

		double binWidth = stats.getDSMax() / (stats.getDSMax() * 100);
		chart.getXYPlot().getDomainAxis().setUpperBound(stats.getDSMax() + binWidth);
		chart.getXYPlot().getDomainAxis().setLowerBound(stats.getDSMin() - binWidth);

		chart.fireChartChanged();

	}



	public static void main(String[] args)
	{
//		new HistogramDialog(new Frame()).setVisible(true);
	}
}