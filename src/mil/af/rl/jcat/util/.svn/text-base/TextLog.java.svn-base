/*
 * Created on Nov 1, 2005
 * Mike Dygert
 * Log text pane with color support
 */
package mil.af.rl.jcat.util;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;


public class TextLog extends JTextPane
{
	
	private static final long serialVersionUID = 1L;
	private SimpleAttributeSet[] attrs = null;
	private AbstractDocument doc = null;
	private static Logger logger = Logger.getLogger(TextLog.class);

	public TextLog()
	{
		setEditable(false);
		
		try{
			StyledDocument styledDoc = getStyledDocument();
			if(styledDoc instanceof AbstractDocument)
				doc = (AbstractDocument)styledDoc;
			else throw new Exception("TextLog init error!");
			initAttributes();
		}catch(Exception exc){
			logger.error("Constructor - error initializing document:  "+exc.getMessage());
		}
	}
		
	private void initAttributes()
	{
        attrs = new SimpleAttributeSet[1];

        attrs[0] = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs[0], "Arial");
        StyleConstants.setFontSize(attrs[0], 10);
    }
	
	public void append(String text, Color clr, boolean bold)
	{
		try{
			StyleConstants.setForeground(attrs[0], clr);
			StyleConstants.setBold(attrs[0], bold);
			doc.insertString(doc.getLength(), text + "\n", attrs[0]);
			autoScroll();
		}catch(BadLocationException exc){
			logger.error("append - BadLocationExc trying to insert text:  "+exc.getMessage());
		}
	}
	
	public void autoScroll()
	{
		try{
			int pos = this.getDocument().getLength();
			setCaretPosition(pos);
		}catch(IllegalArgumentException exc){}
	}
}
