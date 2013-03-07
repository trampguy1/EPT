package mil.af.rl.jcat.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.util.*;

import mil.af.rl.jcat.bayesnet.FirstComeFirstServe;
import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.Policy;
import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.LibProcessArg;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.gui.dialogs.DocumentationDialog;
import mil.af.rl.jcat.gui.dialogs.EventDialog;
import mil.af.rl.jcat.gui.dialogs.FontSelectionDialog;
import mil.af.rl.jcat.gui.dialogs.JCatColorChooser;
import mil.af.rl.jcat.gui.dialogs.MechanismDialog;
import mil.af.rl.jcat.gui.dialogs.PlanTreeViewDialog;
import mil.af.rl.jcat.gui.dialogs.ResourcesDialog;
import mil.af.rl.jcat.gui.dialogs.ResourceSetSelectionDialog;
import mil.af.rl.jcat.gui.dialogs.SchedulerDialog;
import mil.af.rl.jcat.gui.dialogs.EvidenceDialog;
import mil.af.rl.jcat.gui.dialogs.ThreatResourceScheduler;
import mil.af.rl.jcat.gui.dialogs.TimingDialog;
import mil.af.rl.jcat.gui.dialogs.XYConstraints;
import mil.af.rl.jcat.gui.dialogs.XYLayout;
import mil.af.rl.jcat.gui.dialogs.MiscDialogs;
import mil.af.rl.jcat.integration.ConnectionManager;
import mil.af.rl.jcat.integration.gui.AgentMonitor;
import mil.af.rl.jcat.integration.gui.SourceDialog;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.ColorSchemeAttrib;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.LayoutTools;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.util.*;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.c3i.jwb.*;
import com.c3i.jwb.shapes.JWBRoundedRectangle;


import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;

