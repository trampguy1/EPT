package mil.af.rl.jcat.gui.documentationpanels;

import java.text.DateFormat;
import java.util.Date;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Vector;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

import java.awt.event.*;

import mil.af.rl.jcat.gui.MainFrm;

import mil.af.rl.jcat.plan.Comment;
import mil.af.rl.jcat.util.GraphicalDocument;
import mil.af.rl.jcat.gui.ProbabilityProfiles;
import mil.af.rl.jcat.gui.ProfileLegend;
import mil.af.rl.jcat.gui.table.model.NoEditTableModel;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.TextEntryTableCellEditor;
import mil.af.rl.jcat.util.TableEntryTableCellEditor;

import java.nio.channels.*;
import java.io.*;
import java.util.Random;

import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.HtmlWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import javax.swing.table.TableColumn;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

public class GraphicalDocPanel extends JDialog implements TableModelListener, ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;

	MainFrm frame = null;

	public static GraphicalDocPanel DocPanel;

	//set up the table
	private String[] colHeaders = { "Description", "Comments", "Username",
			"Date/Time", "Image File" };

	private int noEdit[] = { 3, 4 };

	private String[] commentHeaders = { "Comment", "Originator", "Date" };

	private int[] commentNoEdit = { 2 };

	NoEditTableModel tableModel = new NoEditTableModel(colHeaders, 0, noEdit);

	JTable tblGraphicalDoc = new JTable(tableModel);

	JLabel lblQuestion = new JLabel();
	JLabel lblScrollPane = new JLabel();
	JButton btnAdd = new JButton();
	JButton btnRemove = new JButton();
	JButton btnCreatePDF = new JButton();
	JButton btnExportImages = new JButton();
	JButton btnCreateWordDoc = new JButton();
	JButton btnClose = new JButton();
	JPanel buttonPanel = new JPanel();
	JCheckBox cbxProbProf = new JCheckBox();
	JCheckBox cbxLegend = new JCheckBox(); 
	JCheckBox cbxImportImage = new JCheckBox();
	JScrollPane ScrollPane = new JScrollPane();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	private static Logger logger = Logger.getLogger(GraphicalDocPanel.class);
	ArrayList commentList = new ArrayList();

	public GraphicalDocPanel(MainFrm _frame)
	{
		super(_frame);
		frame = _frame;

		DocPanel = this;

		try
		{
			init();
			setSize(590, 365);
			setLocationRelativeTo(_frame);

		}
		catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog: ",e);
		}

	}

	public static GraphicalDocPanel getInstance()
	{
		return DocPanel;
	}

	public ArrayList getComments(int index)
	{
		try
		{
			return (ArrayList) commentList.get(index);
		}
		catch (Exception e)
		{
			ArrayList empty = new ArrayList();
			return empty;
		}
	}

	public void setComments(int index, ArrayList comments)
	{

		commentList.set(index, comments.clone());
	}

	private void init() throws Exception
	{
		tableModel.addTableModelListener(this);
		ScrollPane.setViewportView(tblGraphicalDoc);
		populateTable();
		TableColumn col0 = tblGraphicalDoc.getColumnModel().getColumn(0);
		col0.setCellEditor(new TextEntryTableCellEditor(tableModel));

		TableColumn col1 = tblGraphicalDoc.getColumnModel().getColumn(1);
		col1.setCellEditor(new TableEntryTableCellEditor());

		this.addWindowListener(this);

		lblQuestion.setFont(new java.awt.Font("Dialog", 0, 11));
		lblQuestion.setText("What would you like to add to the table?");
		this.getContentPane().setLayout(gridBagLayout1);

		lblScrollPane.setFont(new java.awt.Font("Dialog", 0, 11));
		lblScrollPane.setText("Graphical Documentation Table");

		btnAdd.setFont(new java.awt.Font("Dialog", 0, 10));
		btnAdd.setText("Add Row");
		btnAdd.setActionCommand("Add Row");
		btnAdd.setMaximumSize(new Dimension(99, 23));
		btnAdd.setMinimumSize(new Dimension(99, 23));
		btnAdd.setPreferredSize(new Dimension(99, 23));
		btnAdd.addActionListener(this);

		btnRemove.setFont(new java.awt.Font("Dialog", 0, 10));
		btnRemove.setMaximumSize(new Dimension(99, 23));
		btnRemove.setMinimumSize(new Dimension(99, 23));
		btnRemove.setPreferredSize(new Dimension(99, 23));
		btnRemove.setText("Delete Row");
		btnRemove.setActionCommand("Delete Row");
		btnRemove.addActionListener(this);

		btnCreatePDF.setFont(new java.awt.Font("Dialog", 0, 10));
		btnCreatePDF.setSize(new Dimension(99, 23));
		btnCreatePDF.setText("Create PDF");
		btnCreatePDF.setActionCommand("Create PDF");
		btnCreatePDF.addActionListener(this);

		btnClose.setFont(new java.awt.Font("Dialog", 0, 10));
		btnClose.setSize(new Dimension(99, 23));
		btnClose.setText("Close");
		btnClose.setActionCommand("Close");
		btnClose.addActionListener(this);

		btnExportImages.setFont(new java.awt.Font("Dialog", 0, 10));
		btnExportImages.setSize(new Dimension(99, 23));
		btnExportImages.setText("Export Images");
		btnExportImages.setActionCommand("Export Images");
		btnExportImages.addActionListener(this);

		btnCreateWordDoc.setFont(new java.awt.Font("Dialog", 0, 10));
		btnCreateWordDoc.setText("Create MS Word Doc");
		btnCreateWordDoc.setActionCommand("CreateWordDoc");
		btnCreateWordDoc.addActionListener(this);

		ScrollPane.getViewport().setBackground(Color.white);
		ScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

		this.setEnabled(true);
		this.setFont(new java.awt.Font("Dialog", 0, 12));
		this.setLocale(java.util.Locale.getDefault());
		this.setTitle("Graphical Documentation");

		cbxProbProf.setText("Probability Profiles Graph");
		cbxLegend.setText("Profile Legend");
		cbxImportImage.setText("Import image from file system.");


		buttonPanel.add(btnExportImages);
		buttonPanel.add(btnCreatePDF);
		//buttonPanel.add(btnCreateWordDoc);
		buttonPanel.add(btnClose);

		this.getContentPane().add(
				lblQuestion,
				new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(10, 0, 0, 0), 0, 0));

		this.getContentPane().add(
				cbxProbProf,
				new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

//		this.getContentPane().add(
//		cbxLegend,
//new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0,
//		GridBagConstraints.CENTER, GridBagConstraints.NONE,
//		new Insets(0, -59, 0, 0), 0, 0));

		this.getContentPane().add(
				cbxImportImage,
				new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 22, 0, 0), 0, 0));

		this.getContentPane().add(
				lblScrollPane,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 10, 0, 0), 0, 0));

		this.getContentPane().add(
				btnAdd,
				new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 115), 0, 0));

		this.getContentPane().add(
				btnRemove,
				new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 10), 0, 0));

		this.getContentPane().add(
				ScrollPane,
				new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 10, 0, 10), 35, -249));

		this.getContentPane().add(
				buttonPanel,
				new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
	}

	public LinkedList ReadTableToDoc(NoEditTableModel tbl)
	{
		LinkedList DocList = new LinkedList();
		GraphicalDocument ment = new GraphicalDocument();
		Vector tblData = tbl.getDataVector();

		int i = tbl.getRowCount();

		if (tbl.getRowCount() != 0)
		{
			for (int j = 0; j < i; j++)
			{
				Vector v = (Vector) tblData.get(j);
				Object[] arO = v.toArray();
				ment.setDescription(arO[0].toString());
				ment.setComment(arO[1].toString());
				if (commentList.size() == 0)
				{
					ment.setComments(commentList);
				}
				else
				{
					ment.setComments((ArrayList) commentList.get(j));
				}
				ment.setUsername(arO[2].toString());
				ment.setDate(arO[3].toString());
				LinkedList b = ((LinkedList) arO[4]);

				LinkedList combolist = new LinkedList();

				int k = b.size();

				for (int l = 0; l < k; l++)
				{
					combolist.add(b.get(l));
				}

				ment.setFileList(combolist);

				try
				{
					DocList.add(ment.clone());
				}
				catch (CloneNotSupportedException cee)
				{
					logger.warn("readtableToDoc - CloneNotSupExc, The clone() of GraphicalDocument failed:  "+cee.getMessage());
				}
			}
		}
		else
		{
			return DocList;
		}

		return DocList;
	}

	public void tableChanged(TableModelEvent e)
	{

	}

	public LinkedList getImagesFromUI(int[] selectedImages)
	{

		LinkedList retList = new LinkedList();

		String probprof = "";
		String legend = "";

		String [] extensions = new String[5];

		extensions[0] = "png";
		extensions[1] = "jpg";
		extensions[2] = "jpeg";
		extensions[3] = "gif";
		extensions[4] = "wmf";

		CatFileFilter fFilter = new CatFileFilter(extensions, "Image File");

		File file = null;

		try
		{
			if (selectedImages[0] == 1 && selectedImages[1] == 0 && selectedImages[2] == 0 )
			{
				probprof = ProbabilityProfiles.getInstance().getImage();
				retList.add(probprof);
			}
			else if (selectedImages[0] == 0 && selectedImages[1] == 1 && selectedImages[2] == 0 )
			{
				legend = ProfileLegend.getInstance().getImage();
				retList.add(legend);
			}
			else if (selectedImages[0] == 0 && selectedImages[1] == 0 && selectedImages[2] == 1 )
			{

				//make sure that the filechooser opens to the directory that the plan exports to

				JFileChooser chooser = new JFileChooser();

				chooser.setFileFilter(fFilter);

				boolean reopen = true ;

				while(reopen)
				{
					int returnVal = chooser.showDialog(this, "Import File");

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{                   
						file = chooser.getSelectedFile();
						reopen = false ;                  
					}

					else if(returnVal == JFileChooser.CANCEL_OPTION)
					{	           			
						retList = null ;

						return retList ;
					}

					if(fFilter.accept(file) == false)
					{
						reopen = true ;
						JOptionPane.showMessageDialog(frame, "The selected file was an invalid format.");
					}

				}

				//add the code to add file to the list and copy it to the temp directory.
				retList.add(file.getName());

				try
				{
					boolean dirCreated = (new File(EnvUtils.getUserHome() + "/.JCAT")).mkdir();

					File out = new File(EnvUtils.getUserHome() + "/.JCAT/" + file.getName());
					FileChannel sourceChannel = new FileInputStream(file).getChannel();
					FileChannel destinationChannel = new FileOutputStream(out).getChannel();
					sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
					sourceChannel.close();
					destinationChannel.close();
					out.deleteOnExit();
				}
				catch (FileNotFoundException fnfe)
				{
					logger.error("getImagesFromUI - FileNotFoundExc exporting images:  "+fnfe.getMessage());
				}
				catch (IOException ioe)
				{
					logger.error("getImagesFromUI - IOExc exporting images:  "+ioe.getMessage());
				}
			}
			else if (selectedImages[0] == 1 && selectedImages[1] == 1 && selectedImages[2] == 0 )
			{
				probprof = ProbabilityProfiles.getInstance().getImage();
				retList.add(probprof);
				legend = ProfileLegend.getInstance().getImage();
				retList.add(legend);
			}
			else if (selectedImages[0] == 1 && selectedImages[1] == 0 && selectedImages[2] == 1 )
			{
				probprof = ProbabilityProfiles.getInstance().getImage();
				retList.add(probprof);

				//make sure that the filechooser opens to the directory that the plan exports to

				JFileChooser chooser = new JFileChooser();

				chooser.setFileFilter(fFilter);

				boolean reopen = true ;

				while(reopen)
				{
					int returnVal = chooser.showDialog(this, "Import File");

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{                   
						file = chooser.getSelectedFile();
						reopen = false ;                  
					}

					else if(returnVal == JFileChooser.CANCEL_OPTION)
					{	           			
						retList = null ;

						return retList ;
					}

					if(fFilter.accept(file) == false)
					{
						reopen = true ;
						JOptionPane.showMessageDialog(frame, "The selected file was an invalid format.");
					}

				}

				//add the code to add file to the list and copy it to the temp directory.
				retList.add(file.getName());

				try
				{

					File out = new File(EnvUtils.getUserHome() + "/.JCAT/" + file.getName());
					FileChannel sourceChannel = new FileInputStream(file)
					.getChannel();
					FileChannel destinationChannel = new FileOutputStream(
							out).getChannel();
					sourceChannel.transferTo(0, sourceChannel.size(),
							destinationChannel);
					sourceChannel.close();
					destinationChannel.close();
					out.deleteOnExit();
				}
				catch (FileNotFoundException fnfe)
				{
					logger.error("getImagesFromUI - FileNotFoundExc exporting images:  "+fnfe.getMessage());
				}
				catch (IOException ioe)
				{
					logger.error("getImagesFromUI - IOExc exporting images:  "+ioe.getMessage());
				}

			}
			else if (selectedImages[0] == 0 && selectedImages[1] == 1 && selectedImages[2] == 1 )
			{
				legend = ProfileLegend.getInstance().getImage();
				retList.add(legend);

				//make sure that the filechooser opens to the directory that the plan exports to

				JFileChooser chooser = new JFileChooser();

				chooser.setFileFilter(fFilter);

				boolean reopen = true ;

				while(reopen)
				{
					int returnVal = chooser.showDialog(this, "Import File");

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{                   
						file = chooser.getSelectedFile();
						reopen = false ;                  
					}

					else if(returnVal == JFileChooser.CANCEL_OPTION)
					{	           			
						retList = null ;

						return retList ;
					}

					if(fFilter.accept(file) == false)
					{
						reopen = true ;
						JOptionPane.showMessageDialog(frame, "The selected file was an invalid format.");
					}

				}

				//add the code to add file to the list and copy it to the temp directory.
				retList.add(file.getName());

				try
				{

					File out = new File(EnvUtils.getUserHome() + "/.JCAT/" + file.getName());
					FileChannel sourceChannel = new FileInputStream(file)
					.getChannel();
					FileChannel destinationChannel = new FileOutputStream(
							out).getChannel();
					sourceChannel.transferTo(0, sourceChannel.size(),
							destinationChannel);
					sourceChannel.close();
					destinationChannel.close();
					out.deleteOnExit();
				}
				catch (FileNotFoundException fnfe)
				{
					logger.error("getImagesFromUI - FileNotFoundExc exporting images:  "+fnfe.getMessage());
				}
				catch (IOException ioe)
				{
					logger.error("getImagesFromUI - IOExc exporting images:  "+ioe.getMessage());
				}
			}
			else if (selectedImages[0] == 1 && selectedImages[1] == 1 && selectedImages[2] == 1 )
			{
				probprof = ProbabilityProfiles.getInstance().getImage();
				retList.add(probprof);
				legend = ProfileLegend.getInstance().getImage();
				retList.add(legend);

				//make sure that the filechooser opens to the directory that the plan exports to

				JFileChooser chooser = new JFileChooser();

				chooser.setFileFilter(fFilter);

				boolean reopen = true ;

				while(reopen)
				{
					int returnVal = chooser.showDialog(this, "Import File");

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{                   
						file = chooser.getSelectedFile();
						reopen = false ;                  
					}

					else if(returnVal == JFileChooser.CANCEL_OPTION)
					{	           			
						retList = null ;

						return retList ;
					}

					if(fFilter.accept(file) == false)
					{
						reopen = true ;
						JOptionPane.showMessageDialog(frame, "The selected file was an invalid format.");
					}

				}

				//add the code to add file to the list and copy it to the temp directory.
				retList.add(file.getName());

				try
				{

					File out = new File(EnvUtils.getUserHome() + "/.JCAT/" + file.getName());
					FileChannel sourceChannel = new FileInputStream(file)
					.getChannel();
					FileChannel destinationChannel = new FileOutputStream(
							out).getChannel();
					sourceChannel.transferTo(0, sourceChannel.size(),
							destinationChannel);
					sourceChannel.close();
					destinationChannel.close();
					out.deleteOnExit();
				}
				catch (FileNotFoundException fnfe)
				{
					logger.error("getImagesFromUI - FileNotFoundExc exporting images:  "+fnfe.getMessage());
				}
				catch (IOException ioe)
				{
					logger.error("getImagesFromUI - IOExc exporting images:  "+ioe.getMessage());
				}
			}

		}
		catch (IOException ioe)
		{
			logger.error("getImagesFromUI - IOExc:  "+ioe.getMessage());
		}

		return retList;

	}

	void onWindowCloseOperation()
	{
		try
		{
			tblGraphicalDoc.getCellEditor().stopCellEditing();
		}
		catch (Exception eee)
		{
			//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
		}
		frame.GraphicalDocumentation = ReadTableToDoc(tableModel);

		dispose();
	}

	public void addRow(GraphicalDocument Doc, ArrayList list)
	{
		Vector vrow = new Vector();
		vrow.add(Doc.getDescription());
		vrow.add(Doc.getComment());
		vrow.add(Doc.getUsername());
		vrow.add(Doc.getDate());
		vrow.add(Doc.getFileList());
		commentList.add(list);

		tableModel.addRow(vrow);
	}

	public void populateTable()
	{
		LinkedList tabledata = frame.GraphicalDocumentation;

		for (int i = 0; i < tabledata.size(); i++)
		{
			GraphicalDocument currentDoc = (GraphicalDocument) tabledata.get(i);
			addRow(currentDoc, currentDoc.getComments());
		}
	}

	//  WindowListeners
	public void windowClosing(WindowEvent e)
	{
		onWindowCloseOperation();
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	//ActionListener
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Add Row"))
		{
			if (!cbxProbProf.isSelected() && !cbxLegend.isSelected() && !cbxImportImage.isSelected())
			{
				JOptionPane.showMessageDialog(frame,
						"Please select an image to add.");
				return;
			}

			GraphicalDocument doc = new GraphicalDocument();
			doc.setDescription("");
			doc.setComment("");
			doc.setUsername("");

			Date currentDate = new Date();
			DateFormat currentDateFormat = DateFormat.getDateInstance();
			String strDate = currentDateFormat.format(currentDate);
			doc.setDate(strDate);

			int[] theArray = new int[3];
			theArray[0] = 0;
			theArray[1] = 0;
			theArray[2] = 0;

			if (cbxProbProf.isSelected())
			{
				theArray[0] = 1;
			}
			if(cbxLegend.isSelected())
			{
				theArray[1] = 1;
			}
			if (cbxImportImage.isSelected())
			{
				theArray[2] = 1;
			}

			LinkedList imagesfromui = getImagesFromUI(theArray);

			if(imagesfromui == null)
			{
				return;
			}
			else
			{
				doc.setFileList(imagesfromui);
			}

			ArrayList list = new ArrayList();

			addRow(doc, list);
		}
		else if (e.getActionCommand().equals("Delete Row"))
		{
			int selectedRow = tblGraphicalDoc.getSelectedRow();
			try
			{
				tableModel.removeRow(selectedRow);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException ee)
			{
				logger.error("actionPerformed - AIOOBExc removing table row:  "+ee.getMessage());
				return;
			}
			commentList.remove(selectedRow);
		}
		else if (e.getActionCommand().equals("Create PDF"))
		{
			//this will stop the cell editing and save all current information to the main instance

			try
			{
				tblGraphicalDoc.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}
			frame.GraphicalDocumentation = ReadTableToDoc(tableModel);

			//This will be all the code for the creation of the PDF file.

			LinkedList toBePDF = frame.GraphicalDocumentation;
			boolean createdPDF = false;
			int masterCount = toBePDF.size();

			com.lowagie.text.Document document = new com.lowagie.text.Document();
//			boolean dirCreated = (new File(EnvUtils.getUserHome() + "/PDFGraphicalDocumentation")).mkdir();

			try
			{
				if (masterCount == 0)
				{
					throw new DocumentException();
				}

				//EnvUtils.getUserHome() + "/PDFGraphicalDocumentation/"+ new Random().nextInt(99999999) + ".pdf")
				String outFileName = browseForExport("pdf", "Acrobat PDF files", false);
				PdfWriter.getInstance(document, new FileOutputStream(outFileName));

				HeaderFooter header = new HeaderFooter(new Phrase(
						"JCAT Graphical Documentation"), false);

				com.lowagie.text.Image wmark = com.lowagie.text.Image.getInstance(this.getClass().getClassLoader().getResource("watermark3.png"));

//				Watermark watermark = new Watermark(wmark, 420, 2);
				//TODO:  changed with new itext library, fix or update this
//				document.add(watermark);

				document.setHeader(header);

				document.setFooter(header);

				document.open();

				for (int i = 0; i < masterCount; i++)
				{

					createdPDF = true;

					GraphicalDocument tempDoc = (GraphicalDocument) toBePDF
					.get(i);

					//description and image

					document.add(new Paragraph("Image Description:"));

					document.add(new Paragraph(((GraphicalDocument) (tempDoc
							.clone())).getDescription()));

					document.add(new Paragraph(""));

					int flCount = ((LinkedList) ((GraphicalDocument) (tempDoc
							.clone())).getFileList()).size();

					for (int j = 0; j < flCount; j++)
					{

						String fileName = (String) ((LinkedList) ((GraphicalDocument) (tempDoc
								.clone())).getFileList()).get(j);

						if (fileName == "")
						{
							continue;
						}

						com.lowagie.text.Image png = com.lowagie.text.Image
						.getInstance(EnvUtils.getUserHome() + "/.JCAT/" + fileName);

						float x = 480/png.getPlainWidth();

						if(x < 1)
						{
							png.scaleAbsolute(png.getPlainWidth()*x,png.getPlainHeight()*x);
						}

						png.setAlignment(com.lowagie.text.Image.MIDDLE);

						document.add(png);
						document.add(new Paragraph(""));
					}
					document.add(new Paragraph(""));

					//comments
					document.add(new Paragraph("Image Comments:"));

					ArrayList comments = ((GraphicalDocument) (tempDoc.clone()))
					.getComments();
					if (comments.size() > 0)
					{
						Table commentsTable = new Table(3, comments.size());

						for (int k = 0; k < comments.size(); k++)
						{
							Comment currentCom = (Comment) comments.get(k);

							commentsTable.addCell(currentCom.getComment(),
									new Point(k, 0));
							commentsTable.addCell(currentCom.getOriginator(),
									new Point(k, 1));
							commentsTable.addCell(currentCom.getDate(),
									new Point(k, 2));
						}
						commentsTable.setPadding(2);
						document.add(commentsTable);
					}

					document.newPage();
				}

				document.close();
			}
			catch (CloneNotSupportedException ce)
			{
				logger.error("actionPerformed CloneNotSubExc creating PDF:  "+ce.getMessage());
			}
			catch (DocumentException de)
			{
				createdPDF = false;
				logger.error("actionPerformed DocumentExc creating PDF:  "+de.getMessage());
			}
			catch (IOException ioe)
			{
				createdPDF = false;
				logger.error("actionPerformed IOExc creating PDF:  "+ioe.getMessage());
			}

			if(createdPDF)
				JOptionPane.showMessageDialog(frame, "Adobe PDF exported successfully to the location specified.");
			else
				JOptionPane.showMessageDialog(frame, "No PDF exported.");

		}
		else if (e.getActionCommand().equals("Close"))
		{
			onWindowCloseOperation();
		}
		else if (e.getActionCommand().equals("Export Images"))
		{
			//boolean dirCreated = (new File(EnvUtils.getUserHome() + "/images")).mkdir();

			try
			{
				tblGraphicalDoc.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}

			frame.GraphicalDocumentation = ReadTableToDoc(tableModel);

			LinkedList toBeImages = frame.GraphicalDocumentation;
			boolean exported = false;
			int masterCount = toBeImages.size();
			try
			{
				String outFolderName = browseForExport("", "Directories", true);
				for (int i = 0; i < masterCount; i++)
				{
					GraphicalDocument tempDoc = (GraphicalDocument) toBeImages.get(i);

					int flCount = ((LinkedList) ((GraphicalDocument) (tempDoc.clone())).getFileList()).size();

					for (int j = 0; j < flCount; j++)
					{

						String fileName = (String) ((LinkedList) ((GraphicalDocument) (tempDoc.clone())).getFileList()).get(j);

						if (fileName == "")
						{
							continue;
						}

						try
						{
							File in = new File(EnvUtils.getUserHome() + "/.JCAT/" + fileName);
							File out = new File(outFolderName+"/"+fileName);  //EnvUtils.getUserHome() + "/images/" + fileName);
							FileChannel sourceChannel = new FileInputStream(in)
							.getChannel();
							FileChannel destinationChannel = new FileOutputStream(
									out).getChannel();
							sourceChannel.transferTo(0, sourceChannel.size(),
									destinationChannel);
							sourceChannel.close();
							destinationChannel.close();
						}
						catch (FileNotFoundException fnfe)
						{
							logger.error("actionPerformed - FileNotFoundExc exporting images:  "+fnfe.getMessage());
							continue;
						}
						catch (IOException ioe)
						{
							logger.error("actionPerformed - IOExc exporting images:  "+ioe.getMessage());
							continue;
						}
						exported = true;

					}

				}
			}
			catch(FileNotFoundException exc){} //user canceled export (thats otay)
			catch (CloneNotSupportedException ce)
			{
				logger.error("actionPerformed - CloneNotSupportedExc exporting images:  "+ce.getMessage());
			}

			if (exported)
			{
				JOptionPane.showMessageDialog(frame, "Images exported successfully to the location specified.");
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "No images exported.");
			}

		}
		else if(e.getActionCommand().equals("CreateWordDoc"))
		{
			//this will stop the cell editing and save all current information
			// to
			//the main instance

			try
			{
				tblGraphicalDoc.getCellEditor().stopCellEditing();
			}
			catch (Exception eee)
			{
				//System.out.println("The Cell Editor was stopped and stopcellediting() was called anyhow.");
			}
			frame.GraphicalDocumentation = ReadTableToDoc(tableModel);

			//This will be all the code for the creation of the HTML file.

			LinkedList toBeHTML = frame.GraphicalDocumentation;
			boolean createdHTML = false;
			int masterCount = toBeHTML.size();

			com.lowagie.text.Document document = new com.lowagie.text.Document();
			boolean dirCreated = (new File(EnvUtils.getUserHome() + "/WordGraphicalDocumentation")).mkdir();

			try
			{
				if (masterCount == 0)
				{
					throw new DocumentException();
				}

				HtmlWriter.getInstance(document, new FileOutputStream(
						EnvUtils.getUserHome() + "/WordGraphicalDocumentation/"
						+ new Random().nextInt(99999999) + ".doc"));

				document.open();

				for (int i = 0; i < masterCount; i++)
				{

					createdHTML = true;

					GraphicalDocument tempDoc = (GraphicalDocument) toBeHTML
					.get(i);

					//description and image

					document.add(new Paragraph("Image Description:"));

					document.add(new Paragraph(((GraphicalDocument) (tempDoc
							.clone())).getDescription()));

					document.add(new Paragraph(""));

					int flCount = ((LinkedList) ((GraphicalDocument) (tempDoc
							.clone())).getFileList()).size();

					for (int j = 0; j < flCount; j++)
					{

						String fileName = (String) ((LinkedList) ((GraphicalDocument) (tempDoc
								.clone())).getFileList()).get(j);

						if (fileName == "")
						{
							continue;
						}

						com.lowagie.text.Image png = com.lowagie.text.Image
						.getInstance(EnvUtils.getUserHome() + "/.JCAT/" + fileName);

						float x = 480/png.getPlainWidth();

						if(x < 1)
						{
							png.scaleAbsolute(png.getPlainWidth()*x,png.getPlainHeight()*x);
						}

						png.setAlignment(com.lowagie.text.Image.MIDDLE);

						document.add(png);
						document.add(new Paragraph(""));
					}


					//comments
					document.add(new Paragraph("Image Comments:"));

					ArrayList comments = ((GraphicalDocument) (tempDoc.clone()))
					.getComments();
					if (comments.size() > 0)
					{
						Table commentsTable = new Table(3, comments.size());

						for (int k = 0; k < comments.size(); k++)
						{
							Comment currentCom = (Comment) comments.get(k);

							commentsTable.addCell(currentCom.getComment(),
									new Point(k, 0));
							commentsTable.addCell(currentCom.getOriginator(),
									new Point(k, 1));
							commentsTable.addCell(currentCom.getDate(),
									new Point(k, 2));
						}
						commentsTable.setPadding(2);
						document.add(commentsTable);
					}

					document.newPage();
				}

				document.close();
			}
			catch (CloneNotSupportedException ce)
			{
				logger.error("actionPerformed - CloneNotSupportedExc exporting images:  "+ce.getMessage());
			}
			catch (DocumentException de)
			{
				createdHTML = false;
				logger.error("actionPerformed - DocumentException exporting images:  "+de.getMessage());
			}
			catch (IOException ioe)
			{
				createdHTML = false;
				logger.error("actionPerformed - IOException exporting images:  "+ioe.getMessage());
			}

			if (createdHTML)
			{
				JOptionPane.showMessageDialog(frame, "The Word document was exported to the WordGraphicalDocumentation folder"
						+ " in the user home directory.");
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "No Document exported.");
			}

		}


	}

	//shows a browse file chooser and returns a file path for use with export functions
	private String browseForExport(String ext, String desc, boolean dirsOnly) throws FileNotFoundException
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new CatFileFilter(ext, desc, true));
		if(dirsOnly)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
		{
			if(fileChooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith("."+ext) || dirsOnly)
				return fileChooser.getSelectedFile().getAbsolutePath();
			else
				return fileChooser.getSelectedFile().getAbsolutePath()+"."+ext;
		}
		else
			throw new FileNotFoundException("User canceled export.");
	}

}
