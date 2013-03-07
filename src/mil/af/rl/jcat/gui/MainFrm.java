/*
 * Created on May 13, 2004
 *
 * Main Frame for JCat GUI supports collaboration and RMI.
 * First attempt to merge Ed's Gui.
 */
package mil.af.rl.jcat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.util.LinkedList;
import java.util.Vector;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.dom4j.DocumentException;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.collaboration.CollabDock;
import mil.af.rl.jcat.control.collaboration.CollaborationControl;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.integration.IbcConnector;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.PlanEvent;
import mil.af.rl.jcat.plan.PlanListener;
import mil.af.rl.jcat.siamtransform.io.FileOperations;
import mil.af.rl.jcat.siamtransform.xml.SIAMTransform;
import mil.af.rl.jcat.util.AutoSaveAgent;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.CustomHTMLLayout;
import mil.af.rl.jcat.util.DNDFileHandler;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MaskedValue;

import com.c3i.jwb.JWBPanel;
import com.jidesoft.docking.DefaultDockingManager;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableHolder;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.document.DocumentPane;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.utils.Lm;

/**
 * @author Craig McNamara
 *
 * Main JCat Class
 */
public class MainFrm extends JFrame implements DockableHolder, Runnable, PlanListener, DNDFileHandler.DNDCallback
{
	private static final long serialVersionUID = 1L;
	private static final String PROFILE_NAME = "JCAT";
	public static final Level DEFAULT_LOG_LEVEL = Level.WARN;
	private static DockingManager _dockingManager;
	public static MainFrm _frame; 
	private PlanToolBar catToolBar = new PlanToolBar(this);
	private FileToolBar fileToolBar;
	private CatMenuBar menuBar;
	private DocumentPane desktop = new DocumentPane();
	private StatusBar statusbar = new StatusBar();
	private LabelStatusBarItem serverstatus = new LabelStatusBarItem(null);
	private LabelStatusBarItem samplerStatus = new LabelStatusBarItem(null);
	private JPanel dockingArea = new JPanel();
	// //
	public LinkedList GraphicalDocumentation = new LinkedList();
	private Vector MRUlist = new Vector();
	// DOCKABLE FRAMES //
	private ProbabilityProfiles probProfiles = null;
	private CollabDock collabLogDock;
	private COAViewer coaDock;
	private ResourceViewer resourceDock;
	private NavTree navTree;
	private PropertyViewer propViewer;
	private SchemeLegend schmLegend;
	// //
	private String lastOpenDir = "", lastSaveDir = "";
	private int MRUSize = 5, autoSaveDelay = 10;
	private boolean simpleMode = false, highlightEnabled = false, autoSave = true;
	// //
	private AutoSaveAgent autoSaveAgent = null;
	private HelpBroker mainHB;
	private Thread updateThread = null;
	public IbcConnector ibc; // = new IbcConnector();
	
	private static Logger logger;
	
	

	public static void main(String[] args) throws DuplicateNameException
	{
		Thread.currentThread().setName("MAIN");
//		 Change log level here (determines how much stuff gets logged)
		// Valid levels in order are:  TRACE -> DEBUG -> INFO -> WARN -> ERROR -> FATAL -> OFF
		Logger.getRootLogger().setLevel(DEFAULT_LOG_LEVEL);

		// Change the appender here (where the logging goes, ie. console or file)
//		try{
			// log to console
			Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), ConsoleAppender.SYSTEM_OUT));
			
			// log to file
			// if the log file is getting to big (say about 10mb for now), delete and start fresh
