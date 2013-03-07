/*
 * Created on Sep 13, 2005
 * Author: MikeyD
 */
package mil.af.rl.jcat.gui.dialogs.wizard;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;


public abstract class WizardDialog extends JDialog implements ActionListener
{
	private JPanel mainPane = null;
	private JButton nextButton, backButton, cancButton;
	private JPanel buttonPanel, topPanel, infoPanel, optionPanel;
	private JLabel sideImage;
	private JTextArea infoTitle;
	private JEditorPane infoTxt;
	private Vector[] options = {new Vector()};
	String[] titles = {""};
	String[] messages = {""};
	int pageIndex = 0;
	private boolean showIndex = false;
	private JLabel[] indexItems;
	private Font indexRegFont = new Font("Arial", 0, 11);
	private Font indexBoldFont = indexRegFont.deriveFont(1);
	private JScrollPane infoTxtScrl;
	private String WindowTitle;
	private static Logger logger = Logger.getLogger(WizardDialog.class);
	
	
	public WizardDialog(java.awt.Frame parent, java.awt.Dimension size, String[] title, String[] text, boolean useIndex, String WindowTitle1)
	{
		super(parent);
		titles = title;
		messages = text;
		//options = opts;
		showIndex = useIndex;
		WindowTitle = WindowTitle1;
		
		setResizable(false);
		setSize(size);
		setLocationRelativeTo(parent);
		setAlwaysOnTop(true);
		initialize();
		
		infoTitle.setText(title[0]);
		infoTxt.setText(text[0]);
	}
	
	private void initialize()
	{
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(WindowTitle);
		mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		mainPane.add(getButtonPanel(), BorderLayout.SOUTH);
		mainPane.add(getTopPanel(), BorderLayout.CENTER);
		this.setContentPane(mainPane);
	}
	
	private JPanel getButtonPanel()
	{
		if (buttonPanel == null)
		{
			buttonPanel = new JPanel();
			buttonPanel.setLayout(null);
			buttonPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.SoftBevelBorder.LOWERED));
			buttonPanel.setPreferredSize(new java.awt.Dimension(100,40));
			nextButton = new JButton("Next");
			nextButton.setBounds(new java.awt.Rectangle(getWidth()-100,8,80,25));
			nextButton.addActionListener(this);
			backButton = new JButton("Back");
			backButton.setBounds(new java.awt.Rectangle(getWidth()-190,8,70,25));
			backButton.setEnabled(false);
			backButton.addActionListener(this);
			cancButton = new JButton("Cancel");
			cancButton.setMargin(new java.awt.Insets(2,10,2,10));
			cancButton.setBounds(new java.awt.Rectangle(15,8,70,25));
			cancButton.addActionListener(this);
			
