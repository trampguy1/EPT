package mil.af.rl.jcat.gui.table.model;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Edward Verenich
 * @version 1.0
 */

public class SliderEditor extends AbstractCellEditor implements TableCellEditor
	private static final long serialVersionUID = 1L;
	public SliderEditor(JSlider s)
	
	// Prepares the spinner component and returns it.
		slider.setValue(val.intValue());
	// Enables the editor for double-clicks.
	
	// Returns the spinners current value.
}