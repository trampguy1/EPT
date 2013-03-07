/*
 * Created on Aug 3, 2005
 */
package mil.af.rl.jcat.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Document;
import org.dom4j.Element;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.gui.dialogs.EventDialog;
import mil.af.rl.jcat.gui.dialogs.TimingDialog;
import mil.af.rl.jcat.gui.table.model.TableProperty;
import mil.af.rl.jcat.gui.table.model.base.JPGroup;
import mil.af.rl.jcat.gui.table.model.base.PTModel;
import mil.af.rl.jcat.gui.table.model.base.PTSignal;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.grid.PropertyTableModel;

/**
 * @author dygertm
 * Property Viewer Dock window - shows various properties of a plan item when clicked on
 */
public class PropertyViewer extends DockableFrame implements DockableFrameListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	Vector<TableProperty> rowData = new Vector<TableProperty>();
	PropertyTable propTable;
	private PropertyTableModel model;
	private PlanItem item;
	
	public PropertyViewer()
	{
		super("Property Viewer", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("prop_viewer.png")));
		getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
		getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
		getContext().setInitIndex(1);
		setPreferredSize(new Dimension(180,300));
		
		
		propTable = new PropertyTable(model = new PropertyTableModel(rowData));
		propTable.setDisabledForeground(java.awt.Color.black);
		
		PropertyPane pane = new PropertyPane(propTable);
		pane.setShowToolBar(false);
		pane.setShowDescription(false);
		getContentPane().add(pane);
		propTable.addMouseListener(this);
		javax.help.CSH.setHelpIDString(this, "Property_Viewer");
	}
	
	public void updateViewer(PlanItem pItem)
	{
		try{
			item = pItem;
			rowData.removeAllElements();
			
			rowData.add(new TableProperty("Name", item.getName(), "General"));
			rowData.add(new TableProperty("GUID", item.getGuid(), "General"));
			rowData.add(new TableProperty("Applies to", item.getLabel(), "General"));
			
			rowData.add(new TableProperty("Delay", item.getDelay()+"", "Timing"));
			rowData.add(new TableProperty("Persistence", item.getPersistence()+"", "Timing"));
			rowData.add(new TableProperty("Continuation", item.getContinuation()+"", "Timing"));
			
			if(item.getSchedule().size() > 0)
			{
				TableProperty schedProp;
				rowData.add(schedProp = new TableProperty("Schedule", "", "Timing"));
				Iterator sched = item.getSchedule().keySet().iterator();
				while(sched.hasNext())
				{
					Object val = sched.next();
	//				rowData.add(new TableProperty(val.toString(), item.getSchedule().get(val), "Schedule"));
					schedProp.addChild(new TableProperty(val.toString(), item.getSchedule().get(val), "Schedule"));
				}
			}
			
			
			if(item instanceof Event)
			{
				Event currentEvent = (Event)item;
				rowData.add(new TableProperty("Process GUID", currentEvent.getProcessGuid(), "General"));
				rowData.add(new TableProperty("Leak", currentEvent.getLeak()+"", "General"));
				rowData.add(new TableProperty("Scheme Attrib", currentEvent.getSchemeAttrib()+"", "General"));

				AbstractPlan thePlan = MainFrm.getInstance().getActiveView().getPlan();
				Document procDoc = thePlan.getLibrary().getProcessDocument(currentEvent.getProcessGuid());
				Process process = thePlan.getLibrary().getProcess(currentEvent.getProcessGuid());
				
				rowData.add(new TableProperty("Default Causal", process.getDefault(SignalType.CAUSAL)+"", "General"));
				rowData.add(new TableProperty("Default Inhibiting", process.getDefault(SignalType.INHIBITING)+"", "General"));
				rowData.add(new TableProperty("Default Effecting", process.getDefault(SignalType.EFFECT)+"", "General"));				
				
				// SIGNALS //
				
				// add the causes
		        Element csetC = (Element) procDoc.selectSingleNode("//ModeSet[@mode='"+ SignalType.CAUSAL + "']");
		        PTModel tmodelC = new PTModel(csetC, SignalType.CAUSAL, SignalType.RNOR, thePlan, currentEvent);
		        Hashtable guid_signalC = tmodelC.returnSignals();
		      	Hashtable guid_probC = tmodelC.returnSingleProbabilities();
		      	Enumeration guidsC = guid_signalC.keys();
		        
		      	while(guidsC.hasMoreElements())
		      	{      		
		      		Guid thisGuid = (Guid)guidsC.nextElement();
		      		Object prob = (guid_probC.get(thisGuid) != null) ? guid_probC.get(thisGuid): process.getDefault(SignalType.CAUSAL)+"";
		      		rowData.add(new TableProperty(((PTSignal)guid_signalC.get(thisGuid)).getSignalName(), prob, "Causes"));
		      	}
				
				// add cause groups
		      	Hashtable cGroups = tmodelC.returnGroups();
		      	Enumeration cGroupKeys = cGroups.keys();
		      	while(cGroupKeys.hasMoreElements()) //cause groups
		      	{
		      		JPGroup thisGroup = (JPGroup)cGroups.get(cGroupKeys.nextElement());
		      		TableProperty groupProp = new TableProperty(thisGroup.getName(), thisGroup.getProbability(thePlan.getLibrary()), "Causes");
		      		Iterator grpSigs = thisGroup.getSignals().iterator();
		      		while(grpSigs.hasNext())
		      		{
		      			TableProperty groupMember = new TableProperty(grpSigs.next().toString(), "", "");
			      		groupProp.addChild(groupMember);
		      		}
			      	rowData.add(groupProp);
		      	}
		      	
		      	
		      	//add the inhibitors
		      	Element csetI = (Element) procDoc.selectSingleNode("//ModeSet[@mode='"+ SignalType.INHIBITING + "']");
		        PTModel tmodelI = new PTModel(csetI, SignalType.INHIBITING, SignalType.RNOR, thePlan, currentEvent);
		        Hashtable guid_signalI = tmodelI.returnSignals();
		      	Hashtable guid_probI = tmodelI.returnSingleProbabilities();
		      	Enumeration guidsI = guid_signalI.keys();
		        
		      	while(guidsI.hasMoreElements())
		      	{
		      		Guid thisGuid = (Guid)guidsI.nextElement();
		      		rowData.add(new TableProperty(((PTSignal)guid_signalI.get(thisGuid)).getSignalName(), guid_probI.get(thisGuid), "Inhibitors"));
		      	}

		      	// add inhib groups
		      	Hashtable iGroups = tmodelI.returnGroups();
		      	Enumeration iGroupKeys = iGroups.keys();
		      	while(iGroupKeys.hasMoreElements())
		      	{
		      		JPGroup thisGroup = (JPGroup)iGroups.get(iGroupKeys.nextElement());
		      		TableProperty groupProp = new TableProperty(thisGroup.getName(), thisGroup.getProbability(thePlan.getLibrary()), "Inhibitors");
		      		Iterator grpSigs = thisGroup.getSignals().iterator();
		      		while(grpSigs.hasNext())
		      		{
		      			TableProperty groupMember = new TableProperty(grpSigs.next().toString(), "", "");
			      		groupProp.addChild(groupMember);
		      		}
			      	rowData.add(groupProp);
		      	}
		      	
		      	
		      	// add the effects
		      	Element csetE = (Element) procDoc.selectSingleNode("//ModeSet[@mode='"+ SignalType.EFFECT + "']");
		        PTModel tmodelE = new PTModel(csetE, SignalType.EFFECT, SignalType.RNOR, thePlan, currentEvent);
		        Hashtable guid_signalE = tmodelE.returnSignals();
		      	Hashtable guid_probE = tmodelE.returnSingleProbabilities();
		      	Enumeration guidsE = guid_signalE.keys();
		        
		      	while(guidsE.hasMoreElements())
		      	{
		      		Guid thisGuid = (Guid)guidsE.nextElement();
		      		rowData.add(new TableProperty(((PTSignal)guid_signalE.get(thisGuid)).getSignalName(), guid_probE.get(thisGuid), "Effects"));
		      	}
			}
			else if(item instanceof Mechanism)
			{
				Mechanism thisMech = (Mechanism)item;
				rowData.add(new TableProperty("Signal GUID", thisMech.getSignalGuid(), "General"));
			}
			
			//simple resources
			TableProperty simpleRes = new TableProperty("Simple Resources", "", "Resources");
			Iterator res = item.getResources().keySet().iterator();
			while(res.hasNext())
			{
				ResourceAllocation thisRes = item.getResources().get(res.next());
				
		      	TableProperty sRes = new TableProperty(thisRes.getID().getValue(), thisRes.getAllocated()+"", "Simple Resources");
		      	simpleRes.addChild(sRes);
			}
			if(simpleRes.getChildren() != null)
				rowData.add(simpleRes);
			
			//threat resources
			TableProperty threatRes = new TableProperty("Threat Resources", "", "Resources");
			Iterator res1 = item.getThreatResources().keySet().iterator();
			while(res1.hasNext())
			{
				Integer time = (Integer) res1.next();
				List<ResourceAllocation> thisRes = item.getThreatResources().get(time);               
				
		      	TableProperty sRes = new TableProperty(time.toString(), thisRes, "Threat Resources");
		      	threatRes.addChild(sRes);
			}
			if(threatRes.getChildren() != null)
				rowData.add(threatRes);
			
			
		}catch(NullPointerException exc){   } //item is null (doesn't exist anymore)
			
		model.reloadProperties();
//		propTable.expandFirstLevel();
		model.expandFirstLevel();
//		groupProp.setExpanded(false);
		
	}
	
	public JWBShape getShape(PlanItem theEvent)
	{
		AbstractPlan plan = MainFrm.getInstance().getActiveView().getPlan();
		Vector shapeIDL = new Vector();
		shapeIDL.add(theEvent.getGuid());
		JWBUID shapeUID = (JWBUID)(((LinkedList)(plan.getShapeMapping(shapeIDL))).getFirst());
		return ((JWBController)(Control.getInstance().getController(plan.getId()))).getShape(shapeUID);
	}
	
	public void addDockListener()
	{
		addDockableFrameListener(this);
	}
	
	public void dockableFrameHidden(DockableFrameEvent arg0)
	{
		MainFrm.getInstance().getCatMenuBar().uncheckViewItem(getTitle());
	}
	
	//unused dock events
	public void dockableFrameAdded(DockableFrameEvent arg0){}
	public void dockableFrameRemoved(DockableFrameEvent arg0){}
	public void dockableFrameShown(DockableFrameEvent arg0){}
	public void dockableFrameDocked(DockableFrameEvent arg0){}
	public void dockableFrameFloating(DockableFrameEvent arg0){}
	public void dockableFrameAutohidden(DockableFrameEvent arg0){}
	public void dockableFrameAutohideShowing(DockableFrameEvent arg0){}
	public void dockableFrameActivated(DockableFrameEvent arg0){}
	public void dockableFrameDeactivated(DockableFrameEvent arg0){}
	public void dockableFrameTabShown(DockableFrameEvent arg0){}
	public void dockableFrameTabHidden(DockableFrameEvent arg0){}
	public void dockableFrameMaximized(DockableFrameEvent arg0){}
	public void dockableFrameRestored(DockableFrameEvent arg0){}

	//
	public void mouseClicked(MouseEvent event)
	{
		if(event.getClickCount() == 2)
		{
			try{
				String cat = propTable.getSelectedProperty().getCategory();
				if(cat.equals("Timing"))
					new TimingDialog(MainFrm.getInstance(), getShape(item), MainFrm.getInstance().getActiveView().getPlan());//pop up timing dialog
				else if(cat.equals("Causes"))
				{
					EventDialog dialog = new EventDialog(MainFrm.getInstance(), "Event Editor", getShape(item), (JWBController)(Control.getInstance().getController(MainFrm.getInstance().getActiveView().getPlan().getId())));//pop up event editor on 'special probs'
					dialog.show();
					dialog.setActivePane(2); //"Specified \r Probabilities"); doesnt work yet
				}
				else if(cat.equals("Inhibitors"))
					new EventDialog(null, "Event Editor", getShape(item), (JWBController)(Control.getInstance().getController(MainFrm.getInstance().getActiveView().getPlan().getId()))).show();//pop up event editor on 'special probs - inhibits tab'
			}catch(NullPointerException exc){   updateViewer(null);   } //item is null (doesn't exist anymore)
		}
	}

	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}
