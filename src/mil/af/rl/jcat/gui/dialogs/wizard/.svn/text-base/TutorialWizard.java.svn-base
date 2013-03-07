/*
 * Created on Sep 15, 2005
 * Author: MikeyD
 */
package mil.af.rl.jcat.gui.dialogs.wizard;

import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;
import com.c3i.jwb.shapes.JWBRoundedRectangle;
import com.jidesoft.docking.DockingManager;

import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.CatMenuBar;
import mil.af.rl.jcat.gui.FileToolBar;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.PlanToolBar;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.util.Guid;

public class TutorialWizard extends WizardDialog
{
	private static final long serialVersionUID = 1L;
	private JWBController controller;
	private AbstractPlan plan;
	private JWBShape from;
	private JWBShape to;
	private ArrowWindow arrow;
	private CatMenuBar menuBar;
	private FileToolBar toolBar;
	private PlanToolBar planToolbar;
	private DockingManager dockManager;
	private static Logger logger = Logger.getLogger(TutorialWizard.class);
	
	static private String[] titles = 
	{
		"Welcome to the JCat tutorial!", //0
		"The Menu bar",
		"The Menu bar - File", //2	//File menu
		"The Menu bar - Edit",     //Edit menu
		"The Menu bar - View",	//4	//View menu
		"The Menu bar - Tools", 	//Tools menu
		"User Preferences",	//6	//User preferences
		"Color Scheme", 	//Color scheme
		"The Menu bar - Help",	//8	//Help menu
		"The File toolbar", 
		"The Model toolbar",	//10
		"Probability Profiles Chart dock", 
		"The Navigation tree dock", //12
		"Property Viewer dock", //13
		"Scheme Legend dock",
		"Course of Action dock", //15
		"Collaboration dock", //16
		"Model View", 	//17
		"Model Popup menu",
		"Shape Popup menu", //19
		"Shapes Popup menu",
		"Dialogs - Events", //21	//Event edtr
		"Dialogs - Mechanisms",		//Mechanism
		"Dialogs - Timing", //23	//Timing
		"Dialogs - Graphical Documentation",	//graphical doc
		"Dialogs - Model Documentation", //25	//plan doc
		
		"Finished!" //end
	};
	
