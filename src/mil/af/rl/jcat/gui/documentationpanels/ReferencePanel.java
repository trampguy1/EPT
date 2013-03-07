package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.plan.Documentation;
import mil.af.rl.jcat.util.Resource;

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

public class ReferencePanel extends JPanel implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 1L;

	public ReferencePanel()
	{
		try
		{
			init();
		}
		catch (Exception ex)
		{
			logger.error("Constructor - error initializing dialog: ",ex);
		}
	}

	private String[] colHeaders = { "Reference", "Name", "Originator", "Date" };

	private int noEdit[] = { 3 };

	NoEditTableModel tableModel = new NoEditTableModel(colHeaders, 0, noEdit);

	JTable tblResources = new JTable(tableModel);

	JButton btnAdd = new JButton();

	JButton btnRemove = new JButton();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	JScrollPane scrollPane = new JScrollPane();

	protected Documentation masterDocumentation = null;

	protected String ItemName = "";
	private static Logger logger = Logger.getLogger(ReferencePanel.class);

	public ReferencePanel(String itemName, Documentation document)
	{
		masterDocumentation = document;
		ItemName = itemName;
		try
		{
			init();
		}
		catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog:", e);
		}
	}

	private void init() throws Exception
	{
		tblResources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(tblResources);
		populateTable();
		TableColumn col0 = tblResources.getColumnModel().getColumn(0);
		Resource r = new Resource();
		col0.setCellEditor(new ReferenceCellEditor(r));
		this.setLayout(gridBagLayout1);

		btnAdd.setText("Add");
		btnAdd.setActionCommand("Add");
		btnAdd.addActionListener(this);

		tblResources.addMouseListener(this);

		btnRemove.setText("Remove");
		btnRemove.setActionCommand("Remove");
		btnRemove.addActionListener(this);

		this.add(scrollPane, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 17, 0, 19), -90, -176));
		this.add(btnAdd, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						8, 68, 7, 0), 28, -4));
		this.add(btnRemove, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						8, 8, 7, 0), 10, -4));

	}

	public void populateTable()
	{
		LinkedList resources = masterDocumentation.getResources();
		for (int i = 0; i < resources.size(); i++)
		{
			Resource currentRes = (Resource) resources.get(i);
			addRow(currentRes);
		}
	}

	public void stopCellEditing()
	{
		try
		{
			tblResources.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}

	}

	public void addRow(Resource res)
	{
		Vector vEntry = new Vector();
		vEntry.add(res.getLocation());
		vEntry.add(res.getName());
		vEntry.add(res.getOriginator());
		vEntry.add(res.getDate());
		tableModel.addRow(vEntry);
	}

	public File fileSelect()
	{
		JFileChooser fc = new JFileChooser();
		JFrame fileOpen = new JFrame();

		fc.showOpenDialog(fileOpen);
		File selFile = fc.getSelectedFile();
		return selFile;
	}

	public void saveResources()//MPG
	{
		try
		{
			tblResources.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}
		ReadTableToDoc(tableModel);

	}

	private void ReadTableToDoc(NoEditTableModel model)//MPG
	{
		LinkedList ResList = new LinkedList();
		Resource res = new Resource();

		Vector tblData = tableModel.getDataVector();

		int i = tableModel.getRowCount();

		if (tableModel.getRowCount() != 0)
		{
			for (int j = 0; j < i; j++)
			{
				Vector v = (Vector) tblData.get(j);
				Object[] arO = v.toArray();

				res.setLocation(arO[0].toString());
				res.setName(arO[1].toString());
				res.setOriginator(arO[2].toString());
				res.setDate(arO[3].toString());

				try
				{
					ResList.add((Resource) res.clone());
				}
				catch (CloneNotSupportedException cee)
				{
					logger.error("readTableToDoc - CloneNotSupportedExc:  "+cee.getMessage());
				}
			}
		}
		else
		{
			masterDocumentation.setResources(ResList);
			return;
		}
		masterDocumentation.setResources(ResList);

	}

	//Aciton Listener
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Add"))
		{
			Resource tmp = new Resource();
			Date currentDate = new Date();
			DateFormat currentDateFormat = DateFormat.getDateInstance();
			String strDate = currentDateFormat.format(currentDate);
			tmp.setDate(strDate);
			tmp.setLocation("");
			tmp.setName("");
			tmp.setOriginator("");
			addRow(tmp);
		}
		else if (e.getActionCommand().equals("Remove"))
		{
			try
			{
				tblResources.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}

			int selectedRow = tblResources.getSelectedRow();
			try
			{
				tableModel.removeRow(selectedRow);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException ee)
			{
				logger.error("actionPerformed - AIOOBExc removing table row:  "+ee.getMessage());
			}
		}
	}

	//MouseListeners
	public void mouseReleased(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
		/*
		 * if(e.getClickCount()>1){ resourceTable.getSelectedRow(); String path =
		 * (String)resourceModel.getValueAt(resourceTable.getSelectedRow(),0);
		 * System.load(path); } if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) !=
		 * 0) { }
		 */
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

}
