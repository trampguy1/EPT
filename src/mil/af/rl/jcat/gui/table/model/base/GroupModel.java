package mil.af.rl.jcat.gui.table.model.base;

import javax.swing.table.*;

import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.util.MaskedFloat;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class GroupModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private Object[][] data;
	private String[] columns = {"name","prob","select"};
	private Class[] colclass = new Class[]{String.class,MaskedFloat.class,Boolean.class};
	private Collection sigs;
	private List selected;
	
	
	public GroupModel(Collection signals) {
		sigs = signals;
		init();
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		if(col == 2)
		{
			Boolean v = (Boolean)value;
			data[row][col] = v;
			if(v.booleanValue())
			{
				selected.add(data[row][0]);
			}
			else{
				selected.remove(data[row][0]);
			}
		}
	}
	
	private void init()
	{
		selected = new LinkedList();
		data = new Object[sigs.size()][3];
		Iterator si = sigs.iterator();
		for(int row = 0;si.hasNext();row++)
		{
			PTSignal signal = (PTSignal)si.next();
			data[row][0] = signal;
			data[row][1] = MaskedFloat.getMaskedValue(signal.getProbability()).toString();
			data[row][2] = new Boolean(false);
		}
	}
	
	public boolean getSignalSelected(Signal theSig)
	{
		for(int row=0; row<data.length; row++)
			if(((PTSignal)data[row][0]).getSignal().equals(theSig))
				return true;
		return false;
	}
	
	public void setSignalSelected(PTSignal theSig, boolean selected)
	{
		for(int row=0; row<data.length; row++)
			if(data[row][0].equals(theSig))
				setValueAt(new Boolean(selected), row, 2);
	}
	
	public List getSelectedSignals()
	{
		return selected;
	}
	public int getRowCount() {return data.length;}
	public int getColumnCount() {return columns.length;}
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	public String getColumnName(int col)
	{
		return columns[col];
	}
	public Class getColumnClass(int columnIdx){return colclass[columnIdx];}
	public boolean isCellEditable(int row, int col)
	{
		if(col == 2)return true;else return false;
	}
	
}
