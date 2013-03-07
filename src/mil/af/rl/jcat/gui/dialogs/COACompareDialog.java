package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mil.af.rl.jcat.gui.COAItemPlot;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.util.COAComparator;
import mil.af.rl.jcat.util.CatFileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class COACompareDialog extends JDialog implements ActionListener
{
	
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
	private JFreeChart chart;
	private ChartPanel cp;
	private COAComparator comparator;
	private JPanel bottomPanel;
	private JButton closeButton;
	private JButton returnButton;
	private JButton exportExcel;
	
	
	public COACompareDialog(Frame parent, COAComparator comp)
	{
		super(parent, "COA Comparison");
		setSize(600, 400);
		setLocationRelativeTo(parent);
		
		comparator = comp;
		
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(createButtonPanel(), BorderLayout.SOUTH);
		buildChart();
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	
	private void buildChart()
    {
		XYSeriesCollection plots = comparator.getDataset();
        chart = ChartFactory.createXYStepChart(null, "Time", "Probability", plots, PlotOrientation.VERTICAL, false, true, false);
        //chart.getLegend().setItemFont(new java.awt.Font("Arial", 0, 10));
        cp = new ChartPanel(chart, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYLineAndShapeRenderer plotRend = comparator.getRenderer();
        plot.setRenderer(plotRend);
        cp.setDisableMouseDrag(true); //added to chart by mikeD to disable windowed zooming on chart
        cp.updateUI();
        
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(102.0);
        yAxis.setLowerMargin(1.5);
        yAxis.setUpperMargin(2.5);
        chart.getXYPlot().setRangeAxis(yAxis);

        NumberTickUnit t = new NumberTickUnit(1.0);
        TickUnits tu = new TickUnits();
        tu.add(t);
        xAxis.setLowerBound(0.0);
        xAxis.setUpperBound((comparator.getPlan().getBayesNet() != null) ? comparator.getPlan().getBayesNet().getTimespan() : 1);
        xAxis.setStandardTickUnits(tu);
        xAxis.setAutoTickUnitSelection(true);
        chart.getXYPlot().setDomainAxis(xAxis);
        
        JPanel legendPanel = new JPanel(new BorderLayout());
        
        //create own legend showing coa colors and item line colors (not use chart legend)
        JPanel itemLegend = new JPanel();
        for(COAItemPlot itemPlot : comparator.getPlots())
        {
        	JPanel legPanel = new JPanel();
        	JLabel clr = new JLabel("     ");
        	if(!comparator.isReverseColors())
        		clr.setPreferredSize(new java.awt.Dimension(clr.getPreferredSize().width, 5));
        	clr.setOpaque(true);
        	clr.setBackground((Color)comparator.getItemColor(itemPlot));
        	JLabel nm = new JLabel(itemPlot.getItemName());
        	legPanel.add(clr);
        	legPanel.add(nm);
        	itemLegend.add(legPanel);
        }
        
        JPanel coaLegend = new JPanel();
        if(comparator.getPlots().size() > 0)
        {
	        Object[] coaNames = comparator.getPlots().get(0).getCOANames();
	        for(int x=0; x<coaNames.length; x++)
	        {
	        	JPanel legPanel = new JPanel();
	        	JLabel clr = new JLabel("     ");
	        	if(comparator.isReverseColors())
	        		clr.setPreferredSize(new java.awt.Dimension(clr.getPreferredSize().width, 5));
	        	clr.setOpaque(true);
	        	clr.setBackground((Color)comparator.getCOAColor(x));
	        	JLabel nm = new JLabel(coaNames[x].toString());
	        	legPanel.add(clr);
	        	legPanel.add(nm);
	        	coaLegend.add(legPanel);
	        }
        }
        
        legendPanel.add(itemLegend, BorderLayout.NORTH);
        legendPanel.add(coaLegend, BorderLayout.SOUTH);
        bottomPanel.add(legendPanel, BorderLayout.NORTH);
        
        add(cp, BorderLayout.CENTER);
    }
	
	private JPanel createButtonPanel()
	{
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel top = new JPanel();
		top.setBorder(BorderFactory.createTitledBorder(""));
		JPanel bottom = new JPanel();
		buttonPanel.add(top, BorderLayout.NORTH);
		buttonPanel.add(bottom, BorderLayout.SOUTH);
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		returnButton = new JButton("Start Over");
		returnButton.addActionListener(this);
		bottom.add(returnButton);
		bottom.add(closeButton);
		
		exportExcel = new JButton("Export graphed data (Excel)");
		exportExcel.addActionListener(this);
		top.add(exportExcel);
		
		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == closeButton)
			dispose();
		else if(event.getSource() == returnButton)
		{
			javax.swing.SwingUtilities.invokeLater(new Runnable(){
				public void run()
				{
					//execute compare again for the user after closing this one
					mil.af.rl.jcat.gui.MainFrm.getInstance().getCOAViewer().compareCOAs(comparator.getCoaList(), comparator.getItemList());					
				}
			});
			
			dispose();
		}
		else if(event.getSource() == exportExcel)
		{
			try{
				String exPath = browseForExport("xls", "MS Excel documents", false);
				exportToExcel(exPath);
			}catch(FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "Could not export file. \n"+e.getMessage());
			}catch(IOException e){
				JOptionPane.showMessageDialog(this, "Could not export file. \n"+e.getMessage());
			}
			
		}
	}

	
	private String browseForExport(String ext, String desc, boolean dirsOnly) throws FileNotFoundException
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new CatFileFilter(ext, desc, true));
		if(dirsOnly)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if(fileChooser.showSaveDialog(MainFrm.getInstance()) == JFileChooser.APPROVE_OPTION)
		{
			if(fileChooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith("."+ext) || dirsOnly)
				return fileChooser.getSelectedFile().getAbsolutePath();
			else
				return fileChooser.getSelectedFile().getAbsolutePath()+"."+ext;
		}
		else
			throw new FileNotFoundException("User canceled export.");
	}
	
	public void exportToExcel(String path) throws IOException
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("COA Compare"); 
		HSSFDataFormat format = workbook.createDataFormat();
		
		HSSFCellStyle topHeaderStyle = workbook.createCellStyle();
		topHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont thFont = workbook.createFont();
		thFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		topHeaderStyle.setFont(thFont);
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(thFont);
		
		HSSFRow titleRow = sheet.createRow(0);
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		HSSFFont titleFont = workbook.createFont();
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleFont.setFontHeightInPoints((short)12);
		titleStyle.setFont(titleFont);
		HSSFCell titleCell = titleRow.createCell((short)0);
		titleCell.setCellValue("Course of Action Comparison - "+comparator.getPlan().getPlanName());
		titleCell.setCellStyle(titleStyle);

		int currentRow = 2;
		//create the top header first
		HSSFRow headRow = sheet.createRow(currentRow++);
		for(int y=0; y<xAxis.getUpperBound(); y++)
		{
			HSSFCell cell = headRow.createCell((short)(y+1));
			cell.setCellValue(y);
			cell.setCellStyle(topHeaderStyle);
		}
		
		//first list data from each coa in a separate section
		for(int x=0; x<comparator.getCOACount(); x++) //each coa
		{
			HSSFRow coaTitleRow = sheet.createRow(currentRow++);
			HSSFCell tCell = coaTitleRow.createCell((short)0);
			tCell.setCellValue("Course of Action");
			tCell.setCellStyle(headerStyle);
			HSSFRow coaTitleRow1 = sheet.createRow(currentRow++);
			HSSFCell tCell1 = coaTitleRow1.createCell((short)0);
			tCell1.setCellValue(comparator.getCoaList().get(x).getName());
			tCell1.setCellStyle(headerStyle);
			
			Iterator<COAItemPlot> allItems = comparator.getPlots().iterator();
			while(allItems.hasNext())
			{
				XYSeries thisSeries = allItems.next().getAllSeries().get(x);
				
				addRowFromSeries(sheet, currentRow++, thisSeries, "", format);
			}
			
			sheet.createRow(currentRow++); //blank row
		}

		sheet.createRow(currentRow++); //blank rows
		sheet.createRow(currentRow++);
		
		//array for storing difference calculations to display later
		//int[][] diffs = new int[comparator.getCOACount() * comparator.getItemCount()][comparator.getTime()];
		
		//list an item by item layout of the numberz
		HSSFRow itemTitleRow = sheet.createRow(currentRow++);
		HSSFCell tCell = itemTitleRow.createCell((short)0);
		tCell.setCellValue("Item by Item");
		tCell.setCellStyle(headerStyle);
		
		Iterator<COAItemPlot> allItems = comparator.getPlots().iterator();
		while(allItems.hasNext())
		{
			COAItemPlot plot = allItems.next();
			Vector<XYSeries> itemCOAs = plot.getAllSeries();
			
			for(int x=0; x<itemCOAs.size(); x++)
				addRowFromSeries(sheet, currentRow++, itemCOAs.get(x), "("+plot.getCOANames()[x].toString()+")", format);
		}
		
		
		sheet.setColumnWidth((short)0, (short)8000);
		
		FileOutputStream fos = new FileOutputStream(path);
		
		workbook.write(fos); 
		fos.flush();
		fos.close();
	}
	
	private void addRowFromSeries(HSSFSheet sheet, int rowNum, XYSeries series, String sufix, HSSFDataFormat formater)
	{
		HSSFRow row = sheet.createRow(rowNum);
		
		for(int y=0; y<=series.getItemCount(); y++) //column
		{
			HSSFCell cell = row.createCell((short)(y));
			
			if(y == 0)
				cell.setCellValue(series.getKey().toString()+ " " +sufix);
			else
			{
				if(series.getY(y-1) instanceof Number)
				{
					cell.setCellValue(((Number)series.getY(y-1)).doubleValue());
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.getCellStyle().setDataFormat(formater.getFormat("#0.0"));
				}
			}
		}
	}
}
