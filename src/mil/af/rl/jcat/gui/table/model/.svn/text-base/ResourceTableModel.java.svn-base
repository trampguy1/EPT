package mil.af.rl.jcat.gui.table.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.util.Guid;


public class ResourceTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private String[] fieldNames = {"Name", "# Allocated", "Contingent"};
	Vector<ResourceAllocation> data = new Vector<ResourceAllocation>();
	
	public ResourceTableModel()
	{
		
	}
	
	/**
	 * Creates a new model loading given current values
	 * @param currentRes HashMap of resources to load
	 */
	public ResourceTableModel(HashMap<Guid, ResourceAllocation> currentRes)
	{
		if(currentRes != null && currentRes.size() > 0)
		{
			Iterator guids = currentRes.keySet().iterator();
			while(guids.hasNext())
			{
				Guid name = (Guid)guids.next();
				ResourceAllocation resAlloc = currentRes.get(name);
				data.add(resAlloc);
			}
		}
	}
	
	public void addResource(String name, Integer value, boolean contingent)
	{
		ResourceAllocation newData = new ResourceAllocation(new Guid(), value, name, contingent);
		
		data.add(newData);
	}
	
	public void removeResource(int selectedRow)
	{
		data.remove(selectedRow);
	}
	
	public String getColumnName(int col)
	{
		if(fieldNames.length > col)
			return fieldNames[col];
		else
			return "";
	}
	
	public int getColumnCount()
	{
		return fieldNames.length;
	}

	public int getRowCount()
	{
		return data.size();
	}

	public Object getValueAt(int row, int column)
	{
		if(column == 0)
			return data.get(row).getName();
		else if(column == 1)
			return data.get(row).getAllocated();
		else if(column == 2)
			return data.get(row).isContingent();
		else
			return "";
	}
	
	public HashMap<Guid, ResourceAllocation> buildResourceMap()
	{
		HashMap<Guid, ResourceAllocation> res = new HashMap<Guid, ResourceAllocation>();
		Iterator<ResourceAllocation> allData = data.iterator();
		while(allData.hasNext())
		{
			ResourceAllocation thisData = allData.next();
			res.put(thisData.getID(), thisData);
		}
		
		return res;
	}
}
