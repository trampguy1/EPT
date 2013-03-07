package mil.af.rl.jcat.util;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.Component;

import javax.swing.JTextArea;
import mil.af.rl.jcat.gui.dialogs.TextEntryDialog;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class TextEntryTableCellEditor extends AbstractCellEditor implements TableCellEditor
{

	private static final long serialVersionUID = 1L;
	NoEditTableModel m;
	JComponent component = new JTextArea();
	
    public TextEntryTableCellEditor(NoEditTableModel model)
    {
        m = model;
    }
    

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex)
    {

        ((JTextArea) component).setText((String) value);

        TextEntryDialog g = new TextEntryDialog((JTextArea) component);
        g.setVisible(true);

        return component;
    }

    public Object getCellEditorValue()
    {
        return ((JTextArea) component).getText();
    }

}
