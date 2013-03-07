/*
 * Created on Apr 26, 2004
 */

package mil.af.rl.jcat.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.exceptions.NoSuchNameException;
import mil.af.rl.jcat.gui.dialogs.AboutDialog;
import mil.af.rl.jcat.gui.dialogs.DocumentationDialog;
import mil.af.rl.jcat.gui.dialogs.NodeSearchDialog;
import mil.af.rl.jcat.gui.dialogs.OptionDialog;
import mil.af.rl.jcat.gui.dialogs.PrefsDialog;
import mil.af.rl.jcat.gui.dialogs.PswdDialog;
import mil.af.rl.jcat.gui.dialogs.SecurityDialog;
import mil.af.rl.jcat.gui.dialogs.SignalLibraryEditor;
import mil.af.rl.jcat.gui.dialogs.StatisticsDialog;
import mil.af.rl.jcat.gui.dialogs.XYConstraints;
import mil.af.rl.jcat.gui.dialogs.XYLayout;
import mil.af.rl.jcat.gui.documentationpanels.GraphicalDocPanel;
import mil.af.rl.jcat.gui.dialogs.PlanTrackerDialog;
import mil.af.rl.jcat.gui.dialogs.wizard.TutorialWizard;
import mil.af.rl.jcat.gui.table.MaskedComponent;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.PlanStats;
import mil.af.rl.jcat.util.ShapeHighlighter;
import mil.af.rl.jcat.util.SpellChecker;