//			File logFile = new File(EnvUtils.getJCATSettingsHome()+EnvUtils.sep+"log.html");
//			if(logFile.exists() && logFile.length() > 10485760)
//				logFile.delete();
//			Logger.getRootLogger().addAppender(new FileAppender(new CustomHTMLLayout(), logFile.getPath()));
//		}catch(IOException exc){
//			System.err.println("IO error");
//		}

		logger = Logger.getLogger(MainFrm.class);
		
		com.jidesoft.utils.Lm.verifyLicense("Air Force Research Lab AFRL/IFSA", "Causal Analysis Tool ", "mtNAF4gbpizL:w6.Lzggpr7Igw7tHdD2");
		setLookAndFeel(false);
		JFrame.setDefaultLookAndFeelDecorated(false);
		_frame = new MainFrm();
		_frame.loadMaskedValues();


		// allows for file open / file associations to work
		// this combines all arguments (really arg0 should be the only one used), user should use quotations if 
		// there are long file names with spaces in them
		String file = null;
		if(args.length > 0 && args[0] != null)
		{
			file = new String();
			for(int i = 0; i < args.length; i++)
				file += args[i] + " ";
			_frame.openDocument(new File(file.trim()), true);
		}

		// setup keystore for server / truststore for client / security manager and policy (mostly for collab)
		try{
			try{
				//TODO:  this doesn't appear to work with Web Start jar'd resources, might need to disable colab
				// unless filez are copied to users home first so they can be loaded properly
				File polFile = new File(MainFrm.class.getClassLoader().getResource("jcat.security.policy").toURI());
				File keyFile = new File(MainFrm.class.getClassLoader().getResource("jcat_keystore").toURI());
				File trusFile = new File(MainFrm.class.getClassLoader().getResource("jcat_truststore").toURI());
				System.setProperty("java.security.policy", polFile.getPath());
				System.setProperty("javax.net.ssl.keyStore", keyFile.getPath()); 
				System.setProperty("javax.net.ssl.keyStorePassword", "primate");
				System.setProperty("javax.net.ssl.trustStore", trusFile.getPath());
				System.setProperty("javax.net.ssl.trustStorePassword", "primate");
				//System.setProperty("rmi.channel.bindport", "1097");

				// install RMI security manager - needed for encryption in collaboration
				if(System.getSecurityManager() == null)
					System.setSecurityManager(new RMISecurityManager());
			}
			catch(IllegalArgumentException exc){
				// couldn't make a file from the URI, prolly cause the filez are in a jar which wont work 
				// this must be WebStart, disable collab in WebStart for now but no warnings
				_frame.setCollaborationEnabled(false);
			}
			catch(java.net.URISyntaxException exc){
				_frame.setCollaborationEnabled(false);
				throw new MissingRequiredFileException("Either the keystore, truststore and/or policy file could not be loaded \n" +
				"These files are required for collaboration to function.");
			}
			catch(NullPointerException exc){
				_frame.setCollaborationEnabled(false);
				throw new MissingRequiredFileException("Either the keystore, truststore and/or policy file could not be loaded \n" +
				"These files are required for collaboration to function.");
			}

		}catch(MissingRequiredFileException exc){
			_frame.setCollaborationEnabled(false);
			JOptionPane.showMessageDialog(_frame, "A required configuration file is missing. \n"+exc.getMessage());
		}
	}

	private MainFrm() throws HeadlessException
	{
		super("Java Causal Analysis Tool");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		JWindow banner = new JWindow();
//		banner.setSize(661, 281);
		Icon icon = new ImageIcon(this.getClass().getClassLoader().getResource("banner.png"));
		JLabel label = new JLabel(icon,JLabel.CENTER);
		banner.getContentPane().add(label);
		banner.pack();
		Dimension bannerSize = banner.getSize();
		banner.setLocation((screenSize.width - bannerSize.width) / 2, (screenSize.height - bannerSize.height) / 2);
		banner.setVisible(true);
		
		setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("jcat.png")).getImage());

		if(!loadUserPrefs())
			logger.warn("Constructor - No users preferences file found (or file did not contain all expected data), using defaults");

		initGuiComponents();
		banner.dispose();

		autoSaveAgent = new AutoSaveAgent(autoSave, autoSaveDelay);
	}

	public static MainFrm getInstance() {
		if (_frame == null) {
			_frame = new MainFrm();
		}
		return _frame;
	}

	public void initGuiComponents() {
		setLookAndFeel(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//initialize Help system
		//DONT JUST COMMENT THIS OUT CRAIG! YOU CAN't DO THAT, YOU DON'T NEED TO, FIX UR CLASSPATH
		try{
			ClassLoader classloader = (MainFrm.class).getClassLoader();
			java.net.URL url = HelpSet.findHelpSet(classloader, "Help");
			HelpSet mainHS = new HelpSet(classloader, url);
			mainHB = mainHS.createHelpBroker();
			mainHB.enableHelpKey(this.getRootPane(), "Welcome", mainHS, "javax.help.MainWindow", null);
			mainHB.setSize(new Dimension(850, 600));
		}catch(Exception exc){
			logger.error("initGuiComponents() - Error initializing help system - ensure HelpSet exists in classpath:  ", exc);
		}

		//enables drag and drop to document pane for opening files
		desktop.setTransferHandler(new DNDFileHandler(this));

		// Add toolbars and area for docking window
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(dockingArea, BorderLayout.CENTER);
		getContentPane().add(fileToolBar = new FileToolBar(this), BorderLayout.NORTH);
		getContentPane().add(catToolBar, BorderLayout.EAST);

		// ADD THE STATUS BAR
		//com.jidesoft.status.TimeStatusBarItem
		LabelStatusBarItem statuslabel = new LabelStatusBarItem(null);
		statuslabel.setText("Model Server Status: ");
		((JLabel) statuslabel.getComponent()).setHorizontalAlignment(JLabel.RIGHT);
		serverstatus.setText("<html><font color=red>Offline</font></html>");

		LabelStatusBarItem samplerLabel = new LabelStatusBarItem(null);
		samplerLabel.setText("Sampler Status: ");
		((JLabel)samplerLabel.getComponent()).setHorizontalAlignment(JLabel.RIGHT);
		samplerStatus.setText("<html><font color=red>Stopped &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </font></html>");
		statusbar.add(samplerLabel);
		statusbar.add(samplerStatus);
		statusbar.add(statuslabel);
		statusbar.add(serverstatus);
		statusbar.add(new MemoryStatusBarItem());
		statusbar.add(new TimeStatusBarItem());
		getContentPane().add(statusbar, BorderLayout.SOUTH);
		javax.help.CSH.setHelpIDString(statusbar, "Status_Bar");

		//setup menubar
		setJMenuBar(menuBar = new CatMenuBar(this));

		//Setup Docking Manager
		_dockingManager = new DefaultDockingManager(this, dockingArea);
		//getDockingManager().getWorkspace().add(catToolBar, BorderLayout.EAST);
		javax.help.CSH.setHelpIDString(desktop, "Plan_Layout_Frame");
		getDockingManager().getWorkspace().add(desktop, BorderLayout.CENTER);

		//I dont know what this is but assume its important for
		//the docking windows to work. So I dont Complain -Craig McNamara
		Lm.setParent(_frame);
		// add a widnow listener so that timer can be stopped when exit
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//super.windowClosing(e);
				clearUp();
			}
		});

		// set the profile key
		getDockingManager().setProfileKey(PROFILE_NAME);
		//save to file, not registry
		getDockingManager().setUsePref(false);
		// draw full outline when outside main JFrame
		getDockingManager().setOutlineMode(1);
		getDockingManager().beginLoadLayoutData();
		getDockingManager().setInitNorthSplit(1);

		// create dockable frames
		resourceDock = new ResourceViewer();
		
		// add all dockable frames
		getDockingManager().addFrame(probProfiles = ProbabilityProfiles.getInstance());
