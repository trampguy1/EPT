package mil.af.rl.jcat.util;

import java.util.Iterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



public class FormattedDocument extends DefaultStyledDocument
{
	transient private static Logger logger = Logger.getLogger(FormattedDocument.class);
	
	
	public FormattedDocument()
	{
		
	}

	public FormattedDocument(Element docEl)
	{
		this();
		parseXML(docEl);
	}
	
	
	/**
	 * We would normally call this getDocument but since this Object involves other kinds of 'documents' we'll rename
	 * Generate XML (using dom4j stuff just to fit in) representation of the docuement for output to file
	 * Current support will be only for line breaks in the interest of time (need for stratcom asap)
	 * Later there will be other formatting support hopefully (bold, italic, underline etc)
	 */
	public Document getXML()
	{
		Document doc = DocumentHelper.createDocument();
        Element contEl = doc.addElement("Content");
		
        
        //make a paragraph element for all text between line returns
        try{
			String text = this.getText(0, getLength());
			//char value represented as an int of 10 is a 'line return'
			
			int index = 0;
			
			while(index < text.length())
			{
				int nextIndex = text.indexOf(10, index);
				if(nextIndex < 0)
					nextIndex = text.length();
				String thisPara = text.substring(index, nextIndex);
				contEl.addElement("Paragraph").setText(thisPara);
				
				index = nextIndex + 1;
			}
			
		}catch(BadLocationException e){
			if(logger != null)
				logger.error("getXML - error building xml for a formattedDocument:  "+e.getMessage());
		}
        
        return doc;
	}
	
	/**
	 * Does the opposite of getXML, usually would be called parseDocument()
	 */
	public void parseXML(Element doc)
	{
		Element contEl = (Element)doc.selectSingleNode("Content");
		
		Iterator subEls = contEl.selectNodes("Paragraph").iterator();
		while(subEls.hasNext())
		{
			Element paraEl = (Element)subEls.next();
			try{
				this.insertString(getLength(), paraEl.getText()+"\n", null);
			}catch(BadLocationException e){
				logger.error("parseXML - Unable to insert text while parsing formatted text document:  "+e.getMessage());
			}
			
		}
	}
}
