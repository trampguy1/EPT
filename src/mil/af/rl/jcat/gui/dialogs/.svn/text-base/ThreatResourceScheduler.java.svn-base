package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MultiMap;

public class ThreatResourceScheduler extends JDialog implements ActionListener {

	private String [] headers = {"Time", "Available Resources"};
	private int [] noEdit = {0, 1};
	private JTable table = null; 
	private NoEditTableModel tableModel;
	private SpinnerNumberModel timeModel = new SpinnerNumberModel(0, 0,
            Integer.MAX_VALUE, 1);
	private SpinnerNumberModel allocationModel = new SpinnerNumberModel(0, 0,
            Integer.MAX_VALUE, 1);
	private PlanItem planItem;
	private MultiMap<Integer, ResourceAllocation> threat = new MultiMap<Integer, ResourceAllocation>();
	private JWBController control;
	private JWBShape shape;
    private AbstractPlan plan;
	/**
	 * Default to rid myself of a warning
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ThreatResourceScheduler.class);

	
	public ThreatResourceScheduler(Frame parent, JWBShape item, JWBController pcontroller, AbstractPlan plan)
	{
		super(parent, "Assign Threat Resources");
        this.plan = plan;
		shape = item;
		planItem = plan.getItem((Guid)item.getAttachment());
		// dont want to modify the items threat-map directly, or we can't cancel out modifications
		threat = (MultiMap)planItem.getThreatResources().clone();
		control = pcontroller;
		initializeComonents();
		setLocationRelativeTo(parent);
	}
	
	private void initializeComonents()
	{
		//Setup the panels and the layout
		tableModel = new NoEditTableModel(headers, 0, noEdit);
		JPanel btnPanel = new JPanel();
		JPanel okPane = new JPanel();
		okPane.setLayout(new BoxLayout(okPane, BoxLayout.LINE_AXIS));
		btnPanel.setLayout(new GridLayout(7,1));
		Vector<String> names = new Vector<String>();
        names.add("Time");
        names.add("Allocated Resources");
		table = new JTable(tableModel);
		tableModel.setColumnIdentifiers(names);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		this.add(btnPanel, BorderLayout.EAST);
		this.add(okPane, BorderLayout.SOUTH);
		
		//Adding buttons to button pane
		JButton allocate = new JButton("Allocate at Time");
		allocate.addActionListener(this);
		JButton deallocate = new JButton("DeAllocate at Time");
		deallocate.addActionListener(this);
		JButton all = new JButton("DeAllocate All");
		all.addActionListener(this);
		
		//Put the buttons in the panel
		JLabel time = new JLabel("Time");
		JSpinner stime = new JSpinner(timeModel);
		JLabel allocation = new JLabel("Allocation");
		JSpinner sallocation = new JSpinner(allocationModel);
		btnPanel.add(time);
		btnPanel.add(stime);
		btnPanel.add(allocation);
		btnPanel.add(sallocation);
		btnPanel.add(allocate);
		btnPanel.add(deallocate);
		btnPanel.add(all);
		
		//Create the ok pane
		JButton ok = new JButton("OK");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		okPane.add(ok);
		okPane.add(cancel);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(super.getParent());
		this.setModal(true);
		redrawTable();
        this.setVisible(true);
	}
	
	public void redrawTable()
    {
        tableModel.getDataVector().clear();
        Object[] t = threat.keySet().toArray();
        Arrays.sort(t);
        Set ent = threat.entrySet();
        for (int i = 0; i < t.length; i++)
        {
            Vector entry = new Vector();
            entry.add(((Integer) t[i]));
            String allocText = "";
            
            Iterator<ResourceAllocation> allos = threat.get((Integer)t[i]).iterator();
            while(allos.hasNext())
            {
            	allocText += allos.next().getAllocated() + ", ";
            }
            entry.add(allocText.trim().substring(0, allocText.trim().length()-1));
            tableModel.addRow(entry);
        }
    }

	public void actionPerformed(ActionEvent e) 
	{
		String event = e.getActionCommand();
		if(event.equals("Allocate at Time"))
		{
			//TODO:  Craig.. I changed the 'null' to 'new Guid()' here, donno why u wanted a null ID, changed it so it could save to file
			threat.put((Integer)this.timeModel.getNumber(), new ResourceAllocation(new Guid(), (Integer)this.allocationModel.getNumber()));
			this.redrawTable();
		}
		else if(event.equals("DeAllocate at Time"))
		{
			int row = table.getSelectedRow();
			if(row != -1)
			{
				Integer val = (Integer) tableModel.getValueAt(row, 0);
				this.threat.remove(val);
				tableModel.removeRow(row);
				this.redrawTable();
			}
		}
		else if(event.equals("DeAllocate All"))
		{
			threat.clear();
	        for (int r = table.getRowCount(); 0 < r; r--)
	            tableModel.removeRow(r - 1);
		}
		else if(event.equals("OK"))
		{
			planItem.setThreatResources(this.threat);
             //Iterate through ahapes that are associated with this mechanism
//            List<JWBUID> shapes = plan.getShapeMapping(planItem.getGuid());
//            for(JWBUID shapeId : shapes)
//            {
//                JWBShape shape = control.getShape(shapeId);
    			try{
////    				shape.removeMarkup('R');
////    				if(planItem.getThreatResources().size() > 0)
////    					shape.addMarkup('R');
//    				
//    				control.putShapeExternalUpdate(shape);
    				control.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, planItem, false));
    			}catch(RemoteException exc){
    				logger.error("actionPerformed(ok) - RemoteExc updating plan item:  "+exc.getMessage());
    			}
//            }
			dispose();
		}
		else if(event.equals("Cancel"))
		{
			dispose();
		}
	}
}
