package mil.af.rl.jcat.gui.table.model;
import java.awt.Component;

import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/** 
 * <p>Title: SliderRenderer.java</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2004</p> 
 * <p>Company: </p> 
 * @author Edward Verenich 
 */

public class SliderRenderer extends JSlider implements TableCellRenderer{	
	private static final long serialVersionUID = 1L;	public SliderRenderer()	{		super();	}	
	
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,			boolean hasFocus, int row, int col)	{		Integer val = (Integer)value;		
		if(val == null)		{			this.setValue(50);			return this;		}		
		this.setValue(val.intValue());		
		return this;	}		
}
