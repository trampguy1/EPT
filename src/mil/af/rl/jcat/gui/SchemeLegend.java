/*
 * Created on Aug 2, 2005
 */
package mil.af.rl.jcat.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.ColorSchemeAttrib;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/**
 * @author dygertm
 * ColorScheme legend Dock window - shows a legend for the active color scheme
 */
public class SchemeLegend extends DockableFrame implements DockableFrameListener
{

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private GridBagLayout layout;
	private JScrollPane scrollPane;
	private JLabel title;
	private GridBagConstraints constraints;
	
	public SchemeLegend()
	{
		super("Scheme Legend", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("scheme_legend.png")));
		getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
		getContext().setInitSide(DockContext.DOCK_SIDE_NORTH);
		getContext().setInitIndex(1);
		setPreferredSize(new Dimension(100, 150));
		setDockedWidth(100);
		
		
		layout = new GridBagLayout();
		mainPanel = new JPanel(layout);
		scrollPane = new JScrollPane();
		title = new JLabel();
		title.setFont(title.getFont().deriveFont(1));
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(2,5,2,5);
		
		scrollPane.setViewportView(mainPanel);
		scrollPane.setColumnHeader(new JViewport());
		scrollPane.getColumnHeader().add(title);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		getContentPane().add(scrollPane);
		javax.help.CSH.setHelpIDString(this, "Scheme_Legend1");
	}
	
	public void updateLegend()
	{
		mainPanel.removeAll();
		//populate the legend with items from the active colorscheme
		Vector data = ColorScheme.getInstance().getSchemeData();
		Iterator it = data.iterator();
				
		title.setText(ColorScheme.getInstance().getName());
		
		while(it.hasNext())
		{
			ColorSchemeAttrib attrib = (ColorSchemeAttrib)it.next();
			
			JTextField colorView = new JTextField("     ");
			colorView.setBackground(attrib);
			colorView.setEnabled(false);
			layout.setConstraints(colorView, constraints);
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			mainPanel.add(colorView);
			
			JLabel lbl = new JLabel(attrib.toString());
			lbl.setPreferredSize(new Dimension(lbl.getPreferredSize().width,colorView.getPreferredSize().height));
			layout.setConstraints(lbl, constraints);
			constraints.gridwidth = GridBagConstraints.RELATIVE;
			mainPanel.add(lbl);
		}
		updateUI();
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
}
