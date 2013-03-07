package mil.af.rl.jcat.integration.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.integration.parser.Agent;

public class AgentTableModel extends AbstractTableModel {
    

	private static final long serialVersionUID = 1L;
	private Object[][] data;
    private String[] columns = {"Agent Name","Agent Type","Execution Interval","Detach Agent"};
    private Class[] colclass = new Class[]{Object.class,String.class,Integer.class,Boolean.class};

    public AgentTableModel() {
        super();
        buildModel();
    }
    
    public void buildModel()
    {
        ArrayList<Agent> agents = Control.getInstance().getAgents();
        data = new Object[agents.size()][columns.length];
        int index = 0;
        for(Agent agent : agents)
        {
           data[index][0] = agent;
           data[index][1] = agent.getType() == Agent.INPUT ? "Subscriber" : "Publisher";
           data[index][2] = agent.getInterval();
           data[index][3] = new Boolean(true);
           index++;
        }
    }
    
    public void setValueAt(Object newValue, int row, int col)
    {
        if(col == 3)
        {
            Agent a = (Agent)data[row][0];
            a.detach();
            Control.getInstance().removeAgent(a);
            buildModel();
        }else if(col == 2)
        {
            data[row][col] = newValue;
            ((Agent)data[row][col]).setExecuteInterval(Integer.parseInt(newValue.toString()));
        }
        super.fireTableDataChanged();
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columns.length;
    }

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
      return col == 0 || col == 1 ? false : true;
    }

}
