/*
 * Created on Aug 23, 2005
 */
package mil.af.rl.jcat.gui.table.model;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mil.af.rl.jcat.util.MaskedFloat;

/**
 * @author dygertm
 *
 */
public class JComboRenderer implements TableCellRenderer
{

		
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		JComboBox theBox = (JComboBox)((JComboEditor)table.getCellEditor(row, column)).getComponent();
		
		theBox.setSelectedItem((MaskedFloat)value);
		
		return theBox;
	}

}
