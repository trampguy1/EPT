package mil.af.rl.jcat.gui.table.model;

import java.text.DecimalFormat;

import javax.swing.table.DefaultTableCellRenderer;



public class FloatFormatRenderer extends DefaultTableCellRenderer
{
	private DecimalFormat formatter = new DecimalFormat("#0.###");
	
	public void setValue(Object value)
	{
		if ((value != null) && (value instanceof Float))
		{
			Float numberValue = (Float) value;
			value = formatter.format(numberValue);
		}
		
		super.setValue(value);
	} 


}
