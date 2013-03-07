package mil.af.rl.jcat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.util.FormattedDocument;

/**
 * FormattedTextEditor:  A panel which serves as a formatted text editor including the text pane
 * and formatting buttons such as bold, italic etc
 */


public class FormattedTextEditor extends JPanel implements ActionListener
{

	private JTextPane textPane;
	private FormattedDocument doc = null;
	private SimpleAttributeSet[] attributes;
	private static Logger logger = Logger.getLogger(FormattedTextEditor.class);


	public FormattedTextEditor(FormattedDocument inputDoc)
	{
		super(new BorderLayout());

		doc = inputDoc;
		textPane = new JTextPane();

		init();
	}

	public FormattedTextEditor()
	{
		this(null);
	}


	private void init()
	{
//		textPane = new JTextPane();
		textPane.setCaretPosition(0);
		textPane.setMargin(new Insets(5,15,5,15));

		if(doc == null) //TODO: LOW-PRIORITY  once this works right, clean this up
		{
			StyledDocument styledDoc = new FormattedDocument();//textPane.getStyledDocument();
			textPane.setStyledDocument(styledDoc);
			if(styledDoc instanceof AbstractDocument)
				doc = (FormattedDocument)styledDoc;
			else
				logger.warn("init - Text pane's document isn't an AbstractDocument!");
		}
		else
			textPane.setStyledDocument((StyledDocument)doc);

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(300, 200));

		JPanel formatPanel = new JPanel();
		JToggleButton boldButton = new JToggleButton("B");
		boldButton.setFont(boldButton.getFont().deriveFont(Font.BOLD));
		boldButton.setMargin(new Insets(0,2,0,2));
		boldButton.addActionListener(this);
		boldButton.setEnabled(false);
		formatPanel.add(boldButton);
		JToggleButton italButton = new JToggleButton("I");
		italButton.setMargin(new Insets(0,2,0,2));
		italButton.setFont(italButton.getFont().deriveFont(Font.ITALIC));
		italButton.addActionListener(this);
		italButton.setEnabled(false);
		formatPanel.add(italButton);
		JToggleButton underButton = new JToggleButton("U");
		underButton.setMargin(new Insets(0,2,0,2));
		underButton.addActionListener(this);
		underButton.setEnabled(false);
		formatPanel.add(underButton);


		add(scrollPane, BorderLayout.CENTER);
		add(formatPanel, BorderLayout.NORTH);

		attributes = initAttributes();
		textPane.setCharacterAttributes(attributes[0], false);

//		try{
//	doc.insertString(doc.getLength(), "This is\n", attributes[1]);
//		setBold(true);
//		doc.insertString(doc.getLength(), "  a Test\n", attributes[1]);
//		setColor(Color.RED);
//		doc.insertString(doc.getLength(), "mofff!\n", attributes[1]);
//		resetStyleToDefault();
//		doc.insertString(doc.getLength(), "\n\n bla", attributes[1]);
//		}catch(BadLocationException e){
//		System.err.println("bad insert location");
//		}
	}

	private SimpleAttributeSet[] initAttributes()
	{
		SimpleAttributeSet[] attrs = new SimpleAttributeSet[2];

		attrs[0] = new SimpleAttributeSet(); //the original style
		StyleConstants.setFontFamily(attrs[0], "SansSerif");
		StyleConstants.setFontSize(attrs[0], 16);

		attrs[1] = new SimpleAttributeSet(attrs[0]); //the current style (may be modified)

		return attrs;
	}

	public void setColor(Color newColor)
	{
		StyleConstants.setForeground(attributes[1], newColor);
		applyFormat();
	}

	public void setFontSize(int size)
	{
		StyleConstants.setFontSize(attributes[1], size);
		applyFormat();
	}

	public void setBold(boolean val)
	{
		StyleConstants.setBold(attributes[1], val);
		applyFormat();
	}

	public void setItalic(boolean val)
	{
		StyleConstants.setItalic(attributes[1], val);
		applyFormat();
	}

	public void setUnderline(boolean val)
	{
		StyleConstants.setUnderline(attributes[1], val);
		applyFormat();
	}

	public AttributeSet getDefaultStyle()
	{
		return attributes[0];
	}

	public void resetStyleToDefault()
	{
		attributes[1] = attributes[0];
		applyFormat();
	}

	private void applyFormat()
	{
		textPane.setCharacterAttributes(attributes[1], true);
		textPane.grabFocus();
	}

	public FormattedDocument getDocument()
	{
		return doc;
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}


	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() instanceof JToggleButton)
		{
			if(event.getActionCommand().equals("B"))
				setBold(((JToggleButton)event.getSource()).isSelected());
			if(event.getActionCommand().equals("I"))
				setItalic(((JToggleButton)event.getSource()).isSelected());
			if(event.getActionCommand().equals("U"))
				setUnderline(((JToggleButton)event.getSource()).isSelected());
		}
	}


	public static void main(String[] args)
	{
		javax.swing.JFrame testFrame = new javax.swing.JFrame("Textpane Test");
		testFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//		testFrame.add(new FormattedTextEditor(null));

		testFrame.pack();
		testFrame.setVisible(true);
	}


}
