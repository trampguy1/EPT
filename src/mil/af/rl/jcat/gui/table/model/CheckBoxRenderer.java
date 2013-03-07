package mil.af.rl.jcat.gui.table.model;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/** 
 * <p>Title:</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2004</p> 
 * <p>Company: </p> 
 * @author Edward Verenich 
 * @version 1.0 
 */
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer{	
	private static final long serialVersionUID = 1L;				public CheckBoxRenderer()	{		super();	}	
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int col)	{		Boolean val = (Boolean)value;		
		// set the value		if(val == null)			this.setSelected(false);		else			this.setSelected(val.booleanValue());		
		return this;	}	
}
