package mil.af.rl.jcat.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import mil.af.rl.jcat.bayesnet.Evidence;
import mil.af.rl.jcat.bayesnet.Policy;
import mil.af.rl.jcat.control.collaboration.CollaborationControl;
import mil.af.rl.jcat.control.collaboration.CollaborationException;
import mil.af.rl.jcat.control.collaboration.CustomSSLClientSocketFactory;
import mil.af.rl.jcat.control.collaboration.CustomSSLServerSocketFactory;
import mil.af.rl.jcat.control.collaboration.UserAuthDialog;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.exceptions.NoSuchNameException;
import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.dialogs.EventDialog;
import mil.af.rl.jcat.gui.dialogs.MechanismDialog;
import mil.af.rl.jcat.integration.parser.Agent;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.COAState;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.ColorSchemeAttrib;
import mil.af.rl.jcat.plan.Documentation;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.PlanListener;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitationSet;
import mil.af.rl.jcat.processlibrary.signaldata.ElicitedProbability;
import mil.af.rl.jcat.processlibrary.signaldata.ModeSet;
import mil.af.rl.jcat.processlibrary.signaldata.ProtocolSet;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.util.ElicitationC;
import mil.af.rl.jcat.util.Encrypter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.FileUtils;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;
import mil.af.rl.jcat.util.ProcessC;
import mil.af.rl.jcat.util.SignalC;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBControllerArgument;
import com.c3i.jwb.JWBControllerManager;
import com.c3i.jwb.JWBLine;
import com.c3i.jwb.JWBObserver;
import com.c3i.jwb.JWBPanel;
import com.c3i.jwb.JWBRemoteObservable;
import com.c3i.jwb.JWBSerializableShape;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;
import com.c3i.jwb.shapes.JWBRoundedRectangle;


/**
 * <p>
 * Title: Control.java
 * </p>
 * Description: Control basically is the glue for most of the other modules, it
 * monitors for local updates from the whiteboard model.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author Edward Verenich
 * @version 1.0
 */

public final class Control implements JWBObserver, ActionListener //<-- listener used not for gui stuff
{
	private static Hashtable<Guid, AbstractPlan> plans = new Hashtable<Guid, AbstractPlan>();
	private static Hashtable modelIDs = new Hashtable(); // <planID(Guid),panel(JWBPanel)
	private static Control control = null;
	private static int rmiport = 1099;
	//To be updated with each release
	public static final String fileVer = "1.1.0";
	private static Color defaultColor = new Color(0, 128, 255);//default cool blue color of jwb;
	public static int planCount = 0;
	private static Color defaultTxtColor = Color.black;
	private static JWBUID cuid = null;
	private boolean autoEventEditor = false; //pop up event editor when adding event
	private boolean persistMode = false; //stay in event mode (drop multiple events)
	private boolean promptEventName = false; //ask for event name after placing event
	private boolean alignToGrid = true, showGrid = false;
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	private Vector<JWBControllerArgument> pendingUpdates = new Vector<JWBControllerArgument>();
	private boolean copyComplete = false;
	private UpdateSubProcessor upSubProc = null;
	private UpdateProcessor updateThread;
	protected static Logger logger = Logger.getLogger(Control.class);
	
	public static Control getInstance()
	{
		if (control == null)
		{
			cuid = new JWBUID();
			control = new Control();
//			if(!control.isAuthorizedCopy())
//			{
//				javax.swing.JOptionPane.showMessageDialog(null, "A required access file is missing, RMI settings may not be set.");
//			}
		}
		return control;
	}
	
	public static Control getInstance(int mode)
	{
		if (control == null)
		{
			cuid = new JWBUID();
			control = new Control();
		}
		return control;
		
	}
	
	private Control()
	{
		loadAccessFile();
		updateThread = new UpdateProcessor();
		upSubProc = new UpdateSubProcessor();
		
		updateThread.start();
	}
	
	public ArrayList<Agent> getAgents()
	{
		return agents;
	}
	
	public void addAgent(Agent a)
	{
		agents.add(a);
	}
	
	public void removeAgent(Agent a)
	{
		agents.remove(a);
	}
	
	public JWBUID getUID()
	{
		return cuid;
	}
	
	public void setRmiPort(int port)
	{
		// in case collab was previously disabled due to port conflict, re-enable it
		// TODO: LOW-PRIORITY   this assumes collab was disabled because of the port error, fine for now
		if(rmiport != port)
			MainFrm.getInstance().setCollaborationEnabled(true);
		rmiport = port;
	}
	
	public int getRmiPort()
	{
		return rmiport;
	}
	
	public Collection getPlanObjects()
	{
		return plans.values();
	}
	
