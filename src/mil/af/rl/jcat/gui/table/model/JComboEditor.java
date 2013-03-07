/*
 * Created on Aug 23, 2005
 */
package mil.af.rl.jcat.gui.table.model;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import mil.af.rl.jcat.gui.table.model.event.JComboEventListener;
import mil.af.rl.jcat.util.MaskedFloat;


/**
 * @author dygertm
 *
 */
public class JComboEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = 1L;

	MaskedFloat[] probList = MaskedFloat.getMaskedValues();
	final JComboBox box = new JComboBox(probList);


	public JComboEditor(TableModel m, int r, int c)
	{
		box.addItemListener(new JComboEventListener(m,r,c));
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		return box;
	}

	public JComboBox getComponent()
	{
		return box;
	}

	public Object getCellEditorValue() 
	{
		return box.getSelectedItem();
	}


}