	static private String[] messages = 
	{
		"This tutorial will guide you through nearly all aspects of using JCat and its various tools. <p>" +
		"The yellow arrow will point out areas of interest relating to the current topic.",  //0
		
		"The Menu bar in JCat located on the top of the window provides access to many important tools and options.<p>" +
		"Press next to go through the items in each menu.",
		
		"The File menu contains operations which you would perform on the current model or file. Below are explainations " +
		"for each of the functions contained in this menu. <p><b>New</b> -- Opens a new model  in the model view(described later)<br>" +
		"<b>Open</b> -- Opens a previously saved JCAT file <br><b>Recent</b> -- A sub-menu of recently opened/saved JCAT files which you may click to open<br>" +
		"<b>Import Siam XML</b> -- Converts a Siam to JCat format and opens it for editing <br> " +
		"<b>Join Remote Session</b> -- Connects to a collaboration session hosted by another computer <br><b>Exit Remote Session</b> -- " +
		"Disconnects and active collaboration session connected to a remotely hosted model <br><b>Save/SaveAs</b> -- Saves the current model to the file of " +
		"your choice <br><b>Close</b> -- Closes the current model, prompts for saving if necessary <br><b>Exit</b> -- Exits JCat closing all open models",  //2
		
		"The Edit menu contains operations which will help you to change a model."+
		"<p><b>Copy</b> -- Copies selected Events and Mechanisms to the clipboard, retaining the layout and relationships <br><b>Paste</b>"+
		" -- Adds any Events and Mechanisms on the clipboard to the current model <br><b>Delete</b> -- Permanantly deletes any Events or Mechanisms selected <br>"+
		"<b>Model Documentation</b> -- A tool for entering documentation, comments and keywords to be stored within your model <br>" +
		"<b>Default Probabilities</b> -- Specify probability values to be used by default while building your model"
		,
		"The View menu is used to show or hide any of the dock windows within JCat. Docks are individual windows inside JCat which can be " +
		"shuffled around or closed to match your needs. Each of the following dock windows will be explained later in this tutorial. <p><b>" +
		"Probability Profiles <br>Navigation Tree <br>Scheme Legend <br>Property Viewer <br>Course of Action <br>Collaboration </b>", //4
		
		"The Tools menu contains a variety of tools as well as various user options. Below are explainations for each of the functions " +
		"contained in this menu. <p>" +
		"<b>Node Search</b> -- Utility for searching a word in your model <br><b>Spell Check Model</b> -- A tool for spell checking all nodes in your model <br>" +
		"<b>Model Statistics</b> -- A simple utility which gives some general info about the current model <br>" +
		"<b>Graphical Documentation</b> -- Facility for creating external documentation and presentation materials " +
		"<br><b>Edit Signal Library</b> -- View information about and remove signals from the list of signals available in your model" +
		"<br><b>Security Options</b> -- A dialog for entering a password for use with model collaboration (connecting to a remote session) " +
		"<br><b>User Preferences</b> -- Allows the user to change various user selectable options as well as the color scheme"+
		/*"<br><b>Model Tracker</b> -- A utility for viewing which open models are currently sampling*/ 
		"<p>Press next to review the User Preferences option.",  //5
		
		"The User Preferences option displays the user preferences dialog box where you can change various user selectable options. " +
		"The General tab contains the options listed below. <p><b>Simple Probability Mode</b> -- A mode which will cause JCat to use word phrases " +
		"in places where you will enter a probability instead of numbers <br><b>Auto-Saving</b> -- Save your model automatically to a temporary location " +
		"at an interval specified by delay (in minutes) which will allow for crash recovery <br><b>Recent files list size</b> -- Specify the number of " +
		"files contained in the recent file list in the File menu <br><b>Show grid lines / Snap objects to grid</b> -- grid options for your model view <br>" +
		"<b>Change node default font</b> -- Changes the font used by default for new nodes in your model <br><b>Highlight objects when editing</b> -- Blinks " +
		"nodes in the model when you are editing them to make it more clear which item in the model your working with " +
		"<br><b>Event creation</b> -- Change what happens when you place a new Event on the canvas so that you may model more efficiently" +
		"<br><b>Toolbar behavior</b> -- Choose to stay in the selected tool mode after placing an Event or Mechanism or return to the 'select' cursor each time",
		
		"The Color Scheme tab in user preferences allows you to configure a custom color scheme for use in your various models. The attributes " +
		"of each scheme are user definable. If a scheme is selected it is saved in your model but all schemes are saved under the JCat program " +
		"folder so that you may reuse a scheme in other models or share the scheme with other users. <p>To create a new scheme, press " +
		"the New button and give your scheme a name. Two special attributes will automatically be added to your list. These two attributes " +
		"will be used by default for Events which you do not assign a scheme attribute to. <p>To add a new attribute press the Add button " +
		"and give it a name. Click on an attribute and press Change to change its color. <p>Press OK to save changes and apply the " +
		"selected scheme.",  //7
		
		"The Help menu is you will find tools and information such as the one you're using now. <p><b>Tutorial</b> -- You are here <br>" +
		"<b>Model Wizard</b> -- A step by step guide to creating a very basic model in JCat <br><b>About</b> -- a bit of information on the JCat " +
		"development team",
		
		"The File toolbar is the bar of buttons located at the top of the JCat window, directly below the Menu bar.  These functions are each found " +
		"within the menus but can be more quickly accessed using this toolbar.  The buttons are each described below. <p>" +
		"<b>New</b> -- Opens a new model  in the model view(described later) <br><b>Open</b> -- Opens a previously saved JCat model file <br>" +
		"<b>Save/SaveAs</b> -- Saves the current model to the file of your choice <br><b>Copy</b> -- Copies the selected items in the open model for " +
		"later pasting <br><b>Paste</b> -- Pastes any copied items to the selected model <br><b>Help</b> -- Show JCAT help documentation" +
		"<br><b>Context Help</b> -- Enables the context help cursor.  Click anywhere in JCAT to view help documentation for that component.",  //9
		
		"The Model toolbar is the bar of buttons located to the far right of the JCat window.  Each button is outlined below.  " +
		"This toolbar provides tools used to work with your model.  " +
		"The first four buttons starting at the top of the bar change the action your cursor performs when you click on your model view.  <p>" +
		"<b>Pointer</b> -- when enabled your cursor will act as a selection tool, selecting any item you click on <br>" +
		"<b>Event tool</b> -- when enabled your cursor will add a new event to your model at the location you click <br>" +
		"<b>Mechanism tool</b> -- when enabled your cursor will add a new mechanism connecting the first Event you click and hold on and " +
		"the second which you release on <br> <b>Magnify Mode</b> -- when enabled your cursor will enlarge the model item it moves on top of for" +
		"easier viewing <br><b>Start Sampling</b> -- Perhaps the most important function in JCat as it starts the Bayes sampler " +
		"(the heart of JCat) to enable the plotting of model items for analysis <br><b>Stop Sampling</b> -- Stops the running sampler" +
		"<br><b>Zoom+</b> -- zooms the model view in <br><b>Zoom-</b> -- " +
		"zooms the model view out <br><b>Zoom Default</b> -- Returns the zoom level to the default setting",
		
		"Lets review the various docking windows floating around in JCat.  Each dock can be hidden, floated, auto-hid, maximized or closed using " +
		"the icons in the top right corner of the dock or by right clicking on the docks title bar.  " +
		"The Probability Profiles dock contains the graph output of JCat.  " +
		"Any model item you choose to plot will show up here and will also be added to the legend at the bottom of the graph.  " +
		"The graph shows the probability of each plotted model item for each time slice sampled.  By right clicking on the graph you will " +
		"get a menu with a few options which are described below. <p>" +
		"<b>Properties</b> -- allows you to change a few graphing options <br><b>Save as</b> -- allows you to save the current graph to an " +
		"image file <br><b>Print</b> -- prints the current graph out to a printer <br><b>Zoom In/Out</b> -- allows you to zoom the graph to your " +
		"liking <br><b>Reset Zoom</b> -- restores the graphs zoom to the default <br><b>Plot Predicted</b> -- enables the visibility of predicted " +
		"probabilities for each plotted item <br><b>Plot Inferred</b> -- enables the visibility of inferred probabilities for each plotted item <br>" +
		"<b>Remove plot</b> -- removes the selected plot from the graph <br><b>Clear all</b> -- removes all plots from the graph",  //11
		
		"The Navigation tree dock contains a tree type view of all items in the current model.  By default the tree shows all Event items " +
		"but can be changed by simple clicking on the mechanism option at the top of the tree.  By right clicking inside the tree you can " +
		"access some options which are described below. <p>" +
		"<b>Expand All</b> -- expand all branches of the tree for viewing <br><b>Snap to</b> -- center the selected item in the model view <br>" +
		"<b>Sort by</b> -- allows for various types of sorting of the tree items <br><b>Edit Event</b> -- shows the event editor dialog (described " +
		"later) <br><b>Timing</b> -- shows the timing dialog <br><b>Evidence</b> -- shows the evidence dialog",  //12
		
		"The Property Viewer dock lists several properties for any selected item in the model.  Some sections of properties can be edited by double clicking " +
		"any item in that section. ",

		"The Scheme Legend dock displays the currently active color scheme configuration.  If no scheme is active this dock will be empty.  " +
		"Otherwise the name of your scheme will be in the upper left corner and each defined attribute and its color will be listed.",  //14
		
		"The Course of Action dock displays the list of COAs that exist in your model.  A course of action in JCAT is a snapshot of the parameters " +
		"for each item in your model.  These parameters may include scheduling/timing information and probabilities values. <p>" +
		"Using the COA dock you can apply a selected COA as well as perform various functions on a COA by right clicking it in the list.", //15
		
		"The Collaboration dock is displayed only while JCAT is connected to or hosting a collaboration session.  Using this view you may " +
		"text chat with other connected users as well as set some users options pertaining to the session", //16
		
		"The Model View dock is your workspace in which you will draw your model.  It's the large white area with grid lines and a tab at its top.  " +
		"You may have multiple models open and would then use the tabs to switch between them.  <p>By default items on this view will also snap to " +
		"the grid lines when you drag them around. An Event shape can be resized using the small circles that appear at its edges when you move " +
		"your pointer over it.  Mechanisms (lines) can be bent and shaped to custom paths by moving the small circles that appear when " +
		"the pointer is over them.  Each bend made will create new move points (circles) for you to further bend the line.  You can remove bends " +
		"in a line by draggin those move points on top of each other until you run into a shape at the lines end.  <p>The model view has several " +
		"popup menus which will be described next.", //17

		"The model popup menu is the menu that you will get if you right click on empty canvas space (not on any Event or Mechanism).  This menu " +
		"contains fuctions you would enact on the entire model, not on a single item.  You will find the following functions in this menu. <p>" +
		"<b>Reset View</b> -- resets the hiding of certain shapes after using the 'show immediate' and 'chaining' features described later <br>" +
		"<b>Paste</b> -- paste in shapes that you have copied using the 'copy' feature <br><b>Show signal names</b> -- display signal names along the Mechanism lines <br>" +
		"<b>Start Model Server</b> -- starts a collaboration session, hosted on this computer <br>" +
		"<b>Stop Model Server</b> -- stop a running collaboration session, hosted on your computer <br><b>Create Course of Action</b> -- creates a new" +
		"course of action with the models current parameters <br><b>Save Model Image</b> -- outputs to file an image respresenting your entire model view <br>" +
		"",  //18
		
		"The shape popup menu is the menu that you will get if you right click on a single item on the canvas.  This menu has several common items " +
		"regardless of which type of item you click on but also some items specific to either an Event or Mechanism.  <p> <b>Event Items only:</b><br>" +
		"<b>Plot</b> -- Plot the selected item's probability profile in the graph (the sampler must be running)" +
		"<b>Edit Event</b> -- opens the Event Editor where you can modify the selected Event  <br><b>Documentation</b> -- opens the tool for entering documentation information for the " +
		"selected Event <br><b>Color Options</b> -- allows you to select a color or a color scheme attribute to use for the selected Event <br>" +
		"<b>Set Font</b> -- allows you to change the font for the selected Event " +
		"<br><b>Show Immediate</b> -- a utility for hiding all but the immediate (causes, effects or inhibits) of the selected Event " +
		"<br><b>Show Chain</b> -- a utility similar to above but builds a chain one buy one as you select" +
		"<br><b>Reset</b> -- resets the view for the above two commands by unhiding all model items" +
		"<br><b>Copy</b> -- copies the selected Event for pasting later" +
		"<p><b>Mechanism Items only:</b>" +
		"<br><b>Arched Line</b> -- make the edges on the selected line smooth curves" +
		"<br><b>Straight Line</b> -- make the edges on the selected line sharp" +
		"<br><b>Rename</b> -- rename the signal contained in the selected mechanism" +
		"<p><b>Both Events and Mechanisms:</b>" +
		"<br><b>Evidence</b> -- opens the tools for adding sensor and absolute evidence to the selected item " +
		"<br><b>Timing</b> -- opens the Timing Dialog where you can modify time and schedule properties of the selected item" +
		"<br><b>Delete</b> -- deletes the selected item from the model",

		"The shapes popup menu is the menu that you will get when you right click on a shape when multiple shapes " +
		"are selected.  The items in this menu are described below:  <p><b>Copy</b> -- copy all the selected shapes for pasting later <br>" +
		"<b>Color Options</b> -- allows you to select a color or a color scheme attribute to use for all selected Events <br> " +
		"<b>Set Font</b> -- allows you to change the font for all selected Events <br><b>Create Image</b> -- saves the selected area as an image to the " +
		"specified image file <br><b>Delete</b> -- deletes all of the selected shapes",  //20
		
		"Lets go over the various dialogs you will come across while working in JCat.  First is the <b>Event Editor</b> which you will probably " +
		"use most frequently.  The Event Editor is used to modify the properties of an Event.  You can change both names and probabilities here.  <p>" +
		"The General tab at the left contains the event descriptions as well as a place to specify the leak probability. <p>The Default Probabilities tab " +
		"allows you to change the probabilities used by default when you create new signals for either a cause, inhibitor or effect. <p> The Specified " +
		"Probabilities tab is where you will change the probabilities for each signal associated with the Event being edited.  You may add a signal or " +
		"create a signal group using the respective buttons.  To change the probability for the signal, click the up and down arrow next to its " +
		"alone probability to the right or you may use the scroll wheel on your mouse.  By right clicking on a signal in the list you may also invert, " +
		"rename or remove a signal." +
		"<p>Press Save to make your changes or cancel to close Event Editor with out keeping changes you have made.",

		"The <b>Mechanism dialog</b> will automatically appear when you create a new mechanism.  In this dialog you must specify either a new signal " +
		"name and type (cause or inhibit) or use a previously defined signal.  To create a new signal, choose cause or inhibitor, enter a name and click " +
		"Create New.  To use a previously defined signal, select it from the list and press Use Predefined.",  //22
		
		"The <b>Timing dialog</b> is used to enter timing and scheduling information into the item being edited.  <p>Simply press the up or " +
		"down arrow next to a field to modify its value and press Schedule to enter the time in to the schedule table.  Use the Delete button to " +
		"remove a time from the table or the Delete All button to clear the table.",

		"The Graphical Documentation dialog is used to enter notes, references and other documentation onto items in your model.  There are four tabs " +
		"in this diagram, each of which is described below. <p>" +
		"<b>Description</b> -- an area to enter any type of description of this item which could not fit in the name. <br><b>Reference Library</b>" +
		" -- a list of references used to retrieve information from which the item being edited was based. <br><b>Comments</b> -- a place for " +
		"modelers to enter comments about changes made to this item.  <br><b>Keywords</b> -- a place to enter keywords for this item for use with the " +
		"keyword search feature. <p>Use the three lefthand buttons at the bottom of the dialog to export all the documentation for this item to the " +
		"desired format (XML, PDF, DOC) or press close to complete your documenting.",  //24
		
		"The Model Documentation dialog as you may notice is the same as the Graphical Documentation dialog.  The difference is that the documentation " +
		"entered here will stand for the entire model instead of just a single item.",

		"Congratulations, thats it!  You have experienced JCat in a nutshell. <p>" +
		"Next you might try the Model Wizard to assist you in building a simple model in JCat."  //Finished
	};
	
