package mil.af.rl.jcat.gui.dialogs;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import mil.af.rl.jcat.gui.MainFrm;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import mil.af.rl.jcat.gui.documentationpanels.GraphicalDocPanel;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.Comment;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class TableEntryDialog extends JDialog implements ActionListener, WindowListener
{

	private static final long serialVersionUID = 1L;
	private String[] colHeaders = { "Comment", "Originator", "Date" };
	private int noEdit[] = { 2 };

	NoEditTableModel tableModel = new NoEditTableModel(colHeaders, 0, noEdit);
	JTable comTable = new JTable(tableModel);
	JScrollPane scrollPane = new JScrollPane();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	//will get values in the constructor
	MainFrm frame;
	int rowIndex;
	GraphicalDocPanel GDPanel;
	ArrayList comments = new ArrayList();
	private static Logger logger = Logger.getLogger(TableEntryDialog.class);


	public TableEntryDialog(int index)
	{
		super(MainFrm.getInstance(), "Comments Table", true);

		frame = MainFrm.getInstance();
		rowIndex = index;
		GDPanel = GraphicalDocPanel.getInstance();
		comments = (ArrayList) GDPanel.getComments(rowIndex).clone();

		try
		{
			init();
			setLocationRelativeTo(frame);
		}
		catch (Exception e)
		{
			logger.error("Constructor - Error initializing dialog: ", e);
		}
	}

	private void init() throws Exception
	{

		this.setSize(425, 375);
		this.getContentPane().setLayout(gridBagLayout1);
		this.addWindowListener(this);
		setEnabled(false);
		scrollPane.setViewportView(comTable);
		JButton btnAdd = new JButton();
		btnAdd.setText("Add");
		btnAdd.setActionCommand(btnAdd.getText());
		btnAdd.addActionListener(this);
		JButton btnRemove = new JButton();
		btnRemove.setText("Remove");
		btnRemove.setActionCommand(btnRemove.getText());
		btnRemove.addActionListener(this);

		this.getContentPane().add(
				scrollPane,
				new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(6, 10, 0, 12), -76, -166));
		this.getContentPane().add(
				btnAdd,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(8, 68, 7, 0), 28, -4));
		this.getContentPane().add(
				btnRemove,
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(8, 8, 7, 0), 10, -4));
	}

	public void populateTable()
	{
		if (comments.size() == 0)
		{
			return;
		}
		for (int i = 0; i < comments.size(); i++)
		{
			Comment currentCom = (Comment) comments.get(i);
			Vector vEntry = new Vector();
			vEntry.add(currentCom.getComment());
			vEntry.add(currentCom.getOriginator());
			vEntry.add(currentCom.getDate());
			tableModel.addRow(vEntry);
		}

	}

	public void saveTable()
	{
		ArrayList comList = new ArrayList();
		Comment com = new Comment();

		Vector tblData = tableModel.getDataVector();

		int i = tableModel.getRowCount();

		if (tableModel.getRowCount() != 0)
		{
			for (int j = 0; j < i; j++)
			{
				Vector v = (Vector) tblData.get(j);
				Object[] arO = v.toArray();
				com.setComment(arO[0].toString());
				com.setOriginator(arO[1].toString());
				com.setDate(arO[2].toString());

				try
				{
					comList.add((Comment) com.clone());
				}
				catch (CloneNotSupportedException cee)
				{
					logger.error("The clone() of GraphicalDocument did not work.  The Table did not save.");
				}
			}
		}
		else
		{
			GDPanel.setComments(rowIndex, comList);
			return;
		}
		GDPanel.setComments(rowIndex, comList);

	}

	public void windowClosing(WindowEvent e)
	{
		//need to insert the stop cell editing code.

		try
		{
			comTable.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}

		saveTable();
	}

	public void windowOpened(WindowEvent e)
	{
		populateTable();
	}

	public void windowClosed(WindowEvent e)
	{

	}

	public void windowIconified(WindowEvent e)
	{

	}

	public void windowDeiconified(WindowEvent e)
	{

	}

	public void windowActivated(WindowEvent e)
	{

	}

	public void windowDeactivated(WindowEvent e)
	{

	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Add"))
		{
			Vector vTmp = new Vector();
			vTmp.add("");
			vTmp.add("");
			Date currentDate = new Date();
			DateFormat currentDateFormat = DateFormat.getDateInstance();
			String strDate = currentDateFormat.format(currentDate);
			vTmp.add(strDate);
			tableModel.addRow(vTmp);
		}

		if (e.getActionCommand().equals("Remove"))
		{
			try
			{
				comTable.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}

			int selectedRow = comTable.getSelectedRow();
			try
			{
				tableModel.removeRow(selectedRow);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException ee)
			{
				logger.warn("actionPerformed - ArrayIndexOutofBoundsException trying to remove a row");
			}
		}
	}
}
