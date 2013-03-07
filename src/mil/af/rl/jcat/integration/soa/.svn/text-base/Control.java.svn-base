/*
 * A Control class for use with the JCAT Web Services package
 */

package mil.af.rl.jcat.integration.soa;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.af.rl.jcat.exceptions.UnknownGUIDException;
import mil.af.rl.jcat.integration.JCATControlInterface;
import mil.af.rl.jcat.util.Guid;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

//TODO:  what about delay,persist,contin

public class Control implements Runnable, JCATControlInterface
{
	
	public static boolean debugOutput = false;
	public static final String fileVer = "1.1.0WS";
	private static Control control = null;
	//private URLConnection con = null;
	private HashMap<String, String> addresses = null;
	private HttpClient http = null;
	private GetMethod connection = null;
	private boolean keepAlive = false;
	private Thread keepAliveThread;
	private long keepAliveDelay = 1000 * 10;


	private Control()
	{
		Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		Logger.getLogger("httpclient").setLevel(Level.OFF);
		
		http = new HttpClient();
		readAddresses();
	}

	public static Control getInstance()
	{
		if(control == null)
			control = new Control();
		return control;
	}

	private GetMethod getConnection()
	{
		if(connection == null)
		{
			try{
//				URL url = new URL(addresses.get("jcws"));
//				connection = url.openConnection();
				String addr = addresses.get("jcws");
				if(addr == null)
				{
					System.err.println("No server for jcws was specified in config file!");
					return null;
				}
				connection = new GetMethod(addr);
				
				// try to retrieve the session timeout (in seconds) from the server
				String timeoutQuery = URLEncoder.encode("sestimeout", "UTF-8");
				String response = executeQuery(timeoutQuery);
				try{
					if(response != null)
					{
						int timeout = Integer.parseInt(response.trim());
						if(timeout > 0)
						{
							keepAliveDelay = (timeout / 3) * 1000;
							if(debugOutput)
								System.out.println("Setting keep-alive delay to:  "+keepAliveDelay);
						}
					}
				}catch(NumberFormatException exc){
					if(debugOutput)
						System.err.println("Unexpected session timeout response: "+exc.getMessage());
				}
				
				startKeepAlive();
				
//				connection.setDoOutput(true);
			}catch(ConnectException exc)
			{
				System.err.println("Error connecting to JCWS server ["+addresses.get("jcws")+"]:"+exc.getMessage());
				connection = null;
			}
			catch(Exception e)
			{
				e.printStackTrace(System.err);
			}
		}
		return connection;
	}
	
	/**
	 * Reset the current http connection/session forcing a new session upon next communication attempt
	 */
	@SuppressWarnings("deprecation")
	public void resetConnection()
	{
		if(connection != null)
		{
			stopKeepAlive();
			connection.releaseConnection(); // this doesn't really kill the connection, can't seem to do it
			HttpConnection c = http.getHttpConnectionManager().getConnection(connection.getHostConfiguration());
			if(c != null)
			{
				c.releaseConnection();
				c.close();
			}
			connection = null;
		}
	}

	private void readAddresses()
	{
		addresses = new HashMap<String, String>();
		SAXReader sar = new SAXReader();
		Document doc = null;
		try{
			InputStream config = this.getClass().getClassLoader().getResourceAsStream("config.xml");
			if(config == null)
				throw new MalformedURLException("File not found");
			doc = sar.read(config);
		
			List cons = doc.selectNodes("//connection");
			for(Object c : cons)
			{
				Element e = (Element) c;
				addresses.put(e.attributeValue("name"), e.attributeValue("url"));
				
			}
		}catch(MalformedURLException exc){
			System.err.println("Config file (config.xml) could not be loaded.  Ensure the file exists in your classpath.");
		}
		catch(DocumentException ex){
			ex.printStackTrace(System.err);
		}
		
		if(debugOutput)
			if(addresses.size() < 1)
				System.err.println("Warning:  no servers were parsed from config file");
	}

