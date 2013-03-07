package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.table.MaskedComponent;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.ShapeHighlighter;

import com.c3i.jwb.*;

/**
 * The TimingDialog is used to obtain timing instructions from the user. <br>
 * <br>
 * AFRL provides this Software to you on an "AS IS" basis, without warranty of
 * any kind. AFRL HEREBY EXPRESSLY DISCLAIMS ALL WARRANTIES OR CONDITIONS,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. You are solely responsible for determining the appropriateness of
 * using this Software and assume all risks associated with the use of this
 * Software, including but not limited to the risks of program errors, damage to
 * or loss of data, programs or equipment, and unavailability or interruption of
 * operations.
 * 
 * @author Francis Conover ( C3I Associates )
 * @version JCAT v0.1 <br>
 */
public final class TimingDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private JWBShape canvasShape;

	private PlanItem item = null;
	private TreeMap schedule = null;

	private String[] colHeaders = { "Time", "Probability" };

	private int noEdit[] = { 0, 1 };

	private SpinnerNumberModel delayModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);

	private JSpinner delaySpinner = new JSpinner(delayModel);

	private SpinnerNumberModel persistenceModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);

	private JSpinner persistenceSpinner = new JSpinner(persistenceModel);

	//private SpinnerNumberModel continuationModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);

	private MaskedComponent continuationSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(0.0));

	private SpinnerNumberModel timeModel = new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1);

	private JSpinner timeSpinner = new JSpinner(timeModel);

	//private SpinnerNumberModel probabilityModel = new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1);

	private MaskedComponent probabilitySpinner = new MaskedComponent(MaskedFloat.getMaskedValue(1.0));

	private JTable table;
	private NoEditTableModel tableModel = new NoEditTableModel(colHeaders, 0, noEdit);
	private JWBController pcontroller = null;

	private ShapeHighlighter shapeHighlight;
	private static Logger logger = Logger.getLogger(TimingDialog.class);

	private AbstractPlan plan;

	/**
	 * Used to display a multiple text field dialog which is used to modify the
	 * text of a given shape.
	 * 
	 * @param parent
	 *            the component to place this dialog on top of
	 * @param canvasShape
	 *            the shape to modify the text of
	 */
	public TimingDialog(java.awt.Frame parent, JWBShape canvasShape, AbstractPlan plan)
	{
		super(parent);
		this.plan = plan;
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		pcontroller = JWBControllerManager.getInstance().getControllerByShape(canvasShape.getUID());
		item = plan.getItem((Guid)canvasShape.getAttachment());

		// lock the shape and tell the model
		// canvasShape.setLock(true,model.getUID());
		// model.replaceShape(canvasShape);
		this.canvasShape = canvasShape;

		// setup GUI layout
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// setup timing panel
		JPanel timingPanel = new JPanel();
		timingPanel.setSize(new Dimension(100, 40));
		// GridBagLayout gridbagTimingPanel = new GridBagLayout();
		// GridBagConstraints constraintsTimingPanel = new GridBagConstraints();
		timingPanel.setLayout(new GridLayout(2, 3));
		// constraintsTimingPanel.fill = GridBagConstraints.EAST;

		// add delay label
		/*
		 * constraintsTimingPanel.gridx = 0; constraintsTimingPanel.gridy = 0;
		 * constraintsTimingPanel.insets = new Insets(3, 6, 0, 3);
		 */
		JLabel delayLabel = new JLabel("Delay:");
		// gridbagTimingPanel.setConstraints(delayLabel,
		// constraintsTimingPanel);
		timingPanel.add(delayLabel);

		// add persistence label
		/*
		 * constraintsTimingPanel.gridx = 1; constraintsTimingPanel.gridy = 0;
		 * constraintsTimingPanel.insets = new Insets(3, 6, 0, 3);
		 */
		JLabel persistenceLabel = new JLabel("Persistence:");
		/*
		 * gridbagTimingPanel.setConstraints(persistenceLabel,
		 * constraintsTimingPanel);
		 */
		timingPanel.add(persistenceLabel);

		// add continuation label
		/*
		 * constraintsTimingPanel.gridx = 2; constraintsTimingPanel.gridy = 0;
		 * constraintsTimingPanel.insets = new Insets(3, 6, 0, 3);
		 */
		JLabel continuationLabel = new JLabel("Continuation:");
		/*
		 * gridbagTimingPanel.setConstraints(continuationLabel,
		 * constraintsTimingPanel);
		 */
		timingPanel.add(continuationLabel);

		// add delay spinner
		/*
		 * constraintsTimingPanel.gridx = 0; constraintsTimingPanel.gridy = 1;
		 * constraintsTimingPanel.insets = new Insets(0, 3, 3, 3);
		 */
		delaySpinner.setSize(new Dimension(10, 18));
		// gridbagTimingPanel.setConstraints(delaySpinner,
		// constraintsTimingPanel);
		timingPanel.add(delaySpinner);

		// add persistence spinner
		/*
		 * constraintsTimingPanel.gridx = 1; constraintsTimingPanel.gridy = 1;
		 * constraintsTimingPanel.insets = new Insets(0, 3, 3, 3);
		 */
		persistenceSpinner.setSize(new Dimension(10, 18));
		/*
		 * gridbagTimingPanel.setConstraints(persistenceSpinner,
		 * constraintsTimingPanel);
		 */
		timingPanel.add(persistenceSpinner);

		// add continuation spinner
		/*
		 * constraintsTimingPanel.gridx = 2; constraintsTimingPanel.gridy = 1;
		 * constraintsTimingPanel.insets = new Insets(0, 3, 3, 3);
		 */
		continuationSpinner.setSize(new Dimension(10, 18));
		//continuationSpinner.setEditor(new JSpinner.NumberEditor(continuationSpinner, "#.#"));
		/*
		 * gridbagTimingPanel.setConstraints(continuationSpinner,
		 * constraintsTimingPanel);
		 */
		timingPanel.add(continuationSpinner);

        // add timing panel
        /*
         * constraints.gridx = 0; constraints.gridy = 0; constraints.gridwidth =
         * 3; constraints.insets = new Insets(3, 3, 3, 3);
         */
        timingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Timing"));
        javax.help.CSH.setHelpIDString(timingPanel, "Schedule_Events");
        // gridbag.setConstraints(timingPanel, constraints);
        contentPane.add(timingPanel, BorderLayout.NORTH);

		// setup scheduling panel
		JPanel schedulingPanel = new JPanel();
		schedulingPanel.setSize(120, 80);
		GridBagLayout gridbagSchedulingPanel = new GridBagLayout();
		GridBagConstraints constraintsSchedulingPanel = new GridBagConstraints();
		schedulingPanel.setLayout(gridbagSchedulingPanel);
		constraintsSchedulingPanel.fill = GridBagConstraints.BOTH;

		// add table
		constraintsSchedulingPanel.gridx = 0;
		constraintsSchedulingPanel.gridy = 0;
		constraintsSchedulingPanel.gridheight = 7;
		constraintsSchedulingPanel.insets = new Insets(0, 3, 3, 3);
		Vector names = new Vector();
		names.add("Time");
		names.add("Probability");
		table = new JTable(tableModel);
		if(item instanceof Mechanism)
			table.setEnabled(false);
		tableModel.setColumnIdentifiers(names);
		JScrollPane tablePane = new JScrollPane();
		tablePane.getVerticalScrollBar().setUnitIncrement(15);
		tablePane.setPreferredSize(new Dimension(200, 0));
		tablePane.setMinimumSize(new Dimension(150, 0));
		tablePane.getViewport().setPreferredSize(new Dimension(200, 0));
		tablePane.setMinimumSize(new Dimension(150, 0)); //fixes table being unusably small on a mac - stupid macs
		tablePane.getViewport().add(table);
		gridbagSchedulingPanel.setConstraints(tablePane,
				constraintsSchedulingPanel);
		schedulingPanel.add(tablePane);

		// add time label
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 0;
		constraintsSchedulingPanel.gridheight = 1;
		constraintsSchedulingPanel.insets = new Insets(3, 6, 0, 3);
		JLabel timeLabel = new JLabel("Time:");
		gridbagSchedulingPanel.setConstraints(timeLabel,
				constraintsSchedulingPanel);
		schedulingPanel.add(timeLabel);

		// add time spinner
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 1;
		constraintsSchedulingPanel.insets = new Insets(0, 3, 3, 3);
		timeSpinner.setPreferredSize(new Dimension(100, 18));
		if(item instanceof Mechanism)
			timeSpinner.setEnabled(false);
		gridbagSchedulingPanel.setConstraints(timeSpinner,
				constraintsSchedulingPanel);
		schedulingPanel.add(timeSpinner);

		// add probability label
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 2;
		constraintsSchedulingPanel.insets = new Insets(3, 6, 0, 3);
		JLabel probabilityLabel = new JLabel("Probability:");
		gridbagSchedulingPanel.setConstraints(probabilityLabel,
				constraintsSchedulingPanel);
		schedulingPanel.add(probabilityLabel);

		// add probability spinner
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 3;
		constraintsSchedulingPanel.insets = new Insets(0, 3, 3, 3);
		probabilitySpinner.setPreferredSize(new Dimension(100, 20));
		if(item instanceof Mechanism)
			probabilitySpinner.setEnabled(false);
		gridbagSchedulingPanel.setConstraints(probabilitySpinner,
				constraintsSchedulingPanel);
		schedulingPanel.add(probabilitySpinner);

		// add schedule button
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 4;
		constraintsSchedulingPanel.insets = new Insets(3, 3, 3, 3);
		JButton scheduleButton = new JButton("Schedule");
		if(item instanceof Mechanism)
			scheduleButton.setEnabled(false);
		gridbagSchedulingPanel.setConstraints(scheduleButton,
				constraintsSchedulingPanel);
		schedulingPanel.add(scheduleButton);
		scheduleButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				OnSchedule();
			}
		});

		// add delete button
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 5;
		constraintsSchedulingPanel.insets = new Insets(3, 3, 3, 3);
		JButton deleteButton = new JButton("Delete");
		if(item instanceof Mechanism)
			deleteButton.setEnabled(false);
		gridbagSchedulingPanel.setConstraints(deleteButton,
				constraintsSchedulingPanel);
		schedulingPanel.add(deleteButton);
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				OnDelete();
			}
		});

		// add delete all button
		constraintsSchedulingPanel.gridx = 1;
		constraintsSchedulingPanel.gridy = 6;
		constraintsSchedulingPanel.insets = new Insets(3, 3, 3, 3);
		JButton deleteAllButton = new JButton("Delete All");
		if(item instanceof Mechanism)
			deleteAllButton.setEnabled(false);
		gridbagSchedulingPanel.setConstraints(deleteAllButton,
				constraintsSchedulingPanel);
		schedulingPanel.add(deleteAllButton);
		deleteAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				OnDeleteAll();
			}
		});

        // add scheduling panel
        /*
         * constraints.gridx = 0; constraints.gridy = 1; constraints.insets =
         * new Insets(3, 0, 3, 0);
         */
        schedulingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Scheduling"));
        javax.help.CSH.setHelpIDString(schedulingPanel, "Schedule_Events");
        // gridbag.setConstraints(schedulingPanel, constraints);
        contentPane.add(schedulingPanel, BorderLayout.CENTER);
        schedulingPanel.setVisible(true);

		// Make Button Panel
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new java.awt.FlowLayout());//new GridLayout(1, 3));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		// add spacer
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(20, 20));
		JLabel spacer1 = new JLabel();
		spacer1.setPreferredSize(new Dimension(20, 20));
		// gridbag.setConstraints(label, constraints);
		buttonPane.add(spacer);

		// add ok button
		JButton okButton = new JButton("OK");
