package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;


import mil.af.rl.jcat.bayesnet.Evidence;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.exceptions.DuplicateEvidenceException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.table.MaskedComponent;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.DefaultDialogPage;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.ShapeHighlighter;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBControllerManager;
import com.c3i.jwb.JWBShape;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.MultiplePageDialog;
import com.jidesoft.dialog.PageList;

/**
 * The TempEvidenceDialog is used to obtain experimental evidence values from the user. <br>
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
public final class EvidenceDialog extends MultiplePageDialog implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	private JWBShape canvasShape;

	private PlanItem item = null;
	private HashMap<Integer, Evidence> evidence = null;
	private JWBController pcontroller = null;
	private JCheckBox report = new JCheckBox("Report Was True?");
	private JTable sensorTable;
	private JTable absoluteTable;

	private PageList model = new PageList();

	private String[] sensorTableHeaders = {"Time", "False Alarm Rate", "Missed Detection Rate", "Report"};
	private String[] absoluteTableHeaders = {"Time", "Belief"};

	private int noEdit[] = { 0, 1 ,2, 3 };
	private NoEditTableModel sensorTableModel = new NoEditTableModel(sensorTableHeaders, 0, noEdit);
	private NoEditTableModel absoluteTableModel = new NoEditTableModel(absoluteTableHeaders, 0, noEdit);

	//private SpinnerNumberModel farModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.01);
	//private SpinnerNumberModel mdrModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.01);
	//private SpinnerNumberModel probModel = new SpinnerNumberModel(1.0,0.0, 1.0, 0.1);
	private SpinnerNumberModel timeModel = new SpinnerNumberModel(0, 0, 100000, 1);

//	private MaskedComponent probSpinner;
	private MaskedComponent mdrSpinner;
	private MaskedComponent farSpinner;

	private ShapeHighlighter shapeHighlight;

	private AbstractPlan plan;

	private JRadioButton beliefValueTrue;
	private static Logger logger = Logger.getLogger(EvidenceDialog.class);


	/**
	 * Used to display a multiple text field dialog which is used to modify the
	 * text of a given shape.
	 *
	 * @param parent
	 *            the component to place this dialog on top of
	 * @param canvasShape
	 *            the shape to modify the text of
	 */
	public EvidenceDialog(java.awt.Frame parent, JWBShape canvasShape, AbstractPlan plan) {
		super(parent);
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		pcontroller = JWBControllerManager.getInstance().getControllerByShape (canvasShape.getUID());
		this.canvasShape = canvasShape;
		this.plan = plan;

		// setup GUI layout
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// setup scheduling panel
		DefaultDialogPage sensorEvidencePanel = new DefaultDialogPage(new BorderLayout());
		DefaultDialogPage absoluteEvidencePanel = new DefaultDialogPage(new BorderLayout());
		// add sensorTable
		JPanel sensorPanel = new JPanel(new BorderLayout());
		sensorTable = new JTable(sensorTableModel);
		JScrollPane tablePane = new JScrollPane();
		tablePane.getVerticalScrollBar().setUnitIncrement(15);
		tablePane.getViewport().setPreferredSize(new Dimension(200, 0));
		tablePane.getViewport().add(sensorTable);
		sensorPanel.add(tablePane, BorderLayout.CENTER);

		// add time label
		JPanel buttonPanel =  new JPanel(new GridLayout(11, 1));
		JLabel timeLabel = new JLabel("Time:           ");
		buttonPanel.add(timeLabel);	
		JSpinner timeSpinner = new JSpinner(timeModel);
		timeSpinner.setPreferredSize(new Dimension(100, 16));
		buttonPanel.add(timeSpinner);

		// add FAR
		JLabel farLabel = new JLabel("False Alarm Rate: ");
		buttonPanel.add(farLabel);	
		farSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(0.0));
		farSpinner.setPreferredSize(new Dimension(100, 16));
		buttonPanel.add(farSpinner);

		//add MDR 
		JLabel mdrLabel = new JLabel("Missed Detection Rate");
		buttonPanel.add(mdrLabel);  
		mdrSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(0.0));
		mdrSpinner.setPreferredSize(new Dimension(100, 16));
		buttonPanel.add(mdrSpinner);

		//Add Report CheckBox
		report.setSelected(true);
		buttonPanel.add(report);

		// add evidence button
		JButton addReport = new JButton("Add Report");
		addReport.setSize(new Dimension(100, 16));
		addReport.setPreferredSize(new Dimension(100, 16));
		buttonPanel.add(addReport);
		addReport.addActionListener(this);

		// add delete button
		JButton deleteReport = new JButton("Delete Report");
		deleteReport.setSize(new Dimension(100, 16));
		buttonPanel.add(deleteReport);
		deleteReport.addActionListener(this);

		// add delete all button
		JButton deleteAllReports = new JButton("Delete All");
		deleteAllReports.setActionCommand("Delete All Reports");
		deleteAllReports.setSize(new Dimension(100, 16));
		buttonPanel.add(deleteAllReports);
		deleteAllReports.addActionListener(this);

		JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0,2,0,2));
		try{
			if(parent instanceof MainFrm)
				cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(((MainFrm)parent).getHelpBroker()));
			buttonPanel.add(cHelpBtn);
		}catch(Exception ex){
			logger.warn("Constructor - could not initialize context help button:  "+ex.getMessage());
		}

		//Add button panel to sensor panel
		sensorPanel.add(buttonPanel, BorderLayout.EAST);
		sensorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Reports"));

		// add scheduling panel		
		sensorEvidencePanel.add(sensorPanel, BorderLayout.CENTER);
		sensorEvidencePanel.setTitle("Sensor Evidence");     

		//Create the Abgsolute Panel
		JPanel absolutePanel = new JPanel(new BorderLayout());
		absolutePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Beliefs"));
		absoluteTable = new JTable(absoluteTableModel);
		JScrollPane absoluteScroll = new JScrollPane(absoluteTable);
		absolutePanel.add(absoluteScroll, BorderLayout.CENTER);
		//absoluteScroll.getViewport().setPreferredSize(new Dimension(200, 0));

		//Create Absolute Buttons
		JPanel absButtons = new JPanel(new GridLayout(11,1));
		JLabel timeLbl = new JLabel("Time:");
