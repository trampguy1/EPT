package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

import mil.af.rl.jcat.plan.PlanItem;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class MultiLineCellEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = 1L;

	JTextArea textArea = new JTextArea();

    JScrollPane scrollPane = new JScrollPane();

    PlanItem planItem = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, int, int)
     */

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column)
    {
        scrollPane.setViewportView(textArea);
        //scrollPane.setHorizontalScrollBarPolicy(
        // JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

        if (isSelected)
        {
            // cell (and perhaps other cells) are selected
        }

        // Configure the component with the specified value
        textArea.setText((String) value);

        // Return the configured component
        return scrollPane;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue()
    {
        return textArea.getText();
    }
}
