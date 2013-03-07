/*
 * Created on Aug 3, 2005
 */
package mil.af.rl.jcat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.dialogs.COACompareDialog;
import mil.af.rl.jcat.gui.dialogs.COACompareOptions;
import mil.af.rl.jcat.gui.dialogs.COAReportDialog;
import mil.af.rl.jcat.gui.dialogs.COASummaryDialog;
import mil.af.rl.jcat.gui.dialogs.MiscDialogs;
import mil.af.rl.jcat.gui.dialogs.SamplingOptions;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.COAComparator;

import com.c3i.jwb.JWBController;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/**
 * @author dygertm
 * COA Viewer Dock window - shows various properties of a plan item when clicked on
 */
public class COAViewer extends DockableFrame implements DockableFrameListener, MouseListener, ActionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	private JTable coaList;
	private JPopupMenu popup;
	private AbstractPlan lastPlan;
	private Vector columns = null;
	private Vector<Vector> data = null;
	private Logger logger = Logger.getLogger(COAViewer.class);

	public COAViewer()
	{
		super("Course of Action", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("coa.png")));
		getContext().setInitMode(DockContext.STATE_HIDDEN); //this doesn't work
		getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
		getContext().setInitIndex(0);
		
		setPreferredSize(new Dimension(180, 300));


		JButton newBtn = new JButton("Apply COA");
		JButton createBtn = new JButton("Create COA");
		newBtn.addActionListener(this);
		createBtn.addActionListener(this);

		columns = new Vector();
		columns.add("");
		data = new Vector<Vector>();
		coaList = new CustomTable(data, columns);
		coaList.addMouseListener(this);

		popup = new JPopupMenu();
		JMenuItem apply = new JMenuItem("Apply");
		apply.addActionListener(this);
		popup.add(apply);
		JMenuItem viewSum = new JMenuItem("View Summary");
		viewSum.addActionListener(this);
		popup.add(viewSum);
		JMenuItem viewReport = new JMenuItem("View Report");
		viewReport.addActionListener(this);
		popup.add(viewReport);
		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener(this);
		popup.add(delete);
		JMenuItem create = new JMenuItem("Create COA");
		create.addActionListener(this);
		JMenuItem compare = new JMenuItem("Compare");
		compare.addActionListener(this);
		popup.addSeparator();
		popup.add(create);
		popup.add(compare);

		getContentPane().setLayout(new BorderLayout());
		JScrollPane listScroll = new JScrollPane(coaList);
		listScroll.getViewport().setBackground(coaList.getBackground());
		getContentPane().add(listScroll, BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		btnPane.add(newBtn);
		btnPane.add(createBtn);
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		
		javax.help.CSH.setHelpIDString(this, "What_Is_a_Course_of_Action");
	}


	public void updateViewer(AbstractPlan plan)
	{
		lastPlan = plan;
		data.clear();

		if(plan != null)
		{
			Iterator coas = plan.getCOAList().iterator();
			while(coas.hasNext())
			{
				Vector thisRow = new Vector();
				thisRow.add(coas.next());
				data.add(thisRow);
			}
		}

		coaList.revalidate();
		coaList.repaint();
	}

	public void clearViewer()
	{
		updateViewer(null);
	}

	public void actionPerformed(ActionEvent e)
	{
		int row = coaList.getSelectedRow();
		if(e.getActionCommand().equals("Apply") || e.getActionCommand().equals("Apply COA"))
		{
			if(row >= 0)
			{
				try{
					COA selectedCOA = (COA)((Vector)data.get(row)).firstElement();
					Control.getInstance().applyCOA(selectedCOA, lastPlan, true);

					coaList.revalidate();
					coaList.repaint();

					PlanArgument arg = new PlanArgument(PlanArgument.PLAN_ACTIVECOA);
					arg.getParameters().activeCOAs = lastPlan.getActiveCOAIndicies();
					try{
						Control.getInstance().getController(lastPlan.getId()).foreignUpdate(arg);
					}catch(RemoteException exc){
						logger.error("actionPerformed(apply) - RemoteExc sending plan_activecoa update:  "+exc.getMessage());
					}
					
					//offer to start/restart the sampler
					int choice = JOptionPane.showConfirmDialog(MainFrm.getInstance(), "Would you like to start/restart the sampler now?", "COA", JOptionPane.YES_NO_OPTION);

					CatView view = MainFrm.getInstance().getActiveView();
					if(choice == JOptionPane.YES_OPTION && view != null)
					{
						SamplingOptions dlg = new SamplingOptions(view.getPlan());
					}
				}catch(ArrayIndexOutOfBoundsException exc){} //JList sux, this is ok
			}
		}
		else if(e.getActionCommand().equals("Create COA"))
		{
			if(MainFrm.getInstance().getActiveView() == null)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "There must be a plan open before a COA can be created.");
				return;
			}
			JTextField nameField = new JTextField();
			JPanel trackChoices = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
			JCheckBox trackSched = new JCheckBox("Schedule", true);
			JCheckBox trackRes = new JCheckBox("Resources", true);
			JCheckBox trackElic = new JCheckBox("Elicited Probabilities", true);  
			trackChoices.add(trackSched);
			trackChoices.add(trackRes);
			trackChoices.add(trackElic);
			JComboBox untrackBox = new JComboBox(new String[]{"Clear above parameters", "Leave above parameters unchanged"});

			boolean accepted = MiscDialogs.showCOAOptionsBox(MainFrm.getInstance(), lastPlan, nameField, trackChoices, untrackBox);

			if(accepted)
			{
				COA newCOA = lastPlan.createCOA(nameField.getText(), trackSched.isSelected(), trackRes.isSelected(), trackElic.isSelected(), untrackBox.getSelectedIndex() == 0, true);

				Control.getInstance().applyCOA(newCOA, lastPlan, true);
				updateViewer(lastPlan);
				
				PlanArgument arg = new PlanArgument(PlanArgument.PLAN_COAS);
				PlanArgument arg1 = new PlanArgument(PlanArgument.PLAN_ACTIVECOA);
				arg.getParameters().coaList = lastPlan.getCOAList();
				arg1.getParameters().activeCOAs = lastPlan.getActiveCOAIndicies();
				
				try{
					JWBController controller = Control.getInstance().getController(lastPlan.getId());
					controller.foreignUpdate(arg);
					controller.foreignUpdate(arg1);
				}catch(RemoteException exc){
					logger.error("actionPerformed(delete) - RemoteExc sending plan_coas update:  "+exc.getMessage());
				}
			}
		}
		else if(e.getActionCommand().equals("Delete"))
		{
			if(row >= 0)
			{
				try{
					Vector toRemove = ((Vector)data.remove(row));
					lastPlan.getCOAList().remove(toRemove.firstElement());

					coaList.revalidate();
					coaList.repaint();
					
					PlanArgument arg = new PlanArgument(PlanArgument.PLAN_COAS);
					arg.getParameters().coaList = lastPlan.getCOAList();
					try{
						Control.getInstance().getController(lastPlan.getId()).foreignUpdate(arg);
					}catch(RemoteException exc){
						logger.error("actionPerformed(delete) - RemoteExc sending plan_coas update:  "+exc.getMessage());
					}
				}catch(ArrayIndexOutOfBoundsException exc){} //JList sux, this is ok
			}
		}
		else if(e.getActionCommand().equals("View Summary"))
		{
			COA selectedCOA = (COA)((Vector)data.get(row)).firstElement();
			new COASummaryDialog(MainFrm.getInstance(), selectedCOA);
		}
		else if(e.getActionCommand().equals("View Report"))
		{
			COA selectedCOA = (COA)((Vector)data.get(row)).firstElement();
			new COAReportDialog(MainFrm.getInstance(), lastPlan, selectedCOA);
		}
		else if(e.getActionCommand().equals("Compare"))
		{
			compareCOAs(null, null);
		}
	}

	/**
	 * Show COA compare options dialog (showing all coas and items), preselecting the specified COAs and items (can be null)
	 * @param selectedCOAs can be null
	 * @param selectedItems can be null
	 */
	public void compareCOAs(java.util.List<COA> selectedCOAs, java.util.List<PlanItem> selectedItems)
	{
		if(lastPlan == null)
			return;
		
		COACompareOptions opt = new COACompareOptions(MainFrm.getInstance(), lastPlan.getCOAList(), new Vector<PlanItem>(lastPlan.getItems().values()), selectedCOAs, selectedItems);
		opt.setVisible(true);

		if(opt.getWasAccepted())
		{
			try{
				int defLength = 10;
				if(lastPlan.getBayesNet() == null && lastPlan.getLoadedPlanLength() < 1)
					defLength = Integer.parseInt(JOptionPane.showInputDialog(MainFrm.getInstance(), "This model has never been sampled, \nplease enter a model length:"));
				boolean reverseColors = opt.getReverseColors();
				ProgressMonitor pm = new ProgressMonitor(MainFrm.getInstance(), "Comparing", "", 0, 100);
				COAComparator comparator = new COAComparator(lastPlan, opt.getSelectedCOAs(), opt.getSelectedItems(), defLength, this, pm, reverseColors);
				//compare dialog will pop up when comparator says its done (via listener)
			}catch(NumberFormatException exc){
				//occurs if user enters non number plan length (if prompted)
			}
		}
	}


	public void stateChanged(ChangeEvent event)
	{
		if(event.getSource() instanceof COAComparator)
		{
			COAComparator comp = (COAComparator)event.getSource();
			if(comp.isReady())
			{
				COACompareDialog cDialog = new COACompareDialog(MainFrm.getInstance(), comp);
			}
		}

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
		if(event.getButton() == MouseEvent.BUTTON3) // right click
		{
			try{
				// make right click change selection in list
				int row = coaList.rowAtPoint(event.getPoint());
				coaList.setRowSelectionInterval(row, row);

				if(coaList.getSelectedRow() >= 0)
					popup.show((Component)event.getSource(), event.getX(), event.getY());

			}catch(IllegalArgumentException exc){ //right click in empty space
//				smallPopup.show((Component)event.getSource(), event.getX(), event.getY());
			}
		}
	}

	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}


	private class CustomTable extends JTable
	{
		public CustomTable(Vector data, Vector col)
		{
			super(data, col);
			setShowGrid(false);
			this.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//			this.setCellSelectionEnabled(false);

		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

		public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int column)
		{
			java.awt.Component cell = super.prepareRenderer(renderer, row, column);
			COA[] active = lastPlan.getActiveCOA();


			COA thisCOA = (COA)data.elementAt(row).elementAt(0);
			if(thisCOA == (active[0]) || thisCOA == (active[1]) || thisCOA == active[2])
				cell.setBackground(new Color(159,255,159));
			else if(isCellSelected(row, column))
				;
			else
				cell.setBackground(Color.WHITE);

			return cell;
		}

	}



}