public class CatMenuBar extends JMenuBar implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JMenu fileMenu = new JMenu();
	private JMenu editMenu = new JMenu();
	private JMenu recent = new JMenu("Recent");
	private JMenu helpMenu = new JMenu();
	private JMenu toolsMenu = new JMenu();
	private JMenu viewMenu = new JMenu();
	private MainFrm parent = null;

	private JDialog optionBox;
	private JCheckBox evntOpt;
	private JCheckBox mechOpt;
	private PrefsDialog prefsBox;
	private ShapeHighlighter shapeHighlight;
	private JMenuItem joinItem;
	private JMenuItem exitItem;
	private OptionDialog defOptDialog;
	private DockingManager dockManager;
	private static Logger logger = Logger.getLogger(CatMenuBar.class);

	public CatMenuBar(MainFrm parentFrm)
	{
		try
		{
			parent = parentFrm;
			setup();
		}catch(Exception e)
		{
			logger.error("Constructor - error initializing dialog");
		}
	}

	/**
	 * Author Craig McNamara
	 *
	 * This method dispatches button _events to their proper event handler
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("security"))
			onFileSecurity();
		else if(e.getActionCommand().equals("Copy"))
			onEditCopy();
		else if(e.getActionCommand().equals("Paste"))
			onEditPaste();
		else if(e.getActionCommand().equals("Delete"))
			onEditDelete();
		else if(e.getActionCommand().equals("about"))
			new AboutDialog(parent, false);
		else if(e.getActionCommand().equals("New"))
			onFileNew();
		else if(e.getActionCommand().equals("Open"))
			onFileOpen();
		else if(e.getActionCommand().equals("Save"))
			onFileSave();
		else if(e.getActionCommand().equals("Save As"))
			onFileSaveAs();
		else if(e.getActionCommand().equals("Import Siam XML"))
			onImportSiam();
		else if(e.getActionCommand().equals("Close"))
			onFileClose();
		else if(e.getActionCommand().equals("Exit"))
			onFileExit();
		else if(e.getActionCommand().equals("Graphical Documentation"))
			onToolsGraphicalDocumentation();
		else if(e.getActionCommand().equals("Model Documentation"))
			onToolsPlanDocumentation();
		else if(e.getActionCommand().equals("Node Search"))
			this.onToolsNodeSearch();
		else if(e.getActionCommand().equals("User Preferences"))
			prefsBox = new PrefsDialog(parent);
		else if(e.getActionCommand().equals("Model Wizard"))
			new mil.af.rl.jcat.gui.dialogs.wizard.ModelWizard(parent);
		else if(e.getActionCommand().equals("Model Tracker"))
			new PlanTrackerDialog(parent, false);
		else if(e.getActionCommand().equals("Spell Check Model"))
			showSpellOptions();
		else if(e.getActionCommand().equals("OK"))
		{
			optionBox.dispose();
			spellCheckPlan();
		}
		else if(e.getActionCommand().equals("Cancel"))
			optionBox.dispose();
		else if(e.getActionCommand().equals("Model Statistics"))
			showStatistics();
		else if(e.getActionCommand().equals("Edit Signal Library"))
		{
			if(parent.getActiveView() != null)
				new SignalLibraryEditor(parent, parent.getActiveView().getPlan());
		}
		else if(e.getActionCommand().equals("Tutorial"))
			new TutorialWizard(parent);
		else if(e.getActionCommand().equals("Help"))
			parent.getHelpBroker().setDisplayed(true);
		else if(e.getActionCommand().equals("Join Remote Session"))
		{
			String host = JOptionPane.showInputDialog(parent, "Host Address  (using port: " + Control.getInstance().getRmiPort() + ")");
			if(host == null || host.length() < 7)
				return;
			try
			{
				MainFrm.getInstance().createDocument();//mil.af.rl.jcat.plan.AbstractPlan.STANDARD_DEFAULTS_SET);
				Control.getInstance().startClient(parent.getActiveView().getPlan().getId(), host);
			}catch(RemoteException ex)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "An error has occured while attempting to join the session: \n" +
						"If there is another active collaboration session, please exit that session first.");
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "An error has occured while attempting to join the session: \n"+ex.getMessage());
				logger.error("actionPerformed(join) - error joining remote session:  " + ex.getMessage());
			}
		}
		else if(e.getActionCommand().equals("Exit Remote Session"))
		{
			try
			{
				Control.getInstance().stopClient(parent.getActiveView().getPlan().getId());
			}catch(NoSuchNameException exc)
			{
				logger.warn("actionPerformed(exit) - NoSuchNameExc getting active plan:  " + exc.getMessage());
			}catch(NullPointerException exc)
			{
			} //no document is open to end session on
		}
		else if(e.getSource() instanceof MRUItem)
			openMRUItem(e.getActionCommand());
		else if(e.getActionCommand().equals("Export to Genie"))
		{
			JFileChooser fileChooser = new JFileChooser(EnvUtils.getUserDocHome() + EnvUtils.sep);
			fileChooser.setFileFilter(new CatFileFilter("xdsl", "Genie Files", true));
			int response = fileChooser.showSaveDialog(parent);

			try
			{
				if(response == JFileChooser.APPROVE_OPTION)
				{
					//parent.getActiveView().getPlan().getBayesNet().exportToGenie(fileChooser.getSelectedFile());
				} 
			}catch(Exception exc)
			{
				logger.error("actionPerformed - Error exporting to genie:  " + exc.getMessage());
			}
		}
		else if(e.getActionCommand().equals("Default Probabilities"))
		{
			if(parent.getActiveView() != null)
			{
				float[] defaults = null;
				String[] options = new String[]{"Standard", "AND/OR", "User Defined"};
				int opt = JOptionPane.showOptionDialog(parent, "Select default probability set:", "Default Probabilities", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

				if(opt >= 0)
				{
					if(opt == 0)
						defaults = (AbstractPlan.STANDARD_DEFAULTS_SET);
					else if(opt == 1)
						defaults = (AbstractPlan.AND_OR_DEFAULTS_SET);
					else if(opt == 2)
					{
						float[] vals = showDefaultsOptionBox(parent.getActiveView().getPlan().getDefaultProbSet());
						if(vals != null)
							defaults = vals;
					}
					
					if(defaults != null)
						parent.getActiveView().getPlan().setDefaultProbSet(defaults);
				}
				
			}
		}
		else if(e.getActionCommand().equals("defaults-dialog-ok")) //ok button on 'user defined defaults dialog'
		{
			defOptDialog.setAccepted(true);
			defOptDialog.dispose();
		}
		else if(e.getActionCommand().equals("defaults-dialog-cancel")) //cancel button on 'user defined defaults dialog'
		{
			defOptDialog.setAccepted(false);
			defOptDialog.dispose();
		}
		//if there are other JCheckBoxMenuItems other then in view menu, they should 
		else if(e.getSource() instanceof JCheckBoxMenuItem)
			toggleViewItem((JCheckBoxMenuItem)e.getSource());
	}

	private void buildEditMenu()
	{
		editMenu.setText("Edit");
		JMenuItem item;
		/*       item = new JMenuItem("Cut", new ImageIcon(this.getClass().getClassLoader().getResource("stock_cut.png")));
		 item.setEnabled(false);
		 editMenu.add(item); */

		item = new JMenuItem("Copy", new ImageIcon(this.getClass().getClassLoader().getResource("menu_copy.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('C', java.awt.Event.CTRL_MASK));
		item.addActionListener(this);
		editMenu.add(item);

		item = new JMenuItem("Paste", new ImageIcon(this.getClass().getClassLoader().getResource("menu_paste.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('V', java.awt.Event.CTRL_MASK));
		item.addActionListener(this);
		editMenu.add(item);

		item = new JMenuItem("Delete", new ImageIcon(this.getClass().getClassLoader().getResource("menu_delete.gif")));
		item.addActionListener(this);
		editMenu.add(item);

		editMenu.addSeparator();

		JMenuItem planDocumentation = new JMenuItem("Model Documentation", new ImageIcon(this.getClass().getClassLoader().getResource("stock_task.png")));
		planDocumentation.addActionListener(this);
		editMenu.add(planDocumentation);
		
		JMenuItem planDefaults = new JMenuItem("Default Probabilities", new ImageIcon(this.getClass().getClassLoader().getResource("defaults.png")));
		planDefaults.addActionListener(this);
		editMenu.add(planDefaults);
		
		javax.help.CSH.setHelpIDString(editMenu, "Edit_Menu");
		
		this.add(editMenu);

	}

	private void buildFileMenu()
	{
		//Create File Menu
		fileMenu.setText("File");
		JMenuItem item;

		//File New Setup
		item = new JMenuItem("New", new ImageIcon(this.getClass().getClassLoader().getResource("stock_new.png")));
		item.addActionListener(this);
		item.setAccelerator(KeyStroke.getKeyStroke('N', java.awt.Event.CTRL_MASK));
		fileMenu.add(item);

		//File Open Setup
		item = new JMenuItem("Open", new ImageIcon(this.getClass().getClassLoader().getResource("stock_open.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK));
		item.addActionListener(this);
		fileMenu.add(item);

		//Recent open list
		recent.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stock_open.png")));
		recent.setName("RECENT");
		fileMenu.add(recent);

		//File SaveAs Setup
		item = new JMenuItem("Import Siam XML", new ImageIcon(this.getClass().getClassLoader().getResource("stock_macro-insert.png")));
		item.addActionListener(this);
		fileMenu.add(item);

		//for exporting bayes-net to genie format - FOR DEV, NOT FOR RELEASE
		item = new JMenuItem("Export to Genie");
		item.addActionListener(this);
		fileMenu.add(item);

		fileMenu.addSeparator();

		joinItem = new JMenuItem("Join Remote Session", new ImageIcon(this.getClass().getClassLoader().getResource("connect.png")));
		joinItem.setAccelerator(KeyStroke.getKeyStroke('J', java.awt.Event.CTRL_MASK));
		joinItem.addActionListener(this);
		fileMenu.add(joinItem);

		exitItem = new JMenuItem("Exit Remote Session", new ImageIcon(this.getClass().getClassLoader().getResource("disconnect.png")));
		exitItem.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK));
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);

		fileMenu.addSeparator();

		//File Save Setup
		item = new JMenuItem("Save", new ImageIcon(this.getClass().getClassLoader().getResource("stock_save.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.Event.CTRL_MASK));
		item.addActionListener(this);
		fileMenu.add(item);
		//File SaveAs Setup
		item = new JMenuItem("Save As", new ImageIcon(this.getClass().getClassLoader().getResource("stock_save_as.png")));
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();

		//File Close Setup
		item = new JMenuItem("Close", new ImageIcon(this.getClass().getClassLoader().getResource("stock_stop.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.Event.CTRL_MASK)); //Josh - changed hotkey 
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();
		//File Exit Setup
		item = new JMenuItem("Exit", new ImageIcon(this.getClass().getClassLoader().getResource("stock_exit.png")));
		item.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.Event.CTRL_MASK));
		item.addActionListener(this);
		fileMenu.add(item);

		javax.help.CSH.setHelpIDString(fileMenu, "File_Menu");
		this.add(fileMenu);
	}

	private void buildHelpMenu()
	{
		helpMenu.setText("Help");
		JMenuItem aboutMenu = new JMenuItem("About", new ImageIcon(this.getClass().getClassLoader().getResource("stock_unknown.png")));
		aboutMenu.addActionListener(this);
		aboutMenu.setActionCommand("about");

		JMenuItem modelWizItem = new JMenuItem("Model Wizard", new ImageIcon(this.getClass().getClassLoader().getResource("wizardhat.png")));
		modelWizItem.addActionListener(this);

		JMenuItem tutorialItem = new JMenuItem("Tutorial", new ImageIcon(this.getClass().getClassLoader().getResource("tutorial_sm.png")));
		tutorialItem.addActionListener(this);

		JMenuItem helpItem = new JMenuItem("Help", new ImageIcon(this.getClass().getClassLoader().getResource("help_sm.png")));
		helpItem.addActionListener(this);
		
		helpMenu.add(helpItem);
		helpMenu.add(modelWizItem);
		helpMenu.add(tutorialItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutMenu);

		javax.help.CSH.setHelpIDString(helpMenu, "Help_Menu");
		this.add(helpMenu);
	}

	private void buildToolsMenu()
	{
		toolsMenu.setText("Tools");
		
		JMenuItem nodeSearch = new JMenuItem("Node Search", new ImageIcon(this.getClass().getClassLoader().getResource("stock_show-all.png")));
		nodeSearch.addActionListener(this);

		JMenuItem securityItem = new JMenuItem("Security Options", new ImageIcon(this.getClass().getClassLoader().getResource("stock_lock.png")));
		securityItem.addActionListener(this);
		securityItem.setActionCommand("security");

		JMenuItem planSpellCheck = new JMenuItem("Spell Check Model", new ImageIcon(this.getClass().getClassLoader().getResource("spellcheck.png")));
		planSpellCheck.addActionListener(this);

		JMenuItem userPrefItem = new JMenuItem("User Preferences", new ImageIcon(this.getClass().getClassLoader().getResource("prefs.png")));
		userPrefItem.addActionListener(this);

		JMenuItem planTracker = new JMenuItem("Model Tracker", new ImageIcon(this.getClass().getClassLoader().getResource("plantrack.png")));
		planTracker.addActionListener(this);

		JMenuItem planStats = new JMenuItem("Model Statistics", new ImageIcon(this.getClass().getClassLoader().getResource("pstats.png")));
		planStats.addActionListener(this);
		
		JMenuItem signalLibEdit = new JMenuItem("Edit Signal Library", new ImageIcon(this.getClass().getClassLoader().getResource("library.png")));
		signalLibEdit.addActionListener(this);
		
		JMenuItem graphicDoc = new JMenuItem("Graphical Documentation", new ImageIcon(this.getClass().getClassLoader().getResource("stock_presentation.png")));
		graphicDoc.addActionListener(this);
		

		toolsMenu.add(nodeSearch);
		toolsMenu.add(planSpellCheck);
		toolsMenu.add(planStats);
		toolsMenu.add(planTracker);
		toolsMenu.add(graphicDoc);
		toolsMenu.add(signalLibEdit);
		toolsMenu.add(new JSeparator());
		toolsMenu.add(securityItem);
		toolsMenu.add(userPrefItem);

		javax.help.CSH.setHelpIDString(toolsMenu, "Tools_Menu");
		this.add(toolsMenu);
	}

	private void buildViewMenu()
	{
		viewMenu.setText("View");

		javax.help.CSH.setHelpIDString(viewMenu, "View_Menu");
		this.add(viewMenu);
	}

	public void onEditCopy()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPopupManager().copy();
	}

	public void onEditPaste()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPopupManager().paste();
	}

	public void onEditDelete()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPopupManager().deleteShapes();
	}

	//set the checks in the view menu to what the docks are
	public void initViewMenu(DockingManager dm)
	{
		if(viewMenu == null)
		{
			logger.error("initViewMenu - view menu not yet initialized!");
			return;
		}
		
		dockManager = dm;
		
		for(Object o : dm.getAllFrameNames())
		{
			DockableFrame f = dm.getFrame((String)o);
			JCheckBoxMenuItem viewItem = new JCheckBoxMenuItem(f.getTitle(), true);
			viewItem.addActionListener(this);
			viewItem.setSelected(!f.isHidden());
			viewMenu.add(viewItem);
		}
		
		enableCollabViewItem(false);
	}

	public void uncheckViewItem(String val)
	{
		for(int x=0; x<viewMenu.getItemCount(); x++)
			if(viewMenu.getItem(x).getText().equals(val))
				viewMenu.getItem(x).setSelected(false);
		
	}
	
	public void toggleViewItem(JCheckBoxMenuItem item) 
	{
		if(dockManager != null)
		{
			if(item.isSelected())
			{
				dockManager.showFrame(item.getText()); //do twice to fix what seems to be a little bug in JIDE
				dockManager.showFrame(item.getText());
			}
			else
				dockManager.hideFrame(item.getText());
		}
	}
	
	public void enableCollabViewItem(boolean enab)
	{
		for(int x=0; x<viewMenu.getItemCount(); x++)
			if(viewMenu.getItem(x).getText().equalsIgnoreCase("collaboration"))
			{
				viewMenu.getItem(x).setEnabled(enab);
				viewMenu.getItem(x).setSelected(enab);
				return;
			}
		
		logger.warn("enableCollabViewItem - no collaboration view item found in menu");
	}

	public void enableCollabFileItems(boolean enab)
	{
		joinItem.setEnabled(enab);
		exitItem.setEnabled(enab);
	}
	

	public void updateRecent(Vector newRecent)
	{
		recent.removeAll();
		newRecent.trimToSize();
		Iterator checkMRU = newRecent.iterator();

		while(checkMRU.hasNext())
		{
			Object item = checkMRU.next();
			if(item != null)
			{
				MRUItem newItem = new MRUItem(item.toString());
				newItem.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("jcat-doc.png")));
				newItem.addActionListener(this);
				recent.add(newItem);
			}
		}

	}

	private void openMRUItem(String filename)
	{
		MainFrm.getInstance().openDocument(new File(filename), true);
	}

	void onFileClose()
	{
		parent.closePlan();
	}

	void onFileExit()
	{
		parent.exit();
	}

	void onFileNew()
	{
		parent.createDocument();
	}

	void onFileOpen()
	{
		// openDocument() will prompt the user for a file name to open
		MainFrm.getInstance().openDocument();
	}

	void onFileSave()
	{
		if(parent.getActiveView() != null)
			this.parent.getActiveView().save();
	}

	void onFileSaveAs()
	{
		if(parent.getActiveView() != null)
			this.parent.getActiveView().saveAs();
	}

	void onFileSecurity()
	{
		String pass = PswdDialog.showPswdDialog(parent, "JCAT administration");
		if(pass == null)
			return;
		SecurityDialog dlg = null;
		try
		{
			dlg = new SecurityDialog(pass, parent);
			dlg.setVisible(true);
		}catch(Exception ex)
		{
			logger.error("onFileSecurity - Exception occured in security dialog: ", ex);
			JOptionPane.showMessageDialog(parent, "An error occured determining security settings.\n"+ex.getMessage());
			//dlg.dispose();
		}
	}

	void onImportSiam()
	{
		try
		{
			parent.openSiamPlan();
		}catch(DuplicateNameException e)
		{
			logger.error("onImportSiam - duplicate name, plan must already be open, import failed");
		}catch(FileNotFoundException exc)
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), exc.getMessage());
		}
	}

	void onToolsGraphicalDocumentation()
	{
		GraphicalDocPanel docpanel = new GraphicalDocPanel(parent);
		docpanel.setVisible(true);
	}

	void onToolsNodeSearch()
	{
		if(parent.getActiveView() == null)
		{
			JOptionPane.showMessageDialog(parent, "There is no model open.  Please open a model before using this tool.", "Node search", 0);
			return;
		}
		NodeSearchDialog nSearch = new NodeSearchDialog();
		nSearch.setVisible(true);
	}

	void onToolsPlanDocumentation()
	{
		if(parent.getActiveView() == null)
		{
			JOptionPane.showMessageDialog(parent, "There is no model open.  Please open a model before using this tool.", "Model Documentation", 0);
			return;
		}
		DocumentationDialog docpanel = new DocumentationDialog(parent, parent.getActiveView().getPlan());

	}

	public void spellCheckPlan()
	{
		//go through each plan items check spelling
		//rename the item with the returned string
		if(parent.getActiveView() == null)
		{
			JOptionPane.showMessageDialog(parent, "There is no model open.  Please open a model before using this tool.", "Spell check", 0);
			return;
		}

		boolean canceled = false;
		JWBShape thisShape = null;
		java.awt.Color oldColor = new Color(0, 128, 255);

		try{
			JWBController jwbCont = Control.getInstance().getController(
					Control.getInstance().getPlanId(parent.getActiveView().getPanel().getControllerUID()));
			Iterator shapes = jwbCont.getShapes().values().iterator();

			while(shapes.hasNext() && !canceled)
			{
				thisShape = (JWBShape) shapes.next();
				PlanItem item = parent.getActiveView().getPlan().getItem((Guid)thisShape.getAttachment());

				if(item instanceof Event && evntOpt.isSelected() || item instanceof Mechanism && mechOpt.isSelected())
				{
					oldColor = thisShape.getColor();
					thisShape.setColor(new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getAlpha() - 150));
					//shapeHighlight = new ShapeHighlighter(thisShape, ShapeHighlighter.ALPHA);

					try{
						jwbCont.putShape(thisShape);
					}catch(RemoteException exc){
						logger.error("spellCheckPlan - RemoteExc updating checked shape:  "+exc.getMessage());
					}
					parent.getActiveView().getPanel().updateUI();


					String newName = SpellChecker.check(item.getName(), parent);
					if(newName == null)
						canceled = true;
					String newLabel = (!canceled) ? SpellChecker.check(item.getLabel(), parent) : null;
					if(newLabel == null)
						canceled = true;

					if(!canceled && (!newName.equals(item.getName()) || !newLabel.equals(item.getLabel())))
					{
						item.setName(newName);
						item.setLabel(newLabel);
						thisShape.setText(newName + "\n\n" + newLabel);

						try{
							jwbCont.putShape(thisShape);
							jwbCont.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, item, false));
						}catch(RemoteException exc)
						{
							logger.error("spellCheckPlan - RemoteExc updating checked shape:  " + exc.getMessage());
						}
					}

					try{
						thisShape.setColor(oldColor);
						jwbCont.putShape(thisShape);
					}catch(RemoteException exc)
					{
						logger.error("spellCheckPlan - RemoteExc updating checked shape:  " + exc.getMessage());
					}

					parent.getActiveView().getPanel().updateUI();
					//shapeHighlight.stop();
				}
			}
