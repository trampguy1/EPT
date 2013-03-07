package mil.af.rl.jcat.control.collaboration;

import java.io.IOException;
import java.net.ServerSocket;

import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.exceptions.FatalCollaborationException;



public class CustomSSLServerSocketFactory extends SslRMIServerSocketFactory
{
	private int regPort;
	private int objPort;
	private boolean failSafePortUsed;
	private static Logger logger = Logger.getLogger(CustomSSLServerSocketFactory.class);

	
	/**
	 * Custom factory for ensuring the RMI session uses 2 specific ports instead of
	 * the normal registry port and a random secondary object data port 
	 * @param regPort port desired to be used for the registry (the factory will simply allow
	 * this port to be used when it is requested with createSocket(...) other port requests will
	 * be replaced by the object port
	 * @param objPort secondary port desired to be used for objects
	 */
	public CustomSSLServerSocketFactory(int regPort, int objPort)
	{
		super();
		this.regPort = regPort;
		this.objPort = objPort;
		
	}
	
	@Override
	public ServerSocket createServerSocket(int port) throws IOException
	{
//		System.out.println("server sock port specified:  "+port+", reg:  "+regPort+",  obj:  "+objPort);
		
		ServerSocket sock = null;
		try{
			
			sock = super.createServerSocket((port == regPort) ? regPort : objPort);
			failSafePortUsed = false;
			
/*			final String[] ciphers = this.getEnabledCipherSuites();
			final String[] protocols = this.getEnabledProtocols();
			final boolean needAuth = this.getNeedClientAuth();
			final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			// check if addr is null... nope, if addr is null, socket uses 0.0.0.0 which worx cause RMI
			// is so lame, this for some reason isn't used anyway, its uses java.rmi.hostname property...lame
			sock = new ServerSocket((port == regPort) ? regPort : objPort, 50, addr)
			{
				public Socket accept() throws IOException
				{
					Socket socket = super.accept();
					System.out.println("client socket connected:  "+socket.getInetAddress()+"  >  "+socket.getRemoteSocketAddress());
					SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, socket.getInetAddress().getHostName(), socket.getPort(), true);
					sslSocket.setUseClientMode(false);
					if (ciphers != null) {
						sslSocket.setEnabledCipherSuites(ciphers);
					}
					if (protocols != null) {
						sslSocket.setEnabledProtocols(protocols);
					}
					sslSocket.setNeedClientAuth(needAuth);
					return sslSocket;
				}
			};
*/
		}catch(java.net.BindException exc)
		{
			logger.warn("createServerSocket - BindException occured:  "+exc.getMessage());
			
			try{
				sock = super.createServerSocket(0);  //try a random port
				failSafePortUsed = true;
			}catch(java.net.BindException exc1)
			{
				throw new FatalCollaborationException(exc.getMessage());
			}
		}
		
//		System.out.println("CREATED SERVER SOCK PORT:  "+sock.getLocalPort() + " --- "+sock.getInetAddress().getHostAddress());
		
		return sock;
	}

	public boolean wasFailSafePortUsed()
	{
		return failSafePortUsed;
	}
	
	public void setPorts(int reg, int obj)
	{
		regPort = reg;
		objPort = obj;
	}


}
