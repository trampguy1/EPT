package mil.af.rl.jcat.gui.table.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import mil.af.rl.jcat.bayesnet.NetNode;


	
public class CountsTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CountsTableModel.class);

	Vector<Vector> data = new Vector<Vector>();
	private Map<NetNode, List> resCounts;
	
	public CountsTableModel()
	{
		resCounts = null;
	}
	
	/**
	 * Creates a new model loading given current values
	 * @param currentRes HashMap of resources to load
	 */
	public CountsTableModel(Map<NetNode, List> currentRes, boolean filterNonRes, boolean filterZeros)
	{
		resCounts = currentRes;
		buildTable(filterNonRes, filterZeros);
	}
	
	/**
	 * The order of these constructor inputs are different because to avoid a 'duplicate method'
	 * because java sees a signature with Map<NetNode, List> the same as Map<NetNode Object[]>
	 * @param currentRes
	 */
	public CountsTableModel(boolean filterNonRes, boolean filterZeros, Map<NetNode, float[]> currentRes)
	{
		resCounts = new TreeMap<NetNode, List>();
		Iterator<NetNode> nodes = currentRes.keySet().iterator();
		while(nodes.hasNext())
		{
			NetNode key = nodes.next();
			//Arrays.asList doesn't seam to be working, wtf
			Vector newResData = new Vector();
			float[] resData = currentRes.get(key);
			for(int x=0; x<resData.length; x++)
				newResData.add(resData[x]);
			resCounts.put(key, newResData);
		}
		
		buildTable(filterNonRes, filterZeros);
	}
	
	
	public void buildTable(boolean filterNonRes, boolean filterZeros)
	{
		Vector<Vector> oldData = new Vector<Vector>(data); //keep this to check old info (like the 'keep it' checkboxes)
		data.clear();
		
		if(resCounts != null && resCounts.size() > 0)
		{
			Iterator nodes = resCounts.keySet().iterator();
			while(nodes.hasNext())
			{
				
				NetNode thisNode = (NetNode)nodes.next();
				Boolean markedToKeep = false;
				
				markedToKeep = isNodeMarkedToKeep(thisNode, oldData);
				
				if(!markedToKeep && filterNonRes && (thisNode.getResources() == null))
					continue;
				
				if(!markedToKeep && filterZeros)
				{
					boolean allZero = true;
					List resData = resCounts.get(thisNode);
					//float[] resData = resCounts.get(thisNode);
					
//					for(int x=1; x<resData.size(); x++)
					for(int x=0; x<resData.size(); x++)
					{
						if(!resData.get(x).toString().equals("0.0")) //prolly should do numeric compare here
						//if(resData[x] != (0.0))
						{
							allZero = false;
							break;
						}
					}
					if(allZero)
						continue;
				}
				
				//THIS USED TO BE CLONED, NOW THAT IT IS GENERIC LIST IT COULD NOT BE, hmm
				Vector resData = new Vector(resCounts.get(thisNode));
				
//				float[] resRawData = resCounts.get(thisNode);
//				Vector resData = new Vector(Arrays.asList(resRawData)); //WTF THIS DINT WORK RIGHT
//				Vector resData = new Vector();
//				for(int x=0; x<resRawData.length; x++)
//					resData.add(resRawData[x]);
				
				if(!(resData.get(0) instanceof NetNode))
					resData.add(0, thisNode);  //this is a new line for accepting the float[] instead of Vector
				resData.add(0, markedToKeep);
				
				data.add(resData);
				
			}
		}
		
		this.fireTableDataChanged();
	}
	
	private boolean isNodeMarkedToKeep(NetNode thisNode, Vector<Vector> oldData)
	{
		Iterator<Vector> oldRows = oldData.iterator();
		while(oldRows.hasNext())
		{
			Vector rowData = oldRows.next();
			if(rowData.get(1).equals(thisNode))
				return (Boolean)rowData.get(0);
		}
		
		return false;
	}

	public void removeRow(int selectedRow)
	{
		data.remove(selectedRow);
	}
	
	public String getColumnName(int col)
	{
		if(col == 0)
			return "";
		else if(col == 1)
			return "Node";
		else
			return (col - 2) + "";
	}
	
	
	public int getColumnCount()
	{
		try{
			return data.get(0).size();
		}catch(Exception exc)
		{
			return 5;
		}
	}

	public int getRowCount()
	{
		return data.size();
	}

	public Object getValueAt(int row, int column)
	{
		return data.get(row).get(column);
	}

	public void setValueAt(Object val, int row, int col)
	{
		if(col == 0)
		{
			data.get(row).set(col, (Boolean)val);
		}
		else
			super.setValueAt(val, row, col);
	}
	
	public NetNode getNodeAt(int row)
	{
		try{
			return (NetNode)data.get(row).get(1);
		}catch(Exception exc){
			logger.warn("getNodeAt - NetNode at requested row not found: "+exc.getMessage());
			return null;
		}
	}
	
	
	public void exportToExcel(String path) throws IOException
	{
		HSSFWorkbook workbook = new HSSFWorkbook(); 
		HSSFSheet sheet = workbook.createSheet("Resource Counts"); 
		HSSFDataFormat format = workbook.createDataFormat();
		
		HSSFCellStyle topHeaderStyle = workbook.createCellStyle();
		topHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		topHeaderStyle.setFont(font);
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(font);

		//create the top header first
		HSSFRow headRow = sheet.createRow(0);
		for(int y=1; y<getColumnCount(); y++)
		{
			HSSFCell cell = headRow.createCell((short)(y-1));
			cell.setCellValue(getColumnName(y));
			cell.setCellStyle(topHeaderStyle);
		}
		
		
		for(int x=1; x<=data.size(); x++) //row
		{
			HSSFRow row = sheet.createRow(x);
			
			for(int y=1; y<data.get(x-1).size(); y++) //column
			{
				HSSFCell cell = row.createCell((short)(y-1));
				
				if(y-1 == 0)
					cell.setCellStyle(headerStyle);
				
				if(data.get(x-1).get(y) instanceof Number)
				{
					cell.setCellValue(((Number)data.get(x-1).get(y)).doubleValue());
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.getCellStyle().setDataFormat(format.getFormat("#0.###"));
				}
				else
					cell.setCellValue(data.get(x-1).get(y).toString());
				
			}
		}
		
		sheet.setColumnWidth((short)0, (short)8000);
		
		FileOutputStream fos = new FileOutputStream(path);
		
		workbook.write(fos); 
		fos.flush();
		fos.close();
		
	}

	
/*	public HashMap<String, Vector> buildResourceMap()
//	{
//		HashMap<String, Vector> res = new HashMap<String, Vector>();
//		Iterator allData = data.iterator();
//		while(allData.hasNext())
//		{
//			Vector thisData = (Vector)allData.next();
//			res.put(thisData.get(0).toString(), thisData);
//		}
//		
//		return res;
	} */

}
