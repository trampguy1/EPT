package mil.af.rl.jcat.gui.table;

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * Title: RowEditorModel.java
 * </p>
 * <p>
 * Description: Serves as a holder for different TableCellEditors, where each
 * editor uses a column number as a key, <K,V>idea.
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

public class CellEditorModel
{

    private Hashtable editors;

    private Hashtable renderers;

    public CellEditorModel()
    {
        editors = new Hashtable();
        renderers = new Hashtable();
    }

    public int getModelSize()
    {
        return renderers.size();
    }

    /**
     * Adds a TableCellEditor for a given row.
     *
     * @param row
     *            int
     * @param e
     *            TableCellEditor
     */
    public void addEditor(int row, int col, TableCellEditor e)
    {
        editors.put(row + "," + col, e);
    }

    public void addRenderer(int row, int col, TableCellRenderer e)
    {
        renderers.put(row + "," + col, e);
    }

    /**
     * Removes a TableCellEditor from the RowEditorModel
     *
     * @param row
     *            int
     */
    public void removeEditor(int row, int col)
    {
        editors.remove(row + "," + col);
    }

    public void removeRenderer(int row, int col)
    {
        renderers.remove(row + "," + col);
    }

    /**
     * Returns a TableCellEditor at a given row
     *
     * @param row
     *            int
     * @return TableCellEditor
     */
    public TableCellEditor getEditor(int row, int col)
    {
        return (TableCellEditor) editors.get(row + "," + col);
    }

    public TableCellRenderer getRenderer(int row, int col)
    {
        return (TableCellRenderer) renderers.get(row + "," + col);
    }

    public String toString()
    {
        return editors+"\n"+renderers;
    }

}