/**
 * The CatPopupManager can be used by developers to configure the popup menus
 * and actions associated with them for shapes on the canvasboard. <br>
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
public final class CatPopupManager implements JWBPopupManager, ActionListener, ItemListener
{
	private JWBPanel jwbPanel = null;
	private JFileChooser fileChooser = new JFileChooser(".");
	private JPopupMenu planPopup = null;
	private JRadioButton avgOpt;
	private JRadioButton specOpt;
	private JTextField xBox;
	private JTextField yBox;
	private JDialog optionBox;
	private boolean multiMechMode = false;
	private Vector multiMechEvents = new Vector();
	private JDialog mmDialog;
	private ShapeHighlighter shapeHighlighter;
	private JCheckBoxMenuItem showSigNamesItem;
	private ArrayList selectedShapes;
	private JRadioButtonMenuItem andResOpt;
	private JRadioButtonMenuItem orResOpt;
	private JRadioButtonMenuItem userResOpt;
	private AbstractPlan plan;
	public static boolean collabEnabled = true;
	private static Logger logger = Logger.getLogger(CatPopupManager.class);
	
	
	/**
	 * Constructs a new CatPopupManager.
	 *
	 * @param jwbPanel
	 *            The owner of this class.
	 */
	public CatPopupManager(JWBPanel jwbPanel)
	{
		this.jwbPanel = jwbPanel;
		Guid planid = Control.getInstance().getPlanId(jwbPanel.getControllerUID());
		plan = Control.getInstance().getPlan(planid);
		
		initPlanPopup();
		
	}
	
	/**
	 * Returns the appropriate popup menu based on the given mode.
	 *
	 * @param mode
	 *            An editing mode describing the type of object requiring a
	 *            popup menu.
	 * @return A predefined popup menu.
	 */
	public JPopupMenu getPopupMenu(String shapeType)
	{
		if (shapeType.equals("com.c3i.jwb.JWBLine"))
		{
			if( jwbPanel.getSelectedShapes( ).size( ) > 1 )
				return popupMenuShapes( );
			else
				return popupMenuLine();
		}
		else if (shapeType.equals("com.c3i.jwb.shapes.JWBRoundedRectangle"))
		{
			if( jwbPanel.getSelectedShapes( ).size( ) > 1 )
				return popupMenuShapes( );
			else
				return popupMenuShape();
		}
		else
		{
			return popupMenuCanvas( );
		}
	}
	
	/**
	 * Method returns the tooltip text
	 *
	 * @return String[]
	 */
	public String[] getToolTipText()
	{
		JWBShape jwbShape = null;
		String name = null;
		String label = null;
		try
		{
			jwbShape = (JWBShape) jwbPanel.getFocusedShape().deepCopy();
			
			PlanItem item = plan.getItem((Guid)jwbShape.getAttachment());
			name = item.getName();
			label = item.getLabel();
			if (jwbShape.getType().equals("com.c3i.jwb.JWBLine"))
			{
				return new String[] { name + label };
			}
			
		} catch (NullPointerException e)
		{
			return new String[] { "empty logic" };
		}
		
		if (jwbShape != null
				&& jwbShape.getType().equals(
				"com.c3i.jwb.shapes.JWBRoundedRectangle"))
		{
			return new String[] { name, label };
		} else
		{
			return new String[] { "empty", "item" };
		}
	}
	
	/**
	 * Invoked when an action occurs.
	 *
	 * @param event
	 *            A semantic event which indicates that a component-defined
	 *            action occured.
	 */
	public void actionPerformed(ActionEvent event)
	{
		//ArrayList selectedShapes = jwbPanel.getSelectedShapes();
		JWBShape jwbShape = null;
		if(selectedShapes.size() > 0)
			jwbShape = (JWBShape)selectedShapes.get(0);
		String action = event.getActionCommand();
		final JWBController pcontroller = JWBControllerManager.getInstance().getController(jwbPanel.getControllerUID());
		//Guid planid = Control.getInstance().getPlanId(pcontroller.getUID());
		//AbstractPlan plan = Control.getInstance().getPlan(planid);
		
		if (action.equals("saveplan"))
		{
			MainFrm.getInstance().getActiveView().save();
		}
		else if (action.equals("startserver"))
		{
			try
			{
				Control.getInstance().startPlanServer(plan.getId());
			}catch(Exception ex)
			{
				logger.error("actionPerformed(startserv) - failed to start plan server:  "+ex.getMessage());
			}
		}
		else if (action.equals("stopserver"))
		{
			try
			{
				Control.getInstance().stopPlanServer(plan.getId());
			} catch (Exception ex)
			{
				logger.error("actionPerformed(stopserv) - failed to stop plan server:  "+ex.getMessage());
			}
		}
		else if (action.equals("joinsession"))
		{
			String host = JOptionPane.showInputDialog(MainFrm.getInstance(), "Host Address");
			if (host == null || host.length() < 7)
			{
				return;
			}
			try
			{
				Control.getInstance().startClient(plan.getId(), host);
			} catch (RemoteException ex)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "Please exit your current collaboration session first.");
			} catch (Exception ex)
			{
				logger.error("actionPerformed(join) - error joining remote session:  "+ex.getMessage());
			}
		} 
		else if (action.equals("exitsession"))
		{
			try
			{
				Control.getInstance().stopClient(plan.getId());
			}catch(Exception e)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), e.getMessage());
				logger.error("actionPerformed(exitsess) - failed to exit session:  "+e.getMessage());
			}
		}
		
		else if (action.equals("Delete"))
		{
			deleteShapes(selectedShapes,pcontroller);
			//Moved this stuff to its own method so CatFileMenu can call it - Josh
/*			try{
				if (selectedShapes.size() > 0)
				{
					for (java.util.Iterator iterator = selectedShapes.iterator(); iterator.hasNext();)
						pcontroller.removeShape(((JWBShape)iterator.next()).getUID());
				}
			}catch (Exception e){
				showNoShapeWarning();
			/* lost focus, probably due to remote update */
//			}
			
		}
		else if (action.equals("Edit Event"))
		{
			// check if the shape is locked by any other user
			//  if(jwbShape.isLock())
			// {
			//  JOptionPane.showMessageDialog(null,"Node being edited by another
			// user.");
			// return;
			//}
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new EventDialog(MainFrm.getInstance(), "Event Editor", jwbShape, pcontroller).show();
				System.gc();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Model Tree"))
		{
			final Guid guid = plan.getId();
			Runnable r = new Runnable()
			{
				public void run()
				{
					Document doc = Control.getInstance().getXmlPlanView(guid);
					PlanTreeViewDialog dlg = new PlanTreeViewDialog(doc);
					dlg.setVisible(true);
				}
			};
			new Thread(r, "ModelTree-Builder").start();
			
		}
		else if (action.equals("Timing"))
		{
			//if(jwbShape == null)
			//return;
			//if(jwbShape.isLock())
			//{
			//JOptionPane.showMessageDialog(null,"Timing for this node is being
			// edited by another user.");
			//return;
			//}
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new TimingDialog(MainFrm.getInstance(), jwbShape, plan);
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Scheduler"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				SchedulerDialog scheduler = new SchedulerDialog(MainFrm.getInstance(), jwbShape, plan);
				scheduler.setVisible(true);
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if (action.equals("Documentation"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				
				try{
					new DocumentationDialog(MainFrm.getInstance(), (Guid)jwbShape.getAttachment(), plan);
					pcontroller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, plan.getItem((Guid)jwbShape.getAttachment()), false));
				}catch(Exception e){
					logger.error("actionPerformed(documentation) - error putting update:  "+e.getMessage());
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if (action.equals("Select Color"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				JCatColorChooser chooser = new JCatColorChooser(MainFrm.getInstance(), jwbShape.getColor());
				Color newColor = chooser.getNewColor();
				
				ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
				
				if(selectedShapes.size() > 0 && newColor != null)
				{
					java.util.Iterator shapes = selectedShapes.iterator();
					while(shapes.hasNext())
					{
						
						JWBShape thisShape = (JWBShape)shapes.next();
						//TODO: LOW-PRIORITY   this doesn't always work, sometimes lines change color...wtf
						//plan.getItem((Guid)thisShape.getAttachment()) instanceof Event
						if(thisShape instanceof JWBRoundedRectangle)
						{
							thisShape.setColor(newColor);
							updatedShapes.add(thisShape);
							
							// clear any scheme attribute
							((Event)plan.getItem((Guid)thisShape.getAttachment())).setSchemeAttrib(null);
						}
					}
					try{
						pcontroller.putShapes(updatedShapes);
					}catch(Exception exc){
						logger.error("actionPerformed(setcolor) - error putting shape update:  "+exc.getMessage());
					}
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Set Font"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				FontSelectionDialog fontSelect = new FontSelectionDialog(MainFrm.getInstance(), jwbShape.getFont(), jwbShape.getTextColor());
				if(fontSelect.wasApproved())
				{
					ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
					Font theFont = fontSelect.getSelectedFont();
					Color theColor = fontSelect.getSelectedColor();
					if(selectedShapes.size() > 0)
					{
						java.util.Iterator shapes = selectedShapes.iterator();
						while(shapes.hasNext())
						{
							JWBShape thisShape = (JWBShape)shapes.next();
							if((thisShape instanceof JWBRoundedRectangle && fontSelect.isDoShapes()) || 
									(thisShape instanceof JWBLine && fontSelect.isDoLines()))
							{
								testShapeStillExists(pcontroller, thisShape);
								thisShape.setFont(theFont);
								thisShape.setTextColor(theColor);
								updatedShapes.add(thisShape);
							}
						}
						
						try{
							pcontroller.putShapes(updatedShapes);
						}catch(RemoteException exc){
							logger.error("actionPerformed(setFont) - RemoteExc putting new shape: "+exc.getMessage());
						}
					}
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Evidence"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new EvidenceDialog(MainFrm.getInstance(), jwbShape,  MainFrm.getInstance().getActiveView().getPlan());
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		//else if(action.equals("Explain")){
		//WhyDiegoWhy explanation = new WhyDiegoWhy(plan, ((PlanItem)jwbShape.getAttachment()).getGuid());
		//if(!explanation.isVisible()){
		// display alert that says "No explanations currently available
		//System.out.println("No explanations currently available");
		//}
		//}
		else if(action.equals("Arched Line"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				((JWBLine)jwbShape).setLineType(JWBLine.ARCED);
				try{
					pcontroller.putShape(jwbShape);
				}catch(java.rmi.RemoteException re){
					logger.error("actionPerformed(archedline) - failed to put shape update:  "+re.getMessage());
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Straight Line"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				((JWBLine)jwbShape).setLineType(JWBLine.STRAIGHT);
				try{
					pcontroller.putShape(jwbShape);
				}catch(java.rmi.RemoteException re){
					logger.error("actionPerformed(straightline) - failed to put shape update:  "+re.getMessage());
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Causes"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new NodeHelper(jwbShape, plan, pcontroller, SignalType.CAUSAL).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Effects"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new NodeHelper(jwbShape, plan, pcontroller, SignalType.EFFECT).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Inhibits"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new NodeHelper(jwbShape, plan, pcontroller, SignalType.INHIBITING).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Reset View"))
		{
			Runnable reset = new Runnable()
			{
				public void run()
				{
					ArrayList unhidden = new ArrayList();
					java.util.Iterator i = pcontroller.getShapes().values().iterator();
					JWBShape s;
					for(;i.hasNext();)
					{
						s = (JWBShape)i.next();
						if(s.isHidden())
						{
							s.setHidden(false);
							unhidden.add(s);
						}
					}
					try{
						pcontroller.putShapes(unhidden);
					}catch(java.rmi.RemoteException rex)
					{
						logger.error("actionPerformed(resetview) - RemoteExc reseting view:  "+rex.getMessage());
					}
				}
			};
			new Thread(reset, "Reset-View").start();
		}
		else if(action.equals("Add Cause"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new ChainHelper(jwbShape, plan, pcontroller,SignalType.CAUSAL).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Add Effect"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new ChainHelper(jwbShape, plan, pcontroller,SignalType.EFFECT).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Add Inhibitor"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				new ChainHelper(jwbShape, plan, pcontroller,SignalType.INHIBITING).start();
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("SchemeAttribute"))
		{
			try{
				//set the color of the shape to the color of the attribute selected
				//store the name of the attribute in the event object so it can be scheme is changed
				Color newColor = ColorScheme.getInstance().getColorFor(((JMenuItem)event.getSource()).getText());
				if(selectedShapes.size() > 0)
				{
					ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
					ArrayList<PlanItem> updatedItems = new ArrayList<PlanItem>();
					java.util.Iterator shapes = selectedShapes.iterator();
					while(shapes.hasNext())
					{
						JWBShape thisShape = (JWBShape)shapes.next();
						PlanItem item = plan.getItem((Guid)thisShape.getAttachment());
						if(item instanceof Event)
						{
							testShapeStillExists(pcontroller, jwbShape);
							thisShape.setColor(newColor);
							((Event)item).setSchemeAttrib(((JMenuItem)event.getSource()).getText());
	
							updatedShapes.add(thisShape);
							updatedItems.add(item);
						}
					}
					
					try{
						pcontroller.putShapes(updatedShapes);
						pcontroller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, updatedItems, false));
					}catch(Exception exc){
						logger.error("actionPerformed(scheme-attrib) - failed to put shape update:  "+exc.getMessage());
					}
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Create Image"))
		{
			saveSubImageAs();
		}
		else if(action.equals("Save Model Image"))
		{
			savePlanImage();
		}
		else if(action.equals("publish"))
		{
			MainFrm.getInstance().ibc.publishPlan(plan.getId());
		}
		else if(action.equals("Copy"))
		{
			if(selectedShapes.size() > 0)
				copy(selectedShapes);
		}
		else if(action.equals("Paste"))
		{
			paste();
		}
		else if(action.equals("Equalize Spacing (Horizontal)"))
			showEqualizeBox("SpaceH");
		else if(action.equals("Equalize Spacing (Vertical)"))
			showEqualizeBox("SpaceV");
		else if(action.equals("Equalize Size") && pcontroller.getShapes().size() > 0)
			showEqualizeBox("Size");
		else if(action.startsWith("OK"))
		{
			optionBox.dispose();
			if(action.endsWith("Size"))
			{
				try{
					int xBoxInt = Integer.parseInt(xBox.getText());
					int yBoxInt = Integer.parseInt(yBox.getText());
					if(avgOpt.isSelected())
						new LayoutTools(pcontroller, plan).equalizeSize();
					else
						new LayoutTools(pcontroller, plan).equalizeSize(new java.awt.Dimension(xBoxInt, yBoxInt));
				}catch(NumberFormatException exc){
					JOptionPane.showMessageDialog(optionBox, "You must enter only numbers.");
				}
			}
			else if(action.endsWith("SpaceH"))
			{
				try{
					int yBoxInt = Integer.parseInt(yBox.getText());
					if(avgOpt.isSelected())
						new LayoutTools(pcontroller, plan).equalizeHorizSpace();
					else
						new LayoutTools(pcontroller, plan).equalizeHorizSpace(yBoxInt);
				}catch(NumberFormatException exc){
					JOptionPane.showMessageDialog(optionBox, "You must enter only numbers.");
				}
			}
			else if(action.endsWith("SpaceV"))
			{
				try{
					int yBoxInt = Integer.parseInt(yBox.getText());
					if(avgOpt.isSelected())
						new LayoutTools(pcontroller, plan).equalizeVertSpace();
					else
						new LayoutTools(pcontroller, plan).equalizeVertSpace(yBoxInt);
				}catch(NumberFormatException exc){
					JOptionPane.showMessageDialog(optionBox, "You must enter only numbers.");
				}
			}
		}
		else if(action.equals("Cancel"))
			optionBox.dispose();
		
		else if(action.equals("Rename") && jwbShape.getType().equals("com.c3i.jwb.JWBLine"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				
				Mechanism theMech = (Mechanism)plan.getItem((Guid)jwbShape.getAttachment());
				//warn user about any other mechanisms that contain the same signal
				String message = "This signal is also used in the mechanisms listed below. \nAre you sure you want to rename it? \n";
				StringBuffer msgBuff = new StringBuffer();
				Iterator allMechs = plan.getAllMechanisms().iterator();
				while(allMechs.hasNext())
				{
					Mechanism thisMech = (Mechanism)allMechs.next();
					if(thisMech.getSignalGuid().equals(theMech.getSignalGuid()) && !thisMech.getGuid().equals(theMech.getGuid()))
						msgBuff.append("["+plan.getPlanItemName(thisMech.getFromEvent())+"]  -->  ["+plan.getPlanItemName(thisMech.getToEvent(0))+"]\n");
				}
				int opt = JOptionPane.YES_OPTION;
				if(msgBuff.length() > 0)
					opt = JOptionPane.showConfirmDialog(MainFrm.getInstance(), message+"\n"+msgBuff.toString(), "WARNING", JOptionPane.YES_NO_OPTION);
				
				if(opt == JOptionPane.YES_OPTION)
				{
					//ask for a new name
					Signal theSig = plan.getLibrary().getSignal(theMech.getSignalGuid());
					String newName = JOptionPane.showInputDialog(MainFrm.getInstance(), "Enter new signal name:", theSig);
					
					//rename the mechanisms and lines
					if(newName != null)
					{
						newName = newName.trim();
						if(!newName.equals(""))
						{
							Control.getInstance().renameMechanism(theSig.getSignalID(), newName, plan);
	
							//tell control to rename the signal
							try{
								//theSig.setSignalName(newName);
								Control.getInstance().getController(plan.getId()).foreignUpdate(new RemSignalArg(RemSignalArg.RENAME, theSig.getSignalID(), newName));
							}catch(RemoteException exc){
								logger.error("actionPerformed(rename) - RemExc renaming signal:  "+exc.getMessage());
							}
						}
						else
							JOptionPane.showMessageDialog(MainFrm.getInstance(), "The name chosen was invalid.  A signal name cannot be blank.", "Rename", JOptionPane.ERROR_MESSAGE);
					}
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Create Agents"))
		{
            ConnectionManager.INSTANCE();
			SourceDialog sd = new SourceDialog(new ArrayList<JWBShape>(selectedShapes),pcontroller);
			
			sd.setVisible(true);
		}else if(action.equals("Create Policy Group")){
			ArrayList pGroup = selectedShapes;
			Policy policy = new FirstComeFirstServe();
			plan.addPolicy(policy);
			for(Object shape: pGroup){
				PlanItem item = plan.getItem((Guid)((JWBShape)shape).getAttachment());
				policy.addMember(item.getGuid());
				item.setGroup(policy);
	        	if(item instanceof Event)
	        		((Event) item).setResourceUseType(NetNode.POLICY_TABLE);
			}
			pGroup = null;
		}
		else if(action.equals("Multi-Mech-Mode"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				multiMechMode = true;
				showMultiMechDialog();
				shapeHighlighter = new ShapeHighlighter(multiMechEvents, ShapeHighlighter.ALPHA);
				multiMechEvents.add(0, jwbShape);
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Add"))
		{
			try{
				if(selectedShapes.size() > 0)
				{
					java.util.Iterator shapes = selectedShapes.iterator();
					while(shapes.hasNext())
					{
						Object shape = shapes.next();
						testShapeStillExists(pcontroller, (JWBShape)shape);
						PlanItem item = plan.getItem((Guid)((JWBShape)shape).getAttachment());
						if(item instanceof Event && !multiMechEvents.contains(shape))
						{
							multiMechEvents.add(shape);
							shapeHighlighter.add(shape);
						}
					}
				}
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
		else if(action.equals("Done"))
		{
			multiMechMode = false;
			if(multiMechEvents.size() > 1)
				createMultipleMechs();
			shapeHighlighter.stop();
			multiMechEvents.clear();
			mmDialog.dispose();
		}
		else if(action.equals("cancel multi-mech"))
		{
			multiMechMode = false;
			shapeHighlighter.stop();
			multiMechEvents.clear();
			mmDialog.dispose();
		}
		else if(action.equals("Plot"))
		{
			try{
				testShapeStillExists(pcontroller, jwbShape);
				MainFrm.getInstance().getActiveView().plot(plan.getItem((Guid)jwbShape.getAttachment()));
			}catch(ShapeNotFoundException exc){
				showNoShapeWarning();
			}
		}
        else if(action.equals("Agent List"))
        {
            new AgentMonitor().setVisible(true);
        }
        else if(action.equals("Simple Resources"))
        {
        	//new mil.af.rl.jcat.gui.dialogs.ResourcesDialog1(MainFrm.getInstance()).setVisible(true);
        	new ResourcesDialog(MainFrm.getInstance(), jwbShape, pcontroller, plan);
        	
        }
        else if (action.equals("Threat Resources"))
		{
			// sorry craig you need to 'put' the shape after changing stuff so that this JWB and anyone elses
			// JWB get updated shapes and planitem... etc, someday we can change all that
			new ThreatResourceScheduler(MainFrm.getInstance(), jwbShape, pcontroller, plan);
		}
        else if (action.equals("Resource Pairing"))
        {
            new ResourceSetSelectionDialog(MainFrm.getInstance(), plan.getItem((Guid)jwbShape.getAttachment()), plan.getResourceManager());
        }
        else if (action.equals("AND Expenditure"))
        {
        	PlanItem pi = plan.getItem((Guid)jwbShape.getAttachment());
        	if(pi instanceof Event)
        		((Event)pi).setResourceUseType(NetNode.RESOURCE_USE_AND);
        }
        else if (action.equals("OR Expenditure"))
        {
        	PlanItem pi = plan.getItem((Guid)jwbShape.getAttachment());
        	if(pi instanceof Event)
        	{
        		((Event)pi).setResourceUseType(NetNode.RESOURCE_USE_OR);
			Policy policy = new mil.af.rl.jcat.bayesnet.MostEffectiveSingleResource();
			plan.addPolicy(policy);
			policy.addMember(pi.getGuid());
			pi.setGroup(policy);
		}
        }
        else if(action.equals("User Priority"))
        {
        	PlanItem pi = plan.getItem((Guid)jwbShape.getAttachment());
        	if(pi instanceof Event)
        		((Event) pi).setResourceUseType(NetNode.USER_PRIORITY_USE);
        }
        else if (action.equals("Create Course of Action"))
        {
        	//snap shot certain info for each node by creating a COA
        	JTextField nameField = new JTextField();
        	JPanel trackChoices = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        	JCheckBox trackSched = new JCheckBox("Schedule", true);
        	JCheckBox trackRes = new JCheckBox("Resources", true);
        	JCheckBox trackElic = new JCheckBox("Elicited Probabilities", true);  
        	trackChoices.add(trackSched);
        	trackChoices.add(trackRes);
        	trackChoices.add(trackElic);
        	JComboBox untrackBox = new JComboBox(new String[]{"Clear above parameters", "Leave above parameters unchanged"});
        	
        	boolean accepted = MiscDialogs.showCOAOptionsBox(MainFrm.getInstance(), plan, nameField, trackChoices, untrackBox);
        	
        	if(accepted)
        	{
	        	COA newCOA = plan.createCOA(nameField.getText(), trackSched.isSelected(), trackRes.isSelected(), trackElic.isSelected(), untrackBox.getSelectedIndex() == 0, true);
	        	
	        	Control.getInstance().applyCOA(newCOA, plan, true);
	        	
	        	PlanArgument arg = new PlanArgument(PlanArgument.PLAN_COAS);
			arg.getParameters().coaList = plan.getCOAList();
			try{
				Control.getInstance().getController(plan.getId()).foreignUpdate(arg);
			}catch(RemoteException exc){
				logger.error("actionPerformed(delete) - RemoteExc sending plan_coas update:  "+exc.getMessage());
			}
        	}
        }
	}
	

	/**
	 * Ensures shape still exists in the specified controller (another user collaborating may have deleted it)
	 */
	private void testShapeStillExists(JWBController control, JWBShape jwbShape) throws ShapeNotFoundException
	{
        try
        {
            control.getShape(jwbShape.getUID()).getUID();
        }catch(NullPointerException ex)
        {
        	logger.info("testShapeStillExists - user attempted to edit an item removed by another user");
            throw new ShapeNotFoundException();
        }
	}

	private void showNoShapeWarning()
	{
		JOptionPane.showMessageDialog(MainFrm.getInstance(), "There is no item selected to modify. \n" +
				"The item you selected may have been moved or removed by another user.");
		
	}

	// displays a dialog with instructions and done/cancel buttons for multi-mech-mode
	private void showMultiMechDialog()
	{
		mmDialog = new JDialog(MainFrm.getInstance(), "Multi Mechanism Mode");
		Container mainPane = mmDialog.getContentPane();
		mainPane.setLayout(null);
		mmDialog.setSize(380,130);
		mmDialog.setLocation(jwbPanel.getLocationOnScreen());
		mmDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		JTextArea txt = new JTextArea("Select one or more Events, then right click on one of the selected Events " +
				"and press 'Add'.  When you have finished adding, press 'Done'.");
		txt.setEditable(false);
		txt.setOpaque(false);
		txt.setBounds(5,5,360,55);
		txt.setFont(new Font("Arial", 0, 12));
		txt.setLineWrap(true);   txt.setWrapStyleWord(true);
		JButton doneBut = new JButton("Done");
		doneBut.setBounds(45,60,160,25);
		doneBut.addActionListener(this);
		JButton cancBut = new JButton("Cancel");
		cancBut.setActionCommand("cancel multi-mech");
		cancBut.setBounds(mmDialog.getSize().width-80-50,60,80,25);
		cancBut.addActionListener(this);
		
		mainPane.add(txt);
		mainPane.add(doneBut);
		mainPane.add(cancBut);
		mmDialog.setVisible(true);
	}

	// deleteShapes is called by CatFileMenu - Josh
	public void deleteShapes(){
		deleteShapes(jwbPanel.getSelectedShapes(), JWBControllerManager.getInstance().getController(jwbPanel.getControllerUID()));
	}
	
	public void deleteShapes(ArrayList selectedShapes, JWBController pcontroller){
		if(selectedShapes.size() > 0 && JOptionPane.showConfirmDialog(MainFrm.getInstance(), "Are you sure you would like to delete these items?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			try{
				if (selectedShapes.size() > 0)
				{
					for (java.util.Iterator iterator = selectedShapes.iterator(); iterator.hasNext();)
						pcontroller.removeShape(((JWBShape)iterator.next()).getUID());
				}
			}catch(RemoteException exc){
				logger.error("deleteShapes - RemoteExc deleting shapes: "+exc.getMessage());
			}
//			}catch (Exception e){
//				showNoShapeWarning();
//		/* lost focus, probably due to remote update */
//			}
		}
	}
	
	public void copy()
	{
		copy(jwbPanel.getSelectedShapes());
	}
	
	public void copy(ArrayList shapes)
	{
		//AbstractPlan plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(jwbPanel.getControllerUID()));
		if(plan != null)
		{
			ArrayList copiedShapes = new ArrayList();
			ArrayList<Serializable> serializedCopied = new ArrayList<Serializable>();
			//JWBShape jwbShape;
			try
			{
				Hashtable mapping = new Hashtable( );
				ArrayList selectedShapes = shapes;
				//JWBShape copy = null;
				
				// determine which shapes should actually be copied (not all selected or linked lines necessarily need to be)
				if( selectedShapes.size( ) > 1 )
				{
					for( int i = 0; i < selectedShapes.size( ); i++ )
					{
						JWBShape copy = JWBController.convertShape( ( (JWBShape)selectedShapes.get( i ) ).getSerializableShape( ).copy( ) );
						if( copy.getAttachment( ) != null )
							copy.setAttachment( copy.getAttachment( ).deepCopy( ) );
						
						copiedShapes.add( copy );
						
						if( !copy.getType( ).equals( JWBPanel.LINE ) )
							mapping.put( ( (JWBShape)selectedShapes.get( i ) ).getUID( ), copy.getUID( ) );
					}
					
					//remove the lines that wont have both end points copied, dont need those
					ArrayList toRemove = new ArrayList( );
					for( int i = 0; i < copiedShapes.size( ); i++ )
					{
						JWBShape copy = (JWBShape)copiedShapes.get( i );
						if( copy.getType( ).equals( JWBPanel.LINE ) )
						{
							JWBUID[] linkedShapes = ( (JWBLine)copy ).getLinkedShapes( );
							linkedShapes[0] = (JWBUID)mapping.get( linkedShapes[0] );
							linkedShapes[1] = (JWBUID)mapping.get( linkedShapes[1] );
							if( linkedShapes[0] == null || linkedShapes[1] == null )
								toRemove.add( copy );
							else
							{
								JWBSerializableShape serializableShape = copy.getSerializableShape( );
								((Object[])serializableShape.getExtendedAttributes( ))[1] = linkedShapes;
								copy = JWBController.convertShape( serializableShape );
							}
						}
					}
					copiedShapes.removeAll( toRemove );
				}
				//if its just a single shape that was copied, thats easy, no lines to consider
				else
				{
					JWBShape jwbShape = (JWBShape)jwbPanel.getFocusedShape();
					if(jwbShape != null)
					{
						JWBShape copy = JWBController.convertShape( jwbShape.getSerializableShape( ).copy( ) );
						if( copy.getAttachment( ) != null )
							copy.setAttachment( copy.getAttachment( ).deepCopy( ) );
						if( !copy.getType( ).equals( JWBPanel.LINE ) )
							copiedShapes.add( copy );
					}
				}
			}catch( NullPointerException e ) {
				logger.error("copy - NullPointerExc copying shapes:  "+e.getMessage());
			}
			
//			TODO: ????  wana do this differently, as commented it just sorts the shapes so lines paste last
			ArrayList<JWBSerializableShape> boxes = new ArrayList<JWBSerializableShape>();
			ArrayList<JWBSerializableShape> lines = new ArrayList<JWBSerializableShape>();
			ArrayList<PlanItem> planItems = new ArrayList<PlanItem>(); //for creating a PlanArgument
			MultiMap<Guid, JWBUID> newItemMap = new MultiMap<Guid, JWBUID>(); //for creating a PlanArgument
			
			logger.debug("copiedShapes:  "+copiedShapes.size());
			Iterator it = copiedShapes.iterator();
			for(;it.hasNext();)
			{
				JWBShape s = (JWBShape)it.next();
				//((PlanItem)s.getAttachment()).copied = true;
				if(s.getType().equals("com.c3i.jwb.JWBLine"))
				{
					lines.add(s.getSerializableShape());
					
					PlanItem thisItem = plan.getItem((Guid)s.getAttachment());
					if(!planItems.contains(thisItem))
						planItems.add(thisItem);
					newItemMap.put((Guid)s.getAttachment(), s.getSerializableShape().getUID());
				}
				else
				{
					Event e = (Event)plan.getItem((Guid)s.getAttachment());
					Process proc = plan.getLibrary().getProcess(e.getProcessGuid());
					e.setProcessCArg(new LibProcessArg(LibProcessArg.COPY_SIGNAL_DATA, e.getName(), e.getProcessGuid(), plan, proc.getDefaultsSubType()));
					e.initCopiedSignals(proc, plan.getLibrary());
					
					boxes.add(s.getSerializableShape());
					
					PlanItem thisItem = plan.getItem((Guid)s.getAttachment());
					if(!planItems.contains(thisItem))
						planItems.add(0, plan.getItem((Guid)s.getAttachment()));
					newItemMap.put((Guid)s.getAttachment(), s.getSerializableShape().getUID());
				}
				
			}
			
			serializedCopied.addAll(boxes);
			serializedCopied.addAll(lines);
			serializedCopied.add(new PlanArgument(PlanArgument.ITEM_PASTE, planItems, newItemMap));
			
			// now set the clip board
			setClipboard(serializedCopied);
			serializedCopied = null;
		}
	}
	
	public void paste()
	{
		Point placement = jwbPanel.getMousePosition();
		double zf = jwbPanel.getZoom()/10.0;
		
		ArrayList serialized = getClipboard();
		if(serialized == null)
			return;
		
		ArrayList<JWBShape> copiedShapes = new ArrayList<JWBShape>();
		PlanArgument planArg = null;
		Iterator it = serialized.iterator();
		for(;it.hasNext();)
		{
			Serializable obj = (Serializable)it.next();
			if(obj instanceof JWBSerializableShape)
			{
				JWBShape shape = JWBController.convertShape((JWBSerializableShape)obj);
				shape.removeMarkup('T');
				shape.removeMarkup('S');
				shape.removeMarkup('E');
				shape.removeMarkup('R');
				
				// sort shapes based on type so that events get pasted first, THIS IS DONE DURING 'COPY' ALREADY
				if(shape.getType().equals("com.c3i.jwb.shapes.JWBRoundedRectangle"))
					copiedShapes.add(0, shape);
				else
					copiedShapes.add(shape);
			}
			else if(obj instanceof PlanArgument)
				planArg = (PlanArgument)obj;
		}
		
		if( copiedShapes != null )  //WHY WOULD IT BE NULL EVER??
		{
			if(placement != null)
			{
				placement = jwbPanel.snapPointToGrid(placement);
				Point delta = null;
				Point fromPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
				//find the upper-leftmost point amoungst the copied shapes
				for( int i = 0; i < copiedShapes.size(); i++ )
				{
					JWBShape thisShape = (JWBShape)copiedShapes.get(i);
					if(thisShape.getLocation().x < fromPoint.x)
						fromPoint.x = thisShape.getLocation().x;
					if(thisShape.getLocation().y < fromPoint.y)
						fromPoint.y = thisShape.getLocation().y;
					
				}
				
				//figure out delta to move the shapes (take zoom into account)
				delta = new Point( placement.x - (int)(fromPoint.x * zf) , placement.y - (int)(fromPoint.y * zf) );
				delta = new Point((int)(delta.x / zf), (int)(delta.y / zf));
				
				for( int i = 0; i < copiedShapes.size( ); i++ )
				{
					JWBShape thisShape = (JWBShape)copiedShapes.get( i );
					thisShape.translate( delta );
				}
				
			}
			else
			{
				Point delta = null;
				JWBShape copy = null;
				for( int i = 0; i < copiedShapes.size( ); i++ )
				{
					copy = (JWBShape)copiedShapes.get(i);
					copy.translate(new Point(100, 100));
				}
			}
			
			//send an update for the copied shapes
			try{
				JWBController control = JWBControllerManager.getInstance().getController(jwbPanel.getControllerUID());
				
				//put the shapes
				control.putShapes(copiedShapes);
				
				//put the plan item pasted args
				control.foreignUpdate(planArg);
			}catch(RemoteException e)
			{
				logger.error("paste - RemoteExc putting copied shapes:  "+e.getMessage());
			}
			copiedShapes = null;
			planArg = null;
		}

		setClipboard(null);
	}
	
	private static ArrayList getClipboard()
	{
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try{
			DataFlavor df = new DataFlavor(ArrayList.class, "ArrayList.class");
			if(t != null && t.isDataFlavorSupported(df))
			{
				ArrayList copiedShapes = (ArrayList)t.getTransferData(df);
				return copiedShapes;
				
			}
		}catch(UnsupportedFlavorException e){
			logger.error("getClipboard - UnsupporedFlavorExc getting copied clipboard data:  "+e.getMessage());
		}
		catch(IOException ie){
			logger.error("getClipboard - IOExc reading copied clipboard data:  "+ie.getMessage());
		}
		return null;
	}
	
	private static void setClipboard(ArrayList copiedShapes)
	{
		
		PlanItemSelection cshapes = new PlanItemSelection(copiedShapes);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(cshapes,null);
		
	}
	
	public void showEqualizeBox(String mode)
	{
		optionBox = new JDialog(MainFrm.getInstance(), "Equalize "+mode);
		optionBox.setSize(200, 115);
		optionBox.setLocationRelativeTo(MainFrm.getInstance());
		Container pane = optionBox.getContentPane();
		pane.setLayout(new XYLayout());
		avgOpt = new JRadioButton("Average", true);
		avgOpt.addItemListener(this);
		specOpt = new JRadioButton("Specified");
		specOpt.addItemListener(this);
		ButtonGroup options = new ButtonGroup();
		options.add(avgOpt);   options.add(specOpt);
		xBox = new JTextField("80");
		xBox.setEnabled(false);
		yBox = new JTextField("80");
		yBox.setEnabled(false);
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK-"+mode);
		okButton.addActionListener(this);
		JButton cancButton = new JButton("Cancel");
		cancButton.addActionListener(this);
		
		pane.add(avgOpt, new XYConstraints(10,0,0,0));
		pane.add(specOpt, new XYConstraints(90,0,0,0));
		if(mode.equals("Size"))
			pane.add(xBox, new XYConstraints(50,25,30,0));
		pane.add(yBox, new XYConstraints(90,25,30,0));
		pane.add(okButton, new XYConstraints(40,55,0,0));
		pane.add(cancButton, new XYConstraints(90,55,0,0));
		
		optionBox.setVisible(true);
	}
	
	public void enableCollabItem(boolean enab)
	{
		collabEnabled = enab;
	}
	
	
	/////////////////////// helper methods used to display a popup menu at various times
	private JPopupMenu popupMenuShape()
	{
		// get selected shapes now as the selection could change by time user clicks a menu item
		// if they are collaborating 
		selectedShapes = jwbPanel.getSelectedShapes();
		Event event = null;
		if(selectedShapes.size() > 0)
			event = (Event)plan.getItem((Guid)((JWBShape)selectedShapes.get(0)).getAttachment());
		else
			event = new Event(null, "", "", null); //dumby event just to show the menu (needed for wizard)
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		if(multiMechMode)
		{
			JMenuItem addItem = new JMenuItem("Add");
			addItem.addActionListener(this);
			popupMenu.add(addItem);
		}
		else
		{
			JMenuItem plotItem = new JMenuItem("Plot", new ImageIcon(this.getClass().getClassLoader().getResource("plot.png")));
			JMenuItem menuPopupDrillDown = new JMenuItem("Edit Event", new ImageIcon(
					this.getClass().getClassLoader().getResource( "dd.png")));
			JMenuItem menuPopupTiming = new JMenuItem("Timing", new ImageIcon(
					this.getClass().getClassLoader().getResource( "timing.png")));
			JMenuItem menuPopupScheduler = new JMenuItem("Scheduler", new ImageIcon(
					this.getClass().getClassLoader().getResource( "timing.png")));
			JMenuItem menuPopupDelete = new JMenuItem("Delete", new ImageIcon(
					this.getClass().getClassLoader().getResource( "delete.gif")));
			JMenuItem menuPopupDocumentation = new JMenuItem("Documentation", new ImageIcon(
					this.getClass().getClassLoader().getResource( "doc.png")));
			JMenu colorSubMenu = createColorMenu();
			JMenuItem fontItem = new JMenuItem("Set Font", new ImageIcon(this.getClass().getClassLoader().getResource("font.png")));
			JMenuItem menuPopupEvidence = new JMenuItem("Evidence", new ImageIcon(
					this.getClass().getClassLoader().getResource( "evidence.png")));
			JMenu showSubMenu = new JMenu("Show Immediate");
			showSubMenu.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("eye.png")));
			JMenuItem causesOption = new JMenuItem("Causes");
			causesOption.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("show_causes.png")));
			JMenuItem effectsOption = new JMenuItem("Effects");
			effectsOption.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("show_effects.png")));
			JMenuItem inhibitsOption = new JMenuItem("Inhibits");
			inhibitsOption.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("show_causes.png")));
			JMenuItem resetOption = new JMenuItem("Reset View");
			resetOption.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_refresh.png")));
			
			JMenu chainSubMenu = new JMenu("Show Chain");
			chainSubMenu.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_link.png")));
			JMenuItem causeChain = new JMenuItem("Add Cause");
			causeChain.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("add_cause.png")));
			causeChain.addActionListener(this);
			JMenuItem effectChain = new JMenuItem("Add Effect");
			effectChain.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("add_effect.png")));
			effectChain.addActionListener(this);
			JMenuItem inhibitChain = new JMenuItem("Add Inhibitor");
			inhibitChain.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("add_cause.png")));
			inhibitChain.addActionListener(this);
			chainSubMenu.add(causeChain);
			chainSubMenu.add(effectChain);
			chainSubMenu.add(inhibitChain);
			
			JMenuItem copyMenu = new JMenuItem("Copy");
			copyMenu.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_copy.png")));
			copyMenu.addActionListener(this);
			
			JMenuItem multiMech = new JMenuItem("Multi-Mech-Mode", new ImageIcon(this.getClass().getClassLoader().getResource("multimech.png")));
			multiMech.addActionListener(this);
			
			JMenu resourcesMenu = new JMenu("Resources");
            JMenuItem resourcePair = new JMenuItem("Resource Pairing");
			JMenuItem simpResources = new JMenuItem("Simple Resources");
			JMenuItem threatResources = new JMenuItem("Threat Resources");
			andResOpt = new JRadioButtonMenuItem("AND Expenditure", (event.getResourceUseType() == NetNode.RESOURCE_USE_AND));
			orResOpt = new JRadioButtonMenuItem("OR Expenditure", (event.getResourceUseType() == NetNode.RESOURCE_USE_OR));
			userResOpt = new JRadioButtonMenuItem("User Priority", (event.getResourceUseType() == NetNode.USER_PRIORITY_USE));
			andResOpt.addActionListener(this);
			orResOpt.addActionListener(this);
			userResOpt.addActionListener(this);
			ButtonGroup resOpts = new ButtonGroup();
			resOpts.add(andResOpt);
			resOpts.add(orResOpt);
			resOpts.add(userResOpt);
			threatResources.addActionListener(this);
			simpResources.addActionListener(this);
            resourcePair.addActionListener(this);
			resourcesMenu.add(simpResources);
			resourcesMenu.add(threatResources);
            resourcesMenu.add(resourcePair);
			resourcesMenu.addSeparator();
			resourcesMenu.add(andResOpt);
			resourcesMenu.add(orResOpt);
			resourcesMenu.add(userResOpt);
			
			
			showSubMenu.add(causesOption);
			causesOption.addActionListener(this);
			showSubMenu.add(effectsOption);
			effectsOption.addActionListener(this);
			showSubMenu.add(inhibitsOption);
			inhibitsOption.addActionListener(this);
			resetOption.addActionListener(this);
			
			plotItem.addActionListener(this);
			menuPopupDrillDown.addActionListener(this);
			menuPopupTiming.addActionListener(this);
			menuPopupScheduler.addActionListener(this);
			menuPopupDelete.addActionListener(this);
			menuPopupDocumentation.addActionListener(this);
			fontItem.addActionListener(this);
			menuPopupEvidence.addActionListener(this);
            
			
			popupMenu.add(plotItem);
			popupMenu.add(menuPopupDrillDown);
			popupMenu.addSeparator();
			popupMenu.add(menuPopupTiming);
			popupMenu.add(menuPopupScheduler);
			popupMenu.add(menuPopupEvidence);
			popupMenu.add(menuPopupDocumentation);
			popupMenu.addSeparator();
			popupMenu.add(menuPopupDocumentation);
			popupMenu.add(colorSubMenu);
			popupMenu.add(fontItem);
			popupMenu.addSeparator();
			popupMenu.add(showSubMenu);
			popupMenu.add(chainSubMenu);        
			popupMenu.add(resetOption);
			popupMenu.addSeparator();
			popupMenu.add(menuPopupDelete);
			popupMenu.add(copyMenu);
			popupMenu.addSeparator();
			popupMenu.add(multiMech);
			popupMenu.add(resourcesMenu);
            //popupMenu.add(source); //removed for release
		}
		
		popupMenu.addPopupMenuListener(jwbPanel);
		return popupMenu;
	}
	
	private JPopupMenu popupMenuCanvas()
	{
		// get selected shapes now as the selection could change by time user clicks a menu item
		// if they are collaborating 
		selectedShapes = jwbPanel.getSelectedShapes();
		
		return planPopup;
	}
	
	private void initPlanPopup()
	{
		planPopup = new JPopupMenu();
						
		JMenu layoutMenu = new JMenu("Layout Tools (beta)");
		JMenuItem eqSize = new JMenuItem("Equalize Size");
		eqSize.addActionListener(this);
		layoutMenu.add(eqSize);
		JMenuItem eqSpaceH = new JMenuItem("Equalize Spacing (Horizontal)");
		eqSpaceH.addActionListener(this);
		//eqSpaceH.setEnabled(false); //temp
		layoutMenu.add(eqSpaceH);
		JMenuItem eqSpaceV = new JMenuItem("Equalize Spacing (Vertical)");
		eqSpaceV.addActionListener(this);
		//eqSpaceV.setEnabled(false); //temp
		layoutMenu.add(eqSpaceV);
		
		JMenuItem startServer = new JMenuItem("Start Model Server", new ImageIcon(
				this.getClass().getClassLoader().getResource( "starts.png")));
		startServer.addActionListener(this);
		startServer.setActionCommand("startserver");
		startServer.setEnabled(collabEnabled);
		JMenuItem stopServer = new JMenuItem("Stop Model Server", new ImageIcon(
				this.getClass().getClassLoader().getResource( "cancel.png")));
		stopServer.addActionListener(this);
		stopServer.setActionCommand("stopserver");
		stopServer.setEnabled(collabEnabled);
		
//		JMenuItem closePlan = new JMenuItem("Close Plan", new ImageIcon(
//				this.getClass().getClassLoader().getResource( "stock_stop.png")));
//		closePlan.addActionListener(this);
//		closePlan.setActionCommand("closeplan");
//		closePlan.addActionListener(this);
		//CAPES Shit
//		JMenuItem pubPlan = new JMenuItem("Publish to COWEB", new ImageIcon(
//				this.getClass().getClassLoader().getResource( "starts.png")));
//		pubPlan.addActionListener(this);
//		pubPlan.setActionCommand("publish");
		
		JMenuItem menuPaste = new JMenuItem("Paste");
		menuPaste.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_paste.png")));
		menuPaste.addActionListener(this);
		
		JMenuItem resetOption = new JMenuItem("Reset View");
		resetOption.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_refresh.png")));
		resetOption.addActionListener(this);
		
		JMenuItem planView = new JMenuItem("Model Tree");
		planView.addActionListener(this);
		
		JMenuItem savePlanImage = new JMenuItem("Save Model Image", new ImageIcon(this.getClass().getClassLoader().getResource("plan_img.png")));
		savePlanImage.addActionListener(this);
        
//		JMenuItem agentList = new JMenuItem("Agent List");
//		agentList.addActionListener(this);
        
		showSigNamesItem = new JCheckBoxMenuItem("Show Signal Names", new ImageIcon(this.getClass().getClassLoader().getResource("sig_names.png")));
		showSigNamesItem.addItemListener(this);
			
		JMenuItem coaItem = new JMenuItem("Create Course of Action", new ImageIcon(this.getClass().getClassLoader().getResource("coa.png")));
		coaItem.addActionListener(this);
        
		// add all the items the the menu    
		planPopup.add(resetOption);
		planPopup.add(menuPaste);
		planPopup.add(showSigNamesItem);  //removed for release
		planPopup.addSeparator();
		planPopup.add(startServer);
		planPopup.add(stopServer);
		//planPopup.add(joinSession);
		//planPopup.add(exitSession);
		planPopup.addSeparator();
		//planPopup.add(pubPlan);
		//planPopup.add(savePlan);
		planPopup.add(layoutMenu);
		planPopup.add(planView);
		planPopup.add(savePlanImage);
//		planPopup.add(agentList);
		planPopup.add(coaItem);
		//planPopup.add(new JSeparator());
		
	}
	
	public JPopupMenu popupMenuShapes()
	{
		// get selected shapes now as the selection could change by time user clicks a menu item
		// if they are collaborating 
		selectedShapes = jwbPanel.getSelectedShapes();
		
		JPopupMenu popupMenu = new JPopupMenu( );
		if(multiMechMode)
		{
			JMenuItem addItem = new JMenuItem("Add");
			addItem.addActionListener(this);
			popupMenu.add(addItem);
		}
		else
		{
			// copy/paste
			JMenuItem menuCopy = new JMenuItem("Copy");
			menuCopy.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_copy.png")));
			menuCopy.addActionListener(this);
			popupMenu.add(menuCopy);
			//JMenuItem menuPaste = new JMenuItem("Paste");
			//menuPaste.addActionListener(this);
			//popupMenu.add(menuPaste);
			
			popupMenu.add(new JSeparator());
			
			popupMenu.add(createColorMenu());
			
			JMenuItem fontItem = new JMenuItem("Set Font", new ImageIcon(this.getClass().getClassLoader().getResource("font.png")));
			fontItem.addActionListener(this);			
			popupMenu.add(fontItem);
			
			JMenuItem menuSubImage = new JMenuItem("Create Image", new ImageIcon(this.getClass().getClassLoader().getResource("stock_crop.png")));
			menuSubImage.addActionListener(this);
			popupMenu.add(menuSubImage);
			
			popupMenu.add(new JSeparator());
			
			JMenuItem menuPopupDelete = new JMenuItem("Delete", new ImageIcon(this.getClass().getClassLoader().getResource( "delete.gif")));
			menuPopupDelete.addActionListener( this );
			popupMenu.add( menuPopupDelete );
			
			JMenuItem menuSource = new JMenuItem("Create Agents");
			menuSource.addActionListener(this);
			popupMenu.add(menuSource);
			
			popupMenu.add(new JSeparator());
			JMenuItem menuPolicy = new JMenuItem("Create Policy Group");
			menuPolicy.addActionListener(this);
			popupMenu.add(menuPolicy);

		}
		
		popupMenu.addPopupMenuListener(jwbPanel);
				
		return popupMenu;
	}
	
	private JPopupMenu popupMenuLine()
	{
		// get selected shapes now as the selection could change by time user clicks a menu item
		// if they are collaborating 
		selectedShapes = jwbPanel.getSelectedShapes();
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem plotItem = new JMenuItem("Plot", new ImageIcon(this.getClass().getClassLoader().getResource("plot.png")));
		plotItem.addActionListener(this);
		popupMenu.add(plotItem);
		
		JMenuItem menuPopupTiming = new JMenuItem("Timing", new ImageIcon(
				this.getClass().getClassLoader().getResource( "timing.png")));
		
		JMenuItem menuPopupEvidence = new JMenuItem("Evidence", new ImageIcon(
				this.getClass().getClassLoader().getResource( "evidence.png")));
		
		JMenuItem menuPopupScheduler = new JMenuItem("Scheduler", new ImageIcon(
                this.getClass().getClassLoader().getResource( "timing.png")));
		popupMenu.add(menuPopupScheduler);
		menuPopupScheduler.addActionListener(this);
		
		JMenuItem fontItem = new JMenuItem("Set Font", new ImageIcon(this.getClass().getClassLoader().getResource("font.png")));
		fontItem.addActionListener(this);			
		
		JMenuItem menuSetArch = new JMenuItem("Arched Line", new ImageIcon(
				this.getClass().getClassLoader().getResource( "arch.png")));
		JMenuItem menuSetStraight = new JMenuItem("Straight Line", new ImageIcon(
				this.getClass().getClassLoader().getResource( "arrow.png")));
		menuSetArch.addActionListener(this);
		menuSetStraight.addActionListener(this);
		
		JMenuItem menuPopupDelete = new JMenuItem("Delete", new ImageIcon(
				this.getClass().getClassLoader().getResource( "delete.gif")));
		
		JMenuItem menuPopupRename = new JMenuItem("Rename", new ImageIcon(
				this.getClass().getClassLoader().getResource( "rename.png")));
		
		JMenu resourcesMenu = new JMenu("Resources");
		JMenuItem simpResources = new JMenuItem("Simple Resources");
		JMenuItem threatResources = new JMenuItem("Threat Resources");
		threatResources.addActionListener(this);
		simpResources.addActionListener(this);
		resourcesMenu.add(simpResources);
		resourcesMenu.add(threatResources);
		
		//menuPopupText.addActionListener(this);
		//menuPopupTextColor.addActionListener(this);
		menuPopupTiming.addActionListener(this);
		menuPopupDelete.addActionListener(this);
		menuPopupEvidence.addActionListener(this);
		menuPopupRename.addActionListener(this);
		
		//popupMenu.add(menuPopupText);
		popupMenu.add(menuPopupTiming);
		popupMenu.add(menuPopupEvidence);
		popupMenu.addSeparator();
		popupMenu.add(fontItem);
		popupMenu.add(menuSetArch);
		popupMenu.add(menuSetStraight);
		popupMenu.add(menuPopupDelete);
		popupMenu.add(menuPopupRename);
		popupMenu.add(resourcesMenu);
		
		popupMenu.addPopupMenuListener(jwbPanel);
		
		return popupMenu;
	}
	///////////////////////////////////////////////////////////////////////////////////
	
	public JMenu createColorMenu()
	{
		JMenu colorMenu = new JMenu("Color Options");
		colorMenu.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("colorwheel.png")));
		JMenuItem menuColorOption = new JMenuItem("Select Color");
		menuColorOption.addActionListener(this);
		colorMenu.add(menuColorOption);
		colorMenu.addSeparator();
		//add the user attributes in from the current ColorScheme
		Enumeration attribs = ColorScheme.getInstance().getSchemeData().elements();
		while(attribs.hasMoreElements())
		{
			ColorSchemeAttrib csa = (ColorSchemeAttrib)attribs.nextElement();
			if(csa.toString().equals(ColorScheme.DEF_NODE_TEXT_STRING)) //dont show this one
				continue;
			JMenuItem thisItem = new JMenuItem((csa.toString()));
			thisItem.setActionCommand("SchemeAttribute");
			thisItem.addActionListener(this);
			colorMenu.add(thisItem);
		}
		
		return colorMenu;
	}
	
	private void savePlanImage()
	{
		String imageFileName = null;
		boolean cancel = false;
		File file = null;
		
		JFileChooser fc = new JFileChooser( "." );
		
		fc.addChoosableFileFilter( new CatFileFilter( "jpeg", "JPEG images" ) );
		fc.addChoosableFileFilter( new CatFileFilter( "png", "PNG images" ) );
		FileFilter ff = fc.getAcceptAllFileFilter( ); // get accept all
		fc.removeChoosableFileFilter( ff ); // now remove it
		
		int returnVal = fc.showDialog( MainFrm.getInstance(), "Save" );
		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			file = fc.getSelectedFile( );
			imageFileName = file.getAbsolutePath( );
			
			if( !( imageFileName.endsWith( ".jpeg" ) || imageFileName.endsWith( ".png" ) ) ) {
				imageFileName += "." + ( (CatFileFilter)fc.getFileFilter( ) ).getExtension( );
			}
			file = new File( imageFileName );
			if( file.isFile( ) ) {
				if( javax.swing.JOptionPane.showConfirmDialog( MainFrm.getInstance(), "Overwrite existing file?",
						"Warning", JOptionPane.YES_NO_OPTION ) != 0 ) {
					saveSubImageAs( );
				}
			}
		}else{
			cancel = true;
		}
		
		if( !cancel ) {
			try {
				BufferedImage subImage = jwbPanel.getImage();
				if( subImage != null ) {
					ImageIO.write( subImage, ( (CatFileFilter)fc.getFileFilter( ) ).getExtension( ), file );
				}else {
					JOptionPane.showMessageDialog( MainFrm.getInstance(),
							"You must first use the selection tool to select a portion of the model.",
							"Notice", JOptionPane.INFORMATION_MESSAGE );
				}
			} catch( IOException ioe ) {
				if( imageFileName != null ) {
					JOptionPane.showMessageDialog( MainFrm.getInstance(), "Failed to save file.  Ensure you have the proper file permissions.",
							"Error", JOptionPane.ERROR_MESSAGE );
				}
			}
		}
		
	}
	
	private void saveSubImageAs( ) {
		String imageFileName = null;
		boolean cancel = false;
		File file = null;
		
		JFileChooser fc = new JFileChooser( "." );
		
		fc.addChoosableFileFilter( new CatFileFilter( "jpeg", "JPEG images" ) );
		fc.addChoosableFileFilter( new CatFileFilter( "png", "PNG images" ) );
		FileFilter ff = fc.getAcceptAllFileFilter( ); // get accept all
		fc.removeChoosableFileFilter( ff ); // now remove it
		
		int returnVal = fc.showDialog( MainFrm.getInstance(), "Save" );
		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			file = fc.getSelectedFile( );
			imageFileName = file.getAbsolutePath( );
			
			if( !( imageFileName.endsWith( ".jpeg" ) || imageFileName.endsWith( ".png" ) ) ) {
				imageFileName += "." + ( (CatFileFilter)fc.getFileFilter( ) ).getExtension( );
			}
			file = new File( imageFileName );
			if( file.isFile( ) ) {
				if( javax.swing.JOptionPane.showConfirmDialog( MainFrm.getInstance(), "Overwrite existing file?",
						"Warning", JOptionPane.YES_NO_OPTION ) != 0 ) {
					saveSubImageAs( );
				}
			}
		}else{
			cancel = true;
		}
		
		if( !cancel ) {
			try {
				BufferedImage subImage = jwbPanel.getSubImage( );
				
				if( subImage != null ) {
					ImageIO.write( subImage, ( (CatFileFilter)fc.getFileFilter( ) ).getExtension( ), file );
				}else {
					JOptionPane.showMessageDialog( MainFrm.getInstance(),
							"You must first use the selection tool to select a portion of the whiteboard.",
							"Notice", JOptionPane.INFORMATION_MESSAGE );
				}
			} catch( IOException ioe ) {
				if( imageFileName != null ) {
					JOptionPane.showMessageDialog( MainFrm.getInstance(), "Failed to save file. Check permissions.",
							"Error", JOptionPane.ERROR_MESSAGE );
				}
			}
		}
	}
	
	// helper method used to load a image
	private ImageIcon open()
	{
		ImageIcon image = null;
		int returnVal = fileChooser.showDialog(jwbPanel, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			
			try
			{
				image = new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath());
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(jwbPanel, "Could not load file!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return image;
	}
	
	public JTextField getPopupTextField()
	{
		JTextField textField = new JTextField();
		JWBShape jwbShape = null;
		FontMetrics fontMetrics = null;
		
		try
		{
			jwbShape = (JWBShape) jwbPanel.getFocusedShape().deepCopy();
			fontMetrics = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics()
					.getFontMetrics(jwbShape.getFont());
		} catch (NullPointerException e)
		{
			// lost focus, probably due to remote update
		}
		
		textField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		textField.setActionCommand("TEXTFIELD");
		textField.addActionListener(this);
		
		if (jwbShape != null)
		{
			int width = SwingUtilities.computeStringWidth(fontMetrics, jwbShape.getText());
			if (width < 60)
			{
				width = 60;
			}
			textField.setSize(width, fontMetrics.getMaxAscent()	+ fontMetrics.getMaxDescent());
			textField.setText(jwbShape.getText());
			textField.selectAll();
			
			return textField;
		} else
		{
			return null;
		}
	}
	
	private class ChainHelper extends Thread
	{
		private JWBShape s;
		private AbstractPlan p;
		private JWBController c;
		private int mode;
		
		public ChainHelper(JWBShape shape, AbstractPlan plan, JWBController co, int m)
		{
			super("Chain-Builder");
			s = shape;
			p = plan;
			c = co;
			mode = m;
		}
		
		public void run()
		{
			try{
				PlanItem item = p.getItem((Guid)s.getAttachment());
				java.util.List visiblelist = p.getShapeMapping(p.getFirstLevelItems(item.getGuid(), mode));
				ArrayList updateList = new ArrayList();
				java.util.Iterator si = c.getShapes().keySet().iterator();
				JWBShape shape;
				// this one does not get rid of the old items
				for(;si.hasNext();)
				{
					shape = c.getShape((com.c3i.jwb.JWBUID)si.next());
					
					// do this check to prevent all shapes associated with a consolidated mech from showing
					boolean isUnwantedConsol = false;
					if(shape instanceof JWBLine)
					{
						JWBUID[] linked = ((JWBLine)shape).getLinkedShapes();
						if(linked[0] != s.getUID() && linked[1] != s.getUID())
							isUnwantedConsol = true;
					}
					
					if(visiblelist.contains(shape.getUID()) && !isUnwantedConsol)
					{
						shape.setHidden(false);
						updateList.add(shape);
					}
					else
						shape.setHidden(true);
				}
				c.putShapes(updateList);
			}catch(Exception ex)
			{
				logger.error("ChainHelper(run) - Error in chain helper thread:  "+ex.getMessage());
			}
			
		}
	}
	
	// helper thread
	private class NodeHelper extends Thread
	{
		private JWBShape s;
		private AbstractPlan p;
		private JWBController c;
		private int mode;
		
		public NodeHelper(JWBShape shape, AbstractPlan plan, JWBController co, int m)
		{
			super("Chain-Helper");
			s = shape;
			p = plan;
			c = co;
			mode = m;
		}
		
		public void run()
		{
			try{
				PlanItem item = p.getItem((Guid)s.getAttachment());
				java.util.List visiblelist = p.getShapeMapping(p.getFirstLevelItems(item.getGuid(), mode));
				ArrayList updateList = new ArrayList();
				java.util.Iterator si = c.getShapes().keySet().iterator();
				JWBShape shape;
				for(;si.hasNext();)
				{
					shape = c.getShape((com.c3i.jwb.JWBUID)si.next());
					//do this check to prevent all shapes associated with a consolidated mech from showing
					boolean isUnwantedConsol = false;
					if(shape instanceof JWBLine)
					{
						JWBUID[] linked = ((JWBLine)shape).getLinkedShapes();
						if(linked[0] != s.getUID() && linked[1] != s.getUID())
							isUnwantedConsol = true;
					}
					
					if(visiblelist.contains(shape.getUID()) && !isUnwantedConsol)
					{
						shape.setHidden(false);
					}
					else
					{
						shape.setHidden(true);
					}
					updateList.add(shape);
				}
				// now set them
				c.putShapes(updateList);
				
			}catch(Exception ex)
			{
				logger.error("NodeHelper.run - error show/hiding shapes:  "+ex.getMessage());
			}
			
			
		}
		
	}
		
	
	//this currently is designed for 1 to many mech creation (thus is will always be making consolidators)
	private void createMultipleMechs()
	{
		JWBShape fromShape = (JWBShape)multiMechEvents.remove(0);
		JWBShape firstToShape = (JWBShape)multiMechEvents.remove(0);
		//AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		JWBController controller = Control.getInstance().getController(MainFrm.getInstance().getSelectedPlan());
		StringBuffer mechExceptions = new StringBuffer();
		
		try{
			//create first mech using normal procedure with a mech dialog
			JWBLine newLine = new JWBLine(fromShape, firstToShape, new JWBUID(), showSigNamesItem.isSelected());
			MechanismDialog md = new MechanismDialog();
			boolean created = md.createMechanism(MainFrm.getInstance(), (Event)plan.getItem((Guid)firstToShape.getAttachment()), (Event)plan.getItem((Guid)fromShape.getAttachment()), plan);
			if(!created) //mech dialog was canceled
				return; 
			
			Mechanism mech = md.getMechanism();
			Control.getInstance().addMechanism(newLine, mech, md.getSignal(), md.getType(), controller, plan);
			
			Signal sig = md.getSignal();

			//create all other mechanisms with the signal used on first one
			Iterator restShapes = multiMechEvents.iterator();
			while(restShapes.hasNext())
			{
			    JWBShape to = (JWBShape)restShapes.next();
			    Event toEvent = (Event)plan.getItem((Guid)to.getAttachment());
				
			    if(md.getType() == SignalType.CAUSAL && plan.getLibrary().getProcess(toEvent.getProcessGuid()).getInhibitingSignals().contains(mech.getSignalGuid()))
			    {
			    	mechExceptions.append("The signal is already used as an inhibitor in "+toEvent.getName());
			    	continue;
			    }
                else if(plan.getLibrary().getProcess(toEvent.getProcessGuid()).getCausalSignals().contains(mech.getSignalGuid()))
                {
                	mechExceptions.append("The signal is already used as an cause in "+toEvent.getName());
                	continue;
                }
                else if(plan.edgeWillMakeLoop((Event)plan.getItem((Guid)fromShape.getAttachment()), toEvent))
                {
                	mechExceptions.append("The mechanism would have created a loop when connected to: "+toEvent.getName());
                	continue;
                }
			    
			    //use the created mechanism (changed for consolidators), just add more outputs
			    //Mechanism mech = new Mechanism(new Guid(), sig.getSignalName(), (Event)to.getAttachment(), (Event)fromShape.getAttachment(), sig.getSignalID());
			    mech.addConsolidatedOutput(toEvent);
			    mech.setLoopCloser(false);
					
			    //put it on the whiteboard as a shape
			    JWBLine newLine1 = new JWBLine(fromShape, to, new JWBUID(), showSigNamesItem.isSelected());
					
			    Control.getInstance().addMechanism(newLine1, mech, sig, md.getType(), controller, plan);				
			}
			
			//show 1 dialog listing any problems creating all the mechs
			if(mechExceptions.length() > 0)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "Some mechanisms could not be created: \n"+mechExceptions.toString());
			}
			
		}catch(Exception exc){
			logger.error("createMultipleEvents - Error creating mechanisms:  "+exc.getMessage());
		}  
	}
	
	public void itemStateChanged(ItemEvent event)
	{
		if(event.getSource() == avgOpt)
		{
			xBox.setEnabled(false);
			yBox.setEnabled(false);
		}
		else if(event.getSource() == specOpt)
		{
			xBox.setEnabled(true);
			yBox.setEnabled(true);
		}
		else if(event.getSource() == showSigNamesItem)
		{
			//here simply set the flag in panel
			jwbPanel.setShowLineText(showSigNamesItem.isSelected());
		}
	}

	
}
