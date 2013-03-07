package mil.af.rl.jcat.integration.gui;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


public class SourceTable extends JTable {

	private static final long serialVersionUID = 1L;


	public SourceTable()
    {
        super();
    }

    public SourceTable(TableModel dm) {
        super(dm);
       
    }
    
    
    public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {

      java.awt.Component cell = super.prepareRenderer(renderer,row,column);
      if(!isCellSelected(row,column))
      {
        if((row & 1) == 0)
        {
            cell.setBackground(new Color(231,248,250));
            //Font f = new Font("Arial",Font.BOLD,12);
            //cell.setFont(f);
        }else{
            cell.setBackground(null);
        }
      
      }
      return cell;
    }

   

}
