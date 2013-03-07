package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.Comment;
import mil.af.rl.jcat.plan.Documentation;

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

public class CommentsPanel extends JPanel implements ActionListener
{

	private static final long serialVersionUID = 1L;

	private String[] colHeaders = { "Comment", "Originator", "Date" };

	private int noEdit[] = { 2 };

	//DefaultTableModel tableModel = new DefaultTableModel(colHeaders,0);
	NoEditTableModel tableModel = new NoEditTableModel(colHeaders, 0, noEdit);

	JTable commentsTable = new JTable(tableModel);

	JButton btnAdd = new JButton();

	JButton btnRemove = new JButton();

	JScrollPane scrollPane = new JScrollPane();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	protected Documentation masterDocumentation = null;

	protected String ItemName = "";
	private static Logger logger = Logger.getLogger(CommentsPanel.class);

	public CommentsPanel(String itemName, Documentation document)
	{
		masterDocumentation = document;
		ItemName = itemName;
		try
		{
			init();
		}
		catch (Exception e)
		{
			logger.error("Constructor - Error initializing dialog: ", e);
		}
	}

	private void init() throws Exception
	{

		commentsTable.setDefaultRenderer(String.class,
				new MultiLineCellRenderer());
		commentsTable.setRowHeight(commentsTable.getRowHeight() * 5);
		commentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn colComment = commentsTable.getColumnModel().getColumn(0);
		TableColumn colDate = commentsTable.getColumnModel().getColumn(2);
		setEnabled(false);
		colComment.setCellEditor(new MultiLineCellEditor());
		colComment.setWidth(300);
		scrollPane.setViewportView(commentsTable);
		populateTable();
		this.setLayout(gridBagLayout1);

		btnAdd.setText("Add");
		btnAdd.setActionCommand("Add");
		btnAdd.addActionListener(this);

		btnRemove.setText("Remove");
		btnRemove.setActionCommand("Remove");
		btnRemove.addActionListener(this);

		this.add(scrollPane, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						6, 10, 0, 12), -76, -166));
		this.add(btnAdd, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						8, 68, 7, 0), 28, -4));
		this.add(btnRemove, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						8, 8, 7, 0), 10, -4));
	}

	public void populateTable()
	{
		LinkedList comments = masterDocumentation.getComments();
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

	public void stopCellEditing()
	{
		try
		{
			commentsTable.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}

	}

	public void saveComments()//MPG
	{
		try
		{
			commentsTable.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}
		ReadTableToDoc(tableModel);

	}

	private void ReadTableToDoc(NoEditTableModel model)//MPG
	{
		LinkedList ComList = new LinkedList();
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
					ComList.add((Comment) com.clone());
				}
				catch (CloneNotSupportedException cee)
				{
					logger.warn("readtableToDoc - CloneNotSupExc, The clone() of GraphicalDocument failed:  "+cee.getMessage());
				}
			}
		}
		else
		{
			masterDocumentation.setComments(ComList);
			return;
		}
		masterDocumentation.setComments(ComList);

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
				commentsTable.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}

			int selectedRow = commentsTable.getSelectedRow();
			try
			{
				tableModel.removeRow(selectedRow);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException ee)
			{
				logger.error("actionPerformed - AIOOBExc removing row:  "+ee.getMessage());
			}
		}
	}

}
