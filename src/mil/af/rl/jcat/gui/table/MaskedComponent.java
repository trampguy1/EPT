/*
 * Created on Aug 29, 2005
 */
package mil.af.rl.jcat.gui.table;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.dialogs.EventValueListener;
import mil.af.rl.jcat.gui.table.model.JComboEditor;
import mil.af.rl.jcat.gui.table.model.JComboRenderer;
import mil.af.rl.jcat.gui.table.model.SpinnerEditor;
import mil.af.rl.jcat.gui.table.model.SpinnerRenderer;
import mil.af.rl.jcat.util.MaskedFloat;

/*
 * Used to wrap a JSpinner or JComboBox (depending on the mode) in a one cell table
 *  so that the cell editor and renderer can simple be swapped to change the look based on mode
 *  This is to utilize MaskedFloat outside of a table
 * -used for 'simple probablity mode'  
 */

public class MaskedComponent extends JTable
{
	private static final long serialVersionUID = 1L;
	TableCellEditor editor = null;
	TableCellRenderer renderer = null;
	
	public MaskedComponent(MaskedFloat value)
	{
		super(1, 1);
		this.setValueAt(value, 0, 0);
		this.setRowHeight(20);  //TODO:  height shouldn't be fixed
	}
	
	public Object getValue()
	{
		return getValueAt(0,0);
	}
	
	public void setValue(MaskedFloat value)
	{
		setValueAt(value, 0,0);
	}
	
	public TableCellEditor getCellEditor()
	{
		return getCellEditor(0,0);
	}
	
	@Override  //maintains the disabled (greyed out) look of this component when disabled
	public void setEnabled(boolean enab)
	{
		super.setEnabled(enab);
		getCellRenderer(0,0).getTableCellRendererComponent(this, getValueAt(0,0), false, false, 0, 0).setEnabled(enab);
	}

	public TableCellEditor getCellEditor(int r, int c)
	{
		if(editor != null)
			return editor;
		else
		{
			if(MainFrm.getInstance().isSimpleProbMode())
				return editor = new JComboEditor(this.getModel(), r, c);
			else
				return editor = new SpinnerEditor(this.getModel(), r, c);
		}
	}
	
	public TableCellRenderer getCellRenderer(int r, int c)
	{
		if(renderer != null)
			return renderer;
		else
		{
			if(MainFrm.getInstance().isSimpleProbMode())
				return renderer = new JComboRenderer();
			else
				return renderer = new SpinnerRenderer();
		}
	}

	public void addChangeListener(EventValueListener listener)
	{
		if(this.getCellEditor() instanceof JComboEditor)
			((JComboEditor)getCellEditor()).getComponent().addItemListener(listener);
		else if(this.getCellEditor() instanceof SpinnerEditor)
			((SpinnerEditor)getCellEditor()).getComponent().addChangeListener(listener);
	}
}
