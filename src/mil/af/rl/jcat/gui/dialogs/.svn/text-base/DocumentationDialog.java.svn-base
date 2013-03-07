
package mil.af.rl.jcat.gui.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.documentationpanels.CommentsPanel;
import mil.af.rl.jcat.gui.documentationpanels.DescriptionPanel;
import mil.af.rl.jcat.gui.documentationpanels.KeyWordPanel;
import mil.af.rl.jcat.gui.documentationpanels.ReferencePanel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Comment;
import mil.af.rl.jcat.plan.Documentation;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.Resource;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.ShapeHighlighter;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.c3i.jwb.JWBShape;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.HtmlWriter;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class DocumentationDialog extends JDialog implements ActionListener, WindowListener
{

	private static final long serialVersionUID = 1L;

	protected JTabbedPane documentationTabs = new JTabbedPane();
	protected DescriptionPanel descriptionPanel = null;
	protected ReferencePanel resourcePanel = null;
	protected CommentsPanel commentsPanel = null;
	protected KeyWordPanel keyWordPanel = null;
	//protected XMLTestPanel xmlPanel = null;
	protected Documentation masterDocumentation = null;

	private String objectName = "";
	private PlanItem planItem = null;
	private JPanel buttonPanel = new JPanel();
	private JButton btnClose = new JButton();
	private JButton btnGenXML = new JButton();
	private JButton btnCreatePDF = new JButton();
	private JButton btnCreateWordDoc = new JButton();
	private GridBagLayout gridBagLayout1 = new GridBagLayout();
//	private ShapeHighlighter shapeHighlight = null;
	private AbstractPlan plan;
	private static Logger logger = Logger.getLogger(DocumentationDialog.class);

	public DocumentationDialog(Frame parent, AbstractPlan plan) throws HeadlessException
	{
		this(parent, null, plan);
		
//		javax.help.CSH.setHelpIDString(descriptionPanel, "Event_Context_Menu");
//	        javax.help.CSH.setHelpIDString(resourcePanel, "Event_Context_Menu");
//	        javax.help.CSH.setHelpIDString(commentsPanel, "Event_Context_Menu");
//	        javax.help.CSH.setHelpIDString(keyWordPanel, "Event_Context_Menu");

	}

	public DocumentationDialog(Frame parent, Guid itemID, AbstractPlan plan) throws HeadlessException
	{
		super(parent, true);
		
		if(itemID != null)
		{
			planItem = plan.getItem(itemID);
			objectName = planItem.getName();
		}
		else
			objectName = plan.getPlanName();
		
		setTitle(objectName + " Documentation");
		this.plan = plan;
		
		try{
			masterDocumentation = (planItem != null) ? planItem.getDocumentation() : plan.getDocumentation();

			descriptionPanel = new DescriptionPanel(objectName, masterDocumentation);
			resourcePanel = new ReferencePanel(objectName, masterDocumentation);
			commentsPanel = new CommentsPanel(objectName, masterDocumentation);
			keyWordPanel = new KeyWordPanel(objectName, masterDocumentation);
			//xmlPanel = new XMLTestPanel(planName, documentation);
			
			javax.help.CSH.setHelpIDString(descriptionPanel, "Event_Context_Menu");
		        javax.help.CSH.setHelpIDString(resourcePanel, "Event_Context_Menu");
		        javax.help.CSH.setHelpIDString(commentsPanel, "Event_Context_Menu");
		        javax.help.CSH.setHelpIDString(keyWordPanel, "Event_Context_Menu");
			
			init();
		}catch(Exception e)
		{
			logger.error("Contstructor - Error initializing dialog:  ", e);
		}
		
//		shapeHighlight = new ShapeHighlighter(sh, ShapeHighlighter.ALPHA);
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void init() throws Exception
	{
		this.getContentPane().setLayout(gridBagLayout1);

		this.addWindowListener(this);

		btnClose.setText("Close");
		btnClose.setActionCommand("Close");
		btnClose.addActionListener(this);

		btnGenXML.setText("Create XML");
		btnGenXML.setActionCommand("GenXML");
		btnGenXML.addActionListener(this);

		btnCreatePDF.setText("Create PDF");
		btnCreatePDF.setActionCommand("CreatePDF");
		btnCreatePDF.addActionListener(this);

		btnCreateWordDoc.setText("Create MS Word Doc");
		btnCreateWordDoc.setActionCommand("CreateWordDoc");
		btnCreateWordDoc.addActionListener(this);

		JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0, 2, 0, 2));
		cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(MainFrm.getInstance().getHelpBroker()));

		this.getContentPane().add(
				documentationTabs,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0,
						0), 0, 0));
		documentationTabs.addTab("Description", descriptionPanel);
		documentationTabs.addTab("Reference Library", resourcePanel);
		documentationTabs.addTab("Comments", commentsPanel);
		documentationTabs.addTab("Key Words", keyWordPanel);
		//documentationTabs.addTab("XML Test", xmlPanel);
		buttonPanel.add(btnGenXML);
		buttonPanel.add(btnCreatePDF);
		buttonPanel.add(btnCreateWordDoc);
		buttonPanel.add(btnClose);
