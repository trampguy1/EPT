package mil.af.rl.jcat.control.collaboration;

import java.io.IOException;
import java.net.Socket;

import javax.rmi.ssl.SslRMIClientSocketFactory;



public class CustomSSLClientSocketFactory extends SslRMIClientSocketFactory
{

	private int regPort;
	private int objPort;

	/**
	 * Custom factory for ensuring the RMI session uses 2 specific ports instead of
	 * the normal registry port and a random secondary object data port 
	 * @param regPort port desired to be used for the registry (the factory will simply allow
	 * this port to be used when it is requested with createSocket(...) other port requests will
	 * be replaced by the object port
	 * @param objPort secondary port desired to be used for objects
	 */
	public CustomSSLClientSocketFactory(int regPort, int objPort)
	{
		super();
		this.regPort = regPort;
		this.objPort = objPort;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException
	{
		//System.out.println("port specified:  "+port+"  :  host: "+host);
		
		Socket sock = super.createSocket(host, (port == regPort) ? regPort : objPort);
		
		//System.out.println("CREATED SOCKET PORT: "+sock.getPort()+ " --- bound locally to: "+sock.getLocalAddress()+"  --- remotely: "+sock.getRemoteSocketAddress());
		
		return sock;
	}

	public void setPorts(int reg, int obj)
	{
		regPort = reg;
		objPort = obj;
	}

}