	protected boolean executeCommand(String command) throws Exception
	{
		GetMethod con = getConnection(); // maintain the same connection
		if(con == null)
			throw new ConnectException("Not connected to the server");
		
		boolean success = false;

//		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//		wr.write(command);
//		wr.flush();
		con.setQueryString(command);
		int status = http.executeMethod(con);
		
		// now read the responce
//		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String line = null;
//		while((line = rd.readLine()) != null)
//		{
//			if(line.contains("success"))
//				success = true;
//		}
		if (status != HttpStatus.SC_OK)
			if(debugOutput)
				System.err.println("Command failed: " + con.getStatusLine());
		
		byte[] responseBody = con.getResponseBody();
		if(responseBody != null && new String(responseBody).contains("success"))
			success = true;
		
//		wr.close();
//		rd.close();
		
		return success;
	}

	protected String executeQuery(String query) throws Exception
	{
		GetMethod con = getConnection(); // maintain the same connection
		if(con == null)
			throw new ConnectException("Not connected to the server");
		
//		StringBuffer response = new StringBuffer();

//		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//		wr.write(query);
//		wr.flush();
		con.setQueryString(query);
		int status = http.executeMethod(con);

		// now read the responce
		if(debugOutput)
			System.out.println("Waiting for query response....");
//		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String line = null;
//		while((line = rd.readLine()) != null)
//		{
//			System.out.println("Control talking: " + line);
//			response.append(line);
//		}
		if (status != HttpStatus.SC_OK) 
			if(debugOutput)
				System.err.println("Query failed: " + con.getStatusLine());
		
		byte[] responseBody = con.getResponseBody();

//		wr.close();
//		rd.close();
		
//		return response.toString();
		if(responseBody == null)
			return null;
		else
			return new String(responseBody);
			
	}
	
	private void keepAlive()
	{
		GetMethod con = getConnection(); // maintain the same connection
		if(con == null)
			return;
		
		try{
			if(debugOutput)
				System.out.println("Sending Keep-alive ");
	
			con.setQueryString("");
			int status = http.executeMethod(con);
	
			if(status != HttpStatus.SC_OK) 
				if(debugOutput)
					System.err.println("Keep-alive failed: " + con.getStatusLine());
		}catch(IOException exc){
			System.err.println("IO Error sending keep-alive:  "+exc.getMessage());
		}
	}

	
	/**
	 * Schedule an Event to happen at a time with the given probability
	 * @param planID ID of plan containing the item
	 * @param itemID ID of the item to schedule
	 * @param time time slice to schedule
	 * @param probability
	 */
	public void schedulePlanItem(Guid planID, Guid itemID, int time, float probability) throws UnknownGUIDException
	{
		HashMap<Integer, Float> sched = new HashMap<Integer, Float>();
		sched.put(time, probability);
		insertEventSchedule(sched, planID, itemID);
	}
	
	/**
	 * Set an entire schedule for an Event
	 * @param schedule a map of time to probability
	 * @param planID ID of plan containing the item
	 * @param itemID ID of the item to schedule
	 * @return command success
	 */
	public boolean insertEventSchedule(HashMap<Integer, Float> schedule, Guid planID, Guid itemID)
	{
		Element root = DocumentHelper.createElement("schedule");
		Document doc = DocumentHelper.createDocument(root);
		root.addAttribute("item", itemID.toString());
//		root.addAttribute("plan", planID.toString());
		Element time;
		for(int t : schedule.keySet())
		{
			time = root.addElement("time");
			time.addAttribute("slice", Integer.toString(t));
			time.addAttribute("prob", Float.toString(schedule.get(t)));
		}
		try{
			String request = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("insertschedule", "UTF-8");
			request += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			request += "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(doc.asXML(), "UTF-8");
			
			// now execute
			if(debugOutput)
				System.out.println("Executing command: insertschedule");
			return Control.getInstance().executeCommand(request);
		}catch(Exception exc){
			exc.printStackTrace();
			return false;
		}
	}

	/**
	 * Obtain a temporal probability profile of a given item
	 * @param Guid planID ID of the plan containing the item
	 * @param Guid itemID ID of the item
	 * @return an array of inferred probability values
	 */	
	public double[] getInferred(Guid planID, Guid itemID) throws UnknownGUIDException, Exception
	{
		String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("inferredprofile", "UTF-8");
		query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
		query += "&" + URLEncoder.encode("item", "UTF-8") + "=" + URLEncoder.encode(itemID.toString(), "UTF-8");
		if(debugOutput)
			System.out.println("Executing query: inferredprofile");
		String response = Control.getInstance().executeQuery(query);
		Document doc = null;
		try{
			doc = DocumentHelper.parseText(response);
		}catch(Exception exc){
			String msg = response;
			if(msg.length() <= 0)
				throw exc;
			else
				throw new Exception("Error obtaining inferred probabilities:  " + msg);
		}
		
		List times = doc.selectNodes("//time");
		double[] probs = new double[times.size()];
		for(Object t : times)
		{
			Element e = (Element) t;
			int slice = Integer.parseInt(e.attributeValue("slice"));
			double prob = Double.parseDouble(e.attributeValue("prob"));
			probs[slice] = prob;
		}
		
		return probs;
	}

