package mil.af.rl.jcat.gui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableCellRenderer;

import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.ThreatResource;
import mil.af.rl.jcat.gui.table.model.CountsTableModel;
import mil.af.rl.jcat.gui.table.model.FloatFormatRenderer;


public class ResCountsTable extends JTable
{
	private CountsTableModel model;
	private FloatFormatRenderer floatRenderer;
	private boolean useFRend = false;

	
	public ResCountsTable(CountsTableModel mod, boolean useFloatRenderer)
	{
		super(mod);
		model = mod;
//		setFont(new Font("Arial", 0, 12));
		getTableHeader().setReorderingAllowed(false);
//		getTableHeader().setSize(getTableHeader().getSize().width, 20);
		getTableHeader().setPreferredSize(new java.awt.Dimension(getTableHeader().getSize().width, 20));
		if((useFRend = useFloatRenderer))
			floatRenderer = new FloatFormatRenderer();
	}
	
	
	public Class getColumnClass(int col)
	{
		if(col == 0)
			return Boolean.class;
		return super.getColumnClass(col);
	}
	
	public void columnAdded(TableColumnModelEvent e)
	{
		super.columnAdded(e);
		// set column widths
//		int colSize = Toolkit.getDefaultToolkit().getFontMetrics(this.getFont()).stringWidth("00.000");
		this.getColumnModel().getColumn(e.getToIndex()).setPreferredWidth(50);
	}

	public boolean isCellEditable(int row, int col)
	{
		if(col == 0)
			return true;
		else
			return false;
	}
		
	public Component prepareRenderer(TableCellRenderer rend, int row, int col)
	{
		Component cell = super.prepareRenderer(rend, row, col);
		
		NetNode node = model.getNodeAt(row);
		
		cell.setForeground(Color.BLACK);
		
		if(node != null && node.getResources() != null)
		{
			if(node.getResources() instanceof ThreatResource)
				cell.setBackground(new Color(159, 255, 159));
			else
				cell.setBackground(new Color(148, 197, 237));
		}
		else
		{
			cell.setBackground(java.awt.Color.WHITE);
		}
		
		return cell;
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int col)
	{
		if(col > 1 && useFRend)
			return floatRenderer;
		else
			return super.getCellRenderer(row, col);
	}
}
