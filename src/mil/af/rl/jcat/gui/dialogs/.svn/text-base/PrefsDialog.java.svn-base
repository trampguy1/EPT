/*
 * Created on Jun 7, 2005
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.Guid;

/**
 * @author dygertm
 * User Preferences Dialog box
 * ex. align to grid, show grid, default colors, MRU size etc
 */
public class PrefsDialog extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JButton okButton, cancButton, changeFontBut;
	private JPanel genPanel;
	public ColorSchemePanel colorPanel;
	private JCheckBox gridSnapBox, showGridBox, simpModeBox, highlightBox, autoSaveBox;
	private JRadioButton showEditBox, persistModeBox, nameEvBox;
	private JSpinner MRUSize, autoSaveTime;
	private MainFrm _parent;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();
	private TitledBorder panelBorder = new TitledBorder(null, "", 2, 0);
	private JTabbedPane tabPane;
	private Control control;
	private JCheckBox debugLogBox;
	private static Logger logger = Logger.getLogger(PrefsDialog.class);
	
	
	public PrefsDialog(MainFrm parent)
	{
		super(parent, "User Preferences");
		setSize(470, 500);
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocationRelativeTo(parent);
		getContentPane().setLayout(null);
		_parent = parent;
		control = Control.getInstance();
		
		tabPane = new JTabbedPane();
		//setup tabs panels
		genPanel = new JPanel(layout); 
		genPanel.setBorder(panelBorder);
		javax.help.CSH.setHelpIDString(genPanel, "User_Preference_Dialog");
		tabPane.setBounds(2, 2, getWidth()-12, getHeight()-70);
		tabPane.addTab("General", genPanel);
		////
		colorPanel = new ColorSchemePanel(this);
		javax.help.CSH.setHelpIDString(colorPanel, "User_Preference_Dialog");
		tabPane.addTab("Color Scheme", colorPanel);
		
		//bottom section for buttons
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBounds(2, tabPane.getY()+tabPane.getHeight()+2, getWidth(), getHeight() - (tabPane.getY()+tabPane.getHeight()+2));
		
		okButton = new JButton("  OK  ");
		cancButton = new JButton("Cancel");
		showGridBox = new JCheckBox("Show grid lines", control.isShowGrid());
		gridSnapBox = new JCheckBox("Snap objects to grid", control.isAlignToGrid());
		ButtonGroup eventOptions = new ButtonGroup();
		JLabel eventLbl = new JLabel("Event creation:");
		showEditBox = new JRadioButton("Show event editor with new event", control.isShowEdit());
		nameEvBox = new JRadioButton("Prompt for name with new event", control.isPromptEventName());
		JRadioButton neitherEvBox = new JRadioButton("Neither", (!control.isShowEdit() && !control.isPromptEventName()));
		eventOptions.add(showEditBox);
		eventOptions.add(nameEvBox);
		eventOptions.add(neitherEvBox);
		ButtonGroup dropOptions = new ButtonGroup();
		JLabel modeLbl = new JLabel("Toolbar behavior:");
		persistModeBox = new JRadioButton("Persist mode after placing events and mechanisms", control.isPersistMode());
		JRadioButton normalBox = new JRadioButton("Reset mode after placing events and mechanisms", !control.isPersistMode());
		dropOptions.add(persistModeBox);
		dropOptions.add(normalBox);
		MRUSize = new JSpinner(new SpinnerNumberModel(parent.getMRUSize(), 0, 15, 1));
		JLabel MRULabel = new JLabel("Recent files list size:");
		simpModeBox = new JCheckBox("Enable simple probability mode", parent.isSimpleProbMode());
		highlightBox = new JCheckBox("Highlight objects when editing (blink)", parent.getHighlightEnabled());
		highlightBox.setEnabled(true);  //disable feature for now, not sure it worx perfectly
		changeFontBut = new JButton("Change node default font");
		autoSaveBox = new JCheckBox("Enable Auto-Saving      Delay:", parent.getAutoSvEnabled());
		autoSaveBox.setToolTipText("Auto Save location: "+EnvUtils.getJCATSettingsHome());
		autoSaveTime = new JSpinner(new SpinnerNumberModel(parent.getAutoSvTime(), 1, 60, 1));
		debugLogBox = new JCheckBox("Enable debug logging", Logger.getRootLogger().getLevel() != MainFrm.DEFAULT_LOG_LEVEL);
		
		if(parent.getActiveView() == null)
		{
			changeFontBut.setEnabled(false);
			changeFontBut.setToolTipText("You must first open a model to change this option");
		}
		
		okButton.addActionListener(this);
		cancButton.addActionListener(this);
		changeFontBut.addActionListener(this);
		
		bottomPanel.add(okButton, BorderLayout.WEST);
		bottomPanel.add(cancButton, BorderLayout.EAST);
		
		constraints.fill = GridBagConstraints.NONE; 
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(3, 2, 3, 2);
		
		addOption(simpModeBox, genPanel);		
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		addOption(autoSaveBox, genPanel);
		//addOption(new JLabel("Delay: "), genPanel);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		addOption(autoSaveTime, genPanel);
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		addOption(MRULabel, genPanel);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		addOption(MRUSize, genPanel);
		//constraints.gridwidth = GridBagConstraints.REMAINDER;
		//constraints.gridwidth = GridBagConstraints.RELATIVE;
		addOption(showGridBox, genPanel);
		addOption(gridSnapBox, genPanel);
		addOption(changeFontBut, genPanel);
		addOption(highlightBox, genPanel);
		addOption(debugLogBox, genPanel);
		constraints.insets = new Insets(5, 10, 0, 10);
		addOption(eventLbl, genPanel);
		constraints.insets = new Insets(0, 20, 0, 10);
		addOption(showEditBox, genPanel);
		addOption(nameEvBox, genPanel);
		constraints.insets = new Insets(0, 20, 5, 10);
		addOption(neitherEvBox, genPanel);
		constraints.insets = new Insets(0, 10, 0, 10);
		addOption(modeLbl, genPanel);
		constraints.insets = new Insets(0, 20, 0, 10);
		addOption(persistModeBox, genPanel);
		constraints.insets = new Insets(0, 20, 5, 10);
		addOption(normalBox, genPanel);
		constraints.insets = new Insets(5, 10, 5, 10);
			
		//constraints.gridwidth = GridBagConstraints.RELATIVE;
		
		
		getContentPane().add(tabPane);
		getContentPane().add(bottomPanel);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			_parent.setAlignToGrid(gridSnapBox.isSelected()); //call mainfrm methods instead of control so all panels get updated
			_parent.setShowGrid(showGridBox.isSelected());
		
			control.setAutoEventEdit(showEditBox.isSelected());
			control.setPromptEventName(nameEvBox.isSelected());
			control.setModePersist(persistModeBox.isSelected());
			_parent.setMRUSize(((Integer)MRUSize.getValue()).intValue());
			_parent.setSimpleProbMode(simpModeBox.isSelected());
			_parent.setHighlightEnabled(highlightBox.isSelected());
			_parent.setAutoSvTime(((Integer)autoSaveTime.getValue()).intValue());
			_parent.setAutoSvEnabled(autoSaveBox.isSelected());
			
			if(debugLogBox.isSelected())
				Logger.getRootLogger().setLevel(Level.DEBUG);
			else
				Logger.getRootLogger().setLevel(MainFrm.DEFAULT_LOG_LEVEL);
			
			//set the color scheme active and save the schemes
			if(colorPanel.getSelectedScheme() != null)
			{
				boolean schemeChanged = false;
//				if the scheme didn't change dont waste time updating plan items
				if(!colorPanel.getSelectedScheme().equals(ColorScheme.getInstance()))
					schemeChanged = true;
				colorPanel.getSelectedScheme().makeActive();
				//save it in the plan for later use when multiple plans are open
				if(_parent.getActiveView() != null)
				{
					AbstractPlan plan = _parent.getActiveView().getPlan();
					plan.setColorScheme(ColorScheme.getInstance());
					PlanArgument arg = new PlanArgument(PlanArgument.PLAN_COLORSCHEME);
					arg.getParameters().colorScheme = ColorScheme.getInstance();
					try{
						control.getController(plan.getId()).foreignUpdate(arg);
					}catch(RemoteException exc){
						logger.error("actionPerformed(ok) - RemoteExc sending colorscheme update:  "+exc.getMessage());
					}
				}
			
				if(schemeChanged)
					updateSchemeInPlan();
			}
			colorPanel.saveSchemes();
			
			dispose();
		}
		else if(event.getSource() == cancButton)
			dispose();
		else if(event.getSource() == changeFontBut)
		{
			if(_parent.getActiveView().getPlan() != null)
			{
				AbstractPlan plan = _parent.getActiveView().getPlan();
				plan.setDefaultFont(new FontSelectionDialog(MainFrm.getInstance(), plan.getDefaultFont(), null).getSelectedFont());
			}
		}
	}

	public void updateSchemeInPlan()
	{
		//if there a plan open, update all the shapes in the plan
		if(_parent.getActiveView() != null)
		{
			JWBUID wbID = _parent.getActiveView().getPanel().getControllerUID();
			Guid planID = control.getPlanId(wbID);
			JWBController controller = control.getController(planID);
			AbstractPlan thePlan = _parent.getActiveView().getPlan();
			
			HashMap shapes = controller.getShapes();
			ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
			
			//cycle throught the shapes
			Iterator shapeIter = shapes.values().iterator();
			while(shapeIter.hasNext())
			{
				//get the shape and the colorAttrib associated with its Event
				JWBShape thisShape = (JWBShape)shapeIter.next();
				PlanItem item = thePlan.getItem((Guid)thisShape.getAttachment());
				if(item instanceof Event)
				{
					String colorAttrib = ((Event)item).getSchemeAttrib();
					if(!colorAttrib.equals(""))
					{
						thisShape.setColor(ColorScheme.getInstance().getColorFor(colorAttrib));
						updatedShapes.add(thisShape);
					}
				}
			}
			
			try{
				controller.putShapes(updatedShapes);
			}catch(RemoteException exc){
				logger.warn("updateSchemeInPlan - RemExc updating shape with scheme:  "+exc.getMessage());
			}
		}
	}
	
	protected AbstractPlan getActivePlan()
	{
		if(_parent.getActiveView() != null)
			return _parent.getActiveView().getPlan();
		else
			return null;
	}
	
	protected void addOption(JComponent option, JPanel pane)
	{
		layout.setConstraints(option, constraints);
		pane.add(option);
	}

	public void setTab(int ind)
	{
		tabPane.setSelectedIndex(ind);
	}
}