	/**
	 * Obtain a temporal probability profile of a given item
	 * @param Guid planID ID of the plan containing the item
	 * @param Guid itemID ID of the item
	 * @return an array of inferred probability values
	 */	
	public double[] getPredicted(Guid planID, Guid itemID) throws UnknownGUIDException, Exception
	{
		String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("predictedprofile", "UTF-8");
		query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
		query += "&" + URLEncoder.encode("item", "UTF-8") + "=" + URLEncoder.encode(itemID.toString(), "UTF-8");
		if(debugOutput)
			System.out.println("Executing query: predictedprofile");
		String response = Control.getInstance().executeQuery(query);
		Document doc = null;
		try{
			doc = DocumentHelper.parseText(response);
		}catch(Exception exc){
			String msg = response;
			if(msg.length() <= 0)
				throw exc;
			else
				throw new Exception("Error obtaining predicted probabilities:  " + msg);
		}
		
		List times = doc.selectNodes("//time");
		double[] probs = new double[times.size()];
		for(Object t : times)
		{
			Element e = (Element) t;
			int slice = Integer.parseInt(e.attributeValue("slice"));
			double prob = Double.parseDouble(e.attributeValue("prob"));
			probs[slice] = prob;
		}
		
		return probs;
	}

	/**
	 * Get a list of 'Event' items for a plan
	 * @param Guid planID ID of the plan
	 * @return map of Event IDs and thier names 
	 */
	public Map<Guid, String> getAllEvents(Guid planID)
	{
		HashMap<Guid, String> events = new HashMap<Guid, String>();

		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("allevents", "UTF-8");
		
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: allevents");
			String response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			List list = doc.selectNodes("//item");
			for(Object o : list)
			{
				Element e = (Element) o;
				events.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
			}
			return events;
		}catch(Exception exc){
			exc.printStackTrace();
			return null;
		}
	}

	/**
	 * Get a list of 'Mechanism' items for a plan
	 * @param Guid planID ID of the plan
	 * @return map of Mechanism IDs and thier names 
	 */
	public Map<Guid, String> getAllMechanisms(Guid planID)
	{
		HashMap<Guid, String> mechs = new HashMap<Guid, String>();

		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("allmechanisms", "UTF-8");
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: allmechanisms");
			String response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			List list = doc.selectNodes("//item");
			for(Object o : list)
			{
				Element e = (Element) o;
				mechs.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
			}
			return mechs;
		}catch(Exception exc){
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get a list of all causal Mechanisms for the specified Event
	 * @param planID plan ID of the plan containing the Event
	 * @param eventID ID of the Event to check
	 * @return a Map of causal Mechanism ID and names
	 */
	public Map<Guid, String> getCauses(Guid planID, Guid eventID)
	{
		HashMap<Guid, String> mechs = new HashMap<Guid, String>();

		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("causes", "UTF-8");
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			query += "&" + URLEncoder.encode("event", "UTF-8") + "=" + URLEncoder.encode(eventID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: causes");
			String response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			List list = doc.selectNodes("//item");
			for(Object o : list)
			{
				Element e = (Element) o;
				mechs.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
			}
			return mechs;
		}catch(Exception exc){
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get a list of all inhibiting Mechanisms for the specified Event
	 * @param planID plan ID of the plan containing the Event
	 * @param eventID ID of the Event to check
	 * @return a Map of inhibiting Mechanism ID and names
	 */
	public Map<Guid, String> getInhibitors(Guid planID, Guid eventID)
	{
		HashMap<Guid, String> mechs = new HashMap<Guid, String>();

		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("inhibitors", "UTF-8");
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			query += "&" + URLEncoder.encode("event", "UTF-8") + "=" + URLEncoder.encode(eventID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: inhibitors");
			String response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			List list = doc.selectNodes("//item");
			for(Object o : list)
			{
				Element e = (Element) o;
				mechs.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
			}
			return mechs;
		}catch(Exception exc){
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get a list of all effect Mechanisms for the specified Event
	 * @param planID plan ID of the plan containing the Event
	 * @param eventID ID of the Event to check
	 * @return a Map of effect Mechanism ID and names
	 */
	public Map<Guid, String> getEffects(Guid planID, Guid eventID)
	{
		HashMap<Guid, String> mechs = new HashMap<Guid, String>();

		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("effects", "UTF-8");
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			query += "&" + URLEncoder.encode("event", "UTF-8") + "=" + URLEncoder.encode(eventID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: effects");
			String response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			List list = doc.selectNodes("//item");
			for(Object o : list)
			{
				Element e = (Element) o;
				mechs.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
			}
			return mechs;
		}catch(Exception exc){
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Build and begin sampling a plan on the model server.  <b>NOTE:</b> After building a plan
	 * be sure to wait for some samples to collect before querying for probabilities from the server
	 * @see getSampleCount
	 * @param Guid planID ID of the plan to build
	 * @param int time number of time slices to sample the plan for
	 * @return boolean command success
	 */
	public boolean buildPlan(Guid planID, int time) throws UnknownGUIDException, Exception
	{
		String request = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("buildmodel", "UTF-8");
		request += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
		request += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(time), "UTF-8");
		if(debugOutput)
			System.out.println("Executing command: buildmodel");
		return Control.getInstance().executeCommand(request);
	}

	/**
	 * Stop sampling the specified plan
	 * @param planID ID of the plan
	 */
	public void stopSampler(Guid planID)
	{
		try{
			String request = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("stopsampler", "UTF-8");
			request += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing command: stopsampler");
			Control.getInstance().executeCommand(request);
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	/**
	 * Get the number of samples currently taken by the sampler running on the given plan
	 * @param planID the plan containing the sampler to check
	 */
	public int getSampleCount(Guid planID)
	{
		String response = null;
		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("samplecount", "UTF-8");
		
			query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing query: samplecount");
			response = Control.getInstance().executeQuery(query);
			Document doc = DocumentHelper.parseText(response);
			Element samp = (Element) doc.selectSingleNode("//sampler");

			int count = Integer.parseInt(samp.attributeValue("samplecount"));
			
			return count;
		}catch(Exception exc){
			System.err.println("ERROR IN RESPONSE:  "+response);
			exc.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Obtain a list of 'trigger' Events from a plan.  These are the root Events in the model which are typical to schedule,
	 * therefor triggering the rest of the model.
	 * @parm Guid planID ID of the plan
	 * @return map of Event IDs and their names
	 */
	public Map<Guid, String> getTriggers(Guid planID) throws Exception
	{
		HashMap<Guid, String> events = new HashMap<Guid, String>();
		String query = "";

		query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("alltriggers", "UTF-8");
		query += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
		if(debugOutput)
			System.out.println("Executing query: alltriggers");
		String response = Control.getInstance().executeQuery(query);
		Document doc = DocumentHelper.parseText(response);
		List list = doc.selectNodes("//item");
		for(Object o : list)
		{
			Element e = (Element) o;
			events.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
		}
		return events;
	}
	
	/**
	 * Clear the schedules of all 'trigger' Events in a specified plan.
	 * @param Guid ID of the plan
	 * @return command success
	 *
	 */
	public boolean clearAllTriggerSchedules(Guid planID) throws Exception
	{
		String request = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("cleartriggers", "UTF-8");
		request += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
		if(debugOutput)
			System.out.println("Executing command: cleartriggers");
		return Control.getInstance().executeCommand(request);
	}

	/**
	 * Sets a probability of a mechanism on a given event m -> e where  P(e|m) is x.
	 * @param planID Guid of the plan
	 * @param eventID Guid of the event within the plan
	 * @param mechanismID Guid of the mechanism influencing the event
	 * @param probability float probability to assign
	 * @param protocol use SignalType.RNOR for (Recursive-Noisy-OR)
	 * @throws UnknownGUIDException if any of the IDs are not found
	 */
	public void setSingleElicitedValue(Guid planID, Guid eventID, Guid mechanismID, float probability, int protocol) throws UnknownGUIDException
	{
		ArrayList<Guid> mechs = new ArrayList<Guid>();
		mechs.add(mechanismID);
		setSingleElicitedValue(planID, eventID, mechs, probability, protocol);
	}

	/**
	 * Set a group probability of a number of influences for a given event. 
	 * @param planID Guid plan id
	 * @param eventID Guid event id
	 * @param mechanismIDs List<Guid> collection of mechanism guids that make up the group, NOTE, they must be withing the same mode, e.g. all causes or all inhibitors
	 * @param probability float probability
	 * @param protocol use SignalType.RNOR for (Recursive-Noisy-OR)
	 * @throws UnknownGUIDException if any of the IDs are not found
	 */
	public void setSingleElicitedValue(Guid planID, Guid eventID, List<Guid> mechanismIDs, float probability, int protocol) throws UnknownGUIDException
	{
		Element root = DocumentHelper.createElement("elicitation");
		Document doc = DocumentHelper.createDocument(root);
		root.addAttribute("item", eventID.toString());
		root.addAttribute("probability", probability + "");
		root.addAttribute("protocol", protocol + "");
		
		for(Guid mID : mechanismIDs)
		{
			Element mech = root.addElement("mechanism");
			mech.addAttribute("id", mID.toString());
		}
		try{
			String request = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("elicitprob", "UTF-8");
			request += "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(doc.asXML(), "UTF-8");
			request += "&" + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(planID.toString(), "UTF-8");
			if(debugOutput)
				System.out.println("Executing command: elicitprob"+"\n"+doc.asXML());
			Control.getInstance().executeCommand(request);
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	/**
	 * Obtain a list of plans available on the model sever.
	 * @return a map of plan IDs and the plan names
	 */
	public Map<Guid, String> getPlans() throws Exception
	{
		HashMap<Guid, String> plans = new HashMap<Guid, String>();
		String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("allmodels", "UTF-8");
		if(debugOutput)
			System.out.println("Executing query: allmodels");
		String response = Control.getInstance().executeQuery(query);
		Document doc = DocumentHelper.parseText(response);
		
		List list = doc.selectNodes("//model");
		for(Object o : list)
		{
			Element e = (Element) o;
			plans.put(new Guid(e.attributeValue("guid")), e.attributeValue("name"));
		}
		return plans;
	}
	
	
	/**
	 * Start the 'keep-alive' thread to keep this session (plans modifications and sampling) active even if client is idle.
	 * This is started by default upon first communication initiation.  Use stopKeepAlive() to cancel the thread.
	 */
	public void startKeepAlive()
	{
		if(!keepAlive)
		{
			keepAlive = true;
			keepAliveThread = new Thread(this, "jcws_client_keep-alive");
			keepAliveThread.start();
		}
		else
		{
			stopKeepAlive();
			startKeepAlive();
		}
	}
	
	/**
	 * Stop the 'keep-alive' thread used to keep this session active
	 */
	public void stopKeepAlive()
	{
		keepAlive = false;
		if(keepAliveThread != null)
		{
			keepAliveThread.interrupt();
		}
	}
	
	public void run()
	{
		while(keepAlive)
		{
			try{
				Thread.sleep(keepAliveDelay);
				keepAlive();
			}catch(InterruptedException exc){
				// i dun care
			}
		}
	}

	/**
	 * Be nice and notify the model server it can immediatly clean up this session.  Also resets the current connection.
	 * Any changes made to plans and any running samplers will no longer be available
	 */
	public void shutdown()
	{
		// be nice and send a notice to server (let server know it can immediatly do session cleanup)
		try{
			String query = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("killsession", "UTF-8");
			if(debugOutput)
				System.out.println("Executing command: killsession");
			Control.getInstance().executeCommand(query);
		}catch(UnsupportedEncodingException exc){
			exc.printStackTrace();
		}catch(Exception exc){
			exc.printStackTrace();
		}
		
		
		resetConnection();
	}
	
	
	//NOT IMPLEMENTED FOR JCWS RIGHT NOW
	/** NOT CURRENTLY IMPLEMENTS IN JCWS */
	public void addAbsoluteEvidence(Guid planID, Guid itemID, int time, double probability) throws UnknownGUIDException{}
	/** NOT CURRENTLY IMPLEMENTS IN JCWS */
	public void addSensorEvidence(Guid planID, Guid itemID, int time, boolean wasTrue, double FAR, double MDR) throws UnknownGUIDException{}

}