//			parent.getActiveView().getPanel().updateUI();
			if(!canceled)
				JOptionPane.showMessageDialog(parent, "Spell check complete.");
		}catch(MissingRequiredFileException exc)
		{
			//shapeHighlight.stop();
			if(thisShape != null)
				thisShape.setColor(oldColor);
			JOptionPane.showMessageDialog(parent, "Spell check cannot start. \n" + exc.getMessage());
			logger.error("spellCheckPlan - spell check cannot start, missing config file:  " + exc.getMessage());
		}
	}

	public void showSpellOptions()
	{
		optionBox = new JDialog(parent, "Spell Check");
		optionBox.setSize(200, 115);
		optionBox.setLocationRelativeTo(parent);
		Container pane = optionBox.getContentPane();
		pane.setLayout(new XYLayout());
		evntOpt = new JCheckBox("Events", true);
		mechOpt = new JCheckBox("Mechanisms");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		JButton cancButton = new JButton("Cancel");
		cancButton.addActionListener(this);

		pane.add(evntOpt, new XYConstraints(20, 10, 0, 0));
		pane.add(mechOpt, new XYConstraints(20, 30, 0, 0));
		pane.add(okButton, new XYConstraints(40, 55, 0, 0));
		pane.add(cancButton, new XYConstraints(90, 55, 0, 0));

		optionBox.setVisible(true);
	}

	public void showStatistics()
	{
		if(parent.getActiveView() != null)
		{
			StatisticsDialog statBox = new StatisticsDialog(parent);

			statBox.addStat("Name", PlanStats.getName());
			statBox.addStat("Path", PlanStats.getPath());
			statBox.addStat("File Size", PlanStats.getSize());
			statBox.addStat("Event Count", PlanStats.getEventCount());
			statBox.addStat("Mechanism Count", PlanStats.getMechanismCount());
			statBox.addStat("Signal Count", PlanStats.getSignalCount());
			statBox.addStat("Detatched Events", PlanStats.getDetatchedEvents());
			statBox.addStat("Unused signals", PlanStats.getUnusedSignals());
			statBox.addStat("Documented Items", PlanStats.getDocumentedItems());
			statBox.addStat("Active Scheme", PlanStats.getActiveScheme());
		}
	}

