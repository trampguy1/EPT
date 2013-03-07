/*
 * Created on Oct 17, 2005
 * Author:  Mike Dygert
 * Dialog for selecting a font
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

public class FontSelectionDialog extends JDialog implements ActionListener
{

	private static final long serialVersionUID = -4860826620101562408L;
	private JPanel mainPane = null;
	private JLabel typeLbl, styleLbl, sizeLbl = null;
	private JList fontList, styleList = null;
	private JSpinner sizeSelector = null;
	private JButton okButton, cancButton = null;
	private JScrollPane typeScroll = null;
	private JCheckBox doShapes, doLines;
	private boolean approved = false;
	private Font theFont;
	private JButton colorButton;
	private Color currColor;
	private Color theColor;
	private JPanel colorPanel;

	public FontSelectionDialog(javax.swing.JFrame parent, Font defaultFont, Color defaultColor)
	{
		super(parent, "Font");
		setModal(true);
		setResizable(false);
		theFont = defaultFont;
		currColor = defaultColor;
		initialize();
		setLocationRelativeTo(parent);
		
		if(defaultFont != null)
		{
			fontList.setSelectedValue(defaultFont.getFamily(), true);
			styleList.setSelectedIndex(defaultFont.getStyle());
			sizeSelector.setValue(defaultFont.getSize());
		}
		else
		{
			fontList.setSelectedIndex(0);
			styleList.setSelectedIndex(0);
		}
		if(defaultColor != null)
			colorPanel.setBorder(BorderFactory.createLineBorder(currColor, 3));
		else
		{
			colorButton.setEnabled(false);
			doLines.setEnabled(false);
			doShapes.setEnabled(false);
		}
		
		setVisible(true);
	}

	public Font getSelectedFont()
	{
		return theFont;
	}
	
	public Color getSelectedColor()
	{
		return theColor;
	}
	
	public boolean isDoShapes()
	{
		return doShapes.isSelected();
	}
	
	public boolean isDoLines()
	{
		return doLines.isSelected();
	}
	
	public boolean wasApproved()
	{
		return approved;
	}
	
	private void initialize()
	{
		this.setSize(315, 300);
		sizeLbl = new JLabel("Size:");
		sizeLbl.setBounds(new java.awt.Rectangle(200, 100, 38, 16));
		styleLbl = new JLabel("Style:");
		styleLbl.setBounds(new java.awt.Rectangle(200, 10, 54, 16));
		typeLbl = new JLabel("Type:");
		typeLbl.setBounds(new java.awt.Rectangle(10, 10, 38, 16));
		sizeSelector = new JSpinner(new SpinnerNumberModel(10, 5, 100, 1));
		sizeSelector.setBounds(new java.awt.Rectangle(200, 120, 65, 20));
		styleList = new JList(new String[]{"Regular", "Bold", "Italic"});
		styleList.setBounds(new java.awt.Rectangle(200, 30, 96, 60));
		colorPanel = new JPanel(null);
		colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		colorPanel.setBounds(200, 150, 96, 28);
		colorButton = new JButton("Color");
		colorButton.setBounds(3,3,90,22);
		colorButton.addActionListener(this);
		colorPanel.add(colorButton);
		JLabel applyToLbl = new JLabel("Apply to selected:");
		applyToLbl.setBounds(10, 185, 120, 20);
		doShapes = new JCheckBox("Events", true);
		doShapes.setBounds(20, 205, 75, 20);
		doLines = new JCheckBox("Mechanisms", true);
		doLines.setBounds(110, 205, 130, 20);
		okButton = new JButton("OK");
		okButton.setBounds(new java.awt.Rectangle(70, 240, 75, 24));
		okButton.addActionListener(this);
		cancButton = new JButton("Cancel");
		cancButton.setBounds(new java.awt.Rectangle(160, 240, 75, 24));
		cancButton.addActionListener(this);
		typeScroll = new JScrollPane();
		typeScroll.setBounds(new java.awt.Rectangle(10, 30, 166, 151));
		fontList = new JList(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		typeScroll.setViewportView(fontList);
		mainPane = new JPanel();
		mainPane.setLayout(null);
		mainPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		
		mainPane.add(typeLbl);
		mainPane.add(styleLbl);
		mainPane.add(styleList);
		mainPane.add(sizeLbl);
		mainPane.add(sizeSelector);
		mainPane.add(colorPanel);
		mainPane.add(okButton);
		mainPane.add(cancButton);
		mainPane.add(typeScroll);
		mainPane.add(applyToLbl);
		mainPane.add(doShapes);
		mainPane.add(doLines);
		
		this.setContentPane(mainPane);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{	
			approved = true;
			theFont = new Font((String)fontList.getSelectedValue(), styleList.getSelectedIndex(), (Integer)sizeSelector.getValue());
			theColor = currColor;
			dispose();
		}
		else if(event.getSource() == cancButton)
			dispose();
		else if(event.getSource() == colorButton)
		{
			currColor = JColorChooser.showDialog(this, "Change Color", currColor);
			colorPanel.setBorder(BorderFactory.createLineBorder(currColor, 3));
		}

	}
}