	static private Vector[] options = initOptions(messages.length);
	
	
	//create options for each page
	public static Vector[] initOptions(int length)
	{
		Vector[] allOpts = new Vector[length];
		
		for(int x=0; x<length; x++)
		{
			Vector opts = new Vector();
			
			//if(x == 5)
			//{
			//}
			
			allOpts[x] = opts;
		}
		
		return allOpts;
	}
	
	public TutorialWizard(java.awt.Frame parent)
	{
		super(parent, new java.awt.Dimension(690,450), titles, messages, true, "Tutorial");
		
		//setTitles(titles);
		//setMessages(messages);
		setOptions(options);
		
		arrow = new ArrowWindow(this.getClass().getClassLoader().getResource("wiz_arrow.png"), MainFrm.getInstance());
		
		setVisible(true);
		
		menuBar = MainFrm.getInstance().getCatMenuBar();
		toolBar = MainFrm.getInstance().getFileToolBar();
		planToolbar = MainFrm.getInstance().getCatToolBar();
		dockManager = MainFrm.getInstance().getDockingManager();
	}
	
	//perform functions for the current page
	public void nextPressed()
	{
		if(getCurrentTitle().equals(titles[1]))
		{
			//MainFrm.getInstance().createDocument();
			//controller = Control.getInstance().getController(MainFrm.getInstance().getSelectedPlan());
			//plan = Control.getInstance().getPlan(MainFrm.getInstance().getSelectedPlan());
			menuBar = MainFrm.getInstance().getCatMenuBar();
			toolBar = MainFrm.getInstance().getFileToolBar();
			planToolbar = MainFrm.getInstance().getCatToolBar();
			dockManager = MainFrm.getInstance().getDockingManager();
		}
			
	}