//		timeLbl.setPreferredSize(new Dimension(100, 16));
		absButtons.add(timeLbl);
		timeSpinner = new JSpinner(timeModel);
//		timeSpinner.setPreferredSize(new Dimension(100, 16));
		absButtons.add(timeSpinner);

		//Add probability spinner
		JLabel probLbl = new JLabel("Belief:");
//		probLbl.setPreferredSize(new Dimension(100, 16));
		absButtons.add(probLbl);        
//		probSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(1.0));
//		probSpinner.setPreferredSize(new Dimension(100, 16));
		beliefValueTrue = new JRadioButton("True", true);
		absButtons.add(beliefValueTrue);
		JRadioButton beliefValueFalse = new JRadioButton("False", false);
		absButtons.add(beliefValueFalse);
		ButtonGroup beliefChoices = new ButtonGroup();
		beliefChoices.add(beliefValueTrue);
		beliefChoices.add(beliefValueFalse);

		//Add Belief Button
		JButton addBeliefBtn = new JButton("Add Belief");
		addBeliefBtn.addActionListener(this);
//		addBeliefBtn.setPreferredSize(new Dimension(100, 16));
		absButtons.add(addBeliefBtn);
		//Add delete Button
		JButton deleteBelief = new JButton("Delete Belief");
		deleteBelief.addActionListener(this);
//		deleteBelief.setPreferredSize(new Dimension(100, 16));
		absButtons.add(deleteBelief);
		//Add Delete All button
		JButton deleteAllBeliefs = new JButton("Delete All");
		deleteAllBeliefs.addActionListener(this);
