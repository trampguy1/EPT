package mil.af.rl.jcat.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.table.MaskedComponent;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBControllerManager;
import com.c3i.jwb.JWBShape;
import com.primate.jwb.chart.CDataSet;
import com.primate.jwb.chart.ChartModel;
import com.primate.jwb.chart.ChartPanel;
import com.primate.jwb.chart.ScheduleObject;


public class SchedulerDialog extends JDialog implements ActionListener, WindowListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6086273639089816475L;
	JTabbedPane graphTabs = new JTabbedPane();
	JTabbedPane inputTabs = new JTabbedPane();

	JPanel masterGraphPanel = new JPanel();
	JPanel singleInputGraphPanel = new JPanel();
	JPanel SchedulingPanel = new JPanel();
	JPanel TimingPanel = new JPanel();
	JPanel legendPanel = new JPanel();

	JLabel lblSingleChart = new JLabel();
	JLabel lblPersistance = new JLabel();
	JLabel lblDelay = new JLabel();
	JLabel lblContinuation = new JLabel();
	JLabel lblScheduledProb = new JLabel();
	JLabel lblTimeFrom = new JLabel();
	JLabel lblTimeTo = new JLabel();

	SpinnerNumberModel timeFromModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);
	SpinnerNumberModel timeToModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);
	SpinnerNumberModel persistModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);
	SpinnerNumberModel delayModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);

	JSpinner spTimeFrom = new JSpinner(timeFromModel);
	JSpinner spTimeTo = new JSpinner(timeToModel);
	JSpinner spPersist = new JSpinner(persistModel);
	JSpinner spDelay = new JSpinner(delayModel);

	MaskedComponent spScheduledProb = new MaskedComponent(MaskedFloat.getMaskedValue(1.0));
	MaskedComponent spContinuationProb = new MaskedComponent(MaskedFloat.getMaskedValue(1.0));

	JButton btnApplyTiming = new JButton();
	JButton btnApplyScheduling = new JButton();
	JButton btnApplyChanges = new JButton();

	JCheckBox cbxCounterFactualReasoning = new JCheckBox();

	GridBagLayout MainLayout = new GridBagLayout();
	GridBagLayout SchedulingLayout = new GridBagLayout();
	GridBagLayout TimingLayout = new GridBagLayout();

	//chart pieces
	ChartModel modelSingle = new ChartModel(60,530,175);
	ChartPanel cpanelSingle = new ChartPanel(modelSingle);

	ChartModel modelPlan = new ChartModel(50,500,175);
	ChartPanel cpanelPlan = new ChartPanel(modelPlan);

	//incoming variables
	Component _parent = null ;
	JWBShape canvasShape = null ;
	PlanItem item = null ;
	private JWBController controller = null;
	private static Logger logger = Logger.getLogger(SchedulerDialog.class);

	//Dialog Size
	int x = 580 ;
	int y = 350 ;

	public SchedulerDialog(java.awt.Frame parent, JWBShape canvasshape, AbstractPlan plan)
	{
		//put something in that say if event init1, if mechanism init2, if plan level init3
		super(parent);

		//initilize needed variables
		_parent = parent ;
		canvasShape = canvasshape ;
		item = plan.getItem((Guid)canvasshape.getAttachment());
		controller = JWBControllerManager.getInstance().getControllerByShape(canvasShape.getUID());
		try
		{
			this.setSize(x,y);
			setLocationRelativeTo(parent);
			init1();
		}
		catch (Exception e)
		{
			logger.error("Constructor - Error initializing dialog:  ", e);
		}
	}

	//initialize for event
	public void init1()
	{
		this.setIgnoreRepaint(true);

		this.getContentPane().setLayout(MainLayout);

		this.setTitle("Scheduler");

		this.addWindowListener(this);

		SchedulingPanel.setLayout(SchedulingLayout);
		TimingPanel.setLayout(TimingLayout);
		legendPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY),"Legend"));

		singleInputGraphPanel.add(cpanelSingle);
		cpanelSingle.setToolTipText("Click and drag the graph to modify schedule.");
		singleInputGraphPanel.setBorder(BorderFactory.createTitledBorder("Schedule Graph: "+ item.getName()));
		masterGraphPanel.add(cpanelPlan);
		lblPersistance.setFont(new java.awt.Font("Dialog", 0, 11));
		lblPersistance.setText("Persistance");
		lblDelay.setFont(new java.awt.Font("Dialog", 0, 11));
		lblDelay.setText("Delay");
		lblContinuation.setFont(new java.awt.Font("Dialog", 0, 11));
		lblContinuation.setText("Continuation");
		lblScheduledProb.setFont(new java.awt.Font("Dialog", 0, 11));
		lblScheduledProb.setText("Probability");
		lblTimeFrom.setFont(new java.awt.Font("Dialog", 0, 11));
		lblTimeFrom.setText("Time From");
		lblTimeTo.setFont(new java.awt.Font("Dialog", 0, 11));
		lblTimeTo.setText("Time To");

		spTimeFrom.setPreferredSize(new Dimension(125, 23));
		spTimeTo.setPreferredSize(new Dimension(125, 23)); 
		spPersist.setPreferredSize(new Dimension(125, 23));
		spPersist.setValue(item.getPersistence());
		spDelay.setPreferredSize(new Dimension(125, 23));
		spDelay.setValue(item.getDelay());
		spScheduledProb.setPreferredSize(new Dimension(125,23));
		spContinuationProb.setPreferredSize(new Dimension(125,23));
		spContinuationProb.setValue(MaskedFloat.getMaskedValue(item.getContinuation()));

		btnApplyTiming.setFont(new java.awt.Font("Dialog", 0, 10));
		btnApplyTiming.setText("Apply");
		btnApplyTiming.setActionCommand("ApplyTiming");
		btnApplyTiming.setPreferredSize(new Dimension(60, 23));
		btnApplyTiming.addActionListener(this);

		btnApplyScheduling.setFont(new java.awt.Font("Dialog", 0, 10));
		btnApplyScheduling.setText("Apply");
		btnApplyScheduling.setActionCommand("ApplySchedule");
		btnApplyScheduling.setPreferredSize(new Dimension(60, 23));
		btnApplyScheduling.addActionListener(this);

		btnApplyChanges.setFont(new java.awt.Font("Dialog", 0, 10));
		btnApplyChanges.setText("Apply Chart Changes");
		btnApplyChanges.setActionCommand("ApplyChanges");
		btnApplyChanges.setPreferredSize(new Dimension(125, 20));
		btnApplyChanges.addActionListener(this);

		cbxCounterFactualReasoning.setText("Counter Factual Reasoning");
		cbxCounterFactualReasoning.setSelected(false);

		inputTabs.addTab("Schedule",SchedulingPanel);
		inputTabs.addTab("Timing",TimingPanel);

		if(item.getSchedule().size() != 0)
		{
			int lastTime = Integer.parseInt(item.getSchedule().lastKey().toString());

			if((lastTime-1) - 60 > 0 )
			{
				int addtoChartWidth = 0;
				int addToLength = (lastTime-1) - 60 ;

				if(addToLength >= 10 && addToLength <= 20)
				{

					addtoChartWidth = 180;

				}
				else if(addToLength >= 20)
				{
					int mult = Math.round(addToLength/10);

					addtoChartWidth = mult*90;
				}
				else
				{
					addtoChartWidth = 90 ;
				}

				modelSingle = new ChartModel(lastTime+1,530+addtoChartWidth,175);
				cpanelSingle = new ChartPanel(modelSingle);
				x = 580+addtoChartWidth;

			}

			this.getContentPane().removeAll();
			singleInputGraphPanel.removeAll();
			singleInputGraphPanel.add(cpanelSingle);
		}


		this.getContentPane().add(lblSingleChart,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 4, 0, 0), 0, 0));
		this.getContentPane().add(singleInputGraphPanel,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(btnApplyChanges,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 1), 0, 0));
		this.getContentPane().add(inputTabs,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		SchedulingPanel.add(lblTimeFrom,
				new GridBagConstraints(0, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(lblTimeTo,
				new GridBagConstraints(11, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(lblScheduledProb,
				new GridBagConstraints(21, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(spTimeFrom,
				new GridBagConstraints(0, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(spTimeTo,
				new GridBagConstraints(11, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(spScheduledProb,
				new GridBagConstraints(21, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		SchedulingPanel.add(btnApplyScheduling,
				new GridBagConstraints(11, 2, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		TimingPanel.add(lblDelay,
				new GridBagConstraints(0, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(lblPersistance,
				new GridBagConstraints(11, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(lblContinuation,
				new GridBagConstraints(21, 0, 10, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(spDelay,
				new GridBagConstraints(0, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(spPersist,
				new GridBagConstraints(11, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(spContinuationProb,
				new GridBagConstraints(21, 1, 10, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		TimingPanel.add(btnApplyTiming,
				new GridBagConstraints(11, 2, 10, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		//initilize the chart

		initializeChart(item.getSchedule());

		this.doLayout();
		this.setIgnoreRepaint(false);
		this.repaint();
	}

	//initialize for mechanism
	public void init2()
	{

	}

	//initialize for plan level for master scheduler
	public void init3()
	{

	}

	private void initializeChart(TreeMap schedule)
	{
		modelSingle.removeAllObjects();

		if(schedule.size() == 0)
		{
			return;
		}


		CDataSet ds = new CDataSet();
		Iterator i = schedule.entrySet().iterator();

		ArrayList<Integer> consecutiveKeys = new ArrayList<Integer>();
		ArrayList<Integer> usedKeys = new ArrayList<Integer>();
		ScheduleObject obj ;

		java.util.Map.Entry e1 = (java.util.Map.Entry)i.next();
		consecutiveKeys.add((Integer)e1.getKey());


		if(i.hasNext() == false)
		{
			float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
			obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
					item.getPersistence(),p, item.getContinuation());
			ds.addScheduleObject(obj);
		}

		while(i.hasNext())
		{


			while(i.hasNext())
			{

				java.util.Map.Entry e2 = (java.util.Map.Entry)i.next();

				if(((Integer)e2.getKey() - (Integer)e1.getKey() == 1 ) && (e2.getValue().equals(e1.getValue())) && i.hasNext() == false)
				{	
					consecutiveKeys.add((Integer)e1.getKey());
					consecutiveKeys.add((Integer)e2.getKey());
					float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
					obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
							item.getPersistence(),p, item.getContinuation());
					ds.addScheduleObject(obj);
					usedKeys.addAll(consecutiveKeys);
					consecutiveKeys.clear();
				}
				else if(((Integer)e2.getKey() - (Integer)e1.getKey() == 1 ) && (e2.getValue().equals(e1.getValue())))
				{	
					consecutiveKeys.add((Integer)e1.getKey());
					consecutiveKeys.add((Integer)e2.getKey());
					e1 = e2;
				}
				else if(((Integer)e2.getKey() - (Integer)e1.getKey() == 1 ) && (e2.getValue().equals(e1.getValue())) == false)
				{
					float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
					obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
							item.getPersistence(),p, item.getContinuation());
					ds.addScheduleObject(obj);
					usedKeys.addAll(consecutiveKeys);
					consecutiveKeys.clear();
					e1 = e2;
					consecutiveKeys.add((Integer)e1.getKey());
					break;
				}
				else if(((Integer)e2.getKey() - (Integer)e1.getKey() != 1 ))
				{
					float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
					obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
							item.getPersistence(),p, item.getContinuation());
					ds.addScheduleObject(obj);
					usedKeys.addAll(consecutiveKeys);
					consecutiveKeys.clear();
					e1 = e2;
					consecutiveKeys.add((Integer)e1.getKey());
					break;
				}
				if(i.hasNext() == false && usedKeys.contains((Integer)e2.getKey())== false)
				{
					consecutiveKeys.add((Integer)e2.getKey());
					float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
					obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
							item.getPersistence(),p, item.getContinuation());
					ds.addScheduleObject(obj);
					consecutiveKeys.clear();
				}

			}        	
		}

		if(i.hasNext() == false )
		{
			try
			{
				float p = Float.parseFloat(schedule.get(consecutiveKeys.get(0)).toString());
				obj = new ScheduleObject(consecutiveKeys.get(0),consecutiveKeys.get(consecutiveKeys.size()-1), item.getDelay(), 
						item.getPersistence(),p, item.getContinuation());
				ds.addScheduleObject(obj);
			}
			catch(IndexOutOfBoundsException iob)
			{
				logger.warn("initializeChart - IOOBExc initializing chart");
			}

		}

		modelSingle.addDataSet(ds);
		cpanelSingle.refresh();



	}

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("ApplyTiming"))
		{
			item.setDelay(Integer.parseInt(spDelay.getValue().toString()));
			item.setPersistence(Integer.parseInt(spPersist.getValue().toString()));
			item.setContinuation(Float.parseFloat(spContinuationProb.getValue().toString()));

			initializeChart(item.getSchedule());
		}
		if(e.getActionCommand().equals("ApplySchedule"))
		{

			if((Integer)spTimeFrom.getValue() > (Integer)spTimeTo.getValue())
			{
				JOptionPane.showMessageDialog(this,"'Time To' should be greater than 'Time From'");
				return;
			}
			else if(((Integer)spTimeFrom.getValue()).equals((Integer)spTimeTo.getValue()))
			{
				TreeMap s = item.getSchedule();

				s.put((Integer)spTimeFrom.getValue(),spScheduledProb.getValue());

				item.setSchedule(s);

				initializeChart(item.getSchedule());
			}
			else
			{

				int t2 = Integer.parseInt(spTimeTo.getValue().toString());

				int tr = Integer.parseInt(spTimeFrom.getValue().toString());

				TreeMap s = item.getSchedule();

				for(;tr != t2+1;tr++)
				{
					s.put((Integer)tr,spScheduledProb.getValue());
				}
				item.setSchedule(s);
				initializeChart(item.getSchedule());
			}
			init1();

		}
		if(e.getActionCommand().equals("ApplyChanges"))
		{
			Iterator i = modelSingle.getDataSets().iterator();

			if(i.hasNext()==false)
			{
				TreeMap s = new TreeMap();
				item.setSchedule(s);
			}

			while(i.hasNext())
			{
				Iterator ji = ((CDataSet)i.next()).getSetValues().iterator();
				TreeMap s = new TreeMap();
				if(ji.hasNext()==false)
				{	
					item.setSchedule(s);
				}

				while(ji.hasNext())
				{
					ScheduleObject sO = (ScheduleObject)ji.next();

					int timeFrom = sO.getTime();
					int timeTo = sO.getTimeto(); 

					float prob = sO.getProbability();

					if(timeFrom == timeTo)
					{
						s.put((Integer)timeTo,MaskedFloat.getMaskedValue(prob));
					}
					else if(timeFrom < timeTo)
					{
						for(;timeFrom != timeTo+1;timeFrom++)
						{
							s.put((Integer)timeFrom,MaskedFloat.getMaskedValue(prob));
						}
					}
				}
				item.setSchedule(s);
			}
			initializeChart(item.getSchedule());
		}
	}

	public void windowOpened(WindowEvent arg0) {


	}

	public void windowClosing(WindowEvent arg0) 
	{

		canvasShape.setAttachment(item.getGuid());
		try
		{
			// this putShape() is only here to force whiteboard to shape redraw immediately instead of mouse rollover
			//Controller.putShape(canvasShape);
			controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, item, false));
		} catch (java.rmi.RemoteException re)
		{
			logger.error("windowClosing - RemoteExc updating item:  "+re.getMessage());
		}


	}

	public void windowClosed(WindowEvent arg0) {


	}

	public void windowIconified(WindowEvent arg0) {


	}

	public void windowDeiconified(WindowEvent arg0) {


	}

	public void windowActivated(WindowEvent arg0) {


	}

	public void windowDeactivated(WindowEvent arg0) {


	}

}