//		buttonPanel.add(cHelpBtn);

//		javax.help.CSH.setHelpIDString(buttonPanel, "documentation.buttons");

		this.getContentPane().add(
				buttonPanel,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 164,
						6, 161), 23, -4));

	}

	private void onWindowCloseOperation()
	{
		descriptionPanel.saveDescription();
		resourcePanel.saveResources();
		commentsPanel.saveComments();
		
		//send an update (so documentation collaborates)
		if(planItem == null) //its plan documentation not item documentation
		{
			PlanArgument arg = new PlanArgument(PlanArgument.PLAN_DOCUMENTATION);
			arg.getParameters().planDoc = masterDocumentation;
			try{
				Control.getInstance().getController(plan.getId()).foreignUpdate(arg);
			}catch(RemoteException exc){
				logger.error("onWindowCloseOperation - RemoteExc sending plan doc update:  "+exc.getMessage());
			}
		}
	}

	//WindowListeners
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
		if(e.getActionCommand().equals("Close"))
		{
			onWindowCloseOperation();
			this.dispose();
		}
		else if(e.getActionCommand().equals("GenXML"))
		{
			descriptionPanel.saveDescription();
			resourcePanel.saveResources();
			commentsPanel.saveComments();
			Document d = masterDocumentation.toXML();
			//          boolean dirCreated = (new File(EnvUtils.getUserHome() + "/DocumentationXML")).mkdir();

			try
			{
				//EnvUtils.getUserHome() + "/DocumentationXML/" + ItemName+ new Random().nextInt(99999999) + ".xml");
				String outFileName = browseForExport("xml", "XML Documents", false);
				File xml = new File(outFileName);

				FileWriter wrtr = new FileWriter(xml);
				d.write(wrtr);
				wrtr.flush();
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "XML file created successfully in the specified directory.");

			}catch(FileNotFoundException exc)
			{
			} //user canceled export (thats otay)
			catch(Exception eee)
			{
				logger.error("actionPerformed - Error generating XML:  " + eee.getMessage());
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "No XML Created! \n" + eee.getMessage());

			}

		}
		else if(e.getActionCommand().equals("CreatePDF"))
		{
			descriptionPanel.saveDescription();
			resourcePanel.saveResources();
			commentsPanel.saveComments();
			//MPG all of this code creates a PDF
			Document d = masterDocumentation.toXML();

			LinkedList resources = new LinkedList();
			LinkedList comments = new LinkedList();
			LinkedList keywords = new LinkedList();

			resources = masterDocumentation.getResources();
			comments = masterDocumentation.getComments();
			keywords = masterDocumentation.getKeyWords();

			boolean createdPDF = true;
			//This is where the PDF generation starts.
			com.lowagie.text.Document document = new com.lowagie.text.Document();

			//boolean dirCreated = (new File(EnvUtils.getUserHome() + "/PDFDocumentation")).mkdir();

			try
			{
				//EnvUtils.getUserHome() + "/PDFDocumentation/" + ItemName+ new Random().nextInt(99999999) + ".pdf")
				String outFileName = browseForExport("pdf", "Adobe PDF files", false);
				PdfWriter.getInstance(document, new FileOutputStream(outFileName));

				HeaderFooter header = new HeaderFooter(new Phrase("JCAT Documentation: " + objectName), false);

				com.lowagie.text.Image wmark = com.lowagie.text.Image.getInstance(this.getClass().getClassLoader().getResource("watermark3.png"));
//				Watermark watermark = new Watermark(wmark, 420, 2);
				//TODO:  changed in new itext library, fix or update this
//				document.add(watermark);
				document.setHeader(header);
				document.setFooter(header);

				document.open();

				document.add(new Paragraph("Description:"));
				document.add(new Paragraph(""));
				document.add(new Paragraph(masterDocumentation.getDescription()));
				document.add(new Paragraph(""));

				//resources
				if(resources.size() > 0)
				{
					Table resourcesTable = new Table(4, resources.size());

					document.add(new Paragraph("Resources:"));
					document.add(new Paragraph(""));

					for(int i = 0; i < resources.size(); i++)
					{
						Resource currentres = (Resource) resources.get(i);

						resourcesTable.addCell(currentres.getLocation(), new Point(i, 0));
						resourcesTable.addCell(currentres.getName(), new Point(i, 1));
						resourcesTable.addCell(currentres.getOriginator(), new Point(i, 2));
						resourcesTable.addCell(currentres.getDate(), new Point(i, 3));
					}
					resourcesTable.setPadding(2);
					document.add(resourcesTable);

				}

				//comments
				if(comments.size() > 0)
				{
					Table commentsTable = new Table(3, comments.size());
					document.add(new Paragraph("Comments:"));
					document.add(new Paragraph(""));

					for(int i = 0; i < comments.size(); i++)
					{
						Comment currentCom = (Comment) comments.get(i);

						commentsTable.addCell(currentCom.getComment(), new Point(i, 0));
						commentsTable.addCell(currentCom.getOriginator(), new Point(i, 1));
						commentsTable.addCell(currentCom.getDate(), new Point(i, 2));
					}
					commentsTable.setPadding(2);
					document.add(commentsTable);
				}

				//keywords
				if(keywords.size() > 0)
				{
					document.add(new Paragraph("Keywords:"));
					document.add(new Paragraph(""));

					if(keywords.size() < 4)
					{
						Table keywordsTable = new Table(4, 1);

						for(int i = 0; i < keywords.size(); i++)
						{
							String currentwrd = (String) keywords.get(i);
							keywordsTable.addCell(currentwrd);

						}
						keywordsTable.setPadding(2);
						document.add(keywordsTable);
					}

					if(keywords.size() >= 4)
					{
						int j = keywords.size();
						Table keywordsTable = new Table(4, (j % 4) + 1);

						for(int i = 0; i < keywords.size(); i++)
						{
							String currentwrd = (String) keywords.get(i);
							keywordsTable.addCell(currentwrd);

						}
						keywordsTable.setPadding(2);
						document.add(keywordsTable);
					}
				}

			}catch(DocumentException de)
			{
				createdPDF = false;
				logger.error("actionPerformed - DocumentException creating PDF:  " + de.getMessage());
			}catch(IOException ioe)
			{
				createdPDF = false;
				logger.error("actionPerformed - IOException creating PDF:  " + ioe.getMessage());
			}

			document.close();
			if(createdPDF)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "PDF file created successfully in the specified directory.");
			}
		}
		else if(e.getActionCommand().equals("CreateWordDoc"))
		{
			descriptionPanel.saveDescription();
			resourcePanel.saveResources();
			commentsPanel.saveComments();
			//MPG all of this code creates a HTML
			Document d = masterDocumentation.toXML();

			LinkedList resources = new LinkedList();
			LinkedList comments = new LinkedList();
			LinkedList keywords = new LinkedList();

			resources = masterDocumentation.getResources();
			comments = masterDocumentation.getComments();
			keywords = masterDocumentation.getKeyWords();

			boolean createdHTML = true;
			//This is where the HTML generation starts.
			com.lowagie.text.Document document = new com.lowagie.text.Document();

			boolean dirCreated = (new File(EnvUtils.getUserHome() + "/WordDocumentation")).mkdir();

			try
			{
				//EnvUtils.getUserHome() + "/WordDocumentation/" + ItemName+ new Random().nextInt(99999999) + ".doc")
				String outFileName = browseForExport("doc", "MS Word Documents", false);
				HtmlWriter.getInstance(document, new FileOutputStream(outFileName));

				com.lowagie.text.Image wmark = com.lowagie.text.Image.getInstance(this.getClass().getClassLoader().getResource("watermark3.png"));

				document.open();

				document.add(new Paragraph("Description:"));

				document.add(new Paragraph(""));

				document.add(new Paragraph(masterDocumentation.getDescription()));

				document.add(new Paragraph(""));

				//resources
				if(resources.size() > 0)
				{
					Table resourcesTable = new Table(4, resources.size());

					document.add(new Paragraph("Resources:"));

					document.add(new Paragraph(""));

					for(int i = 0; i < resources.size(); i++)
					{
						Resource currentres = (Resource) resources.get(i);

						resourcesTable.addCell(currentres.getLocation(), new Point(i, 0));
						resourcesTable.addCell(currentres.getName(), new Point(i, 1));
						resourcesTable.addCell(currentres.getOriginator(), new Point(i, 2));
						resourcesTable.addCell(currentres.getDate(), new Point(i, 3));
					}
					resourcesTable.setPadding(2);
					document.add(resourcesTable);

				}

				//comments

				if(comments.size() > 0)
				{
					Table commentsTable = new Table(3, comments.size());
					document.add(new Paragraph("Comments:"));

					document.add(new Paragraph(""));

					for(int i = 0; i < comments.size(); i++)
					{
						Comment currentCom = (Comment) comments.get(i);

						commentsTable.addCell(currentCom.getComment(), new Point(i, 0));
						commentsTable.addCell(currentCom.getOriginator(), new Point(i, 1));
						commentsTable.addCell(currentCom.getDate(), new Point(i, 2));
					}
					commentsTable.setPadding(2);
					document.add(commentsTable);
				}

				if(keywords.size() > 0)
				{
					document.add(new Paragraph("Keywords:"));

					document.add(new Paragraph(""));

					//keywords
					if(keywords.size() < 4)
					{

						Table keywordsTable = new Table(4, 1);

						for(int i = 0; i < keywords.size(); i++)
						{
							String currentwrd = (String) keywords.get(i);
							keywordsTable.addCell(currentwrd);

						}
						keywordsTable.setPadding(2);
						document.add(keywordsTable);
					}

					if(keywords.size() >= 4)
					{
						int j = keywords.size();
						Table keywordsTable = new Table(4, (j % 4) + 1);

						for(int i = 0; i < keywords.size(); i++)
						{
							String currentwrd = (String) keywords.get(i);
							keywordsTable.addCell(currentwrd);

						}
						keywordsTable.setPadding(2);
						document.add(keywordsTable);
					}
				}

			}catch(DocumentException de)
			{
				createdHTML = false;
				logger.error("actionPerformed - DocumentException creating HTML:  " + de.getMessage());
			}catch(IOException ioe)
			{
				createdHTML = false;
				logger.error("actionPerformed - IOException creating HTML:  " + ioe.getMessage());
			}

			document.close();
			if(createdHTML)
			{
				JOptionPane
						.showMessageDialog(MainFrm.getInstance(),
								"Word document created successfully in the specified directory.");
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

		if(fileChooser.showSaveDialog(MainFrm.getInstance()) == JFileChooser.APPROVE_OPTION)
		{
			if(fileChooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith("." + ext) || dirsOnly)
				return fileChooser.getSelectedFile().getAbsolutePath();
			else
				return fileChooser.getSelectedFile().getAbsolutePath() + "." + ext;
		}
		else
			throw new FileNotFoundException("User canceled export.");
	}

	public void dispose()
	{
//		if(shapeHighlight != null)
//			shapeHighlight.stop();
		super.dispose();
	}
}