//		getDockingManager().addFrame(proSeries = ProfileLegend.getInstance()); //f this, dont need it anymore
		getDockingManager().addFrame(navTree = new NavTree());
		getDockingManager().addFrame(schmLegend = new SchemeLegend());
		getDockingManager().addFrame(propViewer = new PropertyViewer());
		getDockingManager().addFrame(collabLogDock = new CollabDock());
		getDockingManager().addFrame(resourceDock);
		getDockingManager().addFrame(coaDock = new COAViewer());

		//Set Initial Layout
		this.probProfiles.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
		this.probProfiles.getContext().setInitSide(DockContext.DOCK_SIDE_NORTH);
		this.probProfiles.setPreferredSize(new Dimension(300, 200));
		getDockingManager().hideFrame(resourceDock.getTitle()); //hide this by default
		getDockingManager().hideFrame(coaDock.getTitle()); //hide this by default
		
		// load layout information from previous session
		// need to relocate this config file (should be in users home directory for permissions)
		String oldLoc = EnvUtils.getUserHome()+EnvUtils.sep+"JCat_layout.prefs";
		String newLoc = EnvUtils.getJCATSettingsHome()+EnvUtils.sep+"JCat_layout.prefs";
		if(!(new File(newLoc).exists()) && new File(oldLoc).exists())
		{
			getDockingManager().loadLayoutDataFromFile(oldLoc);
			try{
				new File(oldLoc).deleteOnExit();
			}catch(Exception exc){
				logger.warn("initGuiComponents - could not remove layout.prefs file from old location:"+exc.getMessage());
			}
		}
		else
			getDockingManager().loadLayoutDataFromFile(newLoc);

		if(MRUlist != null)
			((CatMenuBar)getJMenuBar()).updateRecent(MRUlist);
		((CatMenuBar)getJMenuBar()).initViewMenu(getDockingManager());

		//display the main frame
		toFront();

		//for view menu checkbox updating, must be after GUI is visible to miss a false listener event
		probProfiles.addDockListener();
		navTree.addDockListener();
		schmLegend.addDockListener();
		propViewer.addDockListener();
		coaDock.addDockListener();
		collabLogDock.addDockListener();
		resourceDock.addDockListener();
	}

	/**
	 * Installs the JIDE look and Feel extensions
	 *
	 * @author Craig McNamara
	 */
	public static void setLookAndFeel(boolean fail) {
		try {
			LookAndFeelFactory.installJideExtension();
			//UIManager.setLookAndFeel("com.jidesoft.plaf.eclipse.EclipseWindowsLookAndFeel");
			if (fail)
				UIManager.setLookAndFeel("com.jidesoft.plaf.eclipse.EclipseMetalLookAndFeel");
			else
				UIManager.setLookAndFeel("com.jidesoft.plaf.eclipse.EclipseWindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException e1) {
			logger.info("setLookAndFeel - Look & Feel not supported, reverting to metal:  "+e1.getMessage());
			setLookAndFeel(true);
		}
		catch(Exception exc){
			logger.info("setLookAndFeel - Error installing Look & Feel:  "+exc.getMessage());
		}
	}

/*	public Guid createDocument()
	{
		float[] defaults = null;
		String[] options = new String[]{"Standard", "AND/OR", "User Defined"};
		int opt = JOptionPane.showOptionDialog(this, "Select default probability set:", "Default Probabilities", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

		if(opt >= 0)
		{
			if(opt == 0)
				defaults = (AbstractPlan.STANDARD_DEFAULTS_SET);
			else if(opt == 1)
				defaults = (AbstractPlan.AND_OR_DEFAULTS_SET);
			else if(opt == 2)
			{
				float[] vals = showDefaultsOptionBox();
				if(vals == null)
					return null;
				else
					defaults = (vals);
			}

			return createDocument(defaults);
		}
		else
			return null;
	}
	*/

	public Guid createDocument() //float[] defaults)
	{
		try{
			Guid planid = null;
			planid = Control.getInstance().addPlan(planid, this);
			AbstractPlan plan = Control.getInstance().getPlan(planid);
//			plan.setDefaultProbSet(defaults);

			CatView view = new CatView(Control.getInstance().getPanel(planid), plan, new Guid().toString());
			
			desktop.openDocument(view);
			desktop.setActiveDocument(view.viewid);

			return planid;
		}
		catch(MissingRequiredFileException exc){
//			desktop.closeDocument(view.getDocumentName());
			JOptionPane.showMessageDialog(this, "A required configuration file is missing.\n" + exc.getMessage());
			logger.error("createDocument - MissingRequiredFileExc:  "+exc.getMessage(), exc.getCause());
			return null;
		}
		catch(Exception ex){
//			desktop.closeDocument(view.getDocumentName());
			JOptionPane.showMessageDialog(this, "An error occured trying to open a new document:\n"+ex.getMessage());
			ex.printStackTrace();
			logger.error("createDocument - Error creating document:  ", ex);
			return null;
		}

	}

	public void openDocument()
	{
		JFileChooser fch = null;
		if(lastOpenDir == null)
			fch = new JFileChooser(EnvUtils.getUserDocHome() + EnvUtils.sep); // + "JCatModels");
		else
			fch = new JFileChooser(lastOpenDir);
		fch.setFileFilter(new CatFileFilter());
		int res = fch.showOpenDialog(MainFrm.getInstance());
		if (res == JFileChooser.APPROVE_OPTION)
		{
			openDocument(fch.getSelectedFile(), true);
			lastOpenDir = fch.getSelectedFile().getParent();
		}
	}

	public Guid openDocument(org.dom4j.Document doc)
	{
		Guid planid = null;
		try{
			planid = Control.getInstance().openPlan(doc, this);
			AbstractPlan plan = Control.getInstance().getPlan(planid);
			JWBPanel panel = Control.getInstance().getPanel(planid);
			
			CatView view = new CatView(panel, plan, new Guid().toString());
			desktop.openDocument(view);
			desktop.setActiveDocument(view.viewid);

		}catch(DuplicateNameException exc){
			JOptionPane.showMessageDialog(this, "The selected model is already open!");
		}catch(MissingRequiredFileException exc){
			JOptionPane.showMessageDialog(this, "A required configuration file is missing.\n" + exc.getMessage());
		}catch(DocumentException exc){
			int maxL = (exc.getMessage().length() < 100) ? exc.getMessage().length() : 120;
			JOptionPane.showMessageDialog(this, "An error occured while parsing the specified document.\n" + exc.getMessage().substring(0, maxL));
		}

		return planid;
	}

	public Guid openDocument(File planFile, boolean addToMRU)
	{
		try{
			Guid planid = Control.getInstance().openPlan(planFile, this);
			AbstractPlan plan = Control.getInstance().getPlan(planid);
			JWBPanel panel = Control.getInstance().getPanel(planid);
			
			CatView view = new CatView(panel, plan, new Guid().toString());
			desktop.openDocument(view);
			desktop.setActiveDocument(view.viewid);

			if(addToMRU)
				addToMRU(planFile);
			return planid;
		}catch(DuplicateNameException e){
			JOptionPane.showMessageDialog(this, "The selected model is already open!");
			return null;
		}catch(FileNotFoundException exc){
			JOptionPane.showMessageDialog(this, "Could not find the specificed file to open."+ "\n" + planFile.getName());
			return null;
		}catch(MalformedURLException exc){
			JOptionPane.showMessageDialog(this, "Error opening specified plan! \n" + exc.getMessage());
			return null;
		}catch(MissingRequiredFileException exc){
			JOptionPane.showMessageDialog(this, "A required configuration file is missing.\n" + exc.getMessage());
			return null;
		}catch(DocumentException exc){
			int maxL = (exc.getMessage().length() < 100) ? exc.getMessage().length() : 120;
			JOptionPane.showMessageDialog(this, "An error occured while parsing the specified document.\n" + exc.getMessage().substring(0, maxL));
			return null;
		}

	}

	public void openDocument(File planFile)
	{
		openDocument(planFile, true);
	}

	public void openSiamPlan() throws DuplicateNameException, FileNotFoundException
	{
		File path = FileOperations.fileSelect(FileOperations.OPEN, "xml", "SIAM xml file", MainFrm.getInstance());
		if(path != null)
		{
			SIAMTransform trans = new SIAMTransform();
			trans.setSIAMPath(path);
			trans.setTransformPath(this.getClass().getClassLoader().getResource("SiamToCatStyleSheet_v0_7.xsl").toString());
			trans.setResultPath(EnvUtils.getUserHome()+ EnvUtils.sep + ".JCAT"+ EnvUtils.sep + path.getName()+".tmp");
			trans.writeFile = true;
			trans.performTransform();
			File resultFile = new File(trans.getResultPath());
			
			//openDocument(trans.getCATDocument()); //TODO:  should be able todo this but remapPlanIDs fails
			Guid planID =  openDocument(resultFile, false);
			if(planID != null)
			{
				AbstractPlan siamPlan = Control.getInstance().getPlan(planID);
				siamPlan.setFilePath(null);
				String name = "Imported SIAM Model "+(++Control.planCount);
				siamPlan.setPlanName(name);
				getActiveView().setTitle(name);
				resultFile.delete();
			}
		}
	}

	public void addToMRU(File planFile)
	{
		// add to Most recent used list, at the top and remove the bottom most(least recent)
		if(!MRUlist.contains(planFile))
		{
			if(MRUlist.size() > MRUSize-1)
			{
				MRUlist.removeElementAt(MRUlist.size()-1);
				MRUlist.insertElementAt(planFile, 0);
				MRUlist.setSize(MRUSize);
			}
			else
				MRUlist.insertElementAt(planFile, 0);
		}
		else
			MRUlist.insertElementAt(MRUlist.remove(MRUlist.indexOf(planFile)), 0);
		((CatMenuBar)getJMenuBar()).updateRecent(MRUlist);
	}

	public void exit()
	{
		clearUp();
	}

	public void serverOn(String planname)
	{
		String ip = "Unknown Host";
		CollaborationControl cc = CollaborationControl.getInstance();
		try{
			ip = "at " + cc.getBoundAddress().getHostAddress() + "  :  " + cc.getRMIPort();
		}catch(Exception e){
			logger.warn("serverOn - error determining host IP address:  "+e.getMessage());
		}

		getDockingManager().showFrame(collabLogDock.getTitle());
		getDockingManager().showFrame(collabLogDock.getTitle()); // JIDE bug workaround
		serverstatus.setText("<html><font color=green>" + planname + "<b> online </b>" + ip + "</font></html>");
		((CatMenuBar)getJMenuBar()).enableCollabViewItem(true);
	}

	public void serverOff()
	{
		getDockingManager().hideFrame(collabLogDock.getTitle());
		serverstatus.setText("<html><font color=red>Offline</font></html>");
		((CatMenuBar)getJMenuBar()).enableCollabViewItem(false);
	}

	public void clientOn(String ip, String port)
	{
		getDockingManager().showFrame(collabLogDock.getTitle());
		getDockingManager().showFrame(collabLogDock.getTitle());
		collabLogDock.clear();
		serverstatus.setText("<html><font color=green><b> Connected </b> to " + ip + "</font></html>");
		((CatMenuBar)getJMenuBar()).enableCollabViewItem(true);
	}

	public void clientOff()
	{
		getDockingManager().hideFrame(collabLogDock.getTitle());
		serverstatus.setText("<html><font color=red>Offline</font></html>");
		((CatMenuBar)getJMenuBar()).enableCollabViewItem(false);
	}

	public CatView getActiveView()
	{
		return (CatView) desktop.getActiveDocument();
	}

	public void setActiveMode(String mode)
	{
		getActiveView().getPanel().setMode(mode);
	}

	public Guid getSelectedPlan() {
		return getActiveView().getPlan().getId();
	}

	public HelpBroker getHelpBroker()
	{
		return mainHB;
	}

	public void setEditState(String state)
	{
		getActiveView().getPanel().setMode(state);
	}

	public void setMRUSize(int size)
	{
		//change value and update the current MRU unless value is larger then...
		if(size<MRUSize) //just let the vector grow on its own
		{
			MRUSize = size;
			MRUlist.setSize(MRUSize);
			((CatMenuBar)getJMenuBar()).updateRecent(MRUlist);
		}
		else
			MRUSize = size;
	}

	public int getMRUSize()
	{
		return MRUSize;
	}

	public NavTree getNavTree()
	{
		return navTree;
	}

	public DocumentPane getDockPane()
	{
		return desktop;
	}
	
	public DockingManager getDockingManager()
	{
		return MainFrm._dockingManager;
	}
	
	public PropertyViewer getPropertyViewer()
	{
		return propViewer;
	}

	public ResourceViewer getResourceViewer()
	{
		return resourceDock;
	}

	public SchemeLegend getSchemeLegend()
	{
		return schmLegend;
	}
	
	public COAViewer getCOAViewer()
	{
		return coaDock;
	}

	public CollabDock getCollabLog()
	{
		return collabLogDock;
	}
	

	public LabelStatusBarItem getSamplerStatus()
	{
		return samplerStatus;
	}

	public void setAlignToGrid(boolean align)
	{
		Control.getInstance().setAlignToGrid(align);
		
		for(String docName : desktop.getDocumentNames())
			((CatView)desktop.getDocument(docName)).getPanel().snapToGrid(align);
	}

	public void setShowGrid(boolean show)
	{
		Control.getInstance().setShowGrid(show);
		
		for(String docName : desktop.getDocumentNames())
			((CatView)desktop.getDocument(docName)).getPanel().setGridState(show ? 2 : 0);
	}

	public boolean getHighlightEnabled()
	{
		return highlightEnabled;
	}

	public void setHighlightEnabled(boolean enab)
	{
		highlightEnabled = enab;
	}

	public void setCollaborationEnabled(boolean enab)
	{
		getCatMenuBar().enableCollabFileItems(enab);
		CatPopupManager.collabEnabled = enab;
	}
	
	public boolean getAutoSvEnabled()
	{
		return autoSave;
	}

	public void setAutoSvEnabled(boolean enab)
	{
		autoSave = enab;
		if(autoSave)
			autoSaveAgent.start();
		else
			autoSaveAgent.stop();
	}

	public int getAutoSvTime()
	{
		return autoSaveDelay;
	}

	public void setAutoSvTime(int delay)
	{
		autoSaveDelay = delay;
		autoSaveAgent.setDelay(delay);
	}

	public void removeFrame(String arg0) {
		_dockingManager.removeFrame(arg0);
	}

	public PlanToolBar getCatToolBar()
	{
		return catToolBar;
	}

	public FileToolBar getFileToolBar()
	{
		return fileToolBar;
	}

	public CatMenuBar getCatMenuBar()
	{
		return menuBar;
	}

	private static void clearUp()
	{
		boolean closingCanceled = false;
		while(_frame.getActiveView() != null && !closingCanceled)
			closingCanceled = !_frame.closePlan();
		if(!closingCanceled)
		{
			_frame.dispose();
			_frame.saveUserPrefs();
			Lm.setParent(null);
			LogManager.shutdown();  //necessary to close out the log appenders (like close files, write footers)
			_frame = null;
			System.exit(0);
		}
	}

	public boolean closePlan()
	{
		try{
			if(ensurePlanSaved(getActiveView().getPlan().getPlanName()))
			{
				getActiveView().removeListener(); //prevents DocumentComponent from also responding to the closing event
				desktop.closeDocument(desktop.getActiveDocumentName());
				propViewer.updateViewer(null);
				getActiveView().getPlan(); //cheat and cause the NullPointer
				return true;
			}
			else
				getActiveView().reAddListener();
			return false;
		}catch(NullPointerException exc){
			navTree.clearTree();
			coaDock.clearViewer();
			ColorScheme.reset();
			schmLegend.updateLegend();
			return true;
		}//no document is open to close
	}

	/**
	 * 
	 * @param planName
	 * @return
	 */
	public boolean ensurePlanSaved(String planName)
	{
		//if plan file has been changed since last save, prompt the user
		boolean planHasChanged = getActiveView().getPlan().isModified();
		if(planHasChanged)
		{
			int choice = JOptionPane.showConfirmDialog(this, "You have not saved "+planName+" since your last changes, save now?");
			if(choice == JOptionPane.YES_OPTION)
				return(getActiveView().save()); //save the plan and return true
			else if(choice == JOptionPane.NO_OPTION)
				return true;
			else
				return false;
		}
		else
			return true;
	}

	public String getLastSaveDir()
	{
		if(!lastSaveDir.equals(""))
			return lastSaveDir;
		return null;
	}

	public void setLastSaveDir(String newDir)
	{
		lastSaveDir = newDir;
	}

	public boolean loadUserPrefs()
	{
		try{
			ObjectInputStream input;

			// need to relocate this config file (should be in users home directory for permissions)
			String oldLoc = EnvUtils.getUserHome()+EnvUtils.sep+"JCat.prefs";
			String newLoc = EnvUtils.getJCATSettingsHome()+"/JCat.prefs";
			if(!(new File(newLoc).exists()) && new File(oldLoc).exists())
			{
				input = new ObjectInputStream(new FileInputStream(oldLoc));;
				try{
					new File(oldLoc).deleteOnExit();
				}catch(Exception exc){
					logger.warn("loadUserPrefs - could not remove prefs file from old location:"+exc.getMessage());
				}
			}
			else
				input = new ObjectInputStream(new FileInputStream(newLoc));

			MRUlist = (Vector)(input.readObject());
			MRUSize = ((Integer)input.readObject()).intValue();
			lastOpenDir = (String)(input.readObject());
			lastSaveDir = (String)(input.readObject());
			simpleMode = ((Boolean)input.readObject()).booleanValue();
			highlightEnabled = ((Boolean)input.readObject()).booleanValue();
			autoSave = ((Boolean)input.readObject()).booleanValue();
			autoSaveDelay = ((Integer)input.readObject()).intValue();
			CollaborationControl.getInstance().setServerOptions(((java.util.Hashtable)input.readObject())); //load preferences for collaboration sessions
			Control.getInstance().setOptions((java.util.Vector)input.readObject());
//			alignToGrid = ((Boolean)input.readObject()).booleanValue();
//			showGrid = ((Boolean)input.readObject()).booleanValue();
			input.close();
			return true;
		}catch(Exception exc){
			return false;
		}

	}

	public void saveUserPrefs()
	{
		if (_frame.getDockingManager() != null)
			_frame.getDockingManager().saveLayoutDataToFile(EnvUtils.getJCATSettingsHome()+"/JCat_layout.prefs");
		try{
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(EnvUtils.getJCATSettingsHome()+"/JCat.prefs"));
			output.writeObject(MRUlist);
			output.writeObject(new Integer(MRUSize));
			output.writeObject(lastOpenDir);
			output.writeObject(lastSaveDir); 
			output.writeObject(new Boolean(simpleMode));
			output.writeObject(new Boolean(highlightEnabled));
			output.writeObject(new Boolean(autoSave));
			output.writeObject(new Integer(autoSaveDelay));
			output.writeObject(CollaborationControl.getInstance().getServerOptions()); //save preferences for collaboration sessions
			output.writeObject(Control.getInstance().getOptions());
//			output.writeObject(new Boolean(alignToGrid));
//			output.writeObject(new Boolean(showGrid));
			output.flush();
			output.close();
		}catch(IOException exc){
			logger.warn("saveUserPrefs - IOError saving user preferences: "+exc.getMessage());
		}
	}

	private void updateSampleStatus()
	{
		String sampCount = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
		boolean isRunning = false;
		if(getActiveView() != null && getActiveView().getPlan().getBayesNet() != null)
		{
			sampCount = "[ "+ getActiveView().getPlan().getBayesNet().getSampleCount()+" samples ]";
			isRunning = getActiveView().getPlan().getBayesNet().isSampling();
		}

		if(!isRunning)
		{
			samplerStatus.setText("<html><font color=red>Stopped "+sampCount+"</font></html>");
			//sampStatusTimer.stop();
		}
		else
			samplerStatus.setText("<html><font color=green>Running "+sampCount+"</font></html>");

		getCatToolBar().setBayesNetRunning(isRunning);
	}

	// Simple probability mode - shows text based probabilities for the simple minded users
	public boolean isSimpleProbMode()
	{
		return simpleMode;
	}

	public void setSimpleProbMode(boolean value)
	{
		MaskedFloat.useMasked = value;
		simpleMode = value;
	}

	public void loadMaskedValues()
	{
		try{
			MaskedFloat.setMaskedValues(MaskedValue.readValues());

		}catch(MissingRequiredFileException exc){
			JOptionPane.showMessageDialog(this, "A required configuration file could not be loaded. \n"+exc.getMessage());
			MaskedFloat.setMaskedValues(new MaskedFloat[]{new MaskedFloat("ERROR", .75f)});
			logger.error("loadMaskedValues - config file missing:  "+exc.getMessage());
		}
	}

	/**
	 * The Run method of the MainFrm updateThread. 
	 * Updates the sampler status when we are running our model;
	 */
	public void run()
	{
		boolean isSampling = true;
		while(isSampling)
		{
			try
			{
				Thread.sleep(3000);
			} catch (InterruptedException e)
			{
				logger.warn("run - Thread sleep interrupted in sampler status update loop");
			}
			if(this.getActiveView() != null)
			{
				AbstractPlan plan = getActiveView().getPlan();
				if(plan.isBuilding()) //net is still building, dont stop this thread, just keep waiting
					continue;
				if(plan.getBayesNet() != null)
					isSampling = plan.getBayesNet().isSampling();
				else
					isSampling = false;
			}
			else
				isSampling = false;
			updateSampleStatus();
		}
		updateThread = null;
	}

	/**
	 * starts the updateThread which maintains statusbar info
	 */
	public void startUpdateThread()
	{
		if(updateThread == null)
		{
			updateThread = new Thread(this, "Sampler-Status-Update");
			updateThread.start();
		}
	}

	
	// PLANLISTENER EVENTS // // // //
	public void activeCOAChanged(PlanEvent event) //might happen through collab
	{
		coaDock.updateViewer(event.getPlan());
	}

	public void bayesNetBuilt(PlanEvent event)
	{
	}

	public void coaListChanged(PlanEvent event) //might happen through collab
	{
		coaDock.updateViewer(event.getPlan());
		
		if(event.getID() == PlanEvent.ADDED) //new COA was created
		{
			_dockingManager.showFrame(coaDock.getTitle());
			_dockingManager.showFrame(coaDock.getTitle()); //twice because of a bug in JIDE i think
		}
		
	}

	public void colorSchemeChanged(PlanEvent event) //might happen through collab
	{
		if(getActiveView() != null && getActiveView().getPlan().equals(event.getPlan()))
		{
			event.getPlan().getColorScheme().makeActive();
			Control.getInstance().setDefaultColor(ColorScheme.getInstance().getColorFor(ColorScheme.DEF_NODE_STRING));
			Control.getInstance().setDefaultTextColor(ColorScheme.getInstance().getColorFor(ColorScheme.DEF_NODE_TEXT_STRING));
		}
		schmLegend.updateLegend();
	}

	public void documentationChanged(PlanEvent event) //might happen through collab
	{
	}

	public void itemListChanged(PlanEvent event) //might happen through collab
	{
		navTree.populateTree(getActiveView().getPlan());
	}

	public void opened(PlanEvent event)
	{
		//this is taken care of already due to a document component event		
//		navTree.populateTree(event.getPlan());
//		coaDock.updateViewer(event.getPlan());
//		schmLegend.updateLegend();
	}

	public void saved(PlanEvent event)
	{
		addToMRU(new File(event.getPlan().getFilePath()));
		setLastSaveDir(new File(event.getPlan().getFilePath()).getParent());
		
		CatView cv = null;
		for(String nm : desktop.getDocumentNames())
			if(((CatView)desktop.getDocument(nm)).getPlan().equals(event.getPlan())) //may need to do something with AbstractPlan.equals
				cv = (CatView)desktop.getDocument(nm);
		
		if(cv != null)
			cv.setTitle(event.getPlan().getPlanName());
	}
	
	public void wasModified(PlanEvent event) //might happen through collab
	{
		CatView cv = null;
		for(String nm : desktop.getDocumentNames())
			if(((CatView)desktop.getDocument(nm)).getPlan().equals(event.getPlan())) //may need to do something with AbstractPlan.equals
				cv = (CatView)desktop.getDocument(nm);
		
		if(cv != null)
			cv.setTitle(event.getPlan().getPlanName() + " *");
	}
	// // // // // // // // //
	
}

