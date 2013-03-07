package mil.af.rl.jcat.gui.table;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.event.*;

import mil.af.rl.jcat.gui.table.model.base.PTModel;
import mil.af.rl.jcat.gui.table.model.base.PTSignal;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


/**
 * <p>Title: PTTable.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PTTable extends JTable implements  MouseListener, ActionListener{
	
	private static final long serialVersionUID = 1L;
	protected CellEditorModel emodel;
	protected PTModel tmodel = null;
	private JPopupMenu pmenu = null;
	private JPopupMenu gmenu = null;
	private Color usedColor = new Color(159,255,159);
	private Color usedSelColor = new Color(189,255,189);
	private Color unusedColor = new Color(148,197,237);
	private Color unusedSelColor = new Color(168,217,255);

	
	public PTTable(PTModel tm) {
		//super(tm,null,null);
		super(tm);
		pmenu = getPopupMenu();
		gmenu = getGroupPopup();
		//this.getSelectionModel().addListSelectionListener(this);
		this.addMouseListener(this);
		this.emodel = null;
		this.tmodel = tm;
		this.setRowHeight(20);
		this.getTableHeader().setReorderingAllowed(false);
	}
	
	
	@Override  // to provide tooltip for group names in column header (in case theyre cut off)
	protected JTableHeader createDefaultTableHeader()
	{
		JTableHeader th = new JTableHeader(getColumnModel()){
			
			@Override
			public String getToolTipText(MouseEvent event)
			{
				int columnNum = columnAtPoint(event.getPoint());
				if(columnNum > 1)
					return  getColumnName(columnNum);
				else
					return super.getToolTipText(event);
			}
			
		};
		
		return th;
	}
	
	public void setModel(PTModel mod)
	{
		super.setModel(mod);
		this.emodel = null;
		this.tmodel = mod;
	}
	
	/**
	 * Method sets the RowEditorModel, not that the model must be set before the
	 * table is displayed, either in the constructor or using this method.
	 * @param rm RowEditorModel
	 */
	public void setRowEditorModel(CellEditorModel rm)
	{
		this.emodel = rm;
	}
	/**
	 * Returns the current RowEditorModel for this table.
	 * @return RowEditorModel
	 */
	public CellEditorModel getRowEditorModel()
	{
		return this.emodel;
	}
	/**
	 * Returns a TableCellEditor for a specified cell
	 * @param row int
	 * @param col int
	 * @return TableCellEditor editor
	 */
	public TableCellEditor getCellEditor(int row, int col)
	{
		TableCellEditor temp = null;
		if(tmodel != null)
		{
			temp = tmodel.getCellEditor(row,col);
		}
		if(temp != null)
		{
			return temp;
		}
		return super.getCellEditor(row,col);
	}
	
	public TableCellRenderer getCellRenderer(int row, int col)
	{
		TableCellRenderer r = null;
		
		if(tmodel != null)
		{
			r = tmodel.getCellRenderer(row,col);
		}
		if(r != null)
		{
			return r;
		}
		return super.getCellRenderer(row,col);
	}
	
	/**
	 * Overload the JTable method to change the color of our cells
	 * @param renderer TableCellRenderer
	 * @param row int
	 * @param column int
	 * @return Component
	 */
	public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int column)
	{
		
		java.awt.Component cell = super.prepareRenderer(renderer, row, column);
		
//		if(!isCellSelected(row,column))
//		{
//			if(isEnabled() && getModel().isCellEditable(row,column)) // dont know what this did
//			{
//				if(column == 0 && row < (this.getRowCount() - 1))
//				{
//					cell.setBackground(new Color(255, 255, 153));
//					cell.setFont(cell.getFont().deriveFont(Font.BOLD));
//				}
//			}
			//signal name cells
			if(column == 0 && row < (this.getRowCount() - 1))
			{
				PTSignal pSig = ((PTSignal)tmodel.getValueAt(row, column));
				
				if(pSig.isUsedByMechanism())
				{
					if(!isCellSelected(row, column))
						cell.setBackground(usedColor); //greenish color
					else
						cell.setBackground(usedSelColor);
				}
				else
				{
					if(!isCellSelected(row, column))
						cell.setBackground(unusedColor); //blueish color
					else
						cell.setBackground(unusedSelColor);
				}
				
//				if(cell instanceof JComponent)
//					((JComponent)cell).setToolTipText(pSig.getSignalName());
			}
			//last row (combined)
			if(row == (this.getRowCount() - 1))
			{
				cell.setBackground(unusedColor); //blueish color
				cell.setFont(cell.getFont().deriveFont(Font.BOLD));
//				if(cell instanceof JComponent)
//					((JComponent)cell).setToolTipText(null);
			}
//		}
			//always black text
			cell.setForeground(Color.BLACK);
		
		//do this even if the cell is selected
//		if(row == (this.getRowCount() - 1) && column == 0)
//		{
//			cell.setBackground(new Color(148, 197, 237)); //blueish color
//			cell.setFont(cell.getFont().deriveFont(Font.BOLD));
//		}
		
		return cell;
	}
	
	private JPopupMenu getPopupMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem remove = new JMenuItem("remove signal");
		remove.addActionListener(this);
		remove.setActionCommand("remove");
		JMenuItem invert = new JMenuItem("invert signal");
		invert.setActionCommand("invert");
		invert.addActionListener(this);
		JMenuItem rename = new JMenuItem("rename signal");
		rename.setActionCommand("rename");
		rename.addActionListener(this);
		menu.add(invert);
		menu.add(rename);
		menu.add(remove);
		return menu;
	}
	
	private JPopupMenu getGroupPopup()
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem removeGroup = new JMenuItem("remove group");
		removeGroup.setActionCommand("remgroup");
		removeGroup.addActionListener(this);
		JMenuItem editGroup = new JMenuItem("edit group");
		editGroup.setActionCommand("edtgroup");
		editGroup.addActionListener(this);
		menu.add(removeGroup);
		menu.add(editGroup);
		return menu;
	}
	/**
	 * Handle events from the signal popup menu
	 * @param e ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		int row = this.getSelectedRow();
		int col = this.getSelectedColumn();
		// forward the signal to the model
		this.tmodel.setValueAt(e.getActionCommand(), row, col);
	}
	
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 *
	 * @param e MouseEvent
	 * @todo Implement this java.awt.event.MouseListener method
	 */
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			int row = rowAtPoint(e.getPoint());
			int col = columnAtPoint(e.getPoint());
			
			this.changeSelection(row, col, false, false);
			int rowCount = this.getSelectedRows().length;
			
			if(rowCount == 1 && row < this.getRowCount() - 1)
			{
				if(col == 0)
					pmenu.show(this, e.getX(), e.getY());
				else if(col > 1)
					gmenu.show(this, e.getX(), e.getY());
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	
}
