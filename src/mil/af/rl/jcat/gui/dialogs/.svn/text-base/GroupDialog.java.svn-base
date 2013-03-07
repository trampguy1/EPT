package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.table.model.base.GroupModel;
import mil.af.rl.jcat.gui.table.model.base.JPGroup;
import mil.af.rl.jcat.gui.table.model.base.PTModel;
import mil.af.rl.jcat.gui.table.model.base.PTSignal;

import com.jidesoft.status.ButtonStatusBarItem;
import com.jidesoft.status.StatusBar;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GroupDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private JTable table;
	private StatusBar statusbar = new StatusBar();
	private ButtonStatusBarItem create = new ButtonStatusBarItem(null);
	private ButtonStatusBarItem cancel = new ButtonStatusBarItem(null);
	private GroupModel groupmodel;
	private PTModel pmodel;
	private JPGroup editingGroup;
	private static Logger logger = Logger.getLogger(GroupDialog.class);


	public GroupDialog(Component parent, PTModel model)
	{

		super(MainFrm.getInstance(),true);
		setSize(parent.getWidth()/2,100);
		setLocation((int) parent.getLocationOnScreen().getX() + 50,
				(int) parent.getLocationOnScreen().getY());
		pmodel = model;
		setTitle("Create a Group:");

		try
		{
			jbInit();
			pack();
			
			for(int x = 0; x < table.getColumnCount(); x++)
			{
				TableColumn column = table.getColumnModel().getColumn(x);
				if(x == 0)
					column.setPreferredWidth((int)(table.getWidth() * .75));
				else if(x == 1)
					column.setPreferredWidth((int)(table.getWidth() * .16));
				else
					column.setPreferredWidth((int)(table.getWidth() * .09));
			}
		} catch (Exception ex)
		{
			logger.error("Constructor - error initializing dialog:  "+ex.getMessage());
		}
	}

	public GroupDialog(Component parent, PTModel model, JPGroup group)
	{
		this(parent, model);
		setLocationRelativeTo(parent);
		editingGroup = group;

		//select(check) the items contained in the input group
		List groupedSignals = group.getSignals();
		Iterator allSignals = model.getSignals().iterator();

		while(allSignals.hasNext())
		{
			Object thisSignal = allSignals.next();
			if(groupedSignals.contains(thisSignal))
				groupmodel.setSignalSelected((PTSignal)thisSignal, true);
		}
		setTitle("Edit Group");
		create.setText("Edit");
		((JButton)create.getComponent()).setActionCommand("edit");
		cancel.setText("Cancel");
	}

	private void jbInit() throws Exception
	{
		create.setText("Create Group");
		((JButton) create.getComponent()).setActionCommand("create");
		create.addActionListener(this);
		cancel.setText("Cancel Group");
		((JButton) cancel.getComponent()).setActionCommand("cancel");
		cancel.addActionListener(this);
		statusbar.add(create);
		statusbar.add(cancel);
		panel.setLayout(new BorderLayout());
		panel.add(statusbar, BorderLayout.SOUTH);

		groupmodel = new GroupModel(pmodel.getSignals());
		table = new JTable(groupmodel);
		panel.add(table, BorderLayout.CENTER);

		getContentPane().add(panel);
	}

	private boolean createGroup()
	{
		List selected = groupmodel.getSelectedSignals();
		String name = null;
		if((name = JOptionPane.showInputDialog(this, "Enter a group name (cancel for default):")) == null)
			name = "Grp " + (pmodel.groupNumber() + 1);

		JPGroup g = new JPGroup(name, pmodel.getEvent());
		Iterator si = selected.iterator();
		for (; si.hasNext();)
		{
			g.addSignal(((PTSignal) si.next()).getSignal());
		}

		if (pmodel.containsGroup(g))
		{
			JOptionPane.showMessageDialog(this, "This group already exists, please choose other signals.");
			return false;
		}
		pmodel.addGroup(g);
		return true;
	}

	private void editGroup(JPGroup group)
	{
		//give option to rename
		String name = group.getName();
		if((name = JOptionPane.showInputDialog(MainFrm.getInstance(), "Enter a new group name (cancel to leave unchanged):", name)) == null)
			name = group.getName();

		pmodel.removeGroup(group);
		group.removeAllSignals();    	

		Iterator selected = groupmodel.getSelectedSignals().iterator();
		while(selected.hasNext())
			group.addSignal(((PTSignal)selected.next()).getSignal());
		group.setName(name);
		pmodel.addGroup(group);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("create"))
		{
			boolean r = createGroup();
			if (r)
			{
				groupmodel = null;
				table = null;
				dispose();
			} else
			{
				return;
			}
		} else if (e.getActionCommand().equals("cancel"))
		{
			groupmodel = null;
			table = null;
			dispose();
		}
		else if(e.getActionCommand().equals("edit"))
		{
			editGroup(editingGroup);
			dispose();
		}
	}
}
