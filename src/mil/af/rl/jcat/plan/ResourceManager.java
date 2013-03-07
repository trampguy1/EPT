package mil.af.rl.jcat.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MultiMap;

public class ResourceManager implements TableModel{
	private Map<Guid, PlanResource> resourceMap = new HashMap<Guid, PlanResource>();
	private MultiMap<Guid, ResourceAllocation> allocationMap = new MultiMap<Guid, ResourceAllocation>();
	private static final String [] colNames = {"Name", "Available", "Allocated", "Type"};
    private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	
	public ResourceManager()
	{
        
	}
	
	public void createOperationalResource(String name, int available)
	{
		PlanResource res = new PlanResource(name, available, PlanResource.OPERATIONAL);
		resourceMap.put(res.getTypeId(), res);
        fireTableDataChanged();
	}
	
	public void createThreatResource(String name, int available)
	{
		PlanResource res = new PlanResource(name, available, PlanResource.THREAT);
		resourceMap.put(res.getTypeId(), res);
        fireTableDataChanged();
	}
	
	void updateQuantities(PlanItem item)
	{
		HashMap resources = item.getResources();
	}

	public int getRowCount() {
		return resourceMap.size();
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}

	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
			case 0 : return String.class;
			case 1 : return Integer.class;
			case 2 : return Integer.class;
			case 3 : return String.class;
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

    public void deleteSelectedResource(int rowIndex) throws Exception
    {
        PlanResource val  = (PlanResource)this.resourceMap.values().toArray()[rowIndex];
        if(allocationMap.containsKey(val.getTypeId()))
            throw new Exception("Resources of this type are allocated within the plan. " +
                    "You must first remove the resource allocations within the plan.");
        else
            resourceMap.remove(val.getTypeId());
        fireTableDataChanged();
    }
    
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object [] col  = this.resourceMap.values().toArray();
		switch(columnIndex)
		{
			case 0 : return ((PlanResource)col[rowIndex]).name;
			case 1 : return ((PlanResource)col[rowIndex]).available;
			case 2 : return computeAllocation(((PlanResource)col[rowIndex]));
			case 3 : return ((PlanResource)col[rowIndex]).getTypeAsString();
		}
		return null;
	}

	private Integer computeAllocation(PlanResource resource) {
		int allocated = 0;
		List<ResourceAllocation> values = this.allocationMap.get(resource.typeId);
		if(values == null)
			return 0;
		for(ResourceAllocation all : values)
		{
			allocated += all.allocated;
		}
		return allocated;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);		
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);		
	}
    
    public void fireTableDataChanged() {
        TableModelEvent event = new TableModelEvent(this);
        for(TableModelListener l : listeners)
        {
            l.tableChanged(event);
        }
    }
}
