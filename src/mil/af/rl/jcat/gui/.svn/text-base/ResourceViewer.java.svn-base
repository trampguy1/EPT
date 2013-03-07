/*
 * Created on Aug 3, 2005
 */
package mil.af.rl.jcat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


import mil.af.rl.jcat.gui.dialogs.DefineResourceDialog;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.ResourceManager;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/**
 * @author dygertm
 * Property Viewer Dock window - shows various properties of a plan item when clicked on
 */
public class ResourceViewer extends DockableFrame implements DockableFrameListener, MouseListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	private JTable resTable = null;
	private ResourceManager model;
//	private PlanItem item;
	
	public ResourceViewer()
	{
		super("Model Resource Manager", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("prop_viewer.png")));
		getContext().setInitSide(DockContext.DOCK_SIDE_SOUTH);
//		getContext().setInitIndex(0);
		getContext().setInitMode(DockContext.STATE_HIDDEN); //this doesn't work
		setPreferredSize(new Dimension(180, 300));
		
		
		JButton defBtn = new JButton("Define New Resource");
		JButton delBtn = new JButton("Delete Selected Resource");
		defBtn.setActionCommand("New");
		delBtn.setActionCommand("Delete");
		defBtn.addActionListener(this);
		delBtn.addActionListener(this);
		
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createTable(), BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		btnPane.add(defBtn);
		btnPane.add(delBtn);
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		//javax.help.CSH.setHelpIDString(this, "Resources");
	}
	
	private JComponent createTable()
	{
		resTable = new JTable();
		resTable.setFont(new Font("Dialog", 0, 12));

		JScrollPane tableScroll = new JScrollPane();
		tableScroll.setViewportView(resTable);
		tableScroll.getViewport().setBackground(Color.WHITE);
		
		return tableScroll;
	}
	
	public void updateViewer(AbstractPlan plan)
	{
		resTable.setModel(model = plan.getResourceManager());
        model.addTableModelListener(resTable);
		resTable.repaint();
		
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
		
	}

	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "New")
		{
			new DefineResourceDialog(MainFrm.getInstance(), this.model);
		}
		else if(e.getActionCommand() == "Delete")
		{
			try
            {
                model.deleteSelectedResource(resTable.getSelectedRow());
            } catch (Exception e1)
            {
                JOptionPane.showMessageDialog(this, e1.getMessage());
            }
		}		
	}
}