	public void backPressed()
	{
		
	}

	public void cancelPressed()
	{
		dispose();
	}

	public void pageChanged(int currentPage)
	{
		if(currentPage == 1) //the menu bar{
		{
			if (menuBar != null)
			{
				menuBar.getMenu(0).setPopupMenuVisible(false);
				menuBar.getMenu(1).setPopupMenuVisible(false);
				menuBar.getMenu(2).setPopupMenuVisible(false);
				menuBar.getMenu(3).setPopupMenuVisible(false);
				menuBar.getMenu(4).setPopupMenuVisible(false);
				if(menuBar.getPrefsBox() != null)
					menuBar.getPrefsBox().dispose();
			}
			arrow.pointTo(MainFrm.getInstance().getCatMenuBar(), 90, true);
		}
		else if(currentPage == 2) //file
		{
			if (menuBar != null)
			{
				menuBar.getMenu(1).setPopupMenuVisible(false);
				menuBar.getMenu(0).setPopupMenuVisible(true);
				menuBar.getMenu(2).setPopupMenuVisible(false);
				menuBar.getMenu(3).setPopupMenuVisible(false);
				menuBar.getMenu(4).setPopupMenuVisible(false);		
				if(menuBar.getPrefsBox() != null)
					menuBar.getPrefsBox().dispose();
				arrow.pointTo(menuBar.getMenu(0).getItem(0), 180, false);
			}
		}
		else if(currentPage == 3) //hide file/show edit
		{
			if (menuBar != null)
			{
				menuBar.getMenu(0).setPopupMenuVisible(false);
				menuBar.getMenu(2).setPopupMenuVisible(false);
				menuBar.getMenu(1).setPopupMenuVisible(true);
				menuBar.getMenu(3).setPopupMenuVisible(false);
				menuBar.getMenu(4).setPopupMenuVisible(false);
				if(menuBar.getPrefsBox() != null)
					menuBar.getPrefsBox().dispose();
				arrow.pointTo(menuBar.getMenu(1).getItem(0), 180, false);
			}
		}
		else if(currentPage == 4) //hide edit/show view
		{
			if (menuBar != null)
			{
				menuBar.getMenu(1).setPopupMenuVisible(false);
				menuBar.getMenu(3).setPopupMenuVisible(false);
				menuBar.getMenu(2).setPopupMenuVisible(true);
				menuBar.getMenu(0).setPopupMenuVisible(false);
				menuBar.getMenu(4).setPopupMenuVisible(false);
				if(menuBar.getPrefsBox() != null)
					menuBar.getPrefsBox().dispose();
				arrow.pointTo(menuBar.getMenu(2).getItem(0), 180, false);
			}	
//			if(menuBar.getPrefsBox() != null)
//				menuBar.getPrefsBox().dispose();
		}
		else if(currentPage == 5) //hide view/show tools
		{
			if (menuBar != null)
			{
				menuBar.getMenu(2).setPopupMenuVisible(false);
				menuBar.getMenu(4).setPopupMenuVisible(false);
				menuBar.getMenu(3).setPopupMenuVisible(true);
				menuBar.getMenu(1).setPopupMenuVisible(false);
				menuBar.getMenu(0).setPopupMenuVisible(false);
				arrow.pointTo(menuBar.getMenu(3).getItem(0), 180, false);
				if(menuBar.getPrefsBox() != null)
					menuBar.getPrefsBox().dispose();
			}
		}
		else if(currentPage == 6) //user prefs
		{
			if(menuBar.getPrefsBox() != null)
				menuBar.getPrefsBox().setTab(0);
			else
			{
				menuBar.getMenu(3).getItem(menuBar.getMenu(3).getItemCount()-1).doClick();
				menuBar.getPrefsBox().setLocation(MainFrm.getInstance().getLocation());
				menuBar.getMenu(3).setPopupMenuVisible(false);
				arrow.setVisible(false);
				menuBar.getPrefsBox().setTab(0);
			}
		}
		else if(currentPage == 7) //color scheme
		{
			if(menuBar.getPrefsBox() != null)
				menuBar.getPrefsBox().setTab(1);
			else
			{
				menuBar.getMenu(3).getItem(menuBar.getMenu(3).getItemCount()-1).doClick();
				menuBar.getPrefsBox().setLocation(MainFrm.getInstance().getLocation());
				menuBar.getMenu(3).setPopupMenuVisible(false);
				arrow.setVisible(false);
				menuBar.getPrefsBox().setTab(1);
			}
			menuBar.getMenu(4).setPopupMenuVisible(false);
			menuBar.getMenu(3).setPopupMenuVisible(false);
			menuBar.getMenu(1).setPopupMenuVisible(false);
			menuBar.getMenu(2).setPopupMenuVisible(false);
			menuBar.getMenu(0).setPopupMenuVisible(false);
			//arrow.pointTo(menuBar.getMenu(3).getItem(0), 180, false);
		}
		else if(currentPage == 8) //hide tools/ show help
		{
			if(menuBar.getPrefsBox() != null)
				menuBar.getPrefsBox().dispose();
			menuBar.getMenu(3).setPopupMenuVisible(false);
			menuBar.getMenu(4).setPopupMenuVisible(true);
			menuBar.getMenu(1).setPopupMenuVisible(false);
			menuBar.getMenu(2).setPopupMenuVisible(false);
			menuBar.getMenu(0).setPopupMenuVisible(false);
			arrow.pointTo(menuBar.getMenu(4).getItem(0), 180, false);
		}
		else if(currentPage == 9) //toolbar
		{
			menuBar.getMenu(4).setPopupMenuVisible(false);
			arrow.pointTo(toolBar, 90, true);
		}
		else if(currentPage == 10) //plan toolbar
		{
			arrow.pointTo(planToolbar, 0, true);
		}
		else if(currentPage == 11) //prob prof dock
		{
			dockManager.showFrame("Probability Profiles");
			dockManager.showFrame("Probability Profiles"); // do twice to workaround a JIDE bug
			arrow.pointTo(dockManager.getFrame("Probability Profiles"), 90, true);
		}
		else if(currentPage == 12)
		{
			dockManager.showFrame("Navigation Tree");
			dockManager.showFrame("Navigation Tree");
			arrow.pointTo(dockManager.getFrame("Navigation Tree"), 180, true);
		}
		else if(currentPage == 13)
		{
			dockManager.showFrame("Property Viewer");
			dockManager.showFrame("Property Viewer");
			arrow.pointTo(dockManager.getFrame("Property Viewer"), 180, true);
		}
		else if(currentPage == 14)
		{
			dockManager.showFrame("Scheme Legend");
			dockManager.showFrame("Scheme Legend");
			arrow.pointTo(dockManager.getFrame("Scheme Legend"), 90, true);
		}
		else if(currentPage == 15)
		{
			dockManager.showFrame("Course of Action");
			dockManager.showFrame("Course of Action");
			arrow.pointTo(dockManager.getFrame("Course of Action"), 180, true);
		}
		else if(currentPage == 16)
		{
			dockManager.showFrame("Collaboration");
			dockManager.showFrame("Collaboration");
			arrow.pointTo(dockManager.getFrame("Collaboration"), 0, true);
		}
		else if(currentPage == 17) //plan view
		{
			//open a new plan view and point to it, will use it later too
			if(MainFrm.getInstance().getActiveView() == null)
				MainFrm.getInstance().createDocument(); //AbstractPlan.STANDARD_DEFAULTS_SET);
						
			arrow.pointTo(MainFrm.getInstance().getDockPane(), 270, true);
		}
		else if(currentPage == 18) //canvas popup
		{
			arrow.dispose();
			if(MainFrm.getInstance().getActiveView() == null)
				MainFrm.getInstance().createDocument(); //AbstractPlan.STANDARD_DEFAULTS_SET);
			JPopupMenu menu = MainFrm.getInstance().getActiveView().getPopupManager().getPopupMenu("");
			menu.show(MainFrm.getInstance().getActiveView().getPanel(), 10,10);
		}
		else if(currentPage == 19) //shape popup
		{
			JPopupMenu menu = MainFrm.getInstance().getActiveView().getPopupManager().getPopupMenu("com.c3i.jwb.shapes.JWBRoundedRectangle");
			menu.show(MainFrm.getInstance().getActiveView().getPanel(), 10,10);
		}
		else if(currentPage == 20) //shapes popup
		{
			JPopupMenu menu = MainFrm.getInstance().getActiveView().getPopupManager().popupMenuShapes();
			menu.show(MainFrm.getInstance().getActiveView().getPanel(), 10,10);
		}
		else if(currentPage == 21) //event editor
			;
		else if(currentPage == 22) //mechanism
			;
		else if(currentPage == 23) //timing
			;
		else if(currentPage == 24) //graphical doc
			;
		else if(currentPage == 25) //plan doc
			;
		
		else
			arrow.dispose();
		
	}
	