	//loads stored port number from an encrypted file (prime.jc)
	private boolean loadAccessFile()
	{
		boolean iau = true;
		
		String content = "";
//		URL url = this.getClass().getClassLoader().getResource("prime.jc");
		File accessfile = new File(EnvUtils.getJCATSettingsHome() + EnvUtils.sep + "prime.jc");
		boolean copyStarted = false;
		
		try
		{
//			accessfile = new File(url.toURI().getPath());
			if(!accessfile.exists())
			{
				while(!copyComplete)
				{
					if(!copyStarted)
					{
						// This new way of loading resources worx with a common resource jar for Web Start
						InputStream orig = this.getClass().getClassLoader().getResourceAsStream("prime.jc");
						FileUtils.copyFile(orig, accessfile, this);
						copyStarted = true;
					}
					else
					{
						try{
							Thread.sleep(10);
						}catch(InterruptedException exc){   }
					}
				}
			}
//		}
//		catch(URISyntaxException e1){
//			logger.info("loadAccessFile - URISyntaxExc, couldn't load prime.jc, using default rmi port:  "+e1.getMessage());
//			return false;
		}catch(NullPointerException exc){
			logger.info("loadAccessFile - Missing prime.jc config file, using default rmi port: "+exc.getMessage());
			return false;
		}
		if(!accessfile.exists())
			return false;
		

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(accessfile));
			String str;
			while ((str = in.readLine()) != null) 
				content += str;
			in.close();
		}
		catch (IOException e) {
			return false;
		}
		
		Encrypter encrypter = null;
		String dcipher = null;
		
		// now decrypt it
		encrypter = new Encrypter("jc@tpri#a!e77");
		dcipher = encrypter.decrypt(content);
		
		if(dcipher == null)
			return false;
		
		// for now just look for port settings
		StringTokenizer st = new StringTokenizer(dcipher,":");
		if(st.countTokens() < 1)
		{
			return false;
		}
		st.nextToken();
		rmiport = Integer.parseInt(st.nextToken());
		return iau;
	}
	
	/**
	 * Method adds a plan to the control
	 *
	 * @param planid
	 *            String name of the plan to add
	 * @throws DuplicateNameException
	 * @throws RemoteException
	 */
	public Guid addPlan(Guid planid, PlanListener listener) throws DuplicateNameException, RemoteException, MissingRequiredFileException
	{
		try{
			if(planid == null)
			{
				planid = new Guid();
				planCount++;
			}
			if (plans.containsKey(planid))
			{
				//plans.remove(pname);
				throw(new DuplicateNameException());
			}
			AbstractPlan plan = new AbstractPlan(planid);
			plan.setPlanName("NewModel"+planCount);
			
			JWBUID id = JWBControllerManager.getInstance().createController(new CustomSSLServerSocketFactory(rmiport, rmiport-1), new CustomSSLClientSocketFactory(rmiport, rmiport-1));
			JWBController newController = JWBControllerManager.getInstance().getController(id);
			
			// check to ensure specified ports were able to be used, if not another program (maybe another JCAT) might be using the ports, 
			// disable collaboration for this instance and warn user
			if(newController.getServerSocketFactory() instanceof CustomSSLServerSocketFactory)
			{
				if(((CustomSSLServerSocketFactory)newController.getServerSocketFactory()).wasFailSafePortUsed())
				{
					String msg = "An error occured while initializing the collaborative features of JCAT. \n" +
							"This was due to the specified network port being already in use on this \n" +
							"computer system.  If you currently have another JCAT open, that may be the \n" +
							"cause of this error.  You may continue using JCAT however the collaboration \n" +
							"system will be disabled in this instance of JCAT.";
					new UpdateSubProcessor().showMessage(msg, "New Model", JOptionPane.WARNING_MESSAGE);
					MainFrm.getInstance().setCollaborationEnabled(false);
				}
			}
			
			// keep track of plans using controller JWBUID
			JWBPanel newPan = new JWBPanel(id);
			newPan.setAutoResetMode(!isPersistMode());
			newPan.snapToGrid(alignToGrid);
			newPan.setGridState(showGrid ? 2 : 0);
			
			if(listener != null)
				plan.addPlanListener(listener);

			//TODO: creating controller and adding observer before putting registering in hashmaps can cause race condition style error if an update comes in quicker
			newController.addObserver(this);
			
			plans.put(planid, plan);
			modelIDs.put(planid, newPan);
			
			return planid;
		//thrown when jwb properties file cant be loaded (the first time)
		}catch(ExceptionInInitializerError exc){
			throw new MissingRequiredFileException("jwhiteboard.properties file is missing and is required to view plans. \n" +
				"Restore this file or reinstall JCat.", exc);
		//thrown when jwb properties file cant be loaded (after the first time)
		}catch(NoClassDefFoundError exc){
			throw new MissingRequiredFileException("jwhiteboard.properties file is missing and is required to view plans. \n" +
				"Restore this file or reinstall JCat.", exc);
		}
	}
	
	/**
	 * Removes a plan with a given name from the controler.
	 *
	 * @param pname
	 *            String plan name to remove
	 * @throws NoSuchNameException
	 *             no such plan exists
	 */
	public void removePlan(Guid pname) throws NoSuchNameException
	{
		if (!plans.containsKey(pname))
		{
			throw new NoSuchNameException("No model with name: " + pname);
		}
		synchronized (plans)
		{
			JWBUID uid = this.getController(pname).getUID();
			modelIDs.remove(uid);
			JWBControllerManager.getInstance().destroyController(uid);
			
			//Object b = boards.remove(pname);
			plans.remove(pname);
			
			pname = null;
			uid = null;
			System.gc();// just in case
		}
	}
	
	public void startPlanServer(Guid planid) throws NoSuchNameException, Exception
	{
		if (!plans.containsKey(planid))
			throw new NoSuchNameException("Model: " + planid + " does not exist.");
		
		JWBUID id = ((JWBPanel)modelIDs.get(planid)).getControllerUID();
		JWBController jwbCont = JWBControllerManager.getInstance().getController(id);
		
		CollaborationControl collCont = CollaborationControl.getInstance();
		if(collCont.startServer(rmiport, jwbCont))
		{
			rmiport = collCont.getRMIPort();
			//update the ports and interfaces in socket factories and RMI subsystem
			System.setProperty("java.rmi.server.hostname", collCont.getBoundAddress().getHostAddress());
			if(jwbCont.getServerSocketFactory() instanceof CustomSSLServerSocketFactory)
			{
				((CustomSSLServerSocketFactory)jwbCont.getServerSocketFactory()).setPorts(rmiport, rmiport-1);
				((CustomSSLClientSocketFactory)jwbCont.getClientSocketFactory()).setPorts(rmiport, rmiport-1);
			}
			jwbCont.reExport();
			
			jwbCont.startServer(collCont.getBoundAddress(), rmiport);
			MainFrm.getInstance().serverOn(((AbstractPlan)plans.get(planid)).getPlanName());
		}
	}
	
	/**
	 * Method stops the GUI (if in user mode) and the PLAN collab servers
	 *
	 * @param pname
	 *            String
	 * @throws NoSuchNameException
	 * @throws Exception
	 */
	public void stopPlanServer(Guid planid) throws NoSuchNameException, Exception
	{
		if (!plans.containsKey(planid))
			throw new NoSuchNameException("Model: " + planid + " does not exist.");
		
		JWBUID id = ((JWBPanel)modelIDs.get(planid)).getControllerUID();
		//closing a plan window calls stopplanserver in case a server
		try{
			if(CollaborationControl.getInstance().stopServer(id))
			{
				JWBControllerManager.getInstance().getController(id).endSession();
				mil.af.rl.jcat.gui.MainFrm.getInstance().serverOff();
			}
			
		}catch(NullPointerException exc){   } // no collabcont server or client has been started yet
	}
	
	
	public void startClient(Guid planid, String host) throws Exception
	{
		if (!plans.containsKey(planid))
			throw new NoSuchNameException(planid + " does not exist.");
		
		boolean authenticated = false;
		
		String firewallMsg = "Check to ensure you have entered the correct server address \nand " +
		"that any network firewalls/routers involved are properly configured \nfor the use " +
		"of JCAT collaboration";
		
		JWBUID id = ((JWBPanel)modelIDs.get(planid)).getControllerUID();
		try{
			JWBController jwbCont = JWBControllerManager.getInstance().getController(id);
			CollaborationControl collCont = CollaborationControl.getInstance();
			
			if(collCont.startClient(host, rmiport+1, jwbCont))
			{
				System.setProperty("java.rmi.server.hostname", collCont.getBoundAddress().getHostAddress());
				jwbCont.reExport();
				
				if(collCont.getAuthenticationType() == CollaborationControl.SESSION_PASS_AUTH)
				{
					UserAuthDialog userAuth = new UserAuthDialog(MainFrm.getInstance(), CollaborationControl.SESSION_PASS_AUTH);
					String pass = userAuth.getPassword();
					
					if(pass != null)
					{
						if(collCont.authenticate(pass))
							authenticated = true;
						else
						{
							stopClient(planid);
							new UpdateSubProcessor().showMessage("Authentication failed. \nPlease ensure you have specified the correct password.");
						}
					}
					else
						stopClient(planid);
				}
				else if(collCont.getAuthenticationType() == CollaborationControl.USER_LIST_AUTH)
				{
					UserAuthDialog userAuth = new UserAuthDialog(MainFrm.getInstance(), CollaborationControl.USER_LIST_AUTH);
					String user = userAuth.getUsername();
					String pass = userAuth.getPassword();
					
					if(user != null)
					{
						if(collCont.authenticate(user, pass))
							authenticated = true;
						else
						{
							stopClient(planid);
							new UpdateSubProcessor().showMessage("Authentication failed. \nPlease ensure you have specified the correct username and password.");
						}
					}
					else
						stopClient(planid);
				}
				else if(collCont.getAuthenticationType() == CollaborationControl.IP_LIST_AUTH)
				{
					//don need to send anything, server knows the clients ip
					//send authenticate with a username for fun or maybe we'll use it on server for chat id
					if(collCont.authenticate("something"))
						authenticated = true;
					else
					{
						stopClient(planid);
						new UpdateSubProcessor().showMessage("Authentication failed. \nYour IP address was not allowed by the server.");
					}
				}
				else if(collCont.getAuthenticationType() == CollaborationControl.NO_AUTH)
					authenticated = true;
			}
			
			if(authenticated)
			{
				jwbCont.startClient(host, rmiport);
				mil.af.rl.jcat.gui.MainFrm.getInstance().clientOn(host, rmiport+"");
			}
			
		}catch(CollaborationException exc){
			logger.error("Control.startClient - CollaborationException:  "+exc.getMessage());
			stopClient(planid);
			new UpdateSubProcessor().showMessage("Could not connect to remote host." + "\n" + firewallMsg);
		}
		catch(java.rmi.RemoteException exc){
			logger.error("Control.startClient - RemoteException:  "+exc.getMessage());
			stopClient(planid);
			new UpdateSubProcessor().showMessage("Could not connect to remote host." + "\n" + firewallMsg);
		}
		catch(Exception e){
			logger.error("Control.startClient - Unhandled Exception:  ", e);
			stopClient(planid);
			new UpdateSubProcessor().showMessage("An error occured while attempting to connect, \nthe connection could not be established."  + "\n" + firewallMsg);
		}
	}
	
	public void stopClient(Guid planid)throws NoSuchNameException
	{
		if (!plans.containsKey(planid))
			throw new NoSuchNameException("Model: " + planid + " does not exist.");
		
		//JWBUID id = ((JWBPanel) boards.get(planid)).getControllerUID();
		JWBUID id = ((JWBPanel)modelIDs.get(planid)).getControllerUID();
		try
		{
			if(CollaborationControl.getInstance().stopClient(id))
			{
				JWBControllerManager.getInstance().getController(id).endSession();
				mil.af.rl.jcat.gui.MainFrm.getInstance().clientOff();
			}
		}catch(Exception e)
		{
			logger.warn("stopClient - Error stopping client:  "+e.getMessage());
		}
	}
	
	/**
	 * Method returns a JWBModel associated with a given plan name
	 *
	 * @param planid
	 *            String name of the plan
	 * @return JWBController
	 */
	public JWBController getController(Guid planid)
	{
		return JWBControllerManager.getInstance().getController(((JWBPanel)modelIDs.get(planid)).getControllerUID());
		
	}
	
	/**
	 * Determins if the JWBController sending this argument is the the controller local to this machine
	 * @param arg the controller arugment to check
	 * @return true if the local controller send the argument
	 */
	public boolean isLocalModel(JWBControllerArgument arg)
	{
		boolean contains = false;
		Iterator i = modelIDs.values().iterator();
		for(;i.hasNext();)
		{
			
			if(((JWBPanel)i.next()).getControllerUID().equals(arg.getControllerUID()))
			{
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	/**
	 * Method returns a JWBPanel associated with a given name
	 *
	 * @param name
	 *            String
	 * @return JWBPanel
	 */
	public JWBPanel getPanel(Guid planid)
	{
		return (JWBPanel) modelIDs.get(planid);
	}
	
	public String getPlanAsXML(Guid planID)
	{
		JWBController controller = this.getController(planID);		
		Element root = DocumentHelper.createElement("Plan");
		root.addAttribute("name", getPlan(planID).getPlanName());
		root.addAttribute("guid", planID.toString());
		root.addAttribute("version", fileVer);
		
		//root.add(createDocumentationElement(planID));
		root.add(createLogicElement(getPlan(planID).getItems().values(), getPlan(planID).getLibrary()));
		//root.add(this.createGraphElement(shapes,getPlan(planID)));
		
		return root.asXML();//.getDocument();
	}
	
	public String getPlanGraphics(Guid planID)
	{
		JWBController controller = this.getController(planID);
		Collection shapes = controller.getShapes().values();
		
		Element root = DocumentHelper.createElement("Plan");
		root.addAttribute("name", getPlan(planID).getPlanName());
		root.addAttribute("guid", planID.toString());
		root.addAttribute("version", fileVer);
		
		//root.add(createDocumentationElement(planID));
		//root.add(createLogicElement(shapes));
		root.add(this.createGraphElement(shapes,getPlan(planID)));
		
		return root.asXML();//.getDocument();
		
	}
	
	public AbstractPlan getPlan(Guid planID)
	{
		return (AbstractPlan)plans.get(planID);
	}
	
	public Guid getPlanId(JWBUID cuid)
	{
		Guid guid = null;
		Guid iguid = null;
		Iterator ki = modelIDs.keySet().iterator();
		for(;ki.hasNext();)
		{
			iguid = (Guid)ki.next();
			JWBUID juid = ((JWBPanel)modelIDs.get(iguid)).getControllerUID();
			if(juid.equals(cuid))
			{
				guid = iguid;
				break;
			}
		}
		return guid;
	}
	
	
	public void update(JWBRemoteObservable remoteObservable, JWBControllerArgument argument)
	{
		// just add it to a threaded queue for processing when theres time
		updateThread.add(new ControllerUpdateArgument(remoteObservable, argument));
	}
	
	/**
	 * This is where most of the s@#t happens. The control monitors the model to
	 * listen for graphical changes, and then updates the abstract plan
	 * accordinly.
	 *
	 * @param remoteObservable
	 *            RemoteObservable
	 * @param argument
	 *            Serializable
	 * @throws RemoteException
	 */
	public void update(ControllerUpdateArgument controlArg) throws RemoteException
	{
		JWBControllerArgument argument = controlArg.arg;
		JWBRemoteObservable remoteObservable = controlArg.observable;
		
		// in here we must use the JWBRemoteArgument object to observe the model
		JWBControllerArgument arg =  argument;
		JWBController modelcontrol = (JWBController) remoteObservable;
		
		Guid planid = getPlanId(modelcontrol.getUID());
		if(planid == null)
		{
			logger.warn("update - No plan was found for the remote observer! \n\t Not processing argument:  "+arg);
			return;
		}
		AbstractPlan plan = (AbstractPlan) plans.get(planid);
		
		// put_external is extinct, a PlanArg is sent in its place
		if (arg.getAction() == JWBControllerArgument.PUT_EXTERNAL_UPDATE)
		{
			logger.warn("** WARNING: putShapeExternal was called... this method of updating is extinct and needs to be changed!! **");
		}
		if (arg.getAction() == JWBControllerArgument.PUT)
		{
			if(logger.isDebugEnabled())
				logger.debug("** UPDATE - Put **");
			
			//sort the incoming shapes by type (box or line)
			ArrayList list = (ArrayList)arg.getArgument();
			ArrayList<JWBShape> sortedShapes = new ArrayList<JWBShape>();
			Iterator li = list.iterator();
			JWBShape sh;
			for(;li.hasNext();)
			{
				sh = JWBControllerManager.getInstance( ).getController( modelcontrol.getUID() ).getShape(
						( (JWBSerializableShape)li.next( ) ).getUID( ) );
				if(sh.getType().equals("com.c3i.jwb.JWBLine"))
					sortedShapes.add(sh);
				else
					sortedShapes.add(0, sh);
			}
			
			int sindex = list.size();
			Guid itemID;
			
			for(JWBShape shape : sortedShapes)
			{
				if(shape.getAttachment() != null)
				{
					if(logger.isDebugEnabled())
						logger.debug("\t - (has attachment) **");
					
					--sindex;
					itemID = (Guid)shape.getAttachment();
					
					// if collaborating as a client, process all the shapes then request the library, must be initial connect
					// this happens when pasting also so check to see if the plan has already been retreived once (during initial connect)
					if(!plan.containsItem(itemID) && (modelcontrol.getClientType() == JWBController.CLIENT) && !plan.getLoadComplete())
					{
						if(logger.isTraceEnabled())
							logger.trace("\t\t - Put (collab client) **");
						
						if(sindex == 0)
						{
							// now ask the server for a entire LIBRARY and the entire PLAN
							modelcontrol.foreignUpdate(new LibArgument(LibArgument.CLIENT_REQUEST, "REQUEST"));
							modelcontrol.foreignUpdate(new PlanArgument(PlanArgument.PLAN_REQUEST));
						}
					}
				}
				else //no attachment yet, so its a newly added shape, we must create the plan stuff for it
				{
					if(logger.isDebugEnabled())
						logger.debug("\t - (has NO attachment) **");
					
					if(shape.getType().equals("com.c3i.jwb.shapes.JWBRoundedRectangle"))
					{
						if(isLocalModel(arg))
							this.addEvent(shape, modelcontrol, plan);
					}
					else if(shape.getType().equals("com.c3i.jwb.JWBLine"))
					{
						if(isLocalModel(arg))
							this.addMechanism(shape, modelcontrol, plan, arg);
					}
					
					if(getPanel(getPlanId(modelcontrol.getUID())).isAutoResetMode() && isLocalModel(arg))
						MainFrm.getInstance().getCatToolBar().deselectButtons();
				}
			}
			
			// if there are pending updates that were held until shapes came in, do them now
			synchronized(pendingUpdates)
			{
				for(JWBControllerArgument pendArg : pendingUpdates)
					update(remoteObservable, pendArg);
				pendingUpdates.clear();
			}
		}
		//a SHAPE has been removed, we'll still do plan removal here too
		else if (arg.getAction() == JWBControllerArgument.REMOVE)
		{
			if(logger.isDebugEnabled())
				logger.debug("** UPDATE - REMOVE **  ");
			
			ArrayList list = (ArrayList)arg.getArgument();
			Iterator si = list.iterator();
			int offset = 0;
			for(;si.hasNext();)
			{
				// new way to remove plan items using a guid
				PlanItem removed =  plan.removeItem((JWBUID)si.next(), arg.getLinkedShapes().subList(offset, offset+2));
				offset+=2;
			}
			
		}
		else if(arg.getAction() == JWBControllerArgument.FOREIGN_UPDATE)
		{
			if(logger.isDebugEnabled())
				logger.debug("** UPDATE - FOREIGN_UPDATE **  ");
			
			if( arg.getArgument() instanceof PlanArgument)
			{
				if(logger.isDebugEnabled())
					logger.debug("\t - PlanArgument **");
				PlanArgument pArg = (PlanArgument)arg.getArgument();
				
				// client requesting entire plan (prolly inital connection), should be sending out an arg in this block
				if(pArg.getType() == PlanArgument.PLAN_REQUEST)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_REQUEST");
					
					if(modelcontrol.getClientType() == JWBController.SERVER) //only server should respond
					{
						ArrayList<PlanItem> items = new ArrayList<PlanItem>(plan.getItems().values());
						PlanArgument pResponse = new PlanArgument(PlanArgument.PLAN_RESPONSE, items, plan.getItemMap());
						//include some other plan elements also
						pResponse.getParameters().activeDefaultsSet = plan.getDefaultProbSet();
						pResponse.getParameters().planDoc = plan.getDocumentation();
						pResponse.getParameters().colorScheme = plan.getColorScheme();
						pResponse.getParameters().coaList = plan.getCOAList();
						
						modelcontrol.foreignUpdate(pResponse);
					}
				}
				else if(pArg.getType() == PlanArgument.PLAN_RESPONSE)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_RESPONSE");
					
					// only client that requested should have to process the received plan
					if(plan.isEmpty())
					{
						ArrayList<PlanItem> items = pArg.getParameters().getItems();
						MultiMap<Guid, JWBUID> map = pArg.getParameters().getItemMap();
						for(int x=0; x<items.size(); x++)
						{
							for(JWBUID uid : map.get(items.get(x).getGuid()))
								plan.addItem(items.get(x), uid);
						}
						
						//set other included plan elements (might wana give user the choice on some of these)
						plan.setDefaultProbSet(pArg.getParameters().activeDefaultsSet);
						plan.setDocumentation(pArg.getParameters().planDoc);
						plan.setColorScheme(pArg.getParameters().colorScheme);
						plan.setCOAList(pArg.getParameters().coaList);
						
						plan.setLoadComplete(true);
					}
				}
				else if(pArg.getType() == PlanArgument.ITEM_ADD) //a planitem was added, add it to local plan
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - ITEM_ADD");
					
					PlanArgument.Parameters param = pArg.getParameters();
					plan.addItem(param.getItem(), param.getMappedUIDs());
					
					ArrayList<JWBShape> shapesToUpdate = new ArrayList<JWBShape>();
					if(isLocalModel(arg)) // only fire if this is the observer who performed this update
					{
						List<JWBShape> shapes = modelcontrol.getShapes(plan.getShapeMapping(param.getItem().getGuid()));
						if(applyMarkups(param.getItem(), shapes))
							shapesToUpdate.addAll(shapes);
						
						if(autoEventEditor && param.getItem() instanceof Event && shapes.size() > 0 && !pArg.isAutomated())
							upSubProc.showEventEditor(shapes.get(0), modelcontrol);
						else if(promptEventName && param.getItem() instanceof Event && !pArg.isAutomated())
							new UpdateSubProcessor().nameEvent((Event)param.getItem(), shapes.get(0), modelcontrol);
					}
					if(shapesToUpdate.size() > 0)
						modelcontrol.putShapes(shapesToUpdate);
				}
				else if(pArg.getType() == PlanArgument.ITEM_UPDATE)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - ITEM_UPDATE");
					
					ArrayList<JWBShape> shapesToUpdate = new ArrayList<JWBShape>();
					Iterator<PlanItem> items = pArg.getParameters().getItems().iterator();
					while(items.hasNext())
					{
						PlanItem thisItem = items.next();
						plan.addUnmappedItem(thisItem);
						
						if(isLocalModel(arg)) // only fire if this is the observer who performed this update
						{
							List<JWBShape> shapes = modelcontrol.getShapes(plan.getShapeMapping(thisItem.getGuid()));
							if(applyMarkups(thisItem, shapes))
								shapesToUpdate.addAll(shapes);
						}
					}
					if(shapesToUpdate.size() > 0)
						modelcontrol.putShapes(shapesToUpdate);
					
					// deactivate any COAs since plan items have been modified as long as this isn't the actual update from the coa apply
					if(!pArg.isFromCOA())
					{
						plan.clearCOA();
					}
				}
				// ITEM_REMOVE can be used but doesn't need to be done normally, deleting a shape currently deletes planitem stuff
				// the shape removal also handles deleting planitem (mechs) that are attached, this does not
				else if(pArg.getType() == PlanArgument.ITEM_REMOVE)
				{
					Iterator<PlanItem> items = pArg.getParameters().getItems().iterator();
					while(items.hasNext())
						plan.removeItem(items.next());
				}
				else if(pArg.getType() == PlanArgument.ITEM_PASTE)
				{
					if(isLocalModel(arg))
					{
						if(logger.isTraceEnabled())
							logger.trace("\t\t - ITEM_PASTE");
						
						ArrayList<PlanItem> oldItems = pArg.getParameters().getItems();
						MultiMap<Guid, JWBUID> itemMap = pArg.getParameters().getItemMap();
						
						for(int x=0; x<oldItems.size(); x++)
						{
							PlanItem item = oldItems.get(x);
							// if any of the shapes havn't arrived yet, don't process this update now
							// the shapes were put separatly and therefor might not exist in this model just yet so
							// so hang on to this update so it can be processed after the shape comes in, can't think of anything better
							// TODO: LOW-PRIORITY   this could potentially be faulty
							if(modelcontrol.getShapes(itemMap.get(item.getGuid())).size() < itemMap.get(item.getGuid()).size())
							{
								synchronized(pendingUpdates)
								{
									pendingUpdates.add(arg);
									break;
								}
							}
							
							if(item.getItemType() == PlanItem.MECHANISM)
								this.pasteMechanism(modelcontrol, itemMap.get(item.getGuid()), (Mechanism)item, plan);
							else
								this.pasteEvent(modelcontrol, itemMap.get(item.getGuid()).get(0), (Event)item, plan);
						}
					}
				}
				else if(pArg.getType() == PlanArgument.PLAN_DOCUMENTATION)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_DOCUMENTATION");
					
					if(pArg.getParameters().planDoc != null)
						plan.setDocumentation(pArg.getParameters().planDoc);
				}
				else if(pArg.getType() == PlanArgument.PLAN_COLORSCHEME)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_COLORSCHEME");
					
					if(pArg.getParameters().colorScheme != null)
						plan.setColorScheme(pArg.getParameters().colorScheme);
				}
				else if(pArg.getType() == PlanArgument.PLAN_COAS)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_COAS");
					
					if(pArg.getParameters().coaList != null)
						plan.setCOAList(pArg.getParameters().coaList);
				}
				else if(pArg.getType() == PlanArgument.PLAN_ACTIVECOA)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - PLAN_ACTIVECOA");
					
					if(pArg.getParameters().activeCOAs != null)
						plan.setActiveCOAs(pArg.getParameters().activeCOAs);
				}
			}
			
			else if (arg.getArgument() instanceof mil.af.rl.jcat.control.LibArgument)
			{
				if(logger.isDebugEnabled())
					logger.debug("\t - LibArgument **");
				LibArgument larg = (LibArgument) arg.getArgument();
				if (larg.getOriginator() == LibArgument.CLIENT_REQUEST) // client requesting library
				{
					if(modelcontrol.getClientType() == JWBController.SERVER) //only server should respond
					{
						Document lib = plan.getLibrary().getLibraryDocument();
						LibArgument sla = new LibArgument(LibArgument.SERVER_RESPONSE, lib.asXML());
						modelcontrol.foreignUpdate(sla);
					}
				}
				else if (larg.getOriginator() == LibArgument.SERVER_RESPONSE) // library recvd
				{
					// only client that requested should have to process the received library
					if(plan.getLibrary().isEmpty())
					{
						LibArgument sla = (LibArgument) arg.getArgument();
						try {
							plan.getLibrary().deserializeLibrary(DocumentHelper.parseText((String) sla.getArgument()).getRootElement());
						} catch (Exception e) {
							logger.error("LibArgument - Unhandled Exception", e);
						}
					}
				}
			}
			else if(arg.getArgument() instanceof RemSignalArg)
			{
				RemSignalArg rsa = (RemSignalArg)arg.getArgument();
				if(logger.isDebugEnabled())
					logger.debug("\t - RemSignalArg **  op value:  "+rsa.getOperation());
				
				try{
					if(rsa.getOperation() == RemSignalArg.ADD)
					{
						plan.getLibrary().addSignal(rsa.getNewSignal());
					}
					else if(rsa.getOperation() == RemSignalArg.REMOVE)
					{
						if(rsa.getProcess() != null) // want to remove a signal from a process
							plan.getLibrary().deleteSignalFromProcess(rsa.getProcess(), rsa.getArgument().get(0));
						else // want to remove the signal itself from the library
							plan.getLibrary().deleteSignal(rsa.getArgument().get(0));
					}
					else if(rsa.getOperation() == RemSignalArg.RENAME)
					{
						plan.getLibrary().setSignalName(rsa.getArgument().get(0), rsa.getNewName());
						renameMechanism(rsa.getArgument().get(0), rsa.getNewName(), plan);
					}
					else if(rsa.getOperation() == RemSignalArg.ADD_CAUSE)
					{
						plan.getLibrary().addCause(rsa.getProcess(), rsa.getArgument().get(0));
					}
					else if(rsa.getOperation() == RemSignalArg.ADD_INHIBITOR)
					{
						plan.getLibrary().addInhibitor(rsa.getProcess(), rsa.getArgument().get(0));
					}
					else if(rsa.getOperation() == RemSignalArg.ADD_EFFECT)
					{
						plan.getLibrary().addEffect(rsa.getProcess(), rsa.getArgument().get(0));
					}
					else if(rsa.getOperation() == RemSignalArg.ADD_ELICITED)
					{
						plan.getLibrary().addElicitedValue(rsa.getProcess(), rsa.getProtocol(), rsa.getArgument(),
								rsa.getValue(), rsa.getGroupName());
					}
					else if(rsa.getOperation() == RemSignalArg.REM_ELICITED)
					{
						plan.getLibrary().deleteElicitedValue(rsa.getProcess(), rsa.getArgument());
					}
					else if(rsa.getOperation() == RemSignalArg.CHANGE_ELICITED)
					{
						// for now remove the old one
						plan.getLibrary().deleteElicitedValue(rsa.getProcess(), rsa.getArgument());
						// add the new one
						plan.getLibrary().addElicitedValue(rsa.getProcess(), rsa.getProtocol(), rsa.getArgument(), rsa.getValue(), rsa.getGroupName());
					}
					else if(rsa.getOperation() == RemSignalArg.INVERT)
					{
						plan.getLibrary().setSignalInversion(rsa.getProcess(), rsa.getNewSignal().getSignalID(), rsa.invert());
					}
					
				}catch(SignalModeConflictException sce)
				{
					//this really should not happen as there SHOULD be checks done before attempting to add
					new UpdateSubProcessor().showMessage(sce.toString());
					logger.error("update(RemSigArg) - SignalModeConflictExc detected in control:  "+sce.getMessage());
				}
				
			}
			else if(arg.getArgument() instanceof LibProcessArg)
			{
				if(logger.isDebugEnabled())
					logger.debug("\t - LibProcessArg **");
				
				LibProcessArg rpa = (LibProcessArg)arg.getArgument();
				if(rpa.getOperation() == LibProcessArg.ADD_PROCESS)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - ADD_PROCESS **");
					plan.getLibrary().createProcess(rpa.getProcessGuid(), rpa.getName(), rpa.getDefaults(), rpa.getDefaultsSubType());
				}else if(rpa.getOperation() == LibProcessArg.COPY_SIGNAL_DATA)
				{
					if(logger.isTraceEnabled())
						logger.trace("\t\t - COPY_SIGNAL_DATA **");
					try{
						copySignalData(plan, modelcontrol, rpa, true);
					}catch(SignalModeConflictException exc){
						logger.error("UPDATE - COPY_SIGNAL_DATA:  SignalModeConflict - "+exc.getMessage());
					}
				}
			}
		}
		
		Object argArg = arg.getArgument();
		// dont set plan modified when opened (there are some initial wierd jwb puts)
		if(!(argArg instanceof ArrayList) || (argArg instanceof ArrayList && ((ArrayList)argArg).size() > 0))
			plan.setModified(true);
	}// end of update()
	
	/**
	 * Convience method for applying the proper markups to the list of shapes associated with a single PlanItem
	 * @param item
	 * @param jwbControl controller to retrieve shapes by ID
	 * @param shape ID list
	 */
	public static boolean applyMarkups(PlanItem item, List<JWBShape> shapes)
	{
		boolean markedUp = false;
		Iterator<JWBShape> shapesIt = shapes.iterator();
		while(shapesIt.hasNext())
			if(applyMarkups(item, shapesIt.next()))
				markedUp = true;
		
		return markedUp;
	}
	
	/**
	 * Convience method for applying the proper markups to a shape
	 * @param item
	 * @param shape
	 * @return true if a markup was added or removed
	 */
	public static boolean applyMarkups(PlanItem item, JWBShape shape)
	{
		boolean markedUp = false;
		
		if((item.getDelay() != 0 || item.getPersistence() != 1 || item.getContinuation() != 0.0f))
		{
			if(!shape.containsMarkup('T'))
			{
				shape.addMarkup('T');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('T'))
		{
			shape.removeMarkup('T');
			markedUp = true;
		}
		
        if(item.getSchedule().size() > 0)
        {
        	if(!shape.containsMarkup('S'))
        	{
        		shape.addMarkup('S');
        		markedUp = true;
        	}
        }
        else if(shape.containsMarkup('S'))
        {
        	shape.removeMarkup('S');
        	markedUp = true;
        }
        
		if((item.getResources().size() > 0 || item.getThreatResources().size() > 0))
		{
			if(!shape.containsMarkup('R'))
			{
				shape.addMarkup('R');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('R'))
		{
			shape.removeMarkup('R');
			markedUp = true;
		}
		
		if(item.getEvidence().size() > 0)
		{
			if(!shape.containsMarkup('E'))
			{
				shape.addMarkup('E');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('E'))
		{
			shape.removeMarkup('E');
			markedUp = true;
		}
		
		return markedUp;
	}
	
	private void pasteEvent(JWBController control, JWBUID shapeID, Event oldItem, AbstractPlan plan)
	{
		// first thing make sure all the signals exist in the current library;
		// create a new process
		Guid newProcGuid = new Guid();
		String name = oldItem.getName();
		// Process oldProcess = plan.getLibrary().getProcess(item.getProcessGuid());
		// this synchronizes the process state in all libs
		LibProcessArg parg = new LibProcessArg(LibProcessArg.ADD_PROCESS, newProcGuid, name, plan.getDefaultProbSet(), -1);
		LibProcessArg p2 = null;
		
		// copy the signal data from the copied process to the new copy
		// if the old processid exists then its pasted within the same program instance
		if(plan.getLibrary().getProcess(oldItem.getProcessGuid()) != null)
		{
			int defSubType = plan.getLibrary().getProcess(oldItem.getProcessGuid()).getDefaultsSubType();
			p2 = new LibProcessArg(LibProcessArg.COPY_SIGNAL_DATA, newProcGuid, oldItem.getProcessGuid(), plan, defSubType);
			parg.setDefaultsSubType(defSubType);
		}
		else
		{
			p2 = oldItem.getPArgument();
			p2.setNewGuid(newProcGuid);
			parg.setDefaultsSubType(p2.getDefaultsSubType());
		}
		
		// send the created libprocessarg out
		try{
			// this is cheating to ensure pasted processes are around when pasting the mechs
			// normally you would not manually add the stuff to the library
			plan.getLibrary().createProcess(parg.getProcessGuid(), parg.getName(), parg.getDefaults(), parg.getDefaultsSubType());
			copySignalData(plan, control, p2, false);
			
			
			control.foreignUpdate(parg);
			control.foreignUpdate(p2);
		}catch(RemoteException rex)
		{
			logger.error("pasteEvent - RemoteException:  "+rex.getMessage());
		}
		catch(SignalModeConflictException exc){
			logger.error("UPDATE - pasting event:  SignalModeConflict - "+exc.getMessage());
		}

		
		Event event = new Event(new Guid(), oldItem.getName(), oldItem.getLabel(), p2.getProcessGuid());
		//Event event = new Event(new Guid(),item.getName(),item.getLabel(),p.getProcessID(),plan.getLibrary());
		
		JWBShape shape = control.getShape(shapeID);

		// this is cheating to ensure pasted events are around when pasting the mechs
		// normally you would not manually add the stuff to the plan
		plan.addItem(event, shape.getUID());
		shape.setAttachment(event.getGuid());
		
		try{
			PlanArgument arg = new PlanArgument(PlanArgument.ITEM_ADD, event, shape.getUID());
			arg.setIsAutomated(true);
			control.foreignUpdate(arg);
			control.putShape(shape);
		}catch(RemoteException ex){
			logger.error("pasteEvent - RemoteException:  "+ex.getMessage());
		}
		
	}
	
	private void copySignalData(AbstractPlan plan, JWBController modelcontrol, LibProcessArg rpa, boolean doUpdates) throws SignalModeConflictException
	{
		RemSignalArg rsa;
		ArrayList<Guid> sigs;
		Library lib = plan.getLibrary();
		
		try{
			// pasting into the same plan, means we keep the signal guids
			if(lib.getProcess(rpa.fromProcess()) != null)
			{
				if(logger.isTraceEnabled())
					logger.trace("      ** - COPY_SIG_DATA (process exists) **");

				// first add all the signals from one process to the other in their proper modes
				for(Object guid : lib.getCausalSignals(rpa.fromProcess()))
				{
					sigs = new ArrayList<Guid>();
					sigs.add(((Signal)guid).getSignalID());
					rsa = new RemSignalArg(RemSignalArg.ADD_CAUSE, sigs, rpa.getProcessGuid());
					if(doUpdates)
						modelcontrol.foreignUpdate(rsa);
					else
						lib.addCause(rpa.getProcessGuid(), sigs.get(0));
				}
				for(Object guid : plan.getLibrary().getEffectSignals(rpa.fromProcess()))
				{
					sigs = new ArrayList<Guid>();
					sigs.add(((Signal)guid).getSignalID());
					rsa = new RemSignalArg(RemSignalArg.ADD_EFFECT, sigs, rpa.getProcessGuid());
					if(doUpdates)
						modelcontrol.foreignUpdate(rsa);
					else
						lib.addEffect(rpa.getProcessGuid(), sigs.get(0));
				}
				for(Object guid : plan.getLibrary().getInhibitingSignals(rpa.fromProcess()))
				{
					sigs = new ArrayList<Guid>();
					sigs.add(((Signal)guid).getSignalID());
					rsa = new RemSignalArg(RemSignalArg.ADD_INHIBITOR, sigs, rpa.getProcessGuid());
					if(doUpdates)
						modelcontrol.foreignUpdate(rsa);
					else
						lib.addInhibitor(rpa.getProcessGuid(), sigs.get(0));
				}

				List<ModeSet> modes = plan.getLibrary().getProcess(rpa.fromProcess()).getModeSets();

				for(ModeSet ms : modes)
				{
					for(Object ps : ms.getProtocols())
					{
						ElicitationSet eset = ((ProtocolSet)ps).getElicitations();
						for(Object ep : eset.toArray())
						{
							ElicitedProbability eprob = (ElicitedProbability)ep;
							float prob = eprob.getProbability();
							String name = eprob.getGroupName();
							ArrayList<Guid> guids = new ArrayList<Guid>(eprob.getSignalSet());
							//RemSignalArg(int op, List<Guid> sigs, Guid prcss, float prob, int prot)
							rsa = new RemSignalArg(RemSignalArg.ADD_ELICITED, guids, rpa.getProcessGuid(), prob, ((ProtocolSet)ps).getProtocol(), name);
							modelcontrol.foreignUpdate(rsa);
						}
					}
				}

			}
			// we are pasting to a new plan or a new instance, all guids must be changed to preserve guid uniqeness
			else
			{
				if(logger.isTraceEnabled())
					logger.trace("      ** - COPY_SIG_DATA (process doesnt exists) **");
				ProcessC p = rpa.getTransferCopy();

				for(SignalC s : p.getCauses())
				{
					Signal sig = new Signal(s.getGuid(), s.getName());
					ArrayList<Guid> nsigs = new ArrayList<Guid>();
					nsigs.add(sig.getSignalID());
					if(doUpdates)
					{
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD, sig));
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_CAUSE, nsigs, rpa.getProcessGuid()));
					}
					else
					{
						lib.addSignal(sig);
						lib.addCause(rpa.getProcessGuid(), sig.getSignalID());
					}
				}
				for(SignalC s : p.getEffects())
				{
					Signal sig = new Signal(s.getGuid(), s.getName());
					ArrayList<Guid> nsigs = new ArrayList<Guid>();
					nsigs.add(sig.getSignalID());
					if(doUpdates)
					{
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD, sig));
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_EFFECT, nsigs, rpa.getProcessGuid()));
					}
					else
					{
						lib.addSignal(sig);
						lib.addEffect(rpa.getProcessGuid(), sig.getSignalID());
					}
				}
				for(SignalC s : p.getInhibits())
				{
					Signal sig = new Signal(s.getGuid(), s.getName());
					ArrayList<Guid> nsigs = new ArrayList<Guid>();
					nsigs.add(sig.getSignalID());
					if(doUpdates)
					{
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD, sig));
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_INHIBITOR, nsigs, rpa.getProcessGuid()));
					}
					else
					{
						lib.addSignal(sig);
						lib.addInhibitor(rpa.getProcessGuid(), sig.getSignalID());
					}
				}
				
				// elicitations
				for(ElicitationC e : p.getElicitations())
				{
					if(doUpdates)
						modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, e.getGuidSet(), rpa.getProcessGuid(), e.getProbability(), e.getProtocol(), e.getName()));
					else
						lib.addElicitedValue(rpa.getProcessGuid(), e.getProtocol(), e.getGuidSet(), e.getProbability(), e.getName());
				}

			}
		}catch(RemoteException re){
			logger.error("update(LibProcArg) - RemoteExc processing lib-arg:  "+re.getMessage());
		}

	}

	private void pasteMechanism(JWBController modelcontrol, List<JWBUID> shapeIDs, Mechanism oldMech, AbstractPlan plan)
	{
		Signal newSig = null;
		
		if(plan.getLibrary().getSignal(oldMech.getSignalGuid()) == null) //must be another instance
		{
			//TODO: LOW-PRIORITY  check this, manually adding to the library instead of sending an update prolly will
			// cause problems if the pasted-into plan is collaborating
			// checked:  works for some reason, not sure why
			plan.getLibrary().addSignal(newSig = new Signal(oldMech.getSignalGuid(), oldMech.getName()));//signal);
		}
		
		ArrayList<JWBShape> mechShapes = modelcontrol.getShapes(shapeIDs);
		//JWBLine firstMechShape = ((JWBLine)mechShapes.remove(0));
		JWBUID[] firstEndpoints = ((JWBLine)mechShapes.get(0)).getLinkedShapes();
		
		// a problem here is that newToShape might not have had its guid attachment updated yet during
		// pasteEvent because that happens via putShape and might still be pending, so we must do 
		// a little cheating here to make it happen, do to the cheating done in pasteEvent we know the plan
		// has been updated, so use the mapping there in this special case instead of the attachment
		Event newFrom = (Event)plan.getItem(plan.getGuidMapping(modelcontrol.getShape(firstEndpoints[0]).getUID()));
		
		// create a new mechanism ID to be used in place of the old
		Guid newMechID = new Guid();
		Guid newMechSigID = oldMech.getSignalGuid();
		
		// plan item/event connections are no longer made within mechanism constructor, they must be done manually
		// connect new mech to its from event (only need this once)
		newFrom.pasteEffect(newMechID);

		ArrayList<Event> toEvents = new ArrayList<Event>();
		// connect new mech to all of its to events (consolidators have more then one)
		for(JWBShape newMechShape : mechShapes)
		{
			try{
				JWBShape newToShape = modelcontrol.getShape(((JWBLine)newMechShape).getLinkedShapes()[1]);
				// see big comment above for reasoning on using plans shape map instead of shape uid here 
				Event newToEvent = (Event)plan.getItem(plan.getGuidMapping(newToShape.getUID())); //(Guid)newToShape.getAttachment());

				try{
					if(plan.getLibrary().getProcess(newToEvent.getProcessGuid()).getCausalSignals().contains(newMechSigID))
						newToEvent.pasteCause(newMechID);
					else
						newToEvent.pasteInhibitor(newMechID);
				}catch(NullPointerException exc){
					logger.error("pasteMechanism - error connecting mechanism, process doesnt exist yet or toEvent is null!");
				}          	
				catch(ClassCastException exc){
					logger.error("pasteMechanism - CastExc pasting mechanism:  "+exc.getMessage());
				}

				//update the mech with the toEvent
				toEvents.add(newToEvent);

				//update line with new mech id
				newMechShape.setAttachment(newMechID);


				modelcontrol.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, newToEvent, false)); //update each event, connected mechs changed
				modelcontrol.putShape(newMechShape); //put each line involved, attachment changed
			}catch(RemoteException rex){
				logger.error("pasteMechanism - RemoteException:  "+rex.getMessage());
			}
		}

		//create the new mechanism using the new events identified above
		Mechanism newMech = new Mechanism(newMechID, oldMech.getName(), toEvents, newFrom, newMechSigID);

		// put the remaining updates
		try{
			PlanArgument arg = new PlanArgument(PlanArgument.ITEM_ADD, newMech, shapeIDs);
			arg.setIsAutomated(true);
			modelcontrol.foreignUpdate(arg);
			modelcontrol.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, newFrom, false)); //update from event, connected mechs changed

