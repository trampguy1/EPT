package mil.af.rl.jcat.gui.table.model;


import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mil.af.rl.jcat.util.MaskedFloat;

import org.apache.log4j.Logger;


/**
 
 * <p>Title: SpinnerRenderer.java</p>
 
 * <p>Description: Used to render the spinner in a JTable cell</p>
 
 * <p>Copyright: Copyright (c) 2004</p>
 
 * <p>Company: C3I Associates</p>
 
 * @author Edward Verenich
 
 * @version 1.0
 
 */

public class SpinnerRenderer implements TableCellRenderer
{
	private static Logger logger = Logger.getLogger(SpinnerRenderer.class);
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		
		CustomSpinner theBox = (CustomSpinner)((SpinnerEditor)table.getCellEditor(row, col)).getComponent();

		// set the value
		try{
			MaskedFloat val = (MaskedFloat)value;
			theBox.setValue(val);
		}catch(Exception e){
			logger.error("getTableCellRendererComponent - Error setting table cell value:  "+e.getMessage());
		}
		return theBox;
	}
		
}