//		deleteAllBeliefs.setPreferredSize(new Dimension(100, 16));
		deleteAllBeliefs.setActionCommand("Delete All Beliefs");
		absButtons.add(deleteAllBeliefs);        

		JButton cHelpBtn1 = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn1.setMargin(new java.awt.Insets(0,2,0,2));
		try{
			if(parent instanceof MainFrm)
				cHelpBtn1.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(((MainFrm)parent).getHelpBroker()));
			absButtons.add(cHelpBtn1);
		}catch(Exception ex)
		{
			logger.warn("Constructor - could not initialize context help button:  "+ex.getMessage());
		}

		//Add absolute panel
		absolutePanel.add(absButtons, BorderLayout.EAST);
		absoluteEvidencePanel.add(absolutePanel, BorderLayout.CENTER);
		absoluteEvidencePanel.setTitle("Absolute Evidence");

		//register context help items
		javax.help.CSH.setHelpIDString(sensorEvidencePanel, "Sensor_Evidence");
        javax.help.CSH.setHelpIDString(absoluteEvidencePanel, "Absolute_Evidence");

		//Add Tabs
		model.append(absoluteEvidencePanel);
		model.append(sensorEvidencePanel);
		setPageList(model);

		setModal(true);
		//this.setResizable(false);
		setSize(new Dimension(560, 340));
		initDialog();
		setLocationRelativeTo(parent);	

		shapeHighlight = new ShapeHighlighter(canvasShape, ShapeHighlighter.ALPHA);
		this.setVisible(true);
	}

	/**
	 * Invoked when an action occurs.
	 *
	 * @param item
	 *            the ActionEvent received
	 */
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("OK")) {
			oK();
		} else if (command.equals("Cancel")) {
			dispose();
		} else if(command.equals("Add Report")) {
			addReport();
		} else if(command.equals("Delete Report")) {
			deleteSensorReport();
		} else if(command.equals("Delete All Reports")) {
			deleteAllSensorReports();
		}else if(command.equals("Delete All Beliefs")){
			deleteAllAbsoluteBeliefs();
		}else if(command.equals("Add Belief")){
			addAbsoluteBelief();
		}else if(command.equals("Delete Belief")){
			deleteAbsoluteBelief();
		}


	}

	/**
	 * Called from scheduleButton
	 *
	 */
	private void addReport() {
		int time = timeModel.getNumber().intValue();
		double far = ((MaskedFloat)farSpinner.getValue()).doubleValue();
		double mdr = ((MaskedFloat)mdrSpinner.getValue()).doubleValue();
		boolean selected = report.isSelected();
		try
		{
			this.addReport(time, far, mdr, selected);
		} catch (DuplicateEvidenceException e)
		{
			JOptionPane.showMessageDialog(this, "You may not have two pieces of evidence for one timeslice");
		}
		redrawSensorTable();
	}

	/**
	 * 
	 *
	 */
	private void addAbsoluteBelief(){
		int time = timeModel.getNumber().intValue();
//		double probability = ((MaskedFloat)probSpinner.getValue()).doubleValue();
		boolean belief = beliefValueTrue.isSelected();
		
		try
		{
			this.addAbsoluteBelief(time, (belief) ? 1.0 : 0.0);
		} catch (DuplicateEvidenceException e)
		{
			JOptionPane.showMessageDialog(this, "You may not have two pieces of evidence for one timeslice");
		}
		redrawAbsoluteTable();
	}

	/**
	 * Used for scheduling events from the interface Scheduling from within the
	 * GUI is done using the timing dialog
	 *
	 * @param time
	 *            The time the item occurs
	 * @param prob
	 *            The probability the item occurs at the time
	 * @throws DuplicateEvidenceException 
	 */
	private void addReport(int time, double far, double mdr, boolean report) throws DuplicateEvidenceException
	{
		if(evidence.containsKey(new Integer(time)))
			throw new DuplicateEvidenceException();
		this.evidence.put(new Integer(time), new Evidence(report, far, mdr));
	}

	/**
	 * @throws DuplicateEvidenceException 
	 * 
	 */
	private void addAbsoluteBelief(int time, double prob) throws DuplicateEvidenceException
	{
		if(evidence.containsKey(new Integer(time)))
			throw new DuplicateEvidenceException();
		evidence.put(new Integer(time), new Evidence(prob));
	}

	/**
	 * Called from DeleteAll Button
	 *
	 */
	private void deleteAllSensorReports() {
		Iterator i = ((java.util.HashMap)evidence.clone()).entrySet().iterator();
		while(i.hasNext())
		{
			Entry e = (Map.Entry) i.next();
			if(((Evidence)e.getValue()).getType() == Evidence.SENSOR)
			{
				evidence.remove(e.getKey());
			}
		}
		for (int r = sensorTable.getRowCount(); 0 < r; r--)
			sensorTableModel.removeRow(r - 1);
	}

	private void deleteAllAbsoluteBeliefs() {
		Iterator i = ((java.util.HashMap)evidence.clone()).entrySet().iterator();
		while(i.hasNext())
		{
			Entry e = (Map.Entry) i.next();
			if(((Evidence)e.getValue()).getType() == Evidence.ABSOLUTE)
				evidence.remove(e.getKey());
		}
		for (int r = absoluteTable.getRowCount(); 0 < r; r--)
			absoluteTableModel.removeRow(r - 1);
	}

	/**
	 * Called form Delete button
	 *
	 */
	 private void deleteSensorReport() {
		 int k = sensorTable.getSelectedRow();
		 if(k == -1)
			 return;
		 Integer i = (Integer) sensorTableModel.getValueAt(k, 0);
		 evidence.remove(i);
		 sensorTableModel.removeRow(k);
	 }

	 private void deleteAbsoluteBelief(){
		 int k = absoluteTable.getSelectedRow();
		 if(k == -1)
			 return;
		 Integer i = (Integer) absoluteTableModel.getValueAt(k, 0);
		 evidence.remove(i);
		 absoluteTableModel.removeRow(k);
	 }

	 /**
	  *
	  */
	  private void initDialog() 
	  {
		  //Initially redraw the sensorTable and fill with values
		  item = plan.getItem((Guid)canvasShape.getAttachment());
		  setTitle("Evidence - " + item.getName() + " - " + item.getLabel());
		  evidence = new HashMap<Integer,  Evidence>(item.getEvidence());
		  try{
			  redrawSensorTable();
			  redrawAbsoluteTable();
		  }catch(Exception ex){
			  logger.error("initDialog - error initalizing tables:  "+ex.getMessage());
		  }
	  }

	  private void redrawAbsoluteTable()
	  {
		  absoluteTableModel.getDataVector().clear();
		  Object[] t = this.evidence.keySet().toArray();
		  Arrays.sort(t);
		  for (int i = 0; i < t.length; i++) {
			  Evidence e = (Evidence) evidence.get((Integer) t[i]);
			  if(e.getType() == Evidence.ABSOLUTE)
			  {
				  Vector entry = new Vector();
				  entry.add(((Integer) t[i]));            
				  entry.add(MaskedFloat.getMaskedValue(e.getProbability()));
				  absoluteTableModel.addRow(entry);
			  }
		  }        
	  }

	  private void redrawSensorTable() {
		  sensorTableModel.getDataVector().clear();
		  Object[] t = this.evidence.keySet().toArray();
		  Arrays.sort(t);
		  for (int i = 0; i < t.length; i++) {
			  Evidence e = (Evidence) evidence.get((Integer) t[i]);
			  if(e.getType() == Evidence.SENSOR)
			  {
				  Vector entry = new Vector();
				  entry.add(((Integer) t[i]));            
				  entry.add(MaskedFloat.getMaskedValue(e.getFAR()));
				  entry.add(MaskedFloat.getMaskedValue(e.getMDR()));
				  entry.add(new Boolean(e.isReport()));
				  sensorTableModel.addRow(entry);
			  }
		  }
	  }

	  /**
	   *
	   */
	   private void oK() {
		  item.setEvidence(evidence);
//		  List<JWBUID> shapes = plan.getShapeMapping(item.getGuid());
//		  ArrayList updateList = new ArrayList();
//		  for(JWBUID shapeId : shapes)
//		  {
//		  JWBShape shape = pcontroller.getShape(shapeId);
//		  updateList.add(shape);

//		  if(evidence.size() == 0)
//		  shape.removeMarkup('E');
//		  else
//		  {
//		  shape.removeMarkup('E');
//		  shape.addMarkup('E');
//		  }
//		  }
		  try
		  {
			  pcontroller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, item, false));
		  } catch (RemoteException e)
		  {
			  logger.error("ok - RemoteExc updating shape:  "+e.getMessage());
		  }
		  dispose();
	   }

	   public ButtonPanel createButtonPanel()
	   {
		   ButtonPanel p = new ButtonPanel();
		   //add ok button
		   JButton okButton = new JButton("OK");
		   okButton.setPreferredSize(new Dimension(40, 23));
		   okButton.addActionListener(this);
		   p.add(okButton);

		   // add cancel button
		   JButton cancelButton = new JButton("Cancel");
		   cancelButton.setPreferredSize(new Dimension(40, 23));
		   cancelButton.addActionListener(this);
		   p.add(cancelButton);

		   return p;
	   }

	   public void dispose()
	   {
		   shapeHighlight.stop();
		   super.dispose();
	   }
}
