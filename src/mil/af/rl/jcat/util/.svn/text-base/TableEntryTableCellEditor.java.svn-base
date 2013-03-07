package mil.af.rl.jcat.util;

import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;
import javax.swing.JTextArea;
import javax.swing.JTable;
import mil.af.rl.jcat.gui.dialogs.TableEntryDialog;
import java.awt.Component;
import javax.swing.JComponent;

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
public class TableEntryTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
	
	private static final long serialVersionUID = 1L;
	JComponent component = new JTextArea();
    JTable theTable;

    public TableEntryTableCellEditor()
    {

    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex)
    {
        ((JTextArea) component).setText("Comments Table");

        theTable = table;

        TableEntryDialog g = new TableEntryDialog(rowIndex);
        g.setVisible(true);

        return component;

    }

    public Object getCellEditorValue()
    {
        return ((JTextArea) component).getText();
    }

}