//			if(newSig != null)
//			modelcontrol.foreignUpdate(new RemSignalArg(RemSignalArg.ADD, newSig));
		}catch(RemoteException exc){

		}
	}
	
	private void addEvent(JWBShape shape, JWBController control, AbstractPlan plan)
	{
//		if (control.getClientType() != JWBController.CLIENT)
//		{
			int defaultsSubType = -1;
			
			Guid processguid = new Guid();
			Guid eventguid = new Guid();
			
			Event event = new Event(eventguid, "Undefined", "", processguid);
			shape.setColor(defaultColor); //sets the color of an new shape added
			shape.setTextColor(defaultTxtColor);
			shape.setFont(plan.getDefaultFont());
			shape.setAttachment(event.getGuid());
			shape.setText("Undefined");
			
			
			// update the shape, the library and the plan
			try{
				control.putShape(shape);
				
				LibProcessArg parg = new LibProcessArg(LibProcessArg.ADD_PROCESS, processguid, "Process", plan.getDefaultProbSet(), defaultsSubType);
				control.foreignUpdate(parg);
				
				control.foreignUpdate(new PlanArgument(PlanArgument.ITEM_ADD, event, shape.getUID()));
				
			}catch(RemoteException ex){
				logger.error("addEvent - RemoteExc creating new event:  "+ex.getMessage());
			}
			
			if(plan.getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET)
				upSubProc.handleDefaultSubType(plan, processguid);
			
			// TODO:  this can't be done here anymore
//			if(autoEventEditor)
//				new EventDialog(MainFrm.getInstance(), "Event Editor", shape, control).show();
			
//		}
	}
	
	/**
	 * Calls up a Mechanism dialog for input about a new mechanism then creates it and processes it
	 * @param arg JWBController argument from the initiating shape update, used to determine if the update is local
	 */
	private void addMechanism(JWBShape shape, JWBController control, AbstractPlan plan, JWBControllerArgument arg)
	{
//		if(this.isLocalModel(arg)) //this client created/drew the mech/line
//		{
			JWBLine line = (JWBLine) control.getShape(shape.getUID());
			JWBUID[] linkedShapes = line.getLinkedShapes();
			Event fromEvent = (Event) plan.getItem(((Guid)(control.getShape(linkedShapes[0]).getAttachment())));
			Event toEvent = (Event) plan.getItem(((Guid)(control.getShape(linkedShapes[1]).getAttachment())));
			MechanismDialog mechDiag = new MechanismDialog();
			boolean created = false;
            boolean signalException = false;
			// prompt user to create a new Mechanism (and signal), if they do so...
			if(created = mechDiag.createMechanism(MainFrm.getInstance(), toEvent, fromEvent, plan))
			{
				// get the new mech (plan item) and check for signal mode conflicts before continuing
				Mechanism newMechanism = mechDiag.getMechanism();
                int mechType = mechDiag.getType();
                //Check for signal exception                
                if(mechType == SignalType.CAUSAL)
                    signalException = plan.getLibrary().getProcess(toEvent.getProcessGuid()).getInhibitingSignals().contains(newMechanism.getSignalGuid());
                else
                    signalException = plan.getLibrary().getProcess(toEvent.getProcessGuid()).getCausalSignals().contains(newMechanism.getSignalGuid());
               
                //Perform Foreign Updates to the library          
                if(!signalException)
                {
                	//now that we have our info and did the check, do the actual add
                	//configure the shape and handle the process library stuff
                	//handle the plan stuff
                	//update the shapes involved
			addMechanism(shape, newMechanism, mechDiag.getSignal(), mechType, control, plan);
                }
                else
                {
                	String confMsg = "The signal is already used as " + ((mechType == SignalType.CAUSAL) ? "an inhibitor" : "a cause");
                	String message = "Mechanism could not be created.\n" + confMsg;
                	new UpdateSubProcessor().showMessage(message);
                }
			}
			if(!created || signalException)
			{
				try{
					JWBControllerManager.getInstance().getControllerByShape(shape.getUID()).removeShape(shape.getUID());
				}catch(NullPointerException exc){
					logger.warn("addMechanism - NullPointer removing line:  "+exc.getMessage());
				}
				catch(java.rmi.RemoteException rm){
					logger.error("addMechanism - RemoteException removing line:  "+rm.getMessage());
				}
			}
//		}
	}
	
	/**
	 * Add a mechanism to the plan.  Callers of this method are responsible for performing a signal mode conflict
	 * check before hand.  Everything else necessary is done here including shape stuff and library stuff.  If the conflicts 
	 * are allowed to occur control will catch them when they come in through 'update' but the shapes will not be deleted.
	 * That should not be allowed to happen.
	 * @param shape The shape to use for the new mechanism
	 * @param newMech The new Mechanism
	 * @param newSig The new signal used for the mechanism
	 * @param mechType The type of mechanisms created (Mechanism.CAUSE or Mechanism.INHIBIT)
	 * @param control The whiteboard controller
	 * @param plan Dah
	 */
	public void addMechanism(JWBShape shape, Mechanism newMech, Signal newSig, int mechType, JWBController control, AbstractPlan plan)
	{
		//plan.addItem(newMech, shape.getUID());
		shape.setAttachment(newMech.getGuid());
		
		// create notices to add signal stuff to the appropriate event processes
		Event fromEvent = (Event)plan.getItem(newMech.getFromEvent());
		Event toEvent = (Event)plan.getItem(newMech.getToEvent(newMech.getConsolidatorSize()));
		ArrayList<Guid> guids = new ArrayList<Guid>(1);
		guids.add(newMech.getSignalGuid());
		RemSignalArg effectarg = new RemSignalArg(RemSignalArg.ADD_EFFECT, guids, fromEvent.getProcessGuid());
		RemSignalArg incoming = null;      
        
		if(mechType == SignalType.CAUSAL)
			incoming = new RemSignalArg(RemSignalArg.ADD_CAUSE, guids, toEvent.getProcessGuid());
		else
		{	
			incoming = new RemSignalArg(RemSignalArg.ADD_INHIBITOR, guids, toEvent.getProcessGuid());
			((JWBLine) shape).setColor(new Color(21, 6, 150));
			((JWBLine) shape).setLineStyle(JWBLine.DASHED);
		}
		((JWBLine) shape).setText(newMech.getName());
			
		// send out notices created above and one to simply add the signal to process library's signal list
		try
		{
			control.foreignUpdate(new RemSignalArg(RemSignalArg.ADD, newSig));
			control.foreignUpdate(effectarg);
			control.foreignUpdate(incoming);
		}catch(RemoteException rex){
			logger.error("addMechanism - RemoteExc adding new signal arguments:  "+rex.getMessage());
		}
		// if model is using AND/OR defaults set and this node is an AND, 
		// we should create the group automatically for the AND with all the signals in it
		// could have been pasted into a non and/or plan so check the defaults set too
		if(plan.getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET && 
			plan.getLibrary().getProcess(toEvent.getProcessGuid()).getDefaultsSubType() == Process.AND_OR_SUBTYPE_AND)
		{
			try{
				
				Vector<Guid> allSigGuids = new Vector<Guid>();
				Iterator mechGuids = (mechType == SignalType.CAUSAL) ? toEvent.getCauses().iterator() : toEvent.getInhibitors().iterator();
				//add all previous signals in to create an AND group
				while(mechGuids.hasNext())
				{
					PlanItem mech = plan.getItem((Guid)mechGuids.next());
					if(((Mechanism)mech).getSignalGuid() != newSig.getSignalID()) //dont add the new one yet
						allSigGuids.add(((Mechanism)mech).getSignalGuid());
				}
				
				//remove the old group - this whole thing is fluky unless first removed
				control.foreignUpdate(new RemSignalArg(RemSignalArg.REM_ELICITED, allSigGuids, toEvent.getProcessGuid()));
				
				//now add the new after the old group was removed
				allSigGuids.add(newSig.getSignalID());
				
				float defGrpProb = plan.getLibrary().getProcess(toEvent.getProcessGuid()).getDefault(SignalType.GROUP);
				
				if(allSigGuids.size() > 1)
				{
					//control.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, allSigGuids, toEvent.getProcessGuid()));
					control.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, allSigGuids, toEvent.getProcessGuid(), defGrpProb, SignalType.RNOR, "AND"));
				}
				else if(allSigGuids.size() == 1) // if there is only 1 sig, in and AND, the prob should be 1 (or whatever the group prob is)
				{
					control.foreignUpdate(new RemSignalArg(RemSignalArg.ADD_ELICITED, allSigGuids, toEvent.getProcessGuid(), defGrpProb, SignalType.RNOR));
				}
				
			}catch(RemoteException e){
				logger.error("addMechanism - RemoteException creating AND:  "+e.getMessage());
			}
		}
		
		//handle the plan stuff, update all 3 plan items
		if(!newMech.isConsolidator())
			fromEvent.addEffect(newMech);
        if(mechType == SignalType.CAUSAL)
            toEvent.addCause(newMech);
        else
            toEvent.addInhibitor(newMech);
        
        try{
			control.foreignUpdate(new PlanArgument(PlanArgument.ITEM_ADD, newMech, shape.getUID()));
			control.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, toEvent, false));
			control.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, fromEvent, false));
		}catch(RemoteException exc){
			logger.error("addMechanism - RemoteException putting new Mechanism or connected Event:  "+exc.getMessage());
		}
        
        //update the shapes involved
        finishAddMechanism(shape, control);

    }
    
	/**
	 * This method renames the mechanisms that use given SIGNAL (updates the mech name to the signal name). 
	 * This also sets the text on the JWBLines associated with each mechanism using the given JWBController.
	 * @param sig
	 * @param newName
	 * @param controller
	 */
	public void renameMechanism(Guid sigID, String newName, AbstractPlan plan)
	{
		JWBController controller = getController(plan.getId());
		
		Iterator<Mechanism> allMechs = plan.getAllMechanisms().iterator();
		while(allMechs.hasNext())
		{
			try
			{
				Mechanism thisMech = allMechs.next();
				if(thisMech.getSignalGuid().equals(sigID))
				{
					// change the text on the line
					List<JWBUID> shapes = plan.getShapeMapping(thisMech.getGuid());
					ArrayList<JWBShape> updateShapes = new ArrayList<JWBShape>();
					for(JWBUID id : shapes)
					{
						JWBLine line = (JWBLine) (controller.getShape(id));
						updateShapes.add(line);
						line.setText(newName);

					}
					// change the name of the mech
					thisMech.setName(newName);

					controller.putShapes(updateShapes);
					controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, thisMech, false));
				}
			}catch(RemoteException exc)
			{
				logger.error("renameMechanism - Error setting new mechanism name:  " + exc.getMessage());
			}
		}
	}
	
	public void finishAddMechanism(JWBShape shape, JWBController control)
    {
		
		// put the modified shapes
		try{
			control.putShape(shape);
			control.putShape(control.getShape(((JWBLine)shape).getLinkedShapes()[0]));
			control.putShape(control.getShape(((JWBLine)shape).getLinkedShapes()[1]));
		}catch(RemoteException exc){
			logger.error("addMechanism - RemoteException putting new Mechanism or connected Event shapes:  "+exc.getMessage());
		}
	
		
	}

	public Document getXmlPlanView(Guid planid)
	{
		JWBController controller = this.getController(planid);
		Collection shapes = controller.getShapes().values();
		Guid oldId = planid;
		
		Element root = DocumentHelper.createElement("Plan");
		// root.addAttribute("name", planfile.getName());
		root.addAttribute("guid", planid.getValue());
		root.addAttribute("version", fileVer);
		
		String defSet = "standard";
		if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET)
			defSet = "and_or";
		else if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.USER_DEFINED_DEFAULTS_SET)
			defSet = "user_defined";
		root.addAttribute("default_probability_set", defSet);
		
		Document plan = DocumentHelper.createDocument(root);
		
		root.add(createDocumentationElement(oldId));
		root.add(createGraphElement(shapes, getPlan(planid)));
		root.add(createLogicElement(getPlan(planid).getItems().values(), getPlan(planid).getLibrary()));
		root.add(createPolicyElement(getPlan(oldId)));
		root.add(createLibraryElement(getPlan(planid).getItems().values(), getPlan(planid)));
		root.add(createSchemeElement(getPlan(oldId).getColorScheme()));
		root.add(createSamplingElement(oldId));
		root.add(createDefaultsElement(getPlan(oldId)));
		root.add(createCOAElement(getPlan(oldId)));
		
		return plan;
		
	}
	
	public void applyCOA(COA theCOA, AbstractPlan thePlan, boolean doUpdates)
	{
		ArrayList<PlanItem> itemsToUpdate = new ArrayList<PlanItem>();
		ArrayList<RemSignalArg> sigArgs = new ArrayList<RemSignalArg>();
		JWBController controller = getController(thePlan.getId());
		
		Iterator guids = thePlan.getItems().keySet().iterator();
		while(guids.hasNext())
		{
			Guid thisGuid = (Guid) guids.next();
			//if the coa is tracking this item
			if(theCOA.containsItem(thisGuid))
			{
				COAState thisState = theCOA.get(thisGuid);
				PlanItem theItem = thePlan.getItem(thisGuid);
				if(theCOA.isTrackSchedule()) //apply the schedule
				{
					theItem.setDelay(thisState.getDelay());
					theItem.setPersistence(thisState.getPersistance());
					theItem.setContinuation(thisState.getContinuation());
					theItem.setSchedule((TreeMap<Integer, MaskedFloat>) thisState.getSchedule().clone());
				}
				if(theCOA.isTrackResources()) //apply the resources
				{
					theItem.setResources((HashMap<Guid, ResourceAllocation>) thisState.getResources().clone());
					theItem.setThreatResources((MultiMap<Integer, ResourceAllocation>) thisState.getThreatResources().clone());
				}

				// apply the elicited probabilities
				if(theCOA.isTrackElicited() && theItem.getItemType() == PlanItem.EVENT)
				{
					try
					{
						Process proc = thePlan.getLibrary().getProcess(((Event) theItem).getProcessGuid());
						ProtocolSet prot = proc.getModeSet(SignalType.CAUSAL).findProtocol(SignalType.RNOR);

						if(prot != null)
						{
							if(thisState.getCauseElicit() != null)
								for(ElicitedProbability ep : thisState.getCauseElicit())
									sigArgs.add(new RemSignalArg(RemSignalArg.ADD_ELICITED, ep.getSignalSet(), proc.getProcessID(), ep.getProbability(), prot
											.getProtocol()));
							//prot.setElicitations((ElicitationSet)thisState.cElicit.clone()); //we want this to collaborate too so ^
							else if(theCOA.isClearUntracked()) //?? do i want to check this
								for(ElicitedProbability ep : prot.getElicitations())
									sigArgs.add(new RemSignalArg(RemSignalArg.REM_ELICITED, ep.getSignalSet(), proc.getProcessID()));
							//prot.getElicitations().clear();
						}
					}catch(SignalException exc)
					{
						logger.error("applyCOA - SignalException setting causal elicitations (applying coa):  " + exc.getMessage());
					}
				}
				if(theCOA.isTrackElicited() && thisState.getInhibElicit() != null && theItem.getItemType() == PlanItem.EVENT)
				{
					try
					{
						Process proc = thePlan.getLibrary().getProcess(((Event) theItem).getProcessGuid());
						ProtocolSet prot = proc.getModeSet(SignalType.INHIBITING).findProtocol(SignalType.RNOR);

						if(prot != null)
						{
							for(ElicitedProbability ep : thisState.getInhibElicit())
							{
								if(thisState.getInhibElicit() != null)
									sigArgs.add(new RemSignalArg(RemSignalArg.ADD_ELICITED, ep.getSignalSet(), proc.getProcessID(), ep.getProbability(), prot
											.getProtocol()));
								//prot.setElicitations((ElicitationSet)thisState.cElicit.clone()); //we want this to collaborate too so ^
								else if(theCOA.isClearUntracked()) //?? do i want to check this
									sigArgs.add(new RemSignalArg(RemSignalArg.REM_ELICITED, ep.getSignalSet(), proc.getProcessID()));
								//prot.getElicitations().clear();
							}
						}
					}catch(SignalException exc)
					{
						logger.error("applyCOA - SignalException setting inhibiting elicitations (applying coa):  " + exc.getMessage());
					}
				}

				itemsToUpdate.add(theItem);
			}
			// the COA has no saved state for this item but we want to clear items (tracked info) like this
			else if(theCOA.isClearUntracked())
			{
				PlanItem theItem = thePlan.getItem(thisGuid);
				if(theCOA.isTrackSchedule()) //clear the schedule
				{
					theItem.setDelay(0);
					theItem.setPersistence(1);
					theItem.setContinuation(0f);
					theItem.getSchedule().clear();
				}
				if(theCOA.isTrackResources()) //clear the resources
				{
					theItem.getResources().clear();
					theItem.getThreatResources().clear();
				}

				//do i wana clear elicited values out here?? hmmm
				//TODO: LOW-PRIORITY  what is 'clear' as far as signal probs go, should they goto 0, default or leave alone

				itemsToUpdate.add(theItem);
			}
			
			thePlan.applyCOA(theCOA);
		}

		// update the items in errbodys plan, also update the proc library (for elicited probs)
		try{
			if(controller != null && doUpdates)
			{
				controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, itemsToUpdate, true));

				for(RemSignalArg rsa : sigArgs)
					controller.foreignUpdate(rsa);
			}
		}catch(RemoteException exc)
		{
			logger.error("applyCOA - RemExxc updating shape on COA apply:  " + exc.getMessage());
		}catch(ConcurrentModificationException exc)
		{
			//if this occurs, it should be fixed, MikeD knows what to do 
			logger.error("applyCOA - Conncurrent modification exception updating shapes while applying COA!!\n  Please tell MikeD");
		}
	}
	
	public boolean savePlan(Guid planid, File planfile, boolean saveAs, boolean autoSave) throws FileNotFoundException
	{
		JWBController controller = this.getController(planid);
		Collection shapes = controller.getShapes().values();
		Guid oldId = planid;
		
		Element root = DocumentHelper.createElement("Plan");
		root.addAttribute("name", planfile.getName());
		root.addAttribute("guid", (saveAs ? planid = new Guid() : planid).toString());
		root.addAttribute("version", fileVer);
		
		String defSet = "standard";
		if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET)
			defSet = "and_or";
		else if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.USER_DEFINED_DEFAULTS_SET)
			defSet = "user_defined";
		root.addAttribute("default_probability_set", defSet);
		
		Document plan = DocumentHelper.createDocument(root);
		
		root.add(createDocumentationElement(oldId));
		root.add(createGraphElement(shapes, getPlan(oldId)));
		root.add(createLogicElement(getPlan(oldId).getItems().values(), getPlan(oldId).getLibrary()));
		root.add(createPolicyElement(getPlan(oldId)));
		root.add(createLibraryElement(getPlan(oldId).getItems().values(), getPlan(oldId)));
		root.add(createSchemeElement(getPlan(oldId).getColorScheme()));
		root.add(createSamplingElement(oldId));
		root.add(createDefaultsElement(getPlan(oldId)));
		root.add(createCOAElement(getPlan(oldId)));
		
		if(saveAs == true)
		{
			AbstractPlan pl = getPlan(oldId);
			pl.setId(planid);
			plans.remove(oldId);
			plans.put(planid, pl);
			modelIDs.put(planid, modelIDs.remove(oldId));
		}
		
		// setTrimText was changed in order to allow the extra whitespace that is desired in things
		// such as plan documentation and coa summary documents to be maintained when plan is saved/loaded
		OutputFormat ouf = OutputFormat.createPrettyPrint();
		ouf.setTrimText(false); //<-- This line did it
		
		try{
			File outputFile;
			if(!autoSave)
				outputFile = new File(planfile+(planfile.getName().endsWith(".jcat")?"":".jcat"));
			else
				outputFile = planfile;
			
			if(!saveAs || !outputFile.exists() || JOptionPane.showConfirmDialog(MainFrm.getInstance(),
					"File exists, would you like to overwrite this file?","Overwrite",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile);
				XMLWriter writer;
				try{
					writer = new XMLWriter(fos, ouf);
					writer.write(plan);
					writer.flush();
					if(!autoSave)
						getPlan(planid).setFilePath(outputFile.getAbsolutePath());
				}catch(IOException e){
					logger.error("savePlan - IOExc writing plan file:  "+e.getMessage());
					return false;
				}
				
				return true;
			}
			else
				return false;
		}catch(FileNotFoundException exc){
			new UpdateSubProcessor().showMessage("Could not save to the specified file. Ensure that you have proper permissions.");
			logger.error("savePlan - FileNotFoundExc writing plan file:  "+exc.getMessage());
			return false;
		}
	}
	
	
	/**
	 * @param uid plan guid
	 * @return
	 */
	private Element createDocumentationElement(Guid uid)
	{
		return getPlan(uid).getDocumentation().toXML().getRootElement();
	}
	
	public Guid openPlan(File planFile, PlanListener listener) throws MalformedURLException, DocumentException, FileNotFoundException, DuplicateNameException, MissingRequiredFileException
	{
		Document doc = new SAXReader().read(planFile);
		Guid planID = openPlan(doc, listener);
		AbstractPlan thePlan = getPlan(planID);
		
		thePlan.setFilePath(planFile.getAbsolutePath());
		thePlan.setPlanName(planFile.getName());
		return planID;
	}
	
	public Guid openPlan(Document doc, PlanListener listener) throws DuplicateNameException, MissingRequiredFileException, DocumentException
	{
		Guid planid = null;
		Hashtable<JWBUID, JWBShape> shapeMap = new Hashtable<JWBUID, JWBShape>(); //used as a temp map to help link shapes / recreate lines
		
		try
		{
			Element el = (Element) doc.selectSingleNode("//Plan");
			String guid = el.attributeValue("guid");
			if(guid == null)
				planid = new Guid();
			else
				planid = new Guid(guid);

			this.addPlan(planid, listener);

			String defSet = el.attributeValue("default_probability_set");
			if(defSet != null)
			{
				if(defSet.equals("and_or"))
					getPlan(planid).setDefaultProbSet(AbstractPlan.AND_OR_DEFAULTS_SET);
				else if(defSet.equals("user_defined"))
					getPlan(planid).setDefaultProbSet(AbstractPlan.USER_DEFINED_DEFAULTS_SET);
			}

			JWBController model = Control.getInstance().getController(planid);
			AbstractPlan plan = this.getPlan(planid);

			// ReMap SIAM IDs to GUID's
			if(doc.getRootElement().getName() == "SIAM")
			{
				// remapShapes(doc);
				// remapPlanIDs(doc);
				// remapLibraryIDs(doc);
				// try{
				this.remapPlanUIDs(doc);
				// }catch(Exception
				// e){e.printStackTrace(System.err);}
			}

			// rebuild process library from the document
			plan.getLibrary().deserializeLibrary((Element) doc.selectSingleNode("//ProcessLibrary"));

			// first read in plan documentation and make sure to include the full xPath
			Element pdoc = (Element) doc.selectSingleNode("/Plan/documentation[1]");
			if(pdoc != null)
			{
				plan.setDocumentation(Documentation.getDocumentation(pdoc));
			}

			List eventShapes = doc.selectNodes("//Shape[@type='event']");
			Iterator eventShapesIt = eventShapes.iterator();
			List eventPlanItems = doc.selectNodes("//PlanItem[@type='event']");
			Iterator pevents = eventPlanItems.iterator();
			ArrayList<JWBShape> shapes = new ArrayList<JWBShape>();
			
			// extract all events (planitems)
			for(; pevents.hasNext();)
			{
				Event ev = parseEvent((Element) pevents.next(), plan);
				plan.addUnmappedItem(ev);
			}

			// extract all boxes (events)
			for(; eventShapesIt.hasNext();)
			{
				// first parse shape
				Element evElement = (Element) eventShapesIt.next();
				JWBShape sh = parseShape(evElement, shapeMap);
				String str = evElement.elementText("itemguid");
				Event ev = (Event) plan.getItem(new Guid(str));

				if(ev != null)
				{
					sh.setAttachment(ev.getGuid());
					// plan.addItem(ev,sh.getUID());
					plan.setShapeMapping(ev.getGuid(), sh.getUID());
	
					applyMarkups(ev, sh);
					shapes.add(sh);
				}
				else
					logger.error("openPlan - Event not found in plan for shape ("+sh.getText()+", "+str+")");

			}

			// now do the same for all the lines (mechanisms)
			Iterator mi = doc.selectNodes("//Shape[@type='mechanism']").iterator();
			Iterator mechs = doc.selectNodes("//PlanItem[@type='mechanism']").iterator();

			// extract all mechanisms (planitems)
			for(; mechs.hasNext();)
			{
				Mechanism m = parseMechanism((Element) mechs.next(), plan);
				if(m != null) // mech is null if it failed to parse, we want to continue loading plan
				{
					plan.addUnmappedItem(m);
					// Mechanism was modified... used to add itself to its connecting events inside Mechanism does not do this anymore and therefor
					// needs to be connected(in terms of plan) here. Creating a new mech is already taken care of how it should be in
					// addMechanism() This will handle connecting when loading from file Events have been parsed at this point
					// and library has been reconstructed
					try
					{
						Event fromEv = (Event) plan.getItem(m.getFromEvent());
						fromEv.addEffect(m);

						Iterator<Guid> i = m.getInfluencedEvents().iterator();
						while(i.hasNext())
						{
							Event toEv = (Event) plan.getItem(i.next());
							if(plan.getLibrary().getProcess(toEv.getProcessGuid()).getCausalSignals().contains(m.getSignalGuid()))
								toEv.addCause(m);
							else
								toEv.addInhibitor(m);
						}

					}catch(ClassCastException exc)
					{
						logger.error("openPlan - CastExc attaching mechanism to events:  " + exc.getMessage());
					}
				}
			}

			// extract all lines (shapes)
//			shapes.clear();
			for(; mi.hasNext();)
			{
				Element mechShape = (Element) mi.next();
				JWBShape sh = parseShape(mechShape, shapeMap);
				String str = mechShape.elementText("itemguid");
				Mechanism m = (Mechanism) plan.getItem(new Guid(str));
				Event toEvent = (Event) plan.getItem(plan.getGuidMapping(((JWBLine) sh).getLinkedShapes()[1]));

				if(m != null)
				{
					sh.setText(m.getName()); // ensure plans made before line text feature get the mech name for line text

					// plan.addItem(m,sh.getUID());
					sh.setAttachment(m.getGuid());
					plan.setShapeMapping(m.getGuid(), sh.getUID());

					applyMarkups(m, sh);
					shapes.add(sh);
				}
			}
			
			// do a couple consistancy chex on the plan loading
			String consistMsg = "Make sure the file is a JCAT model file and was created \n with version " + fileVer + " or older.";
			if(shapes.size() < plan.getItems().values().size()) // should be at least as many shapes as PlanItems (maybe more cause of consolidators)
				throw new DocumentException("The model failed the consistancy check (1). \n" + consistMsg);
			else if(plan.getAllEvents().size() != plan.getLibrary().getAllProcesses().size()) // should be 1 to 1 Events and Processes 
				throw new DocumentException("The model failed the consistancy check (2). \n" + consistMsg);
			
			//put all shapes (lines and boxes) now
			model.putShapes(shapes);

			// extract DefaultFont
			Element defFont = (Element) doc.selectSingleNode("//DefaultFont");
			if(defFont != null)
			{
				java.util.StringTokenizer fontTok = new java.util.StringTokenizer(defFont.getText(), ":");
				if(fontTok.countTokens() == 3)
				{
					try
					{
						plan.setDefaultFont(new Font(fontTok.nextElement().toString(), Integer.parseInt(fontTok.nextElement().toString()), Integer.parseInt(fontTok
								.nextElement().toString())));
					}catch(NumberFormatException exc)
					{
						logger.warn("openPlan - error parsing default font:  " + exc.getMessage());
					}
				}
			}

			// extract the ColorScheme
			Element sElement = (Element) doc.selectSingleNode("//ColorScheme");
			if(sElement != null)
			{
				ColorScheme readScheme = parseScheme(sElement);
				plan.setColorScheme(readScheme);
				readScheme.makeActive();
			}

			// extract the Sampling options
			Element soElement = (Element) doc.selectSingleNode("//SamplingOptions");
			if(soElement != null)
			{
				int readLength = parseSampleOptions(soElement);
				if(readLength > 0)
					plan.setLoadedPlanLength(readLength);
			}

			// extract any plan COAs
			Element planCOAElement = (Element) doc.selectSingleNode("//PlanCOAs");
			if(planCOAElement != null)
			{
				plan.setCOAList(parsePlanCOAs(planCOAElement));
			}

			// extract default prob set (donno where this disappeared to)
			Element defElement = (Element) doc.selectSingleNode("//DefaultProbabilities");
			if(defElement != null)
			{
				plan.setDefaultProbSet(parseDefaultsSet(defElement));
			}

			// extract Policies, if any
			Policy.parsePolicies((Element) doc.selectSingleNode("//Policies"), plan);

			getPlan(planid).fireWasOpened(); //TODO:  shapes wont necessarily be processed yet, shouldn't do this here
			
		}catch(RemoteException e)
		{
			logger.error("openPlan - Remote Error parsing plan: ", e);
		}

		return planid;
	}
	
	/**
	 * @param doc
	 */
	private void remapLibraryIDs(Document doc)
	{
		HashMap libID = new HashMap();
		Iterator it = doc.selectNodes("//Process").iterator();
		while(it.hasNext())
			libID.put(((Element)it.next()).attributeValue("guid"), new  Guid());
		it = doc.selectNodes("//Signal").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			if(libID.get(el.attributeValue("guid")) == null)
				libID.put(el.attributeValue("guid"), new Guid());
		}
		it = doc.selectNodes("//PlanItem").iterator();
		while(it.hasNext())
		{
			Element el  =  (Element)it.next();
			//We dont need to worry about Signals in Mechanisms
			//They are selected and remapped in doc.selectNodes("//Signal")
			if(el.attribute("type").getText().equals("event"))
				el.element("PGuid").setText(libID.get(el.element("PGuid").getText()).toString());
		}
		LinkedList pl = new LinkedList();
		pl.addAll(doc.selectNodes("//Process"));
		pl.addAll(doc.selectNodes("//Signal"));
		it = pl.iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			el.attribute("guid").setText(libID.get(el.attribute("guid").getText()).toString());
		}
	}
	
	private void remapPlanUIDs(Document doc)
	{
		int sCounter = 0;
		int mCounter = 0;
		
		HashMap shapesID = new HashMap();
		HashMap guids = new HashMap();
		// do shapes first, because all shapes will correspond to a unique plan item
		Iterator i = doc.selectNodes("//Shape[@type='event']").iterator();
		Element se;
		String oldguid;
		JWBUID jwbuid;
		Guid nguid;
		for(;i.hasNext();)
		{
			
			se = (Element)i.next();
			jwbuid = new JWBUID();
			shapesID.put(se.attributeValue("id"),jwbuid);
			oldguid = se.element("itemguid").getText();
			nguid = new Guid();
			guids.put(oldguid,nguid);
			se.attribute("id").setText(jwbuid.toString());
			se.element("itemguid").setText(nguid.getValue());
		}
		// now select lines and remap them to JWBUIDs and their start/endkey to events
		i = doc.selectNodes("//Shape[@type='mechanism']").iterator();
		String startkey;
		String endkey;
		
		try{
			for(;i.hasNext();)
			{
				
				se = (Element)i.next();
				jwbuid = new JWBUID();
				shapesID.put(se.attributeValue("id"),jwbuid);
				se.attribute("id").setText(jwbuid.toString());
				oldguid = se.element("itemguid").getText();
				
				nguid = new Guid();
				guids.put(oldguid,nguid);
				startkey = se.element("startkey").getText();
				endkey = se.element("endkey").getText();
				
				se.element("itemguid").setText(nguid.getValue());
				se.element("startkey").setText(shapesID.get(startkey).toString());
				se.element("endkey").setText(shapesID.get(endkey).toString());
				
				mCounter++;
			}
		}catch(Exception e){
			logger.error("remapPlanUIDs - Exception remaping line IDs", e);
		}
		// now take care of all the process and signal guids
		i = doc.selectNodes("//TheSignals/Signal").iterator();
		for(;i.hasNext();)
		{
			se = (Element)i.next();
			nguid = new Guid();
			guids.put(se.attributeValue("guid"),nguid);
			se.attribute("guid").setText(nguid.getValue());
			
			sCounter++;
		}
		// convert signals inside processes
		Iterator sit = doc.selectNodes("//Process//Signal").iterator();
		String psig;
		Element signal;
		for(;sit.hasNext();)
		{
			signal = (Element)sit.next();
			psig = signal.attributeValue("guid");
			signal.attribute("guid").setText(guids.get(psig).toString());
		}
		
		// now do the processes
		i = doc.selectNodes("//Process").iterator();
		try{
			for(;i.hasNext();)
			{
				se = (Element)i.next();
				nguid = new Guid();
				guids.put(se.attributeValue("guid"),nguid.getValue());
				se.attribute("guid").setText(nguid.getValue());
				
			}
		}catch(Exception e){
			logger.error("remapPlanUIDs - Exception remapping process IDs", e);
		}
		// get all plan items and substibute all guids
		
		Element im;
		Iterator itm = doc.selectNodes("//PlanItem//Mech").iterator();
		for(;itm.hasNext();)
		{
			im = (Element)itm.next();
			oldguid = im.attributeValue("guid");
			im.attribute("guid").setText(guids.get(oldguid).toString());
		}
		
		i = doc.selectNodes("//PlanItem").iterator();
		String oldsignal;
		String from;
		String to;
		for(;i.hasNext();)
		{
			se = (Element)i.next();
			oldguid = se.attributeValue("guid").toString();
			se.attribute("guid").setText(guids.get(oldguid).toString());
			if(se.attributeValue("type").equals("mechanism"))
			{
				// signal guid
				//if(debug)   System.out.println("CHANGING SIGNAL NAME AND TO FROM INFO....");
				oldsignal = se.element("Signal").attributeValue("guid");
				
				se.element("Signal").attribute("guid").setText(guids.get(oldsignal).toString());
				from = se.element("FromEvent").getText();
				to = se.element("ToEvent").getText();
				se.element("FromEvent").setText(guids.get(from).toString());
				se.element("ToEvent").setText(guids.get(to).toString());  
			}else{
				//try{
				// change the process guid
				oldguid = se.valueOf("PGuid");
				se.element("PGuid").setText(guids.get(oldguid).toString());
				//}catch(Exception e){e.printStackTrace(System.err);}
			}           
		}      
		//if(debug)   System.out.println("Finished remaping: \n"+doc.asXML());
		
		
	}
	
	/**
	 * @param doc A Full XML Model
	 * @throws Exception
	 */
	private void remapShapes(Document doc)
	{
		HashMap shapeID = new HashMap();
		//Extract Current ID's and map them
		Iterator it = doc.selectNodes("//Shape").iterator();
		while(it.hasNext())
		try
		{
				shapeID.put(((Element)it.next()).attributeValue("id"), new UID().toString() + ":" + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e)
		{
			logger.error("remapShapes - UnknownHostExc getting local host address");
		}
		
		//remap to new guids
		it = doc.selectNodes("//Shape").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			el.attribute("id").setValue(shapeID.get(el.attribute("id").getValue()).toString());
			String s = el.attribute("type").getValue().toString();
			if(s.equals("mechanism"))
			{
				el.element("startkey").setText(shapeID.get(el.element("startkey").getText()).toString());
				el.element("endkey").setText(shapeID.get(el.element("endkey").getText()).toString());
			}
		}
	}
	
	/**
	 *
	 * @param doc
	 * @throws Exception
	 */
	private void remapPlanIDs(Document doc)
	{
		HashMap guids = new HashMap();
		//Extract Current ID's and map them
		Iterator it = doc.selectNodes("//Shape").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			guids.put(el.element("itemguid").getText(), new Guid());
		}
		
		it = doc.selectNodes("//Shape").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			el.element("itemguid").setText(guids.get(el.element("itemguid").getText()).toString());
		}
		
		it = doc.selectNodes("//PlanItem").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			guids.put(el.attribute("guid").getText(), new Guid());
		}
		
		//remap to new guids
		it = doc.selectNodes("//PlanItem").iterator();
		while(it.hasNext())
		{
			Element el = (Element)it.next();
			el.attribute("guid").setText(guids.get(el.attribute("guid").getText()).toString());
			if(el.attribute("type").getText().equals("event"))
			{
				ArrayList mechs = new ArrayList();
				mechs.addAll(el.element("Causes").elements());
				mechs.addAll(el.element("Effects").elements());
				mechs.addAll(el.element("Inhibits").elements());
				for(int i = 0; i < mechs.size(); i++)
				{
					try{
						Element ele = (Element)mechs.get(i);
						String t = ele.attribute("guid").getText();
						String s = guids.get(t).toString();
						ele.attribute("guid").setText(s);
					}catch(Exception e){
						logger.error("remapPlanIDs - error remaping plan IDs:  "+e.getMessage());
					}
				}
			}
			else
			{
				try{
					//Process Mechanisms
					el.element("ToEvent").setText(guids.get(el.element("ToEvent").getText()).toString());
					el.element("FromEvent").setText(guids.get(el.element("FromEvent").getText()).toString());
				}catch(Exception e)
				{
					logger.error("remapPlanIDs(mech) - error remaping plan IDs:  "+e.getMessage());
				}
			}
		}
	}
	
	public Event parseEvent(Element e, AbstractPlan plan)
	{
		
		String processguid = e.valueOf("./PGuid");
		Event item = new Event(new Guid(e.attributeValue("guid")), e
				.attributeValue("name"), e.attributeValue("label"), new Guid(processguid));
		
		//Resources
		Element resEl = (Element)e.selectSingleNode("./Resources");
		if(resEl != null)
		{
			HashMap<Guid, ResourceAllocation> resMap = new HashMap<Guid, ResourceAllocation>();
			Iterator allocations = resEl.selectNodes("allocation").iterator(); 
			if(resEl.selectNodes("allocation").size() < 1)
				allocations = resEl.selectNodes("Resource").iterator(); 
			while(allocations.hasNext())
			{
				try{
					Element thisRes = (Element)allocations.next();
					//allow backwards compatability with resources
					String oldResValStr = thisRes.attributeValue("value");
					if(oldResValStr != null)
					{
						try{
							StringTokenizer parser = new StringTokenizer(oldResValStr.substring(1, oldResValStr.length()-1), ",");
							
							String name = parser.nextToken().trim();
							Integer alloc = new Integer(parser.nextToken().trim());
							Boolean conting = new Boolean(parser.nextToken().trim());
							Guid newGuid = new Guid();
							resMap.put(newGuid, new ResourceAllocation(newGuid, alloc, name, conting));
						}catch(NumberFormatException exc){
							logger.error("parseEvent - Error parsing old format resources:  "+exc.getMessage());
						}
					}
					//otherwise load the resource the new way
					else
					{
						Guid theGuid = new Guid(thisRes.attributeValue("guid"));
						resMap.put(theGuid, new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated")), thisRes.attributeValue("name"), new Boolean(thisRes.attributeValue("contingent"))));
					}
				}catch(NumberFormatException exc){
					logger.error("parseEvent - Invalid value parsing resource element:  "+exc.getMessage());
				}
			}
			item.setResources(resMap);
		}
		
		//Threat Resources
		Element tResEl = (Element)e.selectSingleNode("./ThreatResources");
		if(tResEl != null)
		{
			MultiMap<Integer, ResourceAllocation> tResMap = new MultiMap<Integer, ResourceAllocation>();
			Iterator timeEls = tResEl.selectNodes("resource").iterator();
			if(tResEl.selectNodes("resource").size() < 1) //backwards compatability w/t-resources
				timeEls = tResEl.selectNodes("ThreatResource").iterator(); 
			while(timeEls.hasNext())
			{
				Element timeEl = (Element)timeEls.next();
				String time = timeEl.attributeValue("time");
				if(time == null)
					time = timeEl.attributeValue("Time");
				//allow backwards compatability with loading resources
				if(timeEl.selectNodes("allocation").size() < 1)
				{
					Integer alloc = new Integer(timeEl.attributeValue("Allocation"));
					tResMap.put(new Integer(time), new ResourceAllocation(new Guid(), alloc));
				}
				else
				{
					Iterator allocations = timeEl.selectNodes("allocation").iterator();
					while(allocations.hasNext())
					{
						try{
							Element thisRes = (Element)allocations.next();
							Guid theGuid = new Guid(thisRes.attributeValue("guid"));
							tResMap.put(new Integer(time), new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated"))));
						}catch(NumberFormatException exc){
							logger.error("parseEvent - Invalid value parsing t-resource element:  "+exc.getMessage());
						}
					}
				}
			}
			item.setThreatResources(tResMap);
		}
		
		// NOTE that all effects causes and linked will be set when the mechanisms are added
		item.setLeak(Float.parseFloat(e.valueOf("./DefLeak")));
		
		//resource expenditure type
		int resUType = (e.valueOf("./ResourceExpenditureType").equals("OR")) ? mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_OR : mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_AND;
		item.setResourceUseType(resUType);

		// set the notes
		//item.setNotes(e.valueOf("./Notes"));
		item.setDelay(Integer.parseInt(e.valueOf("./Delay")));
		item.setPersistence(Integer.parseInt(e.valueOf("./Persistence")));
		item.setContinuation(Float.parseFloat(e.valueOf("./Continuation")));
		item.setSchemeAttrib(e.valueOf("./SchemeAttribute"));
		
		Element documentation = (Element) e.selectSingleNode("./documentation");
		if (documentation != null)
		{
			item.setDocumentation(Documentation.getDocumentation(documentation));
		}
		// scheduling
		List probs = e.selectNodes("./Schedule/Probability");
		Iterator pi = probs.iterator();
		for (; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.scheduleEvent(Integer.parseInt(p.attributeValue("time")), Float.parseFloat(p.getText()));
		}
		
		//parse evidence
		probs = e.selectNodes("./Evidence/Absolute/Probability");
		pi = probs.iterator();
		for (; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.addObservation(Integer.parseInt(p.attributeValue("time")), new Evidence(Double.parseDouble(p.getText())));
		}
		
		probs = e.selectNodes("./Evidence/Sensor/Report");
		pi = probs.iterator();
		for (; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.addObservation(Integer.parseInt(p.getText()), new Evidence(Boolean.parseBoolean(p.attributeValue("wasTrue")), 
					Double.parseDouble(p.attributeValue("FAR")), Double.parseDouble(p.attributeValue("MDR"))));
		}
		
		/*
		 * Iterator priorpred = e.selectNodes("./PriorPredicted/*").iterator();
		 * ArrayList prdl = new ArrayList(); for(;prior.hasNext();) { Element p =
		 * (Element)priorpred.next(); prdl.add(new
		 * Float(Float.parseFloat(p.valueOf("./Probability")))); }
		 * item.setPredictedProbs(prdl);
		 */
		return item;
	}
	
	public ColorScheme parseScheme(Element e)
	{
		String schemeName = e.attributeValue("name");
		Vector schemeData = new Vector();
		Iterator attribs = e.selectNodes("./Attrib").iterator();
		while(attribs.hasNext())
		{
			Element attrib = (Element)attribs.next();
			schemeData.add(new ColorSchemeAttrib(attrib.getText(), Integer.parseInt(attrib.attributeValue("color"))));
		}
		
		return (new ColorScheme(schemeName, schemeData));
	}
	
	private float[] parseDefaultsSet(Element e)
	{
		String defSetName = e.attributeValue("set");
		float[] defSet = AbstractPlan.STANDARD_DEFAULTS_SET;
		
		if(defSetName.equals("and_or"))
			defSet = AbstractPlan.AND_OR_DEFAULTS_SET;
		else if(defSetName.equals("user_defined"))
		{
			try{
				float[] set = new float[4];
				Element defC = (Element)e.selectNodes("./cause").get(0);
				set[0] = Float.parseFloat(defC.getText());
				Element defI = (Element)e.selectNodes("./inhibit").get(0);
				set[1] = Float.parseFloat(defI.getText());
				Element defE = (Element)e.selectNodes("./effect").get(0);
				set[2] = Float.parseFloat(defE.getText());
				Element defG = (Element)e.selectNodes("./group").get(0);
				set[3] = Float.parseFloat(defG.getText());
				defSet = set;
			}catch(NullPointerException exc){
				logger.warn("parseDefaultsSet - NullPointerExc recreating defaults, using standard set: "+exc.getMessage());
			}
			catch(NumberFormatException exc){
				logger.warn("parseDefaultsSet - NumFormatExc recreating defaults, using standard set: "+exc.getMessage());
			}
		}
		
		return defSet;
	}
	
	public int parseSampleOptions(Element e)
	{
		List options = e.selectNodes("./Option");
		
		int plength = -1;
		if(options.size() > 0)
		{
			Element option = (Element)options.get(0);
			plength = Integer.parseInt(option.attributeValue("PlanLength"));
		}
		return plength;
	}
	
	public Vector<COA> parsePlanCOAs(Element e)
	{
		Vector<COA> coaList = new Vector<COA>();
		
		Iterator coas = e.selectNodes("./COA").iterator();
		while(coas.hasNext())
			coaList.add(new COA((Element)coas.next()));
		
		return coaList;
	}
	
	public Mechanism parseMechanism(Element e, AbstractPlan plan)
	{
		Mechanism m = null;
		String sguid = e.element("Signal").attributeValue("guid");//.valueOf("./Signal[@guid]");
		String sname = e.element("Signal").attributeValue("name");//.valueOf("./Signal[@name]");
		String name = e.attributeValue("name");
		//String label = e.attributeValue("label");
		String mguid = e.attributeValue("guid");
		Collection<Event> toEvents = convertStringToEvents(e.valueOf("./ToEvent"), plan);
		String fromevent = e.valueOf("./FromEvent");
		boolean loop = false;
		if(e.valueOf("ClosesLoop").compareTo("true") == 0)
			loop = true;
        
		// even if it exists, we'll replace the shit
		Signal signal = new Signal(new Guid(sguid), sname);
		Event from = (Event) plan.getItem(new Guid(fromevent));
		
		try{
			m = new Mechanism(new Guid(mguid), name, toEvents, from, signal.getSignalID());
		
			m.setLoopCloser(loop);
			m.setDelay(Integer.parseInt(e.valueOf("./Delay")));
			m.setPersistence(Integer.parseInt(e.valueOf("./Persistence")));
			m.setContinuation(Float.parseFloat(e.valueOf("./Continuation")));
			
			//parse evidence
			List probs = e.selectNodes("./Evidence/Absolute/Probability");
			Iterator pi = probs.iterator();
			for (; pi.hasNext();)
			{
				Element p = (Element) pi.next();
				m.addObservation(Integer.parseInt(p.attributeValue("time")), new Evidence(Double.parseDouble(p.getText())));
			}
			
			//scheduling
			probs = e.selectNodes("./Schedule/Probability");
			pi = probs.iterator();
			for (; pi.hasNext();)
			{
				Element p = (Element) pi.next();
				m.scheduleEvent(Integer.parseInt(p.attributeValue("time")), Float.parseFloat(p.getText()));
			}
			
			//Resources (its a PlanItem thing but is done in both parseMechanism and parseEvent)
			Element resEl = (Element)e.selectSingleNode("./Resources");
			if(resEl != null)
			{
				HashMap<Guid, ResourceAllocation> resMap = new HashMap<Guid, ResourceAllocation>();
				Iterator allocations = resEl.selectNodes("allocation").iterator(); 
				if(resEl.selectNodes("allocation").size() < 1)
					allocations = resEl.selectNodes("Resource").iterator(); 
				while(allocations.hasNext())
				{
					try{
						Element thisRes = (Element)allocations.next();
						//allow backwards compatability with resources
						String oldResValStr = thisRes.attributeValue("value");
						if(oldResValStr != null)
						{
							try{
								StringTokenizer parser = new StringTokenizer(oldResValStr.substring(1, oldResValStr.length()-1), ",");
								
								String pName = parser.nextToken().trim();
								Integer alloc = new Integer(parser.nextToken().trim());
								Boolean conting = new Boolean(parser.nextToken().trim());
								Guid newGuid = new Guid();
								resMap.put(newGuid, new ResourceAllocation(newGuid, alloc, pName, conting));
							}catch(NumberFormatException exc){
								logger.error("parseMechanism - Error parsing old format resources:  "+exc.getMessage());
							}
						}
						//otherwise load the resource the new way
						else
						{
							Guid theGuid = new Guid(thisRes.attributeValue("guid"));
							resMap.put(theGuid, new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated")), thisRes.attributeValue("name"), new Boolean(thisRes.attributeValue("contingent"))));
						}
					}catch(NumberFormatException exc){
						logger.error("parseMechanism - Invalid value parsing resource element:  "+exc.getMessage());
					}
				}
				m.setResources(resMap);
			}
			
			//Threat Resources
			Element tResEl = (Element)e.selectSingleNode("./ThreatResources");
			if(tResEl != null)
			{
				MultiMap<Integer, ResourceAllocation> tResMap = new MultiMap<Integer, ResourceAllocation>();
				Iterator timeEls = tResEl.selectNodes("resource").iterator();
				if(tResEl.selectNodes("resource").size() < 1) //backwards compatability w/t-resources
					timeEls = tResEl.selectNodes("ThreatResource").iterator(); 
				while(timeEls.hasNext())
				{
					Element timeEl = (Element)timeEls.next();
					String time = timeEl.attributeValue("time");
					if(time == null)
						time = timeEl.attributeValue("Time");
					//allow backwards compatability with loading resources
					if(timeEl.selectNodes("allocation").size() < 1)
					{
						Integer alloc = new Integer(timeEl.attributeValue("Allocation"));
						tResMap.put(new Integer(time), new ResourceAllocation(new Guid(), alloc));
					}
					else
					{
						Iterator allocations = timeEl.selectNodes("allocation").iterator();
						while(allocations.hasNext())
						{
							try{
								Element thisRes = (Element)allocations.next();
								Guid theGuid = new Guid(thisRes.attributeValue("guid"));
								tResMap.put(new Integer(time), new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated"))));
							}catch(NumberFormatException exc){
								logger.error("parseMechanism - Invalid value parsing t-resource element:  "+exc.getMessage());
							}
						}
					}
				}
				m.setThreatResources(tResMap);
			}
			
		}catch(Exception ex)
		{
			logger.error("parseMechanism - Error occured parsing a mechanism from file", ex);
		}
		
		return m;
	}
	
	/**
     *  Utility function for converting lists of strings from DOM4J in to Guids for use in loading. 
     * @param list
     * @return
	 */
	private Collection<Event> convertStringToEvents(String stringGuids, AbstractPlan plan)
    {
        ArrayList<Event> eventList = new ArrayList<Event>();
        StringTokenizer tokenizer = new StringTokenizer(stringGuids);
        while(tokenizer.hasMoreTokens())
        {
            Guid id = new Guid(tokenizer.nextToken());
            eventList.add((Event)plan.getItem(id));
        }
        return eventList;
    }

	public Element createGraphElement(Collection shapes, AbstractPlan plan)
	{
		Element graph = DocumentHelper.createElement("Graph");
		// create all the shapes
		Iterator si = shapes.iterator();
		for (; si.hasNext();)
		{
			JWBShape shape = (JWBShape) si.next();
			Element shp = DocumentHelper.createElement("Shape");
			shp.addAttribute("id", shape.getUID().toString());

			if (shape.getType().equals("com.c3i.jwb.shapes.JWBRoundedRectangle"))
			{
				shp.addAttribute("type", "event");
			} else
			{
				shp.addAttribute("type", "mechanism");
			}
			shp.addElement("itemguid").setText(
					((Guid) shape.getAttachment()).getValue());
			if (shape.getType().equals("com.c3i.jwb.JWBLine"))
			{
				JWBUID[] lshapes = ((JWBLine) shape).getLinkedShapes();
				shp.addElement("startkey").setText(lshapes[0].toString());
				shp.addElement("endkey").setText(lshapes[1].toString());
				if(((JWBLine)shape).getLineStyle() == JWBLine.SOLID)
					shp.addElement("style").setText("solid");
				else
					shp.addElement("style").setText("dashed");

				if(((JWBLine)shape).getLineType() == JWBLine.ARCED)
					shp.addElement("type").setText("arced");
				else 
					shp.addElement("type").setText("straight");

				//Saving line control points
				int [] points = ((JWBLine)shape).getLinePoints();
				String str = new String();
				for(int i = 0; i < points.length; i++)
					str += points[i] + " ";                               
				shp.addElement("controlPoints").setText(str);
			} else
			{
				// adding color and alpha information first
				shp.addElement("rcolor").setText(shape.getColor().getRed()+"");
				shp.addElement("gcolor").setText(shape.getColor().getGreen()+"");
				shp.addElement("bcolor").setText(shape.getColor().getBlue()+"");
				shp.addElement("alpha").setText(shape.getColor().getAlpha()+"");
				shp.addElement("height").setText(shape.getHeight() + "");
				shp.addElement("width").setText(shape.getWidth() + "");
				shp.addElement("xpos").setText(shape.getLocation().getX() + "");
				shp.addElement("ypos").setText(shape.getLocation().getY() + "");
			}
			
			//stuff for both
			shp.addElement("text").setText(shape.getText());

			Font shapeFont = shape.getFont();
			shp.addElement("font").setText(shapeFont.getFamily()+":"+shapeFont.getStyle()+":"+shapeFont.getSize());
			shp.addElement("font-rcolor").setText(shape.getTextColor().getRed()+"");
			shp.addElement("font-gcolor").setText(shape.getTextColor().getGreen()+"");
			shp.addElement("font-bcolor").setText(shape.getTextColor().getBlue()+"");
			
			graph.add(shp);
		}

		//Element shp = DocumentHelper.createElement("DefaultFont");
		//shp.addAttribute("id", shape.getUID().toString());
		Font theFont = plan.getDefaultFont();
		graph.addElement("DefaultFont").setText(theFont.getFamily()+":"+theFont.getStyle()+":"+theFont.getSize());

		return graph;
	}

	public Element createPolicyElement(AbstractPlan plan){
		Element policies = DocumentHelper.createElement("Policies");
		for(Policy p: plan.activePolicies){
			p.XMLYourself(policies);
		}
		return policies;
    }
	
	public Element createLogicElement(Collection<PlanItem> planItems, mil.af.rl.jcat.processlibrary.Library lib)
	{
		Element logic = DocumentHelper.createElement("Logic");
		Iterator si = planItems.iterator();
		for (; si.hasNext();)
		{
			PlanItem item = (PlanItem)si.next();
			Element pi = DocumentHelper.createElement("PlanItem");
			pi.addAttribute("name", item.getName());
			pi.addAttribute("label", item.getLabel());
			pi.addAttribute("guid", item.getGuid().getValue());
			
			//create a resources element if item has resources
			HashMap<Guid, ResourceAllocation> resources = item.getResources();
			if(resources != null && resources.size() > 0)
			{
				Element resEl = DocumentHelper.createElement("Resources");
				
				for(Guid id : resources.keySet())
				{
					ResourceAllocation allocation = resources.get(id);
					resEl.addElement("allocation").addAttribute("guid", id.getValue()).addAttribute("name", allocation.getName()).addAttribute("allocated", allocation.getAllocated()+"").addAttribute("contingent", allocation.isContingent()+"");
				}
				pi.add(resEl);
			}
			
			//Handle threat resources
			MultiMap<Integer, ResourceAllocation> threat = item.getThreatResources();
			if(threat != null && threat.size() > 0)
			{
				Element tResEl = DocumentHelper.createElement("ThreatResources");
				for(Object key : threat.keySet())
				{
					Element timeEl = tResEl.addElement("resource").addAttribute("time", ((Integer)key).intValue()+"");
					
					Iterator valuesForKey = threat.get((Integer)key).iterator();
					while(valuesForKey.hasNext())
					{
						ResourceAllocation ra = (ResourceAllocation)valuesForKey.next();
						timeEl.addElement("allocation").addAttribute("guid", ra.getID().getValue()).addAttribute("allocated", ra.getAllocated()+"");
					}
				}
				pi.add(tResEl);
			}
			
			if (item.getItemType() == PlanItem.EVENT)
			{
				Event event = (Event) item;
				pi.addAttribute("type", "event");
				// create event specific elements, including process element
				
				//resource expenditure type
				String expType = (event.getResourceUseType() == mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_AND) ? "AND" : "OR";
				pi.addElement("ResourceExpenditureType").setText(expType);
				
				// documentations
				pi.add(event.getDocumentation().toXML().getRootElement());
				// scheduling
				Element schedule = DocumentHelper.createElement("Schedule");
				java.util.TreeMap times = event.getSchedule();
				Iterator ti = times.keySet().iterator();
				for (; ti.hasNext();)
				{
					Integer key = (Integer) ti.next();
					Float fp = new Float(((MaskedFloat) times.get(key)).floatValue());
					Element te = schedule.addElement("Probability")
					.addAttribute("time", key.toString());
					te.setText(fp.toString());
				}
				pi.add(schedule);
				
				//Evidence
				Element evidenceEl = DocumentHelper.createElement("Evidence");
				Element absoluteEl = DocumentHelper.createElement("Absolute");
				Element sensorEl = DocumentHelper.createElement("Sensor");
				java.util.HashMap evidence = event.getEvidence();
				Iterator evidenceIt = evidence.keySet().iterator();
				for(; evidenceIt.hasNext(); )
				{
					Integer key = (Integer)evidenceIt.next(); //key = time
					Evidence thisEvidence = ((Evidence)evidence.get(key));
					
					if(thisEvidence.getType() == Evidence.ABSOLUTE)
					{
						Element te = absoluteEl.addElement("Probability").addAttribute("time", key.toString());
						te.setText(thisEvidence.getProbability()+"");
					}
					else if(thisEvidence.getType() == Evidence.SENSOR)
					{
						Element te = sensorEl.addElement("Report").addAttribute("FAR", thisEvidence.getFAR()+"")
						.addAttribute("MDR", thisEvidence.getMDR()+"").addAttribute("wasTrue", thisEvidence.isReport()+"");
						te.setText(key.toString());
					}
				}
				evidenceEl.add(absoluteEl);
				evidenceEl.add(sensorEl);
				pi.add(evidenceEl);
				
				pi.addElement("DefLeak").setText(event.getLeak() + "");
				Element causes = DocumentHelper.createElement("Causes");
				Iterator ci = event.getCauses().iterator();
				for (; ci.hasNext();)
				{
					causes.addElement("Mech").addAttribute("guid", ((Guid) ci.next()).getValue());
				}
				pi.add(causes);
				
				Element effects = DocumentHelper.createElement("Effects");
				Iterator ei = event.getEffects().iterator();
				for (; ei.hasNext();)
				{
					effects.addElement("Mech").addAttribute("guid", ((Guid) ei.next()).getValue());
				}
				pi.add(effects);
				
				Element inhibits = DocumentHelper.createElement("Inhibits");
				Iterator ii = event.getInhibitors().iterator();
				for (; ii.hasNext();)
				{
					inhibits.addElement("Mech").addAttribute("guid", ((Guid) ii.next()).getValue());
				}
				pi.add(inhibits);
				
				//pi.addElement("Notes").setText(event.getNotes());
				pi.addElement("Delay").setText(event.getDelay() + "");
				pi.addElement("Persistence").setText(event.getPersistence() + "");
				pi.addElement("Continuation").setText(event.getContinuation() + "f");
				pi.addElement("PGuid").setText(event.getProcessGuid().getValue());
				pi.addElement("SchemeAttribute").setText(event.getSchemeAttrib());
				
			} else
			{
				pi.addAttribute("type", "mechanism");
				Mechanism m = (Mechanism) item;

				Element g = pi.addElement("Signal");
				g.addAttribute("guid", m.getSignalGuid().toString());
				g.addAttribute("name", m.getName());
				
				Element to = pi.addElement("ToEvent");
                String value = "";
                for(Iterator<Guid> i = m.getInfluencedEvents().iterator(); i.hasNext();)
                    value += (i.next().getValue()) + " ";
                to.setText(value);
                
				pi.addElement("FromEvent").setText(m.getFromEvent().getValue());
				pi.addElement("ClosesLoop").setText(Boolean.toString(m.isLoopCloser()));
				
				//Evidence
				Element evidenceEl = DocumentHelper.createElement("Evidence");
				Element absoluteEl = DocumentHelper.createElement("Absolute");
				Element sensorEl = DocumentHelper.createElement("Sensor");
				java.util.HashMap evidence = m.getEvidence();
				Iterator evidenceIt = evidence.keySet().iterator();
				for(; evidenceIt.hasNext(); )
				{
					Integer key = (Integer)evidenceIt.next(); //key = time
					Evidence thisEvidence = ((Evidence)evidence.get(key));
					
					if(thisEvidence.getType() == Evidence.ABSOLUTE)
					{
						Element te = absoluteEl.addElement("Probability").addAttribute("time", key.toString());
						te.setText(thisEvidence.getProbability()+"");
					}
					else if(thisEvidence.getType() == Evidence.SENSOR)
					{
						Element te = sensorEl.addElement("Report").addAttribute("FAR", thisEvidence.getFAR()+"")
						.addAttribute("MDR", thisEvidence.getMDR()+"").addAttribute("wasTrue", thisEvidence.isReport()+"");
						te.setText(key.toString());
					}
				}
				evidenceEl.add(absoluteEl);
				evidenceEl.add(sensorEl);
				pi.add(evidenceEl);
				
				// scheduling
				Element schedule = DocumentHelper.createElement("Schedule");
				java.util.TreeMap times = m.getSchedule();
				Iterator ti = times.keySet().iterator();
				for (; ti.hasNext();)
				{
					Integer key = (Integer) ti.next();
					Float fp = new Float(((MaskedFloat) times.get(key)).floatValue());
					Element te = schedule.addElement("Probability").addAttribute("time", key.toString());
					te.setText(fp.toString());
				}
				pi.add(schedule);
				
				pi.addElement("Delay").setText(m.getDelay() + "");
				pi.addElement("Persistence").setText(m.getPersistence() + "");
				pi.addElement("Continuation").setText(m.getContinuation() + "f");
				
			}
			
			logic.add(pi);
			
		}
		System.gc();
		return logic;
	}
	
	/**
	 * Stores Library elements that are local to the plan in the XML file so
	 * that the plans and their associated libraries are interchangable
	 *
	 * @param shapes
	 * @return Element - an XML element identical to the process library
	 *         structure
	 */
	private Element createLibraryElement(Collection<PlanItem> planItems, AbstractPlan plan)
	{
		Element libs = DocumentHelper.createElement("ProcessLibrary");
		Element pLib = DocumentHelper.createElement("TheProcesses");
		Element sLib = DocumentHelper.createElement("TheSignals");
		libs.add(pLib);
		libs.add(sLib);
//		HashMap sigs = new HashMap();
//		Iterator si = shapes.iterator();
		for (PlanItem item : planItems)
		{
//			PlanItem item = plan.getItem((Guid)((JWBShape) si.next()).getAttachment());
			if (item.getItemType() == PlanItem.EVENT)
			{
				Event event = (Event) item;
				
				Element p = plan.getLibrary().getProcessDocument(event.getProcessGuid()).getRootElement();
				p.setParent(null);
				pLib.add(p);
			}
		}
		
		for(Signal s : plan.getLibrary().getAllSignals())
	        {
	            try{
		            Element sig = sLib.addElement("Signal");
		            s.getSignalID().addToDocument(null, sig);
		            sig.addAttribute("name", s.getSignalName());
	            }catch(Exception e)
	            {
	                logger.error("createLibraryElement - error building Signal section:  "+e.getMessage());
	            }
	        }
		
		return libs;
	}
	
	private Element createSamplingElement(Guid planID)
	{
		Element sampleElement = DocumentHelper.createElement("SamplingOptions");
		
		try{
			if(getPlan(planID).getBayesNet() != null)
			{
				Element optionElement = DocumentHelper.createElement("Option");
				optionElement.addAttribute("PlanLength", getPlan(planID).getBayesNet().getTimespan()+"");
				sampleElement.add(optionElement);
			}
		}catch(NullPointerException exc){
			logger.warn("createSamplingElement - NullPointerExc");
		}
		
		return sampleElement;
	}
	
	private Element createSchemeElement(ColorScheme theScheme)
	{
		//loop through the attribs in the scheme
		Element schemeElement = DocumentHelper.createElement("ColorScheme");
		schemeElement.addAttribute("name", theScheme.getName());
		Iterator attribs = theScheme.getSchemeData().iterator();
		while(attribs.hasNext())
		{
			ColorSchemeAttrib attrib = (ColorSchemeAttrib)attribs.next();
			Element attribElement = DocumentHelper.createElement("Attrib");
			attribElement.setText(attrib.toString());
			attribElement.addAttribute("color", attrib.getRGB()+"");
			schemeElement.add(attribElement);
		}
		
		return schemeElement;
	}
	
	private Element createDefaultsElement(AbstractPlan thePlan)
	{
		float[] defsSet = thePlan.getDefaultProbSet();
		
		Element defsElement = DocumentHelper.createElement("DefaultProbabilities");

		String defSetName = "standard";
		if(defsSet == AbstractPlan.AND_OR_DEFAULTS_SET)
			defSetName = "and_or";
		else if(defsSet == AbstractPlan.USER_DEFINED_DEFAULTS_SET)
			defSetName = "user_defined"; 
		defsElement.addAttribute("set", defSetName);
		
		// also write out the defaults values if its a user defined scheme
		if(defsSet.length >= 4 && defSetName.equals("user_defined"))
		{
			Element defC = DocumentHelper.createElement("cause");
			defC.setText(defsSet[0]+"");
			defsElement.add(defC);
			Element defI = DocumentHelper.createElement("inhibit");
			defI.setText(defsSet[1]+"");
			defsElement.add(defI);
			Element defE = DocumentHelper.createElement("effect");
			defE.setText(defsSet[2]+"");
			defsElement.add(defE);
			Element defG = DocumentHelper.createElement("group");
			defG.setText(defsSet[3]+"");
			defsElement.add(defG);
		}
		
		return defsElement;
	}
	
	private Element createCOAElement(AbstractPlan plan)
	{
		Element planCOAEl = DocumentHelper.createElement("PlanCOAs");
		
		Iterator coas = plan.getCOAList().iterator();
		while(coas.hasNext())
		{
			planCOAEl.add(((COA)coas.next()).getDocument(plan.getLibrary()).getRootElement());
		}
		
		return planCOAEl;
	}
	
	public JWBShape parseShape(Element e, Hashtable<JWBUID, JWBShape> map)
	{
		
		JWBShape shape = null;
		String type = e.attributeValue("type");
		String key = e.attributeValue("id");
		// restore a JWBUID
		JWBUID id = new JWBUID(key);
		
		if (type.equalsIgnoreCase("event"))
		{
			// must use this for backward compatibility
			int h = (int)Double.parseDouble(e.valueOf("./height"));
			int w = (int)Double.parseDouble(e.valueOf("./width"));
			int xpos = (int)Double.parseDouble(e.valueOf("./xpos"));
			int ypos = (int)Double.parseDouble(e.valueOf("./ypos"));
			shape = new JWBRoundedRectangle(new java.awt.Point(xpos, ypos), w, h, id);
			map.put(id, shape); //place in map to assist re-creation/linking of lines
			// get the rgb plus alpha for the color
			try{
				int r = Integer.parseInt(e.valueOf("./rcolor"));
				int g = Integer.parseInt(e.valueOf("./gcolor"));
				int b = Integer.parseInt(e.valueOf("./bcolor"));
				int alpha = Integer.parseInt(e.valueOf("./alpha"));
				shape.setColor(new java.awt.Color(r,g,b,alpha));
			}catch(Exception ex)
			{
				// means color info is missing, could be an older plan, just set to blue
				logger.warn("parseShape - error parsing shape color, might be a very old plan");
				shape.setColor(defaultColor);
			}
			
		}
		else if (type.equalsIgnoreCase("mechanism"))
		{
			String sk = e.selectSingleNode("./startkey").getText();
			String ek = e.selectSingleNode("./endkey").getText();
//			JWBController cont = JWBControllerManager.getInstance().getControllerByShape((JWBUID)map.get(sk));
			// TODO:  should prolly do a check here in case the box shape is not in the map for some reason
			shape = new JWBLine(map.get(new JWBUID(sk)), map.get(new JWBUID(ek)), id, false);
			if (e.valueOf("./style").equals("dashed"))
			{
				((JWBLine) shape).setLineStyle(JWBLine.DASHED);
				((JWBLine) shape).setColor(new Color(21, 6, 150));
			}
			if (e.valueOf("./type").equals("arced"))
				((JWBLine)shape).setLineType(JWBLine.ARCED);
			String ctrlPts = e.valueOf("./controlPoints");
			if(ctrlPts != "")
			{
				StringTokenizer tok = new StringTokenizer(ctrlPts, " ");
				int [] pts = new int[tok.countTokens()];
				for(int i = 0; i< pts.length; i++)
					pts[i] = Integer.parseInt(tok.nextToken());
				((JWBLine) shape).setLinePoints(pts);
			}
		}
		
		//stuff for both
		String text = e.valueOf("./text");
		java.util.StringTokenizer st = new java.util.StringTokenizer(text, ":");
		
		if (st.countTokens() < 2)
			shape.setText(e.valueOf("./text"));
		else
			shape.setText(st.nextElement().toString() + "\n\n" + st.nextElement().toString());
				
		String font = e.valueOf("./font");
		java.util.StringTokenizer fontTok = new java.util.StringTokenizer(font, ":");
		
		if(fontTok.countTokens() == 3)
		{
			try{
				shape.setFont(new Font(fontTok.nextElement().toString(), 
						Integer.parseInt(fontTok.nextElement().toString()), 
						Integer.parseInt(fontTok.nextElement().toString())));
			}catch(NumberFormatException exc){
				logger.warn("parseShape - NumberFormatExc parsing shape font:  "+exc.getMessage());
			}
		}
		//font color
		try{
			int r = Integer.parseInt(e.valueOf("./font-rcolor"));
			int g = Integer.parseInt(e.valueOf("./font-gcolor"));
			int b = Integer.parseInt(e.valueOf("./font-bcolor"));
			shape.setTextColor(new java.awt.Color(r,g,b));
		}catch(Exception ex){
			logger.warn("parseShape - error parsing shape text color, might be an older plan");
		}
		
		return shape;
	}
	
	public void setDefaultColor(Color newColor)
	{
		if(newColor != null)
			defaultColor = newColor;
	}
	
	public Color getDefaultColor()
	{
		return defaultColor;
	}
	
	public Color getDefaultTextColor()
	{
		return defaultTxtColor;
	}
	
	public void setDefaultTextColor(Color newColor)
	{
		if(newColor != null)
			defaultTxtColor = newColor;
	}
	
	public void setAutoEventEdit(boolean show)
	{
		autoEventEditor = show;
	}

	public void setPromptEventName(boolean val)
	{
		promptEventName = val;
	}
	
	public void setModePersist(boolean persist)
	{
		persistMode = persist;
		//update this setting in existing panels
		for(Object p : modelIDs.values())
			((JWBPanel)p).setAutoResetMode(!persistMode);
	}
	
	public boolean isShowEdit()
	{
		return autoEventEditor;
	}
	
	public boolean isPersistMode()
	{
		return persistMode;
	}

	public boolean isPromptEventName()
	{
		return promptEventName;
	}
	
	public void setAlignToGrid(boolean align)
	{
		alignToGrid = align;
	}

	public void setShowGrid(boolean show)
	{
		showGrid = show;
	}

	public boolean isAlignToGrid()
	{
		return alignToGrid;
	}
	
	public boolean isShowGrid()
	{
		return showGrid;
	}
	
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals(FileUtils.COPY))
		{
			copyComplete = true;
		}
	}

	public Vector getOptions()
	{
		Vector opts = new Vector();
		opts.add(new Boolean(autoEventEditor));
		opts.add(new Boolean(persistMode));
		opts.add(new Boolean(promptEventName));
		opts.add(new Boolean(alignToGrid));
		opts.add(new Boolean(showGrid));
		
		return opts;
	}
	
	public void setOptions(Vector opts)
	{
		try{
			autoEventEditor = (Boolean)opts.get(0);
			persistMode = (Boolean)opts.get(1);
			promptEventName = (Boolean)opts.get(2);
			alignToGrid = (Boolean)opts.get(3);
			showGrid = (Boolean)opts.get(4);
		}catch(ArrayIndexOutOfBoundsException exc)
		{
			logger.warn("setOptions - Not all Control options were successfully loaded:  "+exc.getMessage());
		}
	}

//	 Use this class to thread whatever procedures cause delay or sleep time within the UPDATE method
//	 such as input dialog/message dialogs... if they are not threaded
	private class UpdateSubProcessor implements Runnable
	{
		private static final int DEF_SUB_TYPE = 1;
		private static final int EV_EDITOR = 2;
		private static final int SHOW_MESSAGE = 3;
		private static final int EV_NAME = 4;
		private int function = -1;
		private AbstractPlan plan = null;
		private Guid processID = null;
		private JWBShape shape = null;
		private JWBController controller = null;
		private String message = null;
		private String dialogTitle = "";
		private int dialogType = JOptionPane.INFORMATION_MESSAGE;
		private Event event;
		
		public void handleDefaultSubType(AbstractPlan plan, Guid processID)
		{
			this.plan = plan;
			this.processID = processID;
			function = DEF_SUB_TYPE;
			start();
		}
		
		public void nameEvent(Event event, JWBShape shape, JWBController controller)
		{
			this.event = event;
			this.shape = shape;
			this.controller = controller;
			function = EV_NAME;
			start();
		}

		public void showEventEditor(JWBShape shape, JWBController controller)
		{
			this.shape = shape;
			this.controller = controller;
			function = EV_EDITOR;
			start();
		}
		
		public void showMessage(String msg)
		{
			message = msg;
			function = SHOW_MESSAGE;
			start();
		}
		
		public void showMessage(String title, String msg, int type)
		{
			dialogTitle = title;
			message = msg;
			dialogType = type;
			start();
		}
		
		////////////////////////////////////////////////////////////////////
		
		private void start()
		{
			new Thread(this, "CONTROL_UPDATE_SUB-P").start();
		}
		
		
		private void threadedHandleDefaultSubType()
		{
			JWBController controller = control.getController(plan.getId());
			
			if(plan.getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET)
			{
				String[] options = new String[]{ "AND", "OR" };
				int opt = JOptionPane.showOptionDialog(MainFrm.getInstance(), "Select type:", "AND/OR Type", 
	        			JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
				
				int defaultsSubType = (opt == 0 ? Process.AND_OR_SUBTYPE_AND : Process.AND_OR_SUBTYPE_OR);
				
				try{
					LibProcessArg parg = new LibProcessArg(LibProcessArg.ADD_PROCESS, processID, "Process", plan.getDefaultProbSet(), defaultsSubType);
					controller.foreignUpdate(parg);
				}catch(RemoteException ex){
					Control.logger.error("addEvent(threaded) - RemoteExc setting def sub type on new event:  "+ex.getMessage());
				}
			}
		}
		
		private void threadedNameEvent()
		{
			String name = JOptionPane.showInputDialog(MainFrm.getInstance(), "Enter a name for this event", "Name Event", JOptionPane.QUESTION_MESSAGE);
			
			if(name != null && !name.equals(""))
			{
				event.setName(name);
				event.setLabel("");
				shape.setText(name);
				
				try{
					controller.putShape(shape);
					controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, event, false));
				}catch(java.rmi.RemoteException re)
				{
					logger.error("addEvent(threaded) - RemoteExc naming new event:  "+re.getMessage());
				}
			}
		}
		
		
		public void run()
		{
			if(function == DEF_SUB_TYPE)
				threadedHandleDefaultSubType();
			else if(function == EV_EDITOR)
				new EventDialog(MainFrm.getInstance(), "Event Editor", shape, controller).show();
			else if(function == SHOW_MESSAGE)
				JOptionPane.showMessageDialog(MainFrm.getInstance(), message, dialogTitle, dialogType);
			else if(function == EV_NAME)
				threadedNameEvent();
			
			function = -1;
		}

		
	}

	
	private class ControllerUpdateArgument
	{
		protected JWBControllerArgument arg;
		protected JWBRemoteObservable observable;
		
		public ControllerUpdateArgument(JWBRemoteObservable observable, JWBControllerArgument arg)
		{
			this.arg = arg;
			this.observable = observable;
		}
	}
	
	protected class UpdateProcessor extends Thread
	{
//		JWBObserver observer = null;
//		JWBController jwbObservable = null;
		Vector<ControllerUpdateArgument> queue = new Vector<ControllerUpdateArgument>();

		public UpdateProcessor()
		{
//			this.jwbObservable = jwbObservable;
//			this.observer = jwbObserver;
		}

		public void add(ControllerUpdateArgument argument)
		{
			queue.add(argument);
			this.interrupt();
		}

		public void run()
		{

			while(true)
			{
				while(queue.size() > 0)
				{
					ControllerUpdateArgument argument = queue.get(0);
					try{
						
						update(argument);
						
					}catch(RemoteException exc){
						logger.error("Error processing controller update:  "+exc);
					}
					
//						if(observer != null)
//						{
//							try
//							{
//								observer.update(jwbObservable, (JWBControllerArgument) queue.get(0));
//							}catch(RemoteException e1)
//							{
//								e1.printStackTrace();
//							}
//						}

					queue.remove(0);


				}
			
				try{
					sleep(1000 * 60 * 60);
				}catch(InterruptedException ie){
					// wake up
				}
			}
		}

	}
}
