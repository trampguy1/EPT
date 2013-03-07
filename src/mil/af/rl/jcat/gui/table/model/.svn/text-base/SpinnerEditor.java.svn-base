package mil.af.rl.jcat.gui.table.model;


import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import mil.af.rl.jcat.gui.table.model.base.PTSignal;
import mil.af.rl.jcat.gui.table.model.event.SpinnerEventListener;
import mil.af.rl.jcat.util.MaskedFloat;

/**
 
 * <p>Title: SpinnerEditor.java</p>
 
 * <p>Description: Spinner editor that can be used in the JTable</p>
 
 * <p>Copyright: Copyright (c) 2004</p>
 
 * <p>Company: C3I Associates</p>
 
 * @author Edward Verenich
 
 * @version 1.0
 
 */

public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor
{

	private static final long serialVersionUID = 1L;
	final CustomSpinnerModel model = new CustomSpinnerModel(MaskedFloat.getMaskedValue(0.75f),MaskedFloat.getMaskedValue(0.00f),MaskedFloat.getMaskedValue(1.00f),MaskedFloat.getMaskedValue(0.01f));
	CustomSpinner spinner = null;

	
	// Initializes the spinner.
	public SpinnerEditor(TableModel m, int r, int c, PTSignal sig)
	{
		spinner = new CustomSpinner(model, sig);
		spinner.addChangeListener(new SpinnerEventListener(m,r,c));
		spinner.addMouseWheelListener(new SpinnerEventListener(m,r,c));
	}
	
	public SpinnerEditor(TableModel m, int r, int c)
	{
		spinner = new CustomSpinner(model);
		spinner.addChangeListener(new SpinnerEventListener(m,r,c));
		spinner.addMouseWheelListener(new SpinnerEventListener(m,r,c));
	}
	
	// Prepares the spinner component and returns it.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		spinner.setValue((MaskedFloat)value);
		return spinner;
	}
	
	public CustomSpinner getComponent()
	{
		return spinner;
	}
	
	// Enables the editor for double-clicks.
	public boolean isCellEditable(EventObject evt)
	{
		if (evt instanceof MouseEvent)
		{
			return ((MouseEvent)evt).getClickCount() >= 1;
		}
		
		return true;
	}
	
	// Returns the spinners current value.
	public Object getCellEditorValue()
	{
		return spinner.getValue();
	}
	
}