			buttonPanel.add(nextButton);
			buttonPanel.add(backButton);
			buttonPanel.add(cancButton);
		}
		return buttonPanel;
	}
	
	private JPanel getTopPanel()
	{
		if (topPanel == null)
		{
			sideImage = new JLabel();
			sideImage.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("wizard_cat.jpg")));
			JScrollPane indexScroll = new JScrollPane();
			indexScroll.setViewportView(createIndex());
			indexScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			indexScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			indexScroll.setAutoscrolls(true);
			topPanel = new JPanel();
			topPanel.setBackground(new java.awt.Color(17,39,86));
			topPanel.setLayout(new BorderLayout());
			if(!showIndex)
				topPanel.add(sideImage, BorderLayout.WEST);
			else
				topPanel.add(indexScroll, BorderLayout.WEST);
			infoPanel = new JPanel(new BorderLayout());
			infoPanel.setBackground(java.awt.Color.white);
			
			infoTitle = new JTextArea(titles[0]);
			infoTitle.setEditable(false);
			infoTitle.setFont(new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 16));
			infoTitle.setMargin(new java.awt.Insets(10,5,5,5));
			infoTxt = new JEditorPane("text/html", messages[0]);
			infoTxt.setFont(new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 14));
			infoTxt.setMargin(new java.awt.Insets(20,10,10,10));
			infoTxt.setEditable(false);
			infoTxtScrl = new JScrollPane(infoTxt);
			infoTxtScrl.setBorder(null);
			
			infoPanel.add(infoTitle, BorderLayout.NORTH);
			infoPanel.add(infoTxtScrl, BorderLayout.CENTER);
			infoPanel.add(getOptionPanel(), BorderLayout.SOUTH);
			topPanel.add(infoPanel, java.awt.BorderLayout.CENTER);
		}
		return topPanel;
	}
	
	public JPanel createIndex()
	{
		JPanel indexPan = new JPanel(new java.awt.GridLayout(titles.length, 1));
		indexPan.setBackground(new java.awt.Color(17,39,86));
		indexItems = new JLabel[titles.length];
		for(int x=0; x<titles.length; x++)
		{
			JLabel item = new JLabel(titles[x]);
			item.setForeground(java.awt.Color.white);
			item.setFont(indexRegFont);
			indexPan.add(item);
			indexItems[x] = item;
		}
		
		if(indexItems.length > 0)
			indexItems[0].setFont(indexBoldFont);
		return indexPan;
	}
	
	private JPanel getOptionPanel()
	{
		if (optionPanel == null)
		{
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout.setHgap(10);
			optionPanel = new JPanel();
			optionPanel.setBackground(java.awt.Color.white);
			optionPanel.setLayout(flowLayout);
			optionPanel.setBounds(new java.awt.Rectangle(19,197,382,62));

			updatePage();
			
		}
		return optionPanel;
	}

	public String getCurrentTitle()
	{
		return titles[pageIndex];
	}
	
	public int getCurrentPage()
	{
		return pageIndex;
	}
	
	public Vector getCurrentOptions()
	{
		return options[pageIndex];
	}
	
	public void setTitles(String[] tls)
	{
		titles = tls;
		updatePage();
	}
	
	public void setMessages(String[] msgs)
	{
		messages = msgs;
		updatePage();
	}
	
	public void setOptions(Vector[] opts)
	{
		options = opts;
		updatePage();
	}
	
	public void updatePage()
	{
		try{
			infoTitle.setText(titles[pageIndex]);
			infoTxt.setText(messages[pageIndex]);
			
			//update the options
			optionPanel.removeAll();
		
			java.util.Iterator opts = options[pageIndex].iterator();
			while(opts.hasNext())
			{
				JComponent opt = (JComponent)opts.next();
				if(opt instanceof JCheckBox || opt instanceof JRadioButton)
					opt.setOpaque(false);
				else if(opt instanceof JTextField)
					opt.setPreferredSize(new java.awt.Dimension(250,20));
				else if(opt instanceof JTextArea)
					opt.setPreferredSize(new java.awt.Dimension(250,50));
				
				optionPanel.add(opt);
			}
			if(isVisible())
				pageChanged(pageIndex);
			infoTxt.setCaretPosition(0);
			topPanel.updateUI();
		}catch(ArrayIndexOutOfBoundsException exc){
			logger.warn("updatePage - Content missing for this page - "+pageIndex);   
		}
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == nextButton)
		{
			if(nextButton.getText().equals("Finish"))
				dispose();
			else if(verifyInputs())
			{
				nextPressed();
				pageIndex++;
				if(pageIndex-1 >= 0)
					indexItems[pageIndex-1].setFont(indexRegFont);
				indexItems[pageIndex].setFont(indexBoldFont);
				updatePage();
				checkButtons();
			}
			else
				JOptionPane.showMessageDialog(this, "It does not appear that you have filled in all necessary data. \nPlease verify your inputs and then continue.");
		}
		else if(event.getSource() == backButton)
		{
			backPressed();
			pageIndex--;
			if(pageIndex+1 >= 0)
				indexItems[pageIndex+1].setFont(indexRegFont);
			indexItems[pageIndex].setFont(indexBoldFont);
			updatePage();
			checkButtons();
		}
		else if(event.getSource() == cancButton)
			cancelPressed();
	}
	
	public void checkButtons()
	{
		if(pageIndex+1 > messages.length-1)
			nextButton.setText("Finish");
		else nextButton.setText("Next");
		if(pageIndex-1 < 0)
			backButton.setEnabled(false);
		else backButton.setEnabled(true);
	}
	
	public boolean verifyInputs()
	{
		try{
			if(options[pageIndex].size() < 1)  //if there are no options
				return true;
			java.util.Iterator opts = options[pageIndex].iterator();
			while(opts.hasNext())
			{
				Object opt = opts.next();
				if(opt instanceof JTextField || opt instanceof JTextArea)
				{
					if(((JTextComponent)opt).getText().trim().equals(""))
						return false;
				}
			}
			return true;
		}catch(ArrayIndexOutOfBoundsException exc){
			logger.warn("verifyInputs - info missing for this page:  "+pageIndex);
			return true;
		}
	}
	
	
	public abstract void nextPressed();
	public abstract void backPressed();
	public abstract void cancelPressed();
	public abstract void pageChanged(int currentPage);
	
}