//	display option dialog for specifying new default probability values
	private float[] showDefaultsOptionBox(float[] currentVals)
	{
		defOptDialog = new OptionDialog(parent, "Defaults", true);
		defOptDialog.getContentPane().setLayout(new GridLayout(4,1));
		
		if(currentVals == null || currentVals.length < 4)
			currentVals = new float[]{.75f, .8f, 1, .55f};

		JPanel cPanel = new JPanel(new FlowLayout());
		cPanel.add(new JLabel("Causal Probability:  "));
		MaskedComponent cField = new MaskedComponent(MaskedFloat.getMaskedValue(currentVals[0]));
		cPanel.add(cField);
		defOptDialog.add(cPanel);

		JPanel iPanel = new JPanel(new FlowLayout());
		iPanel.add(new JLabel("Inhibiting Probability:  "));
		MaskedComponent iField = new MaskedComponent(MaskedFloat.getMaskedValue(currentVals[1]));
		iPanel.add(iField);
		defOptDialog.add(iPanel);

		JPanel gPanel = new JPanel(new FlowLayout());
		gPanel.add(new JLabel("Group Probability:  "));
		MaskedComponent gField = new MaskedComponent(MaskedFloat.getMaskedValue(currentVals[3]));
		gPanel.add(gField);
		defOptDialog.add(gPanel);

		JPanel butPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("defaults-dialog-ok");
		okButton.addActionListener(this);
		JButton cancButton = new JButton("Cancel");
		cancButton.setActionCommand("defaults-dialog-cancel");
		cancButton.addActionListener(this);
//		JButton loadButton = new JButton("Load");
//		loadButton.addActionListener(this);
		butPanel.add(okButton);
		butPanel.add(cancButton);
		//butPanel.add(loadButton);
		defOptDialog.add(butPanel);

		defOptDialog.pack();
		defOptDialog.setLocationRelativeTo(parent);
		defOptDialog.setVisible(true);

		float[] vals = null;
		while(vals == null && defOptDialog.isAccepted())
		{
			try{
				float cVal = ((MaskedFloat)cField.getValue()).floatValue();
				float iVal = ((MaskedFloat)iField.getValue()).floatValue();
				float gVal = ((MaskedFloat)gField.getValue()).floatValue();

				vals = new float[]{cVal, iVal, 1f, gVal};
			}catch(NumberFormatException exc){
				JOptionPane.showMessageDialog(this, "You have entered invalid values. \n" +
						"Probability values should look similar to this:  .25, .5, .75");
				defOptDialog.setVisible(true);
			}
		}
		return vals;

	}
	
	public PrefsDialog getPrefsBox()
	{
		if(prefsBox != null && prefsBox.isDisplayable() == true)
			return prefsBox;
		else
			return null;

	}

	private void setup() throws Exception
	{
		javax.help.CSH.setHelpIDString(this, "Application_Menu");
		
		//Setup Menus
		buildFileMenu();
		buildEditMenu();
		buildViewMenu();
		buildToolsMenu();
		buildHelpMenu();
	}

}