//		okButton.setPreferredSize(new Dimension(70, 26));
		okButton.addActionListener(this);
		buttonPane.add(okButton);

		// add cancel button
		JButton cancelButton = new JButton("Cancel");
//		cancelButton.setPreferredSize(new Dimension(70, 26));
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);
		okButton.setPreferredSize(cancelButton.getPreferredSize());

		buttonPane.add(spacer1);
		
		JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0,2,0,2));
		if(parent instanceof MainFrm)
			cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(((MainFrm)parent).getHelpBroker()));
		buttonPane.add(cHelpBtn);

		// window closing, dispose dialog
		// this.addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {

		// dispose();
		// }
		// });

		this.setModal(true);
		this.setResizable(false);
		this.setSize(new Dimension(360, 340));
		setLocationRelativeTo(parent);
		initDialog();

		shapeHighlight = new ShapeHighlighter(canvasShape, ShapeHighlighter.ALPHA);
		this.setVisible(true);
	}

	/**
	 * 
	 */
	private void initDialog()
	{
		// Initially redraw the table and fill with values
		setTitle("Timing - " + item.getName() + " - " + item.getLabel());
		schedule = new TreeMap(item.getSchedule());
		try
		{
			redrawTable();
			continuationSpinner.setValue(MaskedFloat.getMaskedValue(item.getContinuation()));
			persistenceModel.setValue(new Integer(item.getPersistence()));
			delayModel.setValue(new Integer(item.getDelay()));
		} catch (Exception ex)
		{
			logger.error("initDialog - error initializing dialog:  "+ex.getMessage());
		}
	}

	/**
	 * Invoked when an action occurs.
	 * 
	 * @param item
	 *            the ActionEvent received
	 */
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals("OK"))
		{
			OnOK();

			//Iterate through ahapes that are associated with this mechanism
//			List<JWBUID> shapes = plan.getShapeMapping(item.getGuid());
//			ArrayList updateList = new ArrayList();
//			for(JWBUID shapeId : shapes)
//			{
//			JWBShape shape = pcontroller.getShape(shapeId);
//			updateList.add(shape);
			// check for default delay/persistence/count
//			if (item.getDelay() != 0 || item.getPersistence() != 1 || item.getContinuation() != 0.0f)
//			{
//			// remove to make sure a second one is not added
//			shape.removeMarkup('T');
//			shape.addMarkup('T');
//			} else
//			shape.removeMarkup('T');

//			if (schedule.size() > 0)
//			{
//			shape.removeMarkup('S');
//			shape.addMarkup('S');
//			} else
//			shape.removeMarkup('S');

			// make sure shape gets across
//			}
			try
			{                
				pcontroller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, item, false));
			} catch (java.rmi.RemoteException re) {
				logger.error("actionPerformed - RemExc applying new timing:  "+re.getMessage());
			}

			dispose();
		} else if (command.equals("Cancel"))
		{
			// canvasShape.setLock(false,model.getUID());
			// model.replaceShape(canvasShape);
			dispose();
		}
	}

	/**
	 * 
	 */
	private void OnOK()
	{
		item.setPersistence(persistenceModel.getNumber().intValue());
		item.setDelay(delayModel.getNumber().intValue());
		item.setContinuation(((MaskedFloat)continuationSpinner.getValue()).floatValue());
		item.setSchedule(this.schedule);
		canvasShape.setAttachment(item.getGuid());
	}

	/**
	 * Called from scheduleButton
	 * 
	 */
	public void OnSchedule()
	{
		this.scheduleEvent(timeModel.getNumber().intValue(), ((MaskedFloat)probabilitySpinner.getValue()) );
		redrawTable();
	}

	/**
	 * Called form Delete button
	 * 
	 */
	public void OnDelete()
	{
		int k = table.getSelectedRow();
		if (k != -1)
		{
			Integer i = (Integer) tableModel.getValueAt(k, 0);
			getSchedule().remove(i);
			tableModel.removeRow(table.getSelectedRow());
		}
	}

	/**
	 * Called from DeleteAll Button
	 * 
	 */
	public void OnDeleteAll()
	{
		getSchedule().clear();
		for (int r = table.getRowCount(); 0 < r; r--)
			tableModel.removeRow(r - 1);
	}

	public void redrawTable()
	{
		tableModel.getDataVector().clear();
		Object[] t = getSchedule().keySet().toArray();
		Arrays.sort(t);
		Set ent = getSchedule().entrySet();
		for (int i = 0; i < t.length; i++)
		{
			Vector entry = new Vector();
			entry.add(((Integer) t[i]));
			entry.add(MaskedFloat.getMaskedValue(((MaskedFloat)getSchedule().get((Integer)t[i])).floatValue()));
			tableModel.addRow(entry);
		}
	}

	/**
	 * Used for scheduling events from the interface Scheduling from within the
	 * GUI is done using the timing dialog
	 * 
	 * @param time
	 *            The time the item occurs
	 * @param prob
	 *            The probability the item occurs at the time
	 */
	public void scheduleEvent(int time, MaskedFloat prob)
	{
		this.schedule.put(new Integer(time), prob);
	}

	/**
	 * @return Returns the schedule.
	 */
	public TreeMap getSchedule()
	{
		return schedule;
	}

	/**
	 * @param schedule
	 *            The schedule to set.
	 */
	public void setSchedule(TreeMap schedule)
	{
		this.schedule = schedule;
	}

	public void dispose()
	{
		shapeHighlight.stop();
		super.dispose();
	}
}
