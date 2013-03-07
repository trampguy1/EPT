/*
 * Created on Aug 13, 2004
 * 
 */
package mil.af.rl.jcat.gui.table.model;

import javax.swing.table.DefaultTableModel;

public class NoEditTableModel extends DefaultTableModel
{

	private static final long serialVersionUID = 1L;
	private int[] iUneditable = null;

    public NoEditTableModel(Object[] headers, int count, int[] iu)
    {
        super(headers, count);
        iUneditable = iu;
    }

    public NoEditTableModel(String[] headers, int count, int[] iu)
    {
        super(headers, count);
        iUneditable = iu;
    }

    public boolean isCellEditable(int row, int col)
    {
    	if(iUneditable == null)
        	return false;
    	
        for (int i = 0; i < iUneditable.length; i++)
        {
            if (iUneditable[i] == col)
            {
                return false;
            }
        }
        
        return true;
    }
}