/*
 * Created on June 14, 2005
 */
package mil.af.rl.jcat.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.gui.dialogs.EventDialog;
import mil.af.rl.jcat.gui.dialogs.EvidenceDialog;
import mil.af.rl.jcat.gui.dialogs.TimingDialog;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.PlanItemSorter;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/**
 * @author dygertm
 * Docking Window containing the Navigation Tree - shows all events or mechanisms in a tree form
 */
public class NavTree extends DockableFrame implements MouseListener, DockableFrameListener, TreeExpansionListener,
														ActionListener, ItemListener
{

	private static final long serialVersionUID = 1L;
	private JTree nodeTree;
	private AbstractPlan plan = null;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Events");
	private String expandedBranch = "";
	private JPopupMenu popMenu = null;
	private JMenuItem expandItem, editItem, findItem, timeItem, evidItem;
	private JRadioButton mechOpt;
	private JRadioButton eventOpt;
	private static int EVENT = 0;
	private static int MECH = 1;
	private int treeMode = 0;
	private int sortMode = PlanItemSorter.ALPHABETIC_ASCEND;
	private JMenuItem ascendSort;
	private JMenuItem descendSort;
	private JMenuItem causeSort;
	private JMenuItem inhibitSort;
	private JMenuItem effectSort;
	private JMenu sortMenu;
	
	public NavTree() 
	{
		super("Navigation Tree", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("nav_tree.png")));
		getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
		getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
		getContext().setInitIndex(0);
				
		
		nodeTree = new JTree(new DefaultTreeModel(root));
		nodeTree.setRootVisible(true);
		nodeTree.setName("Navigation Tree");
		nodeTree.addMouseListener(this);
		getContentPane().add(createOptionPanel(), BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(nodeTree), BorderLayout.CENTER);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(new ImageIcon(this.getClass().getClassLoader().getResource("plan.png")));
		//replace next two lines with a different icon if one is found(for root and branches)
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		nodeTree.setCellRenderer(renderer);
		nodeTree.setToggleClickCount(1);
		nodeTree.addTreeExpansionListener(this);
		
		popMenu = new JPopupMenu();
		sortMenu = new JMenu("Sort by");
		popMenu.add(expandItem = new JMenuItem("Expand All"));
		popMenu.add(findItem = new JMenuItem("Snap to"));
		ascendSort = new JMenuItem("Alphabetic (A->Z)");
		ascendSort.setActionCommand("sort");
		descendSort = new JMenuItem("Alphabetic (Z->A)");
		descendSort.setActionCommand("sort");
		causeSort = new JMenuItem("Number of Causes");
		causeSort.setActionCommand("sort");
		inhibitSort = new JMenuItem("Number of Inhibitors");
		inhibitSort.setActionCommand("sort");
		effectSort = new JMenuItem("Number of Effects");
		effectSort.setActionCommand("sort");
		sortMenu.add(ascendSort);
		sortMenu.add(descendSort);
		sortMenu.add(causeSort);
		sortMenu.add(inhibitSort);
		sortMenu.add(effectSort);
		
		popMenu.add(sortMenu);
		popMenu.addSeparator();
		popMenu.add(editItem = new JMenuItem("Edit Event"));
		popMenu.add(timeItem = new JMenuItem("Timing"));
		popMenu.add(evidItem = new JMenuItem("Evidence"));
		
		expandItem.addActionListener(this);
		editItem.addActionListener(this);
		findItem.addActionListener(this);
		timeItem.addActionListener(this);
		evidItem.addActionListener(this);
		ascendSort.addActionListener(this);
		descendSort.addActionListener(this);
		causeSort.addActionListener(this);
		inhibitSort.addActionListener(this);
		effectSort.addActionListener(this);
		
		javax.help.CSH.setHelpIDString(this, "Navigation_Trees");
		setVisible(true);
	}

	public JPanel createOptionPanel()
	{
		JPanel optionPanel = new JPanel(new GridLayout(1,2));
		optionPanel.add(eventOpt = new JRadioButton("Events", true));
		optionPanel.add(mechOpt = new JRadioButton("Mechanisms"));
		
		ButtonGroup bgroup =  new ButtonGroup();
		bgroup.add(eventOpt);
		bgroup.add(mechOpt);
		
		eventOpt.addItemListener(this);
		mechOpt.addItemListener(this);
		
		return optionPanel;
	}
	
	public synchronized void populateTree(AbstractPlan thisPlan)
	{
		try{
			plan = thisPlan;
			synchronized(plan.getAllEvents())
			{
				root.removeAllChildren();
				
				Vector itemList = new Vector();
				if(treeMode == NavTree.EVENT)
					itemList = new Vector(plan.getAllEvents());
				else if(treeMode == NavTree.MECH)
					itemList = new Vector(plan.getAllMechanisms());
				
				// sorts the items 
				PlanItemSorter.sort(itemList, sortMode);
				Iterator items = itemList.iterator();
				
				while(items.hasNext())
				{
					Object node = items.next();
					//add a node to the tree, use ABC style if sort is alphabetic
					addTreeNode((PlanItem)node, (sortMode == PlanItemSorter.ALPHABETIC_ASCEND) || (sortMode == PlanItemSorter.ALPHABETIC_DESCEND));
				}
				
				((DefaultTreeModel)nodeTree.getModel()).reload();
			
				//expands the last branch that was expanded before the tree was rebuilt
				nodeTree.expandPath(new TreePath(getBranch(new DefaultMutableTreeNode(expandedBranch)).getPath()));
			}
		}catch(NullPointerException exc){} //no plan is open or no branch to expand
		catch(ConcurrentModificationException exc){} //occationally when many items are added at once
		
	}
	
	public void addTreeNode(PlanItem eventNode, boolean abcView)
	{
		try{
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(eventNode.getName().toUpperCase().substring(0,1));
			
			//if there is a branch with the first letter, add to that otherwise make it then add
			if(!abcView)
				root.add(new DefaultMutableTreeNode(eventNode));
			else if(hasBranch(branch))
				getBranch(branch).add(new DefaultMutableTreeNode(eventNode));
			else
			{
				root.add(branch);
				addTreeNode(eventNode, true);
			}
		}catch(StringIndexOutOfBoundsException exc){}
	}
	
	public boolean hasBranch(DefaultMutableTreeNode branch)
	{
		Enumeration mainBranches = root.children();
		while(mainBranches.hasMoreElements())
			if(branch.toString().toLowerCase().equals(mainBranches.nextElement().toString().toLowerCase()))
				return true;
		return false;
	}
	
	public DefaultMutableTreeNode getBranch(DefaultMutableTreeNode branch)
	{
		Enumeration mainBranches = root.children();
		while(mainBranches.hasMoreElements())
		{
			Object thisBranch = mainBranches.nextElement();
			if(branch.toString().toLowerCase().equals(thisBranch.toString().toLowerCase()))
				return (DefaultMutableTreeNode)thisBranch;
		}
		return null;
	}

	public void expandAll()
	{
		Enumeration mainBranches = root.children();
		while(mainBranches.hasMoreElements())
			nodeTree.expandPath(new TreePath(((DefaultMutableTreeNode)mainBranches.nextElement()).getPath()));
	}
	
	public void clearTree()
	{
		root.removeAllChildren();
		((DefaultTreeModel)nodeTree.getModel()).reload();
	}

	public void addDockListener()
	{
		addDockableFrameListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		JWBController pcontroller = null;
		PlanItem currentItem = null;
		try{
			pcontroller = (JWBController)(Control.getInstance().getController(plan.getId()));
			currentItem = ((Event)((DefaultMutableTreeNode)nodeTree.getLastSelectedPathComponent()).getUserObject());
		}catch(ClassCastException exc)
		{
			try{
				currentItem = ((Mechanism)((DefaultMutableTreeNode)nodeTree.getLastSelectedPathComponent()).getUserObject());
			}catch(ClassCastException exc1){} //if root or branch is selected
		}catch(NullPointerException exc){} //nothing is selected
		
		if(event.getSource() == expandItem)
			expandAll();
		else if(event.getSource() == editItem)
			new EventDialog(MainFrm.getInstance(), "Event Editor", getShape(currentItem), pcontroller).show();
		else if(event.getSource() == findItem)
			centerShape(getShape(currentItem));
		else if(event.getSource() == timeItem)
			new TimingDialog(MainFrm.getInstance(), getShape(currentItem), this.plan);
		else if(event.getSource() == evidItem)
			new EvidenceDialog(MainFrm.getInstance(), getShape(currentItem), this.plan);
		else if(event.getActionCommand().equals("sort"))
		{
			if(event.getSource() == ascendSort)
				sortMode = PlanItemSorter.ALPHABETIC_ASCEND;
			else if(event.getSource() == descendSort)
				sortMode = PlanItemSorter.ALPHABETIC_DESCEND;
			else if(event.getSource() == causeSort)
				sortMode = PlanItemSorter.CAUSES;
			else if(event.getSource() == inhibitSort)
				sortMode = PlanItemSorter.INHIBITS;
			else if(event.getSource() == effectSort)
				sortMode = PlanItemSorter.EFFECTS;
			populateTree(plan);
		}
	}
	
	// when a tree item is double-clicked, get the Event object and use its GUID to find its JWBShape
	// use the coordinates of that shape to center the whiteboard
	public void mouseClicked(MouseEvent event)
	{
		if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2)
		{
			try{
				Object node = ((DefaultMutableTreeNode)nodeTree.getLastSelectedPathComponent()).getUserObject();
				JWBShape shape = getShape((PlanItem)node);
				centerShape(shape);
			}catch(ClassCastException exc){}
			catch(NullPointerException exc){}//occurs when root folder is double clicked, no problem
		}
		else if(event.getButton() == MouseEvent.BUTTON3)
		{
			int row = nodeTree.getClosestRowForLocation(event.getPoint().x, event.getPoint().y);
			nodeTree.setSelectionRow(row);
			
			//expandItem.setVisible(false);
			editItem.setVisible(true);
			findItem.setVisible(true);
			timeItem.setVisible(true);
			evidItem.setVisible(true);
			try{
				Event test = ((Event)((DefaultMutableTreeNode)nodeTree.getLastSelectedPathComponent()).getUserObject());
			}catch(ClassCastException exc) //a root or branch or mech is selected
			{
				editItem.setVisible(false);
				try{
					Mechanism test = ((Mechanism)((DefaultMutableTreeNode)nodeTree.getLastSelectedPathComponent()).getUserObject());
				}catch(ClassCastException exc1) //its a root branch
				{
					//expandItem.setVisible(true);
					findItem.setVisible(false);
					timeItem.setVisible(false);
					evidItem.setVisible(false);
				}
			}
			catch(NullPointerException exc) //nothing is selected at all
			{
				//expandItem.setVisible(true);
				editItem.setVisible(false);
				findItem.setVisible(false);
				timeItem.setVisible(false);
				evidItem.setVisible(false);
			}

			popMenu.show((JComponent)event.getSource(), event.getX(), event.getY());
		}
	}
	
	public JWBShape getShape(PlanItem theEvent)
	{
		Vector shapeIDL = new Vector();
		shapeIDL.add(theEvent.getGuid());
		JWBUID shapeUID = (JWBUID)(((LinkedList)(plan.getShapeMapping(shapeIDL))).getFirst());
		return ((JWBController)(Control.getInstance().getController(plan.getId()))).getShape(shapeUID);
	}
	
	public void centerShape(JWBShape theShape)
	{
		MainFrm.getInstance().getActiveView().getPanel().centerOnPoint(theShape.getCenterPoint());
	}
	
	//occurs when a branch is expanded
	public void treeExpanded(TreeExpansionEvent event)
	{
		expandedBranch = event.getPath().getLastPathComponent().toString();
	}
	
	public void dockableFrameHidden(DockableFrameEvent arg0)
	{
		MainFrm.getInstance().getCatMenuBar().uncheckViewItem(getTitle());
	}
	
	//unused method now
	public void removeTreeNode(Event eventNode)
	{
		DefaultMutableTreeNode branch = getBranch(new DefaultMutableTreeNode(eventNode.getName().toUpperCase().substring(0,1)));
		branch.remove(getNode(branch, eventNode));
		((DefaultTreeModel)nodeTree.getModel()).reload();
	}
	//unused method now
	public DefaultMutableTreeNode getNode(DefaultMutableTreeNode branch, Event theEvent)
	{
		Enumeration nodes = branch.children();
		while(nodes.hasMoreElements())
		{
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode)nodes.nextElement();
			if((Event)thisNode.getUserObject() == theEvent)
				return thisNode;
		}
		return null;
	}
	
	//events for changing the display type of the tree
	public void itemStateChanged(ItemEvent event)
	{
		if(event.getStateChange() == ItemEvent.SELECTED)
		{
			if(event.getSource() == eventOpt)
			{
				root.setUserObject("All Events");
				treeMode = NavTree.EVENT;
			}
			else if(event.getSource() == mechOpt)
			{
				root.setUserObject("All Mechanisms");
				treeMode = NavTree.MECH;
			}
			
			((DefaultTreeModel)nodeTree.getModel()).reload();
			populateTree(plan);
		}
	}
	
	//unused mouse events
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
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
	//unused tree events
	public void treeCollapsed(TreeExpansionEvent arg0){}
}