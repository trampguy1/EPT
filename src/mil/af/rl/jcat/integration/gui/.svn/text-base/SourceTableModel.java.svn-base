package mil.af.rl.jcat.integration.gui;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;


public class SourceTableModel extends AbstractTableModel {
   
    private static final long serialVersionUID = 7888891893950511898L;
    private String[][] data;
    private String[] columns = {"application","variable","time","instance"};
    private Class[] colclass = new Class[]{String.class,String.class,String.class,String.class};
    private ResultSet rs;
    private int length;
    
    
    public SourceTableModel(ResultSet resultset) {
        super();
        rs = resultset;
        try{
            rs.last();
            length = rs.getRow();// size of result set
            rs.absolute(1);
            // now move it back yo
        }catch(SQLException s)
        {
            s.printStackTrace(System.err);
            // only supports TYPE_FORWARD_ONLY cursor
        }
        data = new String[length][4];
        //rs = resultset; 
        init();
    }

    public void setValueAt(Object value, int row, int col)
    {
      if(col == 2)
      {
       
      }
    }

    private void init()
    {
        try{
            
            for(int x=0; x<data.length;x++)
            {
                data[x][0] = rs.getString("application");
                data[x][1] = rs.getString("variable");
                data[x][2] = rs.getString("time");
                data[x][3] = rs.getString("instance");
                rs.next();
            }
        }catch(SQLException s)
        {
            s.printStackTrace(System.err);
        }
      
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
