/*
 * Created on Jan 4, 2006
 * Author: MikeD
 * Collaboration Control - designed to run along side a JWB RMI session to provide authentication and
 * enhance user experience with additional features
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.Color;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBControllerArgument;
import com.c3i.jwb.JWBObserver;
import com.c3i.jwb.JWBRemoteObservable;
import com.c3i.jwb.JWBSerializableShape;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;

public class CollaborationControl extends Thread implements JWBObserver
{
	//variables designating the login/authentication type used by the server
	public static final int NO_AUTH = 0;
	public static final int SESSION_PASS_AUTH = 1;
	public static final int USER_LIST_AUTH = 2;
	public static final int IP_LIST_AUTH = 3;
	
	private static CollaborationControl control = null;
	private Socket toServer = null;
	private ServerSocket serverListen = null;
	private JWBController jwbControl = null;
	private ObjectOutputStream toServOut;
	private ObjectInputStream toServIn;
	private Hashtable options = new Hashtable();  //list of configurable server options
	private Hashtable clientOptions = new Hashtable(); //list of configurable client options
	private Vector clients = new Vector();  //list of connected clients
	private Object readLock = new Object();
	private Boolean loginResult = null;
	private boolean clientStoppedByUser = false, ignoreNextUpdate = false;
	private boolean hasStarted = false; //always true after initial start of cc
	private boolean running = false; //true when CC is active as a client or server
	private JWBUID jwbuid;
	private String uName = "";
	private StringBuffer sessionLog;
	private InetAddress boundAddr;
	private int rmiPort;
	private static Logger logger = Logger.getLogger(CollaborationControl.class);
	
	
	public static CollaborationControl getInstance()
	{
		if(control == null)
			return control = new CollaborationControl();
		else
			return control;
	}
		
	private CollaborationControl()
	{
		super("Collaboration-Control");
		populateDefaultOptions();
		jwbuid = new JWBUID();
	}
	
	private void populateDefaultOptions()
	{
		options.put("auth", new Boolean(true));
		options.put("auth-type", new Integer(1));
		options.put("chat", new Boolean(true));
		options.put("log", new Boolean(false));
		options.put("port", "2000");
		//options.put("interface", null); //don't put a default, so get("interface") returns null
		//specific auth-type options
		options.put("auth-pass", new String("primate"));
		options.put("auth-ulist", new Vector());
		options.put("auth-iplist", new Vector());
		
		
		//////////////////////////////////////////
		
		clientOptions.put("announce", new Boolean(true));
	}

	public Hashtable getServerOptions()
	{
		return options;
	}
	
	public Hashtable getClientOptions()
	{
		return clientOptions;
	}
	
	public void setServerOptions(Hashtable opts)
	{
		options.clear();
		options.putAll(opts);
		//options = opts;
	}
	
	public boolean startServer(int port, JWBController jwbc)
	{
		if(!running)
		{
			options.put("port", new Integer(port));
			
			if(showServerOptions())
			{
				jwbControl = jwbc;
				
				jwbControl.allowClient(this.jwbuid);
				try{
					//used so that CC can know when whiteboard things are occuring (to do some additional messaging)
					jwbControl.addObserver(this);
				}catch(RemoteException exc){
					logger.warn("startServer() - Could not add ColabControl as a model observer: "+exc.getMessage());
				}
				try{
					if(serverListen == null || !serverListen.isBound())
					{
						int newPort = ((Integer)options.get("port")).intValue();
						rmiPort = newPort;
						boundAddr = (InetAddress)options.get("interface");
						
						uName = (String)options.get("name");
						if(uName == null || uName.trim().equals(""))
							uName = "server/host";
												
						//TODO: LOW-PRIORITY  why does this not need an inetaddress specified????
						serverListen = SSLServerSocketFactory.getDefault().createServerSocket(newPort + 1);//new ServerSocket(newPort+1);
						
						if(!hasStarted)
							start();
						else
							interrupt();
						
						MainFrm.getInstance().getCollabLog().clear();
						MainFrm.getInstance().getCollabLog().log("Server Started", Color.BLUE, true);
						if(((Boolean)options.get("auth")).booleanValue() == true)
							MainFrm.getInstance().getCollabLog().log("   Authentication Enabled", Color.BLUE, false);
						MainFrm.getInstance().getCollabLog().insertLine();
						
						if(((Boolean)options.get("log")).booleanValue())
						{
							sessionLog = new StringBuffer("** Collaboration session started - "+getTimeStamp(1)+"\n\n");
							logger.info("startServer - Collaboration session started");
						}
						
						return true;
					}
				}catch(IOException exc){
					logger.error("startServer - IOException starting server ", exc);
					JOptionPane.showMessageDialog(MainFrm.getInstance(), "Could not start a collaboration server.\n"+exc.getMessage());
				}
			}
			return false;
		}
		else
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "You can only join or host one model at a time. \n" +
					"Stop your current session before starting a new one.");
			return false;
		}
	}

	public InetAddress getBoundAddress()
	{
		return boundAddr;
	}
	
	private String getTimeStamp(int mode) //0 is time only, 1 is date and time
	{
		Calendar cal = Calendar.getInstance();
		String timeStamp = ((mode==1)?(cal.get(Calendar.MONTH)+1+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR)+" - "):"")+
				cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+" "+(cal.get(Calendar.AM_PM)==1?"PM":"AM");
		return timeStamp;
	}
	
	public boolean stopServer(JWBUID id)
	{
		try{
			if(id == jwbControl.getUID() && serverListen != null && !serverListen.isClosed())
			{
				running = false;
				serverListen.close();
				serverListen = null;
				closeAllClients();
				jwbControl.deleteObserver(this.getUID());
				jwbControl.clearAllowedClients();
				MainFrm.getInstance().getCollabLog().log("Server Stopped", Color.BLUE, true);
				MainFrm.getInstance().serverOff();
				
				if(((Boolean)options.get("log")).booleanValue())
				{
					sessionLog.append("\n** Collaboration Session ended - "+getTimeStamp(1));
					writeSessionLog();
					logger.info("stopServer - Collaboration session ended");
				}
				return true;
			}
			else
				return false;
		}catch(IOException exc){
			logger.warn("stopServer - IOExc stopping server:  "+exc.getMessage());
			return false;
		}
		
	}
	
	public void toSessionLog(String txt)
	{
		sessionLog.append("("+getTimeStamp(0)+")  "+txt+"\n");
	}

	private void writeSessionLog()
	{
		try{
			JFileChooser fileChoose = new JFileChooser();
			fileChoose.setDialogTitle("Save collaboration session log");
			
			if(fileChoose.showSaveDialog(MainFrm.getInstance()) == JFileChooser.APPROVE_OPTION && fileChoose.getSelectedFile() != null)
			{
				FileWriter fileOut = new FileWriter(fileChoose.getSelectedFile());
				fileOut.write(sessionLog.toString());
				fileOut.flush();
				fileOut.close();
			}
		}catch(IOException exc){
			logger.error("writeSessionLog - IOExc writing collab session log: "+exc.getMessage());
		}
	}
	
	private boolean showServerOptions()
	{
		ServerOptionsDialog optBox = new ServerOptionsDialog(MainFrm.getInstance(), options);
		
		if(optBox.startWasPressed())
			return true;
		
		return false;
	}

	public boolean startClient(String host, int port, JWBController jwbc) throws CollaborationException
	{
		if(!running)
		{
			jwbControl = jwbc;
			clientStoppedByUser = false;
			options.put("auth-type", new Integer(0)); //reset auth-type in case no connection is made
			
			try{
				//TODO: LOW-PRIORITY   why does this not need to have local interface specified to work? 
				toServer = SSLSocketFactory.getDefault().createSocket(host, port);
				toServIn = new ObjectInputStream(toServer.getInputStream());
				toServOut = new ObjectOutputStream(toServer.getOutputStream());
				boundAddr = toServer.getLocalAddress(); //use whichever local address this socket bound to
				
				//send jwb controller id for further identification on server
				toServOut.writeObject(jwbControl.getUID());
				toServOut.flush();
				
				//wait for server to specify an expected authentication type
				Integer authType = ((Integer)((CCDataPacket)toServIn.readObject()).getValue()).intValue();
				options.put("auth-type", authType);
				
				//start this thread for listening to further communication
				if(!hasStarted)
					start();
				else
					interrupt();
				mil.af.rl.jcat.gui.MainFrm.getInstance().clientOn(host, rmiPort+"");
				return true;
			}catch(java.net.ConnectException exc){	
				throw new CollaborationException("Connection attempt timed out. \nThe server did not respond, check your address and port.");
			}catch(UnknownHostException exc){
				throw new CollaborationException("Connection refused by host. \nThe server did not accept your connection, check your address.");
			}catch(IOException exc){
				throw new CollaborationException("Error connecting to host. \n"+exc.getMessage());
			}catch(ClassNotFoundException exc){
				logger.warn("startClient - ClassNotFoundExc:  "+exc.getMessage());
			}
			return false;
		}
		else
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "You can only join OR host one model at a time. \n" +
					"Stop your current session before starting a new one.");
			return false;
		}
	}
	
	public boolean stopClient(JWBUID id)
	{
		//if the id(jwb being stopped/closed) is this jwb being hosted then stop it
		if(jwbControl == null) //was never started
			return false;
		if(jwbControl.getUID() == id && toServer != null && !toServer.isClosed())
		{
			try{
				clientStoppedByUser = true; //flag for determining if error should be shown when socket excpetion occurs after closing
				toServIn.close();
				toServOut.close();
				toServer.close();
				toServer = null;
				toServIn = null;
				toServOut = null;
				running = false;
				return true;
			}catch(IOException exc){
				logger.warn("stopClient - Error stopping client: "+exc.getMessage());
				return false;
			}
		}
		else
			return false;
	}
	
	public boolean authenticate(String pass)
	{
		return authenticate(null, pass);
	}
	
	public boolean authenticate(String name, String pass)
	{
		try{
			if(name != null)
		   		toServOut.writeObject(name);
			toServOut.writeObject(pass);
			toServOut.flush();
			
//			synchronized(readLock) // this way caused bad thread lock sometimes
			try{
				int timeout = 50;
				while(loginResult == null && timeout > 0)
				{
					timeout--;
					Thread.sleep(200);
				}
			}catch(InterruptedException exc){
			}
			
			boolean result = (loginResult != null) ? loginResult.booleanValue() : false;
			loginResult = null;
			return result;
		}catch(IOException exc)
		{
			logger.error("authenticate - IOExc sending authentication info:  "+exc.getMessage());
			return false;
		}
	}
	
	public void closeAllClients()
	{
		synchronized(clients)
		{
			for(Object client : clients)
				((ClientConnection)client).close();
			clients.clear();
		}
	}
	
	
	public void run()
	{
		hasStarted = true;
		running = true;
		// workaround for java bug if working directory is not app path (launched by file association in windows)
		// which causes certificate or cipher suites to not be found
		int consecutiveAcceptFail = 0;
		
		while(true)
		{
			if(serverListen != null) // server was started, listen for clients to connect
			{
				try{
					Socket toClient = serverListen.accept(); //should produce an SSLSocket (from SSLServerSocket)
					ClientConnection newClient = null;
					boolean authEnabled = ((Boolean)options.get("auth")).booleanValue();
					if(authEnabled)
					{
						int authType = ((Integer)options.get("auth-type")).intValue();
						if(authType == CollaborationControl.SESSION_PASS_AUTH)
							newClient = new ClientConnection(toClient, (String)options.get("auth-pass"));
						else if(authType == CollaborationControl.USER_LIST_AUTH)
							newClient = new ClientConnection(toClient, (Vector)options.get("auth-ulist"), true);
						else if(authType == CollaborationControl.IP_LIST_AUTH)
							newClient = new ClientConnection(toClient, (Vector)options.get("auth-iplist"), false);
					}
					else
						newClient = new ClientConnection(toClient);
					
					ignoreNextUpdate = true; //ignore the mv/resize update that occurs when a user connects
					
					if(newClient != null)
					{
						synchronized(clients)
						{
							clients.add(newClient);
						}
					}
					
					consecutiveAcceptFail = 0; // reset if connection succeeds
				}catch(IOException exc){
					logger.error("run - IOExc handling incoming connection: "+exc.getMessage());
					consecutiveAcceptFail++;
					if(consecutiveAcceptFail >= 5 && running)
					{
						stopServer(jwbControl.getUID());
						JOptionPane.showMessageDialog(MainFrm.getInstance(), "The collaboration server failed to accept incoming connections. \n"+exc.getMessage());
					}
				}
			}
		
			else if(toServer != null) //start listening as a client for server commands 
			{
				try{
					synchronized(readLock)
					{
						CCDataPacket pkt = ((CCDataPacket)toServIn.readObject());
						
						if(pkt.getPacketType() == CCDataPacket.LOGIN_INFO)
							loginResult = ((Boolean)pkt.getValue());
						else if(pkt.getPacketType() == CCDataPacket.LOG_INFO)
						{
							String msg = (String)pkt.getValue();
							if(msg.startsWith("["))
								MainFrm.getInstance().getCollabLog().log(msg); // this sux, keep log in collabcontrol
							else
								MainFrm.getInstance().getCollabLog().log(msg, Color.BLUE, false);
						}
						else if(pkt.getPacketType() == CCDataPacket.ANNOUNCE)
						{
							showAnnouncment(pkt.getLoc(), (String)pkt.getValue(), false);
						}
						else if(pkt.getPacketType() == CCDataPacket.ANNOUNCE_HIGH_PRIORITY)
						{
							showAnnouncment(pkt.getLoc(), (String)pkt.getValue(), true);
						}
						
						
					}
				}catch(IOException exc)
				{
					if(!clientStoppedByUser)
					{
						JOptionPane.showMessageDialog(MainFrm.getInstance(), "Connection to collaboration host was lost unexpectedly. \n" +
								"The serving host may have shut down.");
						stopClient(jwbControl.getUID());
						jwbControl.endSession();
						MainFrm.getInstance().clientOff();
						logger.info("run(listining as client) - connection lost, possible server shutdown");
					}
					else
						logger.info("run - IOExc listening for server commands:  "+exc.getMessage());
				}catch(ClassNotFoundException exc){
					logger.error("run - ClassNotFoundException listening for server commands: "+exc.getMessage());
				}catch(NullPointerException exc){
					logger.warn("run - NullPointer listening for server commands (should be ok): "+exc.getMessage());
				}
				
			}
		
			else
			{				
				try{
					while(true)
						sleep(100000000);
				}catch(InterruptedException exc){
					// server wants to start again or clients gona start
					hasStarted = true;
					running = true;
				}
			}
		}
	}

	
	private void showAnnouncment(Point loc, String txt, boolean highPriority)
	{
		if(((Boolean)clientOptions.get("announce")).booleanValue())
		{
			Point panLoc = MainFrm.getInstance().getActiveView().getPanel().getLocationOnScreen();
			//take zoom level into account for placing of things on the whiteboard
			double zoom = MainFrm.getInstance().getActiveView().getPanel().getZoom();
			
			Point announceLoc = new Point((int)(panLoc.x+(loc.x*(zoom/10))), (int)(panLoc.y+(loc.y*(zoom/10))));
			Point docLoc = MainFrm.getInstance().getDockPane().getLocationOnScreen();
			java.awt.Dimension docSize = MainFrm.getInstance().getDockPane().getSize();
			//location on whiteboard could be out of view due to scrolling, if it is place the announcment on the dock tab 
			if(announceLoc.x < docLoc.x || announceLoc.x > docLoc.x+docSize.width || announceLoc.y < docLoc.y || announceLoc.y > docLoc.y+docSize.height)
				announceLoc = docLoc;
			
			AnnouncmentWindow wind = new AnnouncmentWindow(MainFrm.getInstance(), txt, announceLoc.x, announceLoc.y);
			
			wind.setAlwaysOnTop(highPriority);
		}
	}

	public JWBController getJWBControl()
	{
		return jwbControl;
	}

	public int getAuthenticationType()
	{
		return ((Integer)options.get("auth-type")).intValue();
	}

	public boolean isRunning()
	{
		return running;
	}
	
	public void send(CCDataPacket packet)
	{
		try{
			if(toServer != null) //this CC is a client so just send pkt to server
			{
				toServOut.writeObject(packet);
				toServOut.flush();
			}
			// this CC is a server so pop it on its own log and send it to clients
			// should only be here for server if its a log message so assume its a log msg
			else 
			{
				packet.setValue("["+uName+"]: "+packet.getValue());
				MainFrm.getInstance().getCollabLog().log((String)packet.getValue());
				sendToClients(packet);
				if(((Boolean)options.get("log")).booleanValue())
					toSessionLog((String)packet.getValue());
			}
		}catch(IOException exc){
			logger.error("send - IOExc sending CCDataPacket: "+exc.getMessage());
		}
	}
	
	//used in server to send data to all clients
	public void sendToClients(CCDataPacket packet)
	{
		sendToClients(packet, null);
	}

	//send to all clients except for one specified
	public void sendToClients(CCDataPacket packet, ClientConnection except)
	{
		synchronized(clients)
		{
			for(Iterator clientsIt = clients.iterator(); clientsIt.hasNext();)
			{
				ClientConnection client = (ClientConnection)clientsIt.next();
				
				try{
					if(except == null || !client.equals(except))
						((ClientConnection)client).send(packet);
				}catch(IOException exc){
					clientsIt.remove();
					logger.info("sendToClients - IOExc sending to client (removing client):  "+exc.getMessage());
				}
			}
		}
	}
	
	//observer method attatched to this servers jwbcontroller in order to know about actions that are happening
	//on the wb and which client performed them in order to send the announcments
	public void update(JWBRemoteObservable remoteObservable, JWBControllerArgument arg) throws RemoteException
	{
		try{
			ClientConnection client = null;
			String action = "";
			String itemLabel = "";
			Point loc = new Point(0,0);
			
			try{
				client = getClient(arg.getControllerUID());
			}catch(NullPointerException exc){
				logger.warn("update - Unknown client or the client is the server");
			}
			
			
			// determine a name for the action that occured
			if(arg.isNew())
			{
				action = "added a new item ";
				//ignore the next update - its the library stuff
				ignoreNextUpdate = true;
			}
			else
			{
				if(arg.getArgument() instanceof PlanArgument)
				{
					if(ignoreNextUpdate)
					{
						ignoreNextUpdate = false;
						return;
					}
					action = "changed logic properties of";
				}
				else
				{
					if(arg.getAction() == JWBControllerArgument.PUT) //put (shape changed)
					{
						if(ignoreNextUpdate)
						{
							ignoreNextUpdate = false;
							return;
						}
						action = "moved/resized";
					}
//					else if(arg.getAction() == JWBControllerArgument.PUT_EXTERNAL_UPDATE) //put extern (attachment changed)
//					{
//						if(ignoreNextUpdate)
//						{
//							ignoreNextUpdate = false;
//							return;
//						}
//						action = "changed logic properties of";
//					}
					else if(arg.getAction() == JWBControllerArgument.REMOVE) //remove
					{
						if(ignoreNextUpdate)
						{
							ignoreNextUpdate = false;
							return;
						}
						action = "removed";
					}
				}
			}
			
			// determine a name for the item the action was performed on
			JWBSerializableShape shp = null;
			if(arg.getArgument() instanceof ArrayList)
			{
				if(((ArrayList)arg.getArgument()).size() > 1)
					itemLabel = "multiple items";
				else if(((ArrayList)arg.getArgument()).size() == 1)
				{
					if(((ArrayList)arg.getArgument()).get(0) instanceof JWBSerializableShape)
						shp = (JWBSerializableShape)((ArrayList)arg.getArgument()).get(0);
					else //should be a JWBUID then
						itemLabel = "an item";
				}
			}
			else if(arg.getArgument() instanceof JWBSerializableShape)
				shp = (JWBSerializableShape)arg.getArgument();
				
			if(shp != null)
			{
				if(itemLabel.equals("") && shp.getAttachment() != null) //TODO: ???? need change here 
				{
					//try and determine the item info
					Guid pID = Control.getInstance().getPlanId(jwbControl.getUID());
					AbstractPlan plan = Control.getInstance().getPlan(pID);
					PlanItem item = plan.getItem((Guid)shp.getAttachment());
					if(item != null)
						itemLabel = " - "+item.getName();
					else
						itemLabel = " - this item";
				}
				
				//need to set the location relative to jwbpanel
				loc = (jwbControl.getShape(shp.getUID())).getLocation();
			}
			// no shape specified in the update and there still hasn't been a label assigned,
			// must be a foreign update (plan arg), so we need to find the shape to get location info
			else if(shp == null && itemLabel.equals(""))
			{
				if(arg.getArgument() instanceof PlanArgument)
				{
					PlanItem item = ((PlanArgument)arg.getArgument()).getParameters().getItem();
					if(item == null)
						return;
					itemLabel = item.getName();
					
					//try to determine shape info
					Guid pID = Control.getInstance().getPlanId(jwbControl.getUID());
					AbstractPlan plan = Control.getInstance().getPlan(pID);
					JWBUID uid = plan.getShapeMapping(item.getGuid()).get(0);
					JWBShape shape = jwbControl.getShape(uid);
					if(shape != null)
						loc = shape.getLocation();
				}
			}
			
			// determine the name of the user who performed the action
			String uName = "";
			if(client == null)
				uName = this.uName;
			else
				uName = client.getUserName();
		
			//send out and display proper message
			boolean isPlanArg = arg.getArgument() instanceof PlanArgument;
			String message = "User "+uName+" "+action+" "+itemLabel;
			Point panLoc = MainFrm.getInstance().getActiveView().getPanel().getLocationOnScreen();
			//take zoom level into account for placing of things on the whiteboard
			double zoom = MainFrm.getInstance().getActiveView().getPanel().getZoom();
						
			if(arg.getAction() == 0 || arg.getAction() == 2 || (arg.getAction() == 3 && isPlanArg))
			{
				//if this didn't originate from the server, make announcment window on the server also
				if(!uName.equals("server/host") && !uName.equals((String)options.get("name")))
				{
					Point announceLoc = new Point((int)(panLoc.x+(loc.x*(zoom/10))), (int)(panLoc.y+(loc.y*(zoom/10))));
					Point docLoc = MainFrm.getInstance().getDockPane().getLocationOnScreen();
					java.awt.Dimension docSize = MainFrm.getInstance().getDockPane().getSize();
					//location on whiteboard could be out of view due to scrolling, if it is place the announcment on the dock tab 
					if(announceLoc.x < docLoc.x || announceLoc.x > docLoc.x+docSize.width || announceLoc.y < docLoc.y || announceLoc.y > docLoc.y+docSize.height)
						announceLoc = docLoc;
					showAnnouncment(loc, message, isPlanArg);
				}
				
				//send the announcment to all clients except the originator
				sendToClients(new CCDataPacket((isPlanArg) ? CCDataPacket.ANNOUNCE_HIGH_PRIORITY : CCDataPacket.ANNOUNCE, message, loc), client);
				
				if(((Boolean)options.get("log")).booleanValue())
					toSessionLog(message);
			}
		// nullpointer otay, last announment comes in sometimes between panel closing and connection stopping
		// when user just closes the panel instead of explicitly stopping server
		// even for other exceptions we dont want to kill collaboration because this part isn't crucial
		}catch(NullPointerException exc){
			logger.warn("update - NullPointer occurred, this should be ok:  "+exc.getMessage());
		}
	}

	public ClientConnection getClient(JWBUID id)
	{
		synchronized(clients)
		{
			for(Object client : clients)
			{
				ClientConnection thisClient = (ClientConnection)client;
				if(thisClient.getUID().equals(id))
					return thisClient;
			}
			return null;
		}
	}
	
	//uid of this CC observer
	public JWBUID getUID() throws RemoteException
	{
		return jwbuid;
	}
	
	public int getRMIPort()
	{
		return rmiPort;
	}
}