	public JWBShape createEvent(int x, int y)
	{
		String firstOptText = ((JTextField)getCurrentOptions().firstElement()).getText();		
		try{
			JWBRoundedRectangle shape = new JWBRoundedRectangle(new java.awt.Point(x,y), 80, 80, new JWBUID());
			Event event = new Event(new Guid(), firstOptText, "Undefined", plan.getLibrary().createProcess(plan.getDefaultProbSet(), -1));
			shape.setText(firstOptText);
			shape.setAttachment(event.getGuid());
//			plan.addItem(event, shape.getUID());
			
			controller.putShape(shape);
			controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_ADD, event, shape.getUID()));
			
			return shape;
		}catch(Exception exc){
			logger.warn("createEvent - error creating event:  "+exc.getMessage());
			return null;
		}
	}
	
/*	private void createMech()
//	{
//		String firstOptText = ((JTextField)getCurrentOptions().firstElement()).getText();
//		try
//		{
//			Signal signal = plan.getLibrary().getSignal(plan.getLibrary().createSignal());
//			signal.setSignalName(firstOptText);
//			Mechanism mech = new Mechanism(new Guid(), firstOptText, (Event)to.getAttachment(), (Event)from.getAttachment(), signal.getSignalID(), Mechanism.CAUSE, plan.getLibrary());
//			mech.setLoopCloser(false);
//			JWBLine newLine = new JWBLine(from, to, new JWBUID(), false);
//			newLine.setAttachment(mech);
//			plan.addItem(mech,newLine.getUID());
//            plan.setShapeMapping(mech.getGuid(), newLine.getUID());
//			controller.putShapeExternalUpdate(newLine);
//			
//		}catch(Exception exc){
//			logger.warn("createMech - error creating mechanism:  "+exc.getMessage());
//		}
//	}

//	private void createTiming()
//	{
//		TreeMap schedule = new TreeMap();
//		schedule.put(new Integer(4), MaskedFloat.getMaskedValue(.6f));
//		((Event)from.getAttachment()).setSchedule(schedule);
//		from.addMarkup('S');
//		try{
//			controller.putShapeExternalUpdate(from);
//		}catch(RemoteException exc){
//			logger.warn("createTiming - RemoteExc creating timing:  "+exc.getMessage());
//		}
//	}

//	private void startSample()
//	{
//		try{
//			plan.buildBayesNet(10);
//		}catch(Exception exc){
//			logger.warn("startSample - Error starting sampler");
//		}		
//	}

//	private void plotEvent()
//	{
//		 PlanItem item = (PlanItem) to.getAttachment();
//		 //item.setPredictedProbs(plan.getInferredProbs(new Guid(item.getGuid())));
//		 mil.af.rl.jcat.gui.ProfileDataModel.getInstance().addPlot(plan, item);
//	} */

	public void dispose()
	{
		//do some cleanup
		if(arrow != null)
			arrow.dispose();
		
		dockManager.hideFrame("Collaboration");
		menuBar.getMenu(3).setPopupMenuVisible(false);
		menuBar.getMenu(4).setPopupMenuVisible(false);
		menuBar.getMenu(1).setPopupMenuVisible(false);
		menuBar.getMenu(2).setPopupMenuVisible(false);
		menuBar.getMenu(0).setPopupMenuVisible(false);
		if(menuBar.getPrefsBox() != null && menuBar.getPrefsBox().isVisible())
			menuBar.getPrefsBox().dispose();
		
		super.dispose();
	}

		
}
