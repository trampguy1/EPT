/*
 * Created on Jun 23, 2005
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.ColorSchemeAttrib;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.EnvUtils;

/**
 * @author dygertm
 * This is the tab in user prefs that sets the color scheme settings
 */
public class ColorSchemePanel extends JPanel implements ListSelectionListener, ActionListener, ItemListener
{	

	private static final long serialVersionUID = 1L;
	private TitledBorder panelBorder = new TitledBorder(null,"",2,0);
	public JButton changeButton, newButton, delButton, addButton, remButton, editButton;;
	private JLabel schemeLbl, attribLbl;
	private JList attribView;
	private Vector<ColorScheme> schemes;
	public JComboBox currentScheme;
	private PrefsDialog parent;
	private JLabel icon;
	private PreviewCanvas currentColor = new PreviewCanvas();
	private BufferedImage backgroundImage;
	private Dimension currColorViewSize = new Dimension(60,50);
	private JRadioButton selectNorm, selectAdv;
	private String oldSchemeDir = null;
	private String schemesLocation = EnvUtils.getJCATSettingsHome()+"/schemes";
	private static Logger logger = Logger.getLogger(ColorSchemePanel.class);
	
	public ColorSchemePanel(PrefsDialog dialog)
	{
		super(new XYLayout());
		parent = dialog;
		
		setBorder(panelBorder);
		setBounds(2,2,dialog.getWidth()-8,dialog.getHeight()-70);
				
		changeButton = new JButton("Change");   changeButton.setEnabled(false);
		newButton = new JButton("New");
		newButton.setMargin(new Insets(2,0,2,0));
		addButton = new JButton("Add");   addButton.setEnabled(false);
		remButton = new JButton("Remove");   remButton.setEnabled(false);
		remButton.setMargin(new Insets(2,0,2,0));
		editButton = new JButton("Edit");   editButton.setEnabled(false);
		delButton = new JButton("Delete");
		delButton.setMargin(new Insets(2,0,2,0));
		schemeLbl = new JLabel("Current Scheme:");
		attribLbl = new JLabel("User Attributes:");
		icon = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("scheme.gif")));
		schemes = new Vector<ColorScheme>();
		attribView = new JList();
		currentScheme = new JComboBox(schemes);
		selectNorm = new JRadioButton("Normal");   selectNorm.setEnabled(false);
		selectAdv = new JRadioButton("Advanced", true);   selectAdv.setEnabled(false);
		ButtonGroup chgOpts = new ButtonGroup();
		chgOpts.add(selectNorm);   chgOpts.add(selectAdv);
		
		currentColor.setPreferredSize(currColorViewSize);
		
		currentScheme.addItemListener(this);
		attribView.addListSelectionListener(this);
		changeButton.addActionListener(this);
		newButton.addActionListener(this);
		addButton.addActionListener(this);
		remButton.addActionListener(this);
		editButton.addActionListener(this);
		delButton.addActionListener(this);
		
		int center = getWidth()/2-80;
		add(icon, new XYConstraints(5,5,60,60));
		add(schemeLbl, new XYConstraints(center-20,0,0,0));
		add(currentScheme, new XYConstraints(center-20,15,200,0));
		add(newButton, new XYConstraints(center+190,15,60,0));
		add(delButton, new XYConstraints(center+190,45,60,0));
		add(attribLbl, new XYConstraints(center-140,70,0,0));
		add(new JScrollPane(attribView), new XYConstraints(center-140,85,220,260));
		add(addButton, new XYConstraints(center+90,125,60,0));
		add(editButton, new XYConstraints(center+90,155,60,0));
		add(remButton, new XYConstraints(center+90,185,60,0));
		add(currentColor, new XYConstraints(center+170,125,0,0));
		add(changeButton, new XYConstraints(center+170,185,0,0));
		add(selectNorm, new XYConstraints(center+170,215,0,0));
		add(selectAdv, new XYConstraints(center+170,235,0,0));
		
		// setup preview for current color box
		backgroundImage = new BufferedImage(currColorViewSize.width, currColorViewSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) backgroundImage.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, currColorViewSize.width, currColorViewSize.height);
		graphics.setColor(Color.GRAY);
		graphics.setFont(new Font("Arial", 0, 14));
		graphics.drawString("Color", 12, currColorViewSize.height / 2 + 5);
		graphics.setStroke(new BasicStroke(2));
		graphics.drawRect(0, 0, currColorViewSize.width - 1, currColorViewSize.height - 1);
		graphics.dispose();
		
		loadSchemes();
	}
	
	public void actionPerformed(ActionEvent event)
	{
		ColorSchemeAttrib attrib = null;
		try{
			attrib = (ColorSchemeAttrib)attribView.getSelectedValue();
		}catch(ArrayIndexOutOfBoundsException exc){}
		
		if(event.getSource() == changeButton && attrib != null)
		{
			Color newColor = null;
			if(selectNorm.isSelected())
				newColor = JColorChooser.showDialog(this, attrib.toString(), getCurrentColor());
			else
				newColor = new JCatColorChooser(this, getCurrentColor()).getNewColor();
			if(newColor != null) //if OK was hit
			{
				setCurrentColor(newColor);
				//update the vector data with new color
				Vector currentData = ((ColorScheme)currentScheme.getSelectedItem()).getSchemeData();
				currentData.setElementAt(new ColorSchemeAttrib(attrib.toString(), newColor), currentData.indexOf(attrib));
				attribView.updateUI();
				currentColor.repaint();
			}
		}
		else if(event.getSource() == addButton)
			addAttrib();
		else if(event.getSource() == remButton && attrib != null)
		{
			((ColorScheme)currentScheme.getSelectedItem()).getSchemeData().remove(attrib);
			attribView.updateUI();
		}
		else if(event.getSource() == editButton && attrib != null)
			editAttrib(attrib);
		else if(event.getSource() == newButton)
			newScheme();
		else if(event.getSource() == delButton)
			deleteScheme();
					
	}
	
	// load scheme files from from /schemes directory
	public void loadSchemes()
	{
		File[] schemeFiles = null;
		// if there are no files create and load a default scheme
		// need to relocate this config file (should be in users home directory for permissions)
		String oldLoc = "./schemes";
		String newLoc = schemesLocation;

		if(!(new File(newLoc).exists()) && new File(oldLoc).exists())
		{
			schemeFiles = new File(oldLoc).listFiles(new CatFileFilter("jcs", false));

			oldSchemeDir = oldLoc; //once files are saved in new place, old ones will be deleted
			JOptionPane.showMessageDialog(parent, "Note:  When you press OK on the preferences dialog your saved color schemes will \n" +
			"be relocated from the JCAT program folder to your user home/profile directory.");
		}
		else
			schemeFiles = new File(newLoc).listFiles(new CatFileFilter("jcs", false));

		if(schemeFiles == null)
		{
			new File(newLoc).mkdir();
			fillAttributes(null);
		}
		// otherwise load the scheme files
		else
		{
			int activeIndex = -1;
			int indexOffset = 0; //used incase there are invalid scheme files in the mix
			for(int x=0; x<schemeFiles.length; x++)
			{
				File thisFile = null;
				try{
					thisFile = schemeFiles[x];
					ObjectInputStream input = new ObjectInputStream(new FileInputStream(thisFile));
					// go through this file, pull out a vector with attribs in it
					String name = thisFile.getName().substring(0, thisFile.getName().length()-4);
					Vector attribs = (Vector)input.readObject();
					schemes.add(new ColorScheme(name, attribs));
					if(ColorScheme.getInstance().getName().equals(name))
						activeIndex = x;
				}catch(Exception exc){
					activeIndex = -1;
					indexOffset++;
					logger.error("loadSchemes - Error Reading scheme file ["+thisFile.getName()+"]  "+exc.getMessage());
				}
			}
			if(activeIndex > -1)
				currentScheme.setSelectedIndex(activeIndex-indexOffset);

			// load the scheme stored in plan into the list as it might have been loaded from the plan file
			if(parent.getActivePlan() != null)
			{
				ColorScheme planScheme = parent.getActivePlan().getColorScheme();
				if(!planScheme.getName().equals(""))
				{
					// if one with same name but different data exists create a "name (from file)"
					// this will be only for modifying the scheme, will not be saved in schemes directory
					if(ColorScheme.hasDuplicateNameOnly(schemes, planScheme))
					{
						planScheme.setFromFile(true);
						schemes.add(planScheme);
						currentScheme.setSelectedIndex(currentScheme.getItemCount()-1);
					}
					else if(!schemes.contains(planScheme))
					{
						schemes.add(planScheme);
						currentScheme.setSelectedIndex(currentScheme.getItemCount()-1);
					}
	
				}
			}

			currentScheme.updateUI();
		}
	}
	
	public void saveSchemes()
	{
		try{
			// need to relocate these files (should be in users home directory for permissions)
			boolean newDirCreated = false; 
			if(oldSchemeDir != null)
				newDirCreated = new File(EnvUtils.getJCATSettingsHome()+"/schemes").mkdir();

			Enumeration allSchemes = schemes.elements();
			while(allSchemes.hasMoreElements())
			{
				ColorScheme thisScheme = (ColorScheme)(allSchemes.nextElement());
				if(!thisScheme.isFromFile())
				{
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(EnvUtils.getJCATSettingsHome()+"/schemes/"+thisScheme.toString()+".jcs"));
					output.writeObject(thisScheme.getSchemeData());
					output.close();
				}
			}

			// scheme files sucessfully writted to new location (safe to delete old ones if there is one)
			try{
				if(newDirCreated)
				{
					for(File oldFile : new File(oldSchemeDir).listFiles())
						oldFile.delete();
					new File(oldSchemeDir).deleteOnExit();
					oldSchemeDir = null;
				}		        		
			}catch(Exception exc){
				logger.warn("loadUserPrefs - could not remove scheme file(s) from old location:"+exc.getMessage());
			}

		}catch(IOException exc){
			logger.error("saveSchemes - IOExc Writing scheme files:  "+exc.getMessage());
		}
	}
	
	public void fillAttributes(ColorScheme theScheme)
	{
		Vector inputData = null;
		if(theScheme != null)
			inputData = theScheme.getSchemeData();
		if(inputData != null)
			attribView.setListData(inputData);
		else //create a default scheme and save it to file
		{
			Vector defaultData = new Vector(ColorSchemeAttrib.getDefaultAttribs(Control.getInstance().getDefaultColor()));
			defaultData.add(new ColorSchemeAttrib("Test Attrib 1", Color.ORANGE));
			schemes.add(new ColorScheme("Default", defaultData));
			saveSchemes();
			attribView.setListData(defaultData);
		}
		attribView.updateUI();
	}	
	
	public void newScheme()
	{
		Vector newData = new Vector(ColorSchemeAttrib.getDefaultAttribs(Control.getInstance().getDefaultColor()));
		String name = JOptionPane.showInputDialog(this, "Enter new scheme name:");
		if(name != null && !name.trim().equals(""))
		{
			if(!schemes.contains(name))
				schemes.add(new ColorScheme(name, newData));
			else
				JOptionPane.showMessageDialog(this, "Scheme already exists, use another name or edit existing scheme.");
			
			currentScheme.setSelectedIndex(currentScheme.getItemCount()-1);
			attribView.setListData(newData);
			attribView.updateUI();
		}
	}
	
	public void deleteScheme()
	{
		try{
			new File(schemesLocation+"/"+((ColorScheme)currentScheme.getSelectedItem()).toString()+".jcs").delete();
			schemes.remove((ColorScheme)currentScheme.getSelectedItem());
			currentScheme.setSelectedIndex(0);
		}catch(SecurityException exc){
			JOptionPane.showMessageDialog(this, "Could not delete this scheme, check your file permissions.");
		}catch(IllegalArgumentException exc){
			currentScheme.setSelectedIndex(-1);
			toggleButtons(false);
			attribView.setListData(new Vector());
		}catch(NullPointerException exc){
			logger.error("deleteScheme - NullPointer deleting scheme", exc);
		}
	}
	
	public void addAttrib()
	{
		String name = JOptionPane.showInputDialog(this, "Enter attribute name:");
		if(name != null)
		{
			if(attribView.getModel().getSize() < 1 || attribView.getNextMatch(name, 0, Position.Bias.Forward) == -1)
			{
				ColorSchemeAttrib newAttrib = new ColorSchemeAttrib(name, Color.BLACK);
				((ColorScheme)currentScheme.getSelectedItem()).getSchemeData().add(newAttrib);
				attribView.setSelectedIndex(attribView.getModel().getSize()-1);
			}
			else
				JOptionPane.showMessageDialog(this, "Attribute already exists, use another name or edit existing attribute.");
		}
		attribView.updateUI();
	}
	
	public void editAttrib(ColorSchemeAttrib attribute)
	{
		String newName = JOptionPane.showInputDialog(this, "Enter new attribute name:", attribute.toString());
		if(newName != null)
			attribute.setName(newName);
		attribView.updateUI();
	}
	
	public Color getCurrentColor()
	{
		return currentColor.getBackground();
	}
	
	public void setCurrentColor(Color newColor)
	{
		currentColor.setBackground(newColor);
	}

	
	public ColorScheme getSelectedScheme()
	{
		return ((ColorScheme)currentScheme.getSelectedItem());
	}
	
	//user selected a different value in the list
	public void valueChanged(ListSelectionEvent arg0)
	{
		//get the color for this attribute and set the current color to it
		setCurrentColor((ColorSchemeAttrib)attribView.getSelectedValue());
	}

	public void toggleButtons(boolean value)
	{
		changeButton.setEnabled(value);
		addButton.setEnabled(value);
		remButton.setEnabled(value);
		editButton.setEnabled(value);
		selectNorm.setEnabled(value);
		selectAdv.setEnabled(value);
	}
	//scheme selection changed
	public void itemStateChanged(ItemEvent event)
	{
		toggleButtons(true);
		if(event.getStateChange() == ItemEvent.SELECTED)
			fillAttributes((ColorScheme)currentScheme.getSelectedItem());
	}

	//ahh an inner class, may I burn in hell - used to show the transparency of the currentColor
	private class PreviewCanvas extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) 
	    {
	      Graphics2D graphics = (Graphics2D)g;
	      graphics.drawImage(backgroundImage, null, 0, 0);
	      graphics.setColor(getCurrentColor());
	      graphics.fillRect(0, 0, currColorViewSize.width, currColorViewSize.height);
	      graphics.dispose();
	    }
	}
}