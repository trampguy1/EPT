package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

import mil.af.rl.jcat.gui.FormattedTextEditor;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.COAState;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitationSet;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitedProbability;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.FormattedDocument;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;



public class COAReportDialog extends JDialog implements ActionListener
{
	private COA theCOA;
	private FormattedTextEditor textEditor;
	private JButton closeButton;
	private AbstractPlan thePlan;
	private JButton pdfButton;
	private JButton rtfButton;
	public static int EXPORT_PDF = 0;
	public static int EXPORT_RTF = 1;
	private static Logger logger = Logger.getLogger(COAReportDialog.class);


	public COAReportDialog(Frame parent, AbstractPlan plan, COA inputCOA)
	{
		super(parent, "COA Report");
		setSize(600, 600);
		//setLocation(100, 100);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		thePlan = plan;
		theCOA = inputCOA;
		
		init(generateReport());
		
		setVisible(true);
	}
	
	
	private FormattedDocument generateReport()
	{
		FormattedDocument doc = new FormattedDocument();
		SimpleAttributeSet normStyle = new SimpleAttributeSet(); //the original style
        StyleConstants.setFontFamily(normStyle, "SansSerif");
        StyleConstants.setFontSize(normStyle, 12);
		SimpleAttributeSet boldStyle = new SimpleAttributeSet(normStyle);
		StyleConstants.setBold(boldStyle, true);
		SimpleAttributeSet largerStyle1 = new SimpleAttributeSet(normStyle);
		StyleConstants.setFontSize(largerStyle1, 14);
		SimpleAttributeSet largerStyle2 = new SimpleAttributeSet(boldStyle);
		StyleConstants.setFontSize(largerStyle2, 16);
		SimpleAttributeSet italStyle = new SimpleAttributeSet(normStyle);
		StyleConstants.setItalic(italStyle, true);
		
		
		String name = theCOA.getName();
		boolean trackSched = theCOA.isTrackSchedule();
		boolean trackRes = theCOA.isTrackResources();
		boolean trackElicit = theCOA.isTrackElicited();
		boolean clearUntrack = theCOA.isClearUntracked();
		
		// title
		appendLine(doc, "Course of Action", largerStyle2);
		appendLine(doc, name+"\n", largerStyle1);
		
		
		// print out scheduling/timing if tracked
		if(trackSched)
		{
			appendLine(doc, indent(1)+"Scheduling", boldStyle);
			Iterator<Guid> itemIDs = theCOA.getItemGuids().iterator();
			while(itemIDs.hasNext())
			{
				Guid itemID = itemIDs.next();
				String itemName = thePlan.getItem(itemID).getName();
				String itemLabel = thePlan.getItem(itemID).getLabel();
				
				COAState thisState = theCOA.get(itemID);
				if(thisState.getSchedule().size() < 1) //dont list it at all if theres nothing to put here
					continue;
				
				appendLine(doc, indent(2) + itemName + " (" + itemLabel + ")", normStyle);
				appendLine(doc, indent(3) + "Delay:  "+thisState.getDelay() + "   Persistence:  "+thisState.getPersistance() + "   Continuation:  "+thisState.getContinuation(), italStyle);
				
				TreeMap<Integer, MaskedFloat> sched = thisState.getSchedule();
				Iterator<Integer> times = sched.keySet().iterator();
				while(times.hasNext())
				{
					Integer time = times.next();
					appendLine(doc, indent(3)+"scheduled at time: "+time+"  with probability: "+sched.get(time), normStyle);
				}
			}
			appendLine(doc, "", normStyle);
		}
		
		
		// print out resources allocation if tracked
		if(trackRes)
		{
			appendLine(doc, indent(1)+"Resources", boldStyle);
			Iterator<Guid> itemIDs = theCOA.getItemGuids().iterator();
			while(itemIDs.hasNext())
			{
				Guid itemID = itemIDs.next();
				String itemName = thePlan.getItem(itemID).getName();
				String itemLabel = thePlan.getItem(itemID).getLabel();
				
				COAState thisState = theCOA.get(itemID);
				if(thisState.getResources().size() < 1) //dont list it at all if theres nothing to put here
					continue;
				
				appendLine(doc, indent(2) + itemName + " (" + itemLabel + ")", normStyle);
				HashMap<Guid, ResourceAllocation> res = thisState.getResources();
				Iterator<Guid> ids = res.keySet().iterator();
				while(ids.hasNext())
				{
					ResourceAllocation resAll = res.get(ids.next());
					String cont = (resAll.isContingent()) ? "" : "not";
					appendLine(doc, indent(3)+resAll.getAllocated()+"  "+resAll.getName()+"  allocated and are "+cont+" contingent", normStyle);
				}
			}
			appendLine(doc, "", normStyle);
		}
		
		
		// print out threat resources allocation if tracked
		if(trackRes)
		{
			appendLine(doc, indent(1)+"Threat Resources", boldStyle);
			Iterator<Guid> itemIDs = theCOA.getItemGuids().iterator();
			while(itemIDs.hasNext())
			{
				Guid itemID = itemIDs.next();
				String itemName = thePlan.getItem(itemID).getName();
				String itemLabel = thePlan.getItem(itemID).getLabel();
				
				COAState thisState = theCOA.get(itemID);
				if(thisState.getThreatResources().size() < 1) //dont list it at all if theres nothing to put here
					continue;
				
				appendLine(doc, indent(2) + itemName + " (" + itemLabel +")", normStyle);
				MultiMap<Integer, ResourceAllocation> res = thisState.getThreatResources();
				Iterator<Integer> ids = res.keySet().iterator();
				while(ids.hasNext())
				{
					Integer time = ids.next();
					Iterator<ResourceAllocation> resAlls = res.get(time).iterator();
					while(resAlls.hasNext())
					{
						ResourceAllocation resAll = resAlls.next();
						appendLine(doc, indent(3)+resAll.getAllocated()+"  "+resAll.getName()+"  allocated at time  "+time, normStyle);
					}
				}
			}
			appendLine(doc, "", normStyle);
		}
		
		
		// print out elicited if tracked
		Library theLibrary = thePlan.getLibrary();  //needed to extract signal names
		
		if(trackElicit)
		{
			appendLine(doc, indent(1)+"Elicited Probabilities", boldStyle);
			appendLine(doc, indent(2)+"Causes", boldStyle);
			Iterator<Guid> itemIDs = theCOA.getItemGuids().iterator();
			while(itemIDs.hasNext())
			{
				Guid itemID = itemIDs.next();
				String itemName = thePlan.getItem(itemID).getName();
				String itemLabel = thePlan.getItem(itemID).getLabel();
				
				COAState thisState = theCOA.get(itemID);
				if(thisState.getCauseElicit() == null || thisState.getCauseElicit().size() < 1) //dont list it at all if theres nothing to put here
					continue;
				
				appendLine(doc, indent(3) + itemName + " (" + itemLabel + ")", normStyle);
				ElicitationSet eSet = thisState.getCauseElicit();
				Iterator elicits = eSet.iterator();
				while(elicits.hasNext())
				{
					ElicitedProbability elicit = (ElicitedProbability)elicits.next();
					boolean isGroup = elicit.size() > 1; //ep has more then one signal in it
					
					if(isGroup)
						appendLine(doc, indent(4)+"Group ("+elicit.getGroupName()+") with probability  "+elicit.getProbability(), normStyle);;
					
					Iterator sigGuids = elicit.iterator();
					while(sigGuids.hasNext())
					{
						Signal thisSignal = (Signal)theLibrary.getSignal((Guid)sigGuids.next());
						if(isGroup)
							appendLine(doc, indent(5)+thisSignal.getSignalName(), normStyle);
						else
							appendLine(doc, indent(4)+thisSignal.getSignalName()+"  with probability  "+elicit.getProbability(), normStyle);
					}
				}
			}
			appendLine(doc, "", normStyle);
		}
		
		
		if(trackElicit)
		{
			appendLine(doc, indent(2)+"Inhibitors", boldStyle);
			Iterator<Guid> itemIDs = theCOA.getItemGuids().iterator();
			while(itemIDs.hasNext())
			{
				Guid itemID = itemIDs.next();
				String itemName = thePlan.getItem(itemID).getName();
				String itemLabel = thePlan.getItem(itemID).getLabel();
				
				COAState thisState = theCOA.get(itemID);
				if(thisState.getInhibElicit() == null || thisState.getInhibElicit().size() < 1) //dont list it at all if theres nothing to put here
					continue;
				
				appendLine(doc, indent(3) + itemName + " (" + itemLabel + ")", normStyle);
				ElicitationSet eSet = thisState.getInhibElicit();
				Iterator elicits = eSet.iterator();
				while(elicits.hasNext())
				{
					ElicitedProbability elicit = (ElicitedProbability)elicits.next();
					boolean isGroup = elicit.size() > 1; //ep has more then one signal in it
					
					if(isGroup)
						appendLine(doc, indent(4)+"Group ("+elicit.getGroupName()+") with probability  "+elicit.getProbability(), normStyle);;
					
					Iterator sigGuids = elicit.iterator();
					while(sigGuids.hasNext())
					{
						Signal thisSignal = (Signal)theLibrary.getSignal((Guid)sigGuids.next());
						if(isGroup)
							appendLine(doc, indent(5)+thisSignal.getSignalName(), normStyle);
						else
							appendLine(doc, indent(4)+""+thisSignal.getSignalName()+"  with probability  "+elicit.getProbability(), normStyle);
					}
				}
			}
			appendLine(doc, "", normStyle);
		}
		
		
		return doc;
	}

	
	private void init(FormattedDocument theDoc)
	{
		textEditor = new FormattedTextEditor(theDoc);
		JPanel buttonPanel = new JPanel();
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		pdfButton = new JButton("Export - PDF");
		pdfButton.addActionListener(this);
		rtfButton = new JButton("Export - RTF");
		rtfButton.addActionListener(this);
		buttonPanel.add(pdfButton);
		buttonPanel.add(rtfButton);
		buttonPanel.add(closeButton);
		
		add(textEditor, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void appendLine(FormattedDocument doc, String str, AttributeSet style)
	{
		try{
			doc.insertString(doc.getLength(), str+"\n", style);
		}catch(BadLocationException exc){
			logger.error("appendLine - Bad document insertion location generating COAReport:  "+exc.getMessage());
		}
	}
	
	private String indent(int num)
	{
		String indent = "     ";
		String retIndent = "";
		for(int x=0; x<num; x++)
			retIndent += indent;
		
		return retIndent;
	}
	
	private boolean createPDF(String outPath)
	{
    	try
        {
    		Document pdfDoc = new Document();
			PdfWriter.getInstance(pdfDoc, new FileOutputStream(outPath));
			
			return buildAndExport(pdfDoc);
			
        }catch (IOException ioe){
//            logger.error("actionPerformed - IOException creating PDF:  "+ioe.getMessage());
        	return false;
        }
        catch (DocumentException de){
//      logger.error("actionPerformed - DocumentException creating PDF:  "+de.getMessage());
        	return false;
        }
        
	}
	
	private boolean createRTF(String outPath)
	{
    	try
        {
    		Document rtfDoc = new Document();
		RtfWriter2.getInstance(rtfDoc, new FileOutputStream(outPath));
		
		return buildAndExport(rtfDoc);
			
        }catch (IOException ioe){
//            logger.error("actionPerformed - IOException creating PDF:  "+ioe.getMessage());
        	return false;
        }
//        catch (DocumentException de){
//      logger.error("actionPerformed - DocumentException creating PDF:  "+de.getMessage());
//        	return false;
//        }
        
	}
	
	
	private boolean buildAndExport(Document exportDoc)
	{
		try{
			HeaderFooter header = new HeaderFooter(new Phrase("JCAT Course of Action Report"), false);
			
			com.lowagie.text.Image wmark = com.lowagie.text.Image.getInstance(this.getClass().getClassLoader().getResource("watermark3.png"));
//			Watermark watermark = new Watermark(wmark, 420, 2);
			//TODO:  changed in new itext library, fix or update this
			
//			exportDoc.add(watermark);
			exportDoc.setHeader(header);
			exportDoc.setFooter(header);
			
			exportDoc.open();
			
			exportDoc.add(new Paragraph(""));
			// this maybe be pretty inefficient (processing at character level) but I see no other way to do it
			// as formatting could be done down to the character level
			try{
				FormattedDocument fDoc = textEditor.getDocument();
				for(int x=0; x<fDoc.getLength(); x++)
				{
					String cTxt = fDoc.getText(x, 1); //get the text
					Font cFont = fDoc.getFont(fDoc.getCharacterElement(x).getAttributes()); //get the formatting of the text
					Color clr = fDoc.getForeground(fDoc.getCharacterElement(x).getAttributes());
					com.lowagie.text.Font lowFont = FontFactory.getFont(cFont.getFamily(), cFont.getSize(), cFont.getStyle(), clr);
					exportDoc.add(new Phrase(cTxt, lowFont));
				}
				
			}catch(BadLocationException exc){
				logger.error("buildAndExport - Bad location specified in traversing document:  "+exc.getMessage());
			}
	
			exportDoc.close();
			return true;
			
		}catch (DocumentException de){
//          logger.error("actionPerformed - DocumentException creating PDF:  "+de.getMessage());
			return false;
		}catch(MalformedURLException e){
			return false;
		}catch(IOException e){
			return false;
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
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == closeButton)
		{
			dispose();
		}
		else if(event.getSource() == pdfButton)
		{
			try{
				String path = browseForExport("pdf", "Adobe PDF", false);
				if(createPDF(path))
					JOptionPane.showMessageDialog(this, "PDF exported successfully!");
			}catch(FileNotFoundException exc){
				JOptionPane.showMessageDialog(this, "PDF could not be exported! \n"+exc.getMessage());
			}
		}
		else if(event.getSource() == rtfButton)
		{
			try{
				String path = browseForExport("rtf", "Rich Text Format", false);
				if(createRTF(path))
					JOptionPane.showMessageDialog(this, "RTF exported successfully!");
			}catch(FileNotFoundException exc){
				JOptionPane.showMessageDialog(this, "RTF could not be exported! \n"+exc.getMessage());
			}
		}
	}

}
