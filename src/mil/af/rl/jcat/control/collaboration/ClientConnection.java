/*
 * Created on Jan 4, 2006
 * Author:  MikeD
 * ClientConnection - threaded socket handler used by CollaborationControl for each client connected
 */
package mil.af.rl.jcat.control.collaboration;


import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.MainFrm;

import com.c3i.jwb.JWBUID;

public class ClientConnection extends Thread
{
	private Socket sock = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private String passPhrase = null;
	private Vector userList = null;
	private boolean isUserList = true;
	private String userID = "0.0.0.0";
	private JWBUID id;
	private static Logger logger = Logger.getLogger(ClientConnection.class);
		
	public ClientConnection(Socket inSock)
	{
		super("Collab-Client-Connection");
		sock = inSock;
		start();
	}
	
	public ClientConnection(Socket inSock, String passw)
	{
		super("Collab-Client-Connection");
		passPhrase = passw;
		sock = inSock;
		start();
	}
			
	public ClientConnection(Socket inSock, Vector usrLst, boolean isUsrList)
	{
		super("Collab-Client-Connection");
		userList = usrLst;
		isUserList = isUsrList;
		sock = inSock;
		start();
	}
	
	public void run()
	{
		userID = sock.getInetAddress().getHostAddress();
		
		try{
			out = new ObjectOutputStream(sock.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sock.getInputStream());
			
			id = (JWBUID)in.readObject();
			
			if(passPhrase != null)
			{
				out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_TYPE, new Integer(CollaborationControl.SESSION_PASS_AUTH)));
				out.flush();
				String pass = (String)in.readObject();
				if(pass.equals(passPhrase))
				{
					CollaborationControl.getInstance().getJWBControl().allowClient(id);
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(true)));
					MainFrm.getInstance().getCollabLog().log("User connected ("+userID+")", Color.BLUE, false);
					CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User connected ("+userID+")"));
				}
				else
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(false)));
			
				out.flush();
			}
			else if(userList != null && isUserList)
			{
				out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_TYPE, new Integer(CollaborationControl.USER_LIST_AUTH)));
				out.flush();
				String user = (String)in.readObject();
				String pass = (String)in.readObject();
				
				if(!userList.contains(user+" : "+pass))
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(false)));
				else
				{
					CollaborationControl.getInstance().getJWBControl().allowClient(id);
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(true)));
					userID = user;
					MainFrm.getInstance().getCollabLog().log("User connected ("+userID+")", Color.BLUE, false);
					CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User connected ("+userID+")"));
				}
				out.flush();				
			}
			else if(userList != null && !isUserList)
			{
				out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_TYPE, new Integer(CollaborationControl.IP_LIST_AUTH)));
				out.flush();
				String something = (String)in.readObject(); //not used for anything since we have the IP				
				
				if(userList.contains(userID))
				{
					CollaborationControl.getInstance().getJWBControl().allowClient(id);
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(true)));
					MainFrm.getInstance().getCollabLog().log("User connected ("+userID+")", Color.BLUE, false);
					CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User connected ("+userID+")"));
				}
				else
					out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(false)));
				out.flush();
			}
			else //authentication is not enabled so just accept the client
			{
				CollaborationControl.getInstance().getJWBControl().allowClient(id);
				out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_TYPE, new Integer(CollaborationControl.NO_AUTH)));
				out.writeObject(new CCDataPacket(CCDataPacket.LOGIN_INFO, new Boolean(true)));
				out.flush();
				MainFrm.getInstance().getCollabLog().log("User connected ("+userID+")", Color.BLUE, false);
				CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User connected ("+userID+")"));
			}
			
		}catch(IOException exc){
			logger.warn("run - IOException:  "+exc.getMessage());
		}
		catch(ClassNotFoundException exc){
			logger.warn("run - ClassNotFoundExc:  "+exc.getMessage());
		}
		
		//continue listening for further communication from client
		try{
			while(true)
			{
				try{
					CCDataPacket pkt = (CCDataPacket)in.readObject();
					
					if(pkt.getPacketType() == CCDataPacket.LOG_INFO)
					{
						pkt.setValue("["+userID+"]:  "+pkt.getValue());
						MainFrm.getInstance().getCollabLog().log((String)pkt.getValue());
						CollaborationControl.getInstance().sendToClients(pkt);
						if(((Boolean)CollaborationControl.getInstance().getServerOptions().get("log")).booleanValue())
							CollaborationControl.getInstance().toSessionLog((String)pkt.getValue());
					}
					//if type is other just pass it along to clients
					else if(pkt.getPacketType() == CCDataPacket.OTHER)
						CollaborationControl.getInstance().sendToClients(pkt);
					
				}catch(EOFException exc)
				{
					MainFrm.getInstance().getCollabLog().log("User disconnected ("+userID+")", Color.RED, false);
					CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User disconnected ("+userID+")"));
					break;
				}catch(ClassNotFoundException exc){
					logger.warn("run(listening) - ClassNotFoundExc:  "+exc.getMessage());
				}
			}
		}catch(IOException exc)
		{
			MainFrm.getInstance().getCollabLog().log("User disconnected ("+userID+")", Color.RED, false);
			CollaborationControl.getInstance().sendToClients(new CCDataPacket(CCDataPacket.LOG_INFO, "User disconnected ("+userID+")"));
		}
	}
	
	public String getUserName()
	{
		return userID;
	}

	public JWBUID getUID()
	{
		return id;
	}
	
	public void close()
	{
		try{
			out.close();
			in.close();
			sock.close();
			out = null;
			in = null;
			sock = null;
		}catch(IOException exc){
			logger.warn("close - Error closing connection: "+exc.getMessage());
		}
	}

	public void send(CCDataPacket packet) throws IOException
	{
		out.writeObject(packet);
		out.flush();
	}
}
