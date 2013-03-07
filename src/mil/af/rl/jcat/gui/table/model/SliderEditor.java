package mil.af.rl.jcat.gui.table.model;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;import javax.swing.JSlider;import javax.swing.JTable;import javax.swing.table.TableCellEditor;

/** 
 * <p>Title: </p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2004</p> 
 * <p>Company: </p> 
 * @author Edward Verenich 
 * @version 1.0 
 */

public class SliderEditor extends AbstractCellEditor implements TableCellEditor{	
	private static final long serialVersionUID = 1L;	private JSlider slider = new JSlider(0,100,1);	
	public SliderEditor(JSlider s)	{		slider = s;	}	
	
	// Prepares the spinner component and returns it.	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)	{		Integer val = (Integer)value;		if(val == null)		{			slider.setValue(50);			return slider;		}		
		slider.setValue(val.intValue());		return slider;	}	
	// Enables the editor for double-clicks.	public boolean isCellEditable(EventObject evt)	{		return true;	}	
	
	// Returns the spinners current value.	public Object getCellEditorValue()	{		return new Integer(slider.getValue());	}	
}
