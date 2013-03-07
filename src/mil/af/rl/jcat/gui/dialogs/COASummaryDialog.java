package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import mil.af.rl.jcat.gui.FormattedTextEditor;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.util.FormattedDocument;



public class COASummaryDialog extends JDialog implements ActionListener
{
	
	/**
	 * problem here is how to save the abstract document with its formatting intact into and XML structure
	 * a fairly complex xml struction can be devised but how do you figure out the formatting of text once
	 * it is in the document?
	 */

	private FormattedDocument document = null;
	private FormattedTextEditor textEditor;
	private JButton closeButton;
	private COA theCOA;

	public COASummaryDialog(Frame parent, COA inputCOA)
	{
		super(parent, "COA Summary");
		setSize(600, 400);
		//setLocation(100, 100);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		theCOA = inputCOA;
		document = theCOA.getSummary();
		
		init();
		setVisible(true);
	}
	
	
	public void init()
	{
		textEditor = new FormattedTextEditor(document);
		JPanel buttonPanel = new JPanel();
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		
		add(textEditor, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == closeButton)
		{
			theCOA.setSummary(textEditor.getDocument());
			dispose();
			
			//test the shit by writing to file
//			Document outDoc = textEditor.getDocument().getXML();
//			OutputFormat format = OutputFormat.createPrettyPrint();
//	        try{
//	            XMLWriter writer = new XMLWriter(new FileOutputStream("test.xml"), format);
//	            writer.write(outDoc);
//	        }catch(UnsupportedEncodingException e){
//	            e.printStackTrace();
//	        }catch(IOException e){
//	            e.printStackTrace();
//	        }
//			System.exit(0); ///// TAKE THIS OUT LATER!!
		}
	}

	
	public static void main(String[] args)
	{
		//test the shit by loading from file
		FormattedDocument fDoc = null;
		try{
			Document doc = new SAXReader().read("test.xml");
			fDoc = new FormattedDocument();
			fDoc.parseXML(doc.getRootElement());
		}catch(DocumentException e){
			System.err.println("file not found");
		}
		
		new COASummaryDialog(new Frame(), null);
	}
}
