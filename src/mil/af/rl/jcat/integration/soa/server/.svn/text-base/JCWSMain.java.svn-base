/*
 * JCWSMain - Main JCWS Servlet
 *
 * Created on March 3, 2006, 9:37 AM
 */

package mil.af.rl.jcat.integration.soa.server;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import mil.af.rl.jcat.bayesnet.BayesNet;
import mil.af.rl.jcat.exceptions.BayesNetException;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.integration.api.Control;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/*
 * TODO:  could add - download plan file link, perhaps generate plan images automatically, delay/persist/contin
 * need - error handling for incorrect values passed in, missing guids and such, better command/query error response to client (non-html)
 * perhaps allow specify name for server in web.xml
 */
public class JCWSMain extends HttpServlet
{
	public static final String version = "1.1.0WS";
	private String fatalErrorText = null;
	private ArrayList<Document> planDocuments = null;
	

	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		if(action != null)
			action = action.toLowerCase();
		Control sesController = SessionManager.getInstance().getController(request.getSession());
		
		// Special cases
		if(fatalErrorText != null)
			writeErrorResponse(response, fatalErrorText);
		else if(sesController == null)
			writeErrorResponse(response, "Your session may have timed out.");
		else if(request.getParameter("sestimeout") != null) // client wants to know what the session timeout is
			out.println(request.getSession().getMaxInactiveInterval() + "");
		else if(action == null || action.equals(""))
			writeErrorResponse(response, "You must specify and action.");
		
		// basic action queries and commands 
		else if(action.equals("activemodels")) // html output
		{
			out.println("<html>");
			out.println("<head>");
			out.println("<title>JCWS - View Active Models</title>");
			out.println("</head>");
			out.println("<body>");
			out.println(getHeader(request.getSession()));
			
			out.println("<h2>Active Models @ JCAT Model Server</h2>");
			out.println("<p>");
			
			for(Guid g : sesController.getPlans().keySet())
			{
				AbstractPlan plan = sesController.getPlan(g);
				out.println("<br><b>Model Name: <a href='" + "JCWSMain?action=viewmodel&model="+ plan.getId() +"'>" + plan.getPlanName() + "</a></b>");
				out.println("<br><center><table width='90%' border=0>");
				out.println("<tr><td><b>ID:</b>  "+plan.getId()+"</td></tr>");
				out.println("<tr><td>"+plan.getDocumentation().getDescription()+"</td></tr>");
				out.println("</table></center>");
				out.println("<p>");
			}
			
			out.println("</body></html>");

		}
		else if(action.equals("viewmodel")) // html output
		{
			String pID = request.getParameter("model");
//			if(pID == null) // not required for this page
//				writeErrorResponse(response, "Required parameter 'plan' is missing.");
			
//			else
//			{
				out.println("<html>");
				out.println("<head>");
				out.println("<title>JCWS - View Model</title>");
				out.println("</head>");
				out.println("<body>");
				out.println(getHeader(request.getSession()));
				
				out.println("<h2>Model View @ JCAT Model Server</h2>");
				
				Set<Guid> planList;
				if(pID == null || pID.equals("") || pID.equals("all"))
					planList = sesController.getPlans().keySet();
				else
				{
					planList = new java.util.TreeSet<Guid>();
					planList.add(new Guid(pID));
				}
				
				for(Guid g : planList)
				{
					AbstractPlan plan = sesController.getPlan(g);
					if(plan != null)
					{
						out.println("<br><b>Model Name: </b>" + plan.getPlanName() + "");
						out.println("<br><b>Model ID: </b>" + plan.getId() + "<br>");
						out.println("<b><br>Model Description:</b>");
						out.println("<br>" + plan.getDocumentation().getDescription() + "<br>");
						out.println("<br><b></b><font color='red'>RED = trigger nodes </font><br>");
						// now create a table with items
						out.println("<br><TABLE border=0><TR><TD width='40%'><b>Event Name</b></TD><TD><b>Event ID</b></TD></TR>");
						// table data...
						for(Event e : plan.getAllEvents())
						{
							if(e.getCauses().size() == 0 && e.getInhibitors().size() == 0)
								out.println("<TR><TD><font color='red'>" + e.getName() + "</font></TD><TD>" + e.getGuid() + "</TD>");
							else
								out.println("<TR><TD>" + e.getName() + "</TD><TD>" + e.getGuid() + "</TD>");
							
							out.println("<TD><a href='JCWSMain?action=viewevent&model="+pID+"&event="+e.getGuid()+"'>Details</a></TD></TR>");
						}
						out.println("</TABLE>");
						
						String[] tokens = plan.getPlanName().split("[.]"); //<--could be another dot in the filename
						String imagename = tokens[0] + ".jpeg"; //what about jpg and gif 
						// insert plan image
						out.println("<br><img src='" + "model_imgs/" + imagename + "' alt='" + "Model preview unavailable" + "'/>");
						//width='900' height='400'
						out.println("<p>&nbsp; <p>&nbsp;");
					}
					else
						out.println("<p> Model ID " + g + " was not found");
				}
				out.println("</body></html>");
//			}
		}
		else if(action.equals("viewevent")) // html output
		{
			String pID = request.getParameter("model");
			String eID = request.getParameter("event");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			if(eID == null)
				writeErrorResponse(response, "Required parameter 'event' is missing.");
			
			else
			{
				out.println("<html>");
				out.println("<head>");
				out.println("<title>JCWS - View Event</title>");
				out.println("</head>");
				out.println("<body>");
				out.println(getHeader(request.getSession()));
				
				out.println("<h2>Event View @ JCAT Model Server</h2>");
				
				Guid planID = new Guid(pID);
				
				AbstractPlan plan = sesController.getPlan(planID);
				if(plan != null)
				{
					Event event = (Event)plan.getItem(new Guid(eID));
					out.println("<br><b>Model Name: </b><a href='JCWSMain?action=viewmodel&model="+planID+"'>" + plan.getPlanName() + "</a>");
					out.println("<br><b>Event ID: </b>" + event.getGuid());
					out.println("<br><b>Event Name: </b>"+event.getName());

					out.println("<p><b>Mechanisms</b> (Causes)");
					out.println("<br><TABLE border=0 width='100%'><TR><TD width='40%'><b>Mechanism Name</b></TD><TD width='40%'><b>Mechanism ID</b></TD><TD><b>From Event</b></TD></TR>");
					for(Guid id : event.getCauses())
					{
						Mechanism mech = (Mechanism)plan.getItem(id);
						out.println("<TR><TD>" + mech.getName() + "</TD><TD>" + mech.getGuid() + "</TD><TD>" +plan.getItem(mech.getFromEvent()).getName()+ "</TD></TR>");
					}
					out.println("</TABLE>");
					
					out.println("<p><b>Mechanisms</b> (Inhibitors)");
					out.println("<br><TABLE border=0 width='100%'><TR><TD width='40%'><b>Mechanism Name</b></TD><TD width='40%'><b>Mechanism ID</b></TD><TD><b>From Event</b></TD></TR>");
					for(Guid id : event.getInhibitors())
					{
						Mechanism mech = (Mechanism)plan.getItem(id);
						out.println("<TR><TD>" + mech.getName() + "</TD><TD>" + mech.getGuid() + "</TD><TD>" +plan.getItem(mech.getFromEvent()).getName()+ "</TD></TR>");
					}
					out.println("</TABLE>");
					
					out.println("<p><b>Mechanisms</b> (Effects)");
					out.println("<br><TABLE border=0 width='100%'><TR><TD width='40%'><b>Mechanism Name</b></TD><TD width='40%'><b>Mechanism ID</b></TD><TD><b>To Event</b></TD></TR>");
					for(Guid id : event.getEffects())
					{
						Mechanism mech = (Mechanism)plan.getItem(id);
						out.println("<TR><TD>" + mech.getName() + "</TD><TD>" + mech.getGuid() + "</TD><TD>" +plan.getItem(mech.getToEvent(0)).getName()+ "</TD></TR>");
					}
					out.println("</TABLE>");
					
					String[] tokens = plan.getPlanName().split("[.]"); //<--could be another dot in the filename
					String imagename = tokens[0] + ".jpeg"; //what about jpg and gif 
					// insert plan image
					out.println("<br><img src='" + "model_imgs/" + imagename + "' alt='" + "Model preview unavailable" + "'/>");
					//width='900' height='400'
					out.println("<p>&nbsp; <p>&nbsp;");
				}
				else
					out.println("<p> Model ID " + planID + " was not found");

				out.println("</body></html>");
			}
		}
		else if(action.equals("buildmodel")) // command
		{
			String pID = request.getParameter("model");
			String timeStr = request.getParameter("time");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			else if(timeStr == null)
				writeErrorResponse(response, "Required parameter 'time' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				int time = Integer.parseInt(timeStr);
				
				try{
					sesController.getPlan(plan).buildBayesNet(time);
					out.println("success");
				}catch(Exception e)
				{
					out.println(e.toString());
				}
			}
		}
		else if(action.equals("stopsampler")) // command
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				try{
					BayesNet bayesNet = sesController.getPlan(plan).getBayesNet();
					if(bayesNet != null)
						bayesNet.killSampler();
					out.println("success");
				}catch(Exception e)
				{
					out.println(e.toString());
				}
			}
		}		
		else if(action.equals("alltriggers")) // query response
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				out.println(this.prepareTriggerEvents(sesController.getPlan(plan).getAllEvents()));
			}
		}
		else if(action.equals("predictedprofile") || action.equals("inferredprofile")) // query response
		{
			String pID = request.getParameter("model");
			String itemID = request.getParameter("item");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			else if(itemID == null)
				writeErrorResponse(response, "Required parameter 'item' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				Guid item = new Guid(itemID);
				if(sesController.getPlan(plan).getBayesNet() == null)
					out.println("The bayes net has not yet been built, please build the net first.");
				else
				{
					String profile = prepareTimelineData(sesController.getPlan(plan), item, action.equals("predictedprofile") ? 0 : 1);
					out.println(profile);
				}
			}
		}
		else if(action.equals("insertschedule")) // command
		{
			String pID = request.getParameter("model");
			String value = request.getParameter("value");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			else if(value == null)
				writeErrorResponse(response, "Required parameter 'value' is missing.");
			
			else
			{
				Guid plang = new Guid(pID);
				AbstractPlan plan = sesController.getPlan(plang);
				try
				{
					Document doc = DocumentHelper.parseText(value);// get the data
					Element schedule = (Element)doc.selectSingleNode("//schedule");
					String itemID = schedule.attributeValue("item");
					Guid itemguid = new Guid(itemID);
					HashMap<Integer, Float> itemschedule = new HashMap<Integer, Float>();
					
					List timelist = ((Element) schedule).selectNodes("./time");
					for(Object t : timelist)
					{
						Element timelement = (Element) t;
						itemschedule.put(Integer.parseInt(timelement.attributeValue("slice")), Float.parseFloat(timelement.attributeValue("prob")));
					}
					// now schedule item
					scheduleItem(itemschedule, itemguid, plan);
	
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				//why no return "success"
			}
		}
		else if(action.equals("elicitprob")) // command
		{
			String pID = request.getParameter("model");
			String value = request.getParameter("value");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			else if(value == null)
				writeErrorResponse(response, "Required parameter 'value' is missing.");
			
			else
			{
				Guid plang = new Guid(pID);
				AbstractPlan plan = sesController.getPlan(plang);
				try
				{
					Document doc = DocumentHelper.parseText(value);// get the data
					Element elicitEl = (Element)doc.selectSingleNode("//elicitation");
					Guid itemID = new Guid(elicitEl.attributeValue("item"));
					float prob = Float.parseFloat(elicitEl.attributeValue("probability"));
					int prot = Integer.parseInt(elicitEl.attributeValue("protocol"));
					
					ArrayList<Guid> mechIDs = new ArrayList<Guid>();
					
					List mechs = ((Element) elicitEl).selectNodes("./mechanism");
					for(Object t : mechs)
					{
						Element mechEl = (Element) t;
						mechIDs.add(new Guid(mechEl.attributeValue("id")));
					}
					// now elicit the value
					elicitProbability(plan, itemID, mechIDs, prob, prot);
	
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			//why no return "success"
		}
		else if(action.equals("allevents")) // query response
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				out.println(this.prepareEvents(sesController.getPlan(plan).getAllEvents()));
			}
		}
		else if(action.equals("allmechanisms")) // query response
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid plan = new Guid(pID);
				out.println(this.prepareMechanisms(sesController.getPlan(plan).getAllMechanisms()));
			}
		}
		else if(action.equals("causes")) // query response
		{
			String pID = request.getParameter("model");
			String eID = request.getParameter("event");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			if(eID == null)
				writeErrorResponse(response, "Required parameter 'event' is missing.");
			
			else
			{
				Guid planID = new Guid(pID);
				Event event = (Event)sesController.getPlan(planID).getItem(new Guid(eID));
				ArrayList<Mechanism> mechs = new ArrayList<Mechanism>();
				for(Guid c : event.getCauses())
					mechs.add((Mechanism)sesController.getPlan(planID).getItem(c));
				out.println(this.prepareMechanisms(mechs));
			}
		}
		else if(action.equals("inhibitors")) // query response
		{
			String pID = request.getParameter("model");
			String eID = request.getParameter("event");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			if(eID == null)
				writeErrorResponse(response, "Required parameter 'event' is missing.");
			
			else
			{
				Guid planID = new Guid(pID);
				Event event = (Event)sesController.getPlan(planID).getItem(new Guid(eID));
				ArrayList<Mechanism> mechs = new ArrayList<Mechanism>();
				for(Guid c : event.getInhibitors())
					mechs.add((Mechanism)sesController.getPlan(planID).getItem(c));
				out.println(this.prepareMechanisms(mechs));
			}
		}
		else if(action.equals("effects")) // query response
		{
			String pID = request.getParameter("model");
			String eID = request.getParameter("event");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			if(eID == null)
				writeErrorResponse(response, "Required parameter 'event' is missing.");
			
			else
			{
				Guid planID = new Guid(pID);
				Event event = (Event)sesController.getPlan(planID).getItem(new Guid(eID));
				ArrayList<Mechanism> mechs = new ArrayList<Mechanism>();
				for(Guid c : event.getEffects())
					mechs.add((Mechanism)sesController.getPlan(planID).getItem(c));
				out.println(this.prepareMechanisms(mechs));
			}
		}
		else if(action.equals("allmodels")) // query response
		{
			Element root = DocumentHelper.createElement("jcatws");
			Document doc = DocumentHelper.createDocument(root);
			Element plan;
			for(Guid g : sesController.getPlans().keySet())
			{
				plan = root.addElement("model");
				plan.addAttribute("name", sesController.getPlan(g).getPlanName());
				plan.addAttribute("guid", g.getValue());
			}
			out.println(doc.asXML());
		}
		else if(action.equals("cleartriggers")) // command
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid p = new Guid(pID);
				AbstractPlan plan = sesController.getPlan(p);
				for(Event e : plan.getAllEvents())
				{
					if(e.getCauses().size() < 1 && e.getInhibitors().size() < 1)
					{
						// clear schedule
						e.setSchedule(new TreeMap());
					}
				}
			}
			// why no return 'success'
		}
		else if(action.equals("samplecount")) // query response
		{
			String pID = request.getParameter("model");
			if(pID == null)
				writeErrorResponse(response, "Required parameter 'model' is missing.");
			
			else
			{
				Guid p = new Guid(pID);
				AbstractPlan plan = sesController.getPlan(p);
				Document doc = DocumentHelper.createDocument();
				Element root = doc.addElement("sampler");
				if(plan != null && plan.getBayesNet() != null)
					root.addAttribute("samplecount", plan.getBayesNet().getSampler().getSampleCount() + ""); //TODO: change to sesController.getSampleCount(planID);
				else
					root.addAttribute("samplecount", "-1");
				
				out.println(doc.asXML());
			}
		}
		else if(action.equals("killsession")) // command
		{
			// client is specifically requesting its session be disposed now
			SessionManager.getInstance().destroyController(request.getSession());
		}
		// ADMIN HTML SHITS
		else if(action.equals("viewsessions"))
		{
			String function = request.getParameter("function");
			
			if(function == null || function.equals(""))
			{
				out.println("<html>");
				out.println("<head>");
				out.println("<title>JCWS - Admin</title>");
				out.println("</head>");
				out.println("<body>");
				out.println(getHeader(request.getSession()));
				
				out.println("<h2>Admin - View Sessions @ JCAT Model Server</h2>");
				out.println("<p> &nbsp;");
				out.println("<p> &nbsp;");
				out.println("Current Time:  "+System.currentTimeMillis()+"  :  "+new java.util.Date(System.currentTimeMillis()));
				
				out.println("<table width='100%' border='1' cellspacing='0' cellpadding='5'>");
				out.println("<th>ID</th>  <th>Last Access Time</th>  <th>Timeout Delay (sec)</th>  <th>Expires in (sec)</th>  <th>Status</th>  <th>Models</th>  <th>Sampers Running</th>");
				
				for(HttpSession session : SessionManager.getInstance().getSessions())
				{
					out.println("<tr>");
					out.println("<td align='left' valign='center'> "+session.getId()+" </td>");
					boolean expired = SessionManager.isExpired(session);
					java.util.Date aTime = (expired) ? null : new java.util.Date(session.getLastAccessedTime());
					// Not sure about this lastAccessedTime stuff, comparing to currentTimeMilis
					out.println("<td align='right' valign='center'> "+((expired) ? "" : aTime)+" </td>");
					out.println("<td align='right' valign='center'> "+session.getMaxInactiveInterval()+" </td>");
					out.println("<td align='right' valign='center'> "+((expired) ? "" : 
						(((session.getMaxInactiveInterval() * 1000) - (System.currentTimeMillis() - session.getLastAccessedTime())) / 1000)
						)+" </td>");
					out.println("<td align='right' valign='center'> "+((expired) ?
								"<font color='red'> EXPIRED </font>" : "ACTIVE")
							+" </td>");
					out.println("<td align='right' valign='center'> "+SessionManager.getInstance().getController(session).getPlans().size()+" </td>");
					int sampCount = 0;
					for(AbstractPlan p : SessionManager.getInstance().getController(session).getPlans().values())
						if(p.getBayesNet() != null && p.getBayesNet().isSampling())
							sampCount++;
					out.println("<td align='right' valign='center'> "+sampCount+" </td>");
					out.println("</tr>");
				}
				
				out.println("</table>");
				out.println("<p>");
				
				// cleanup expired sessions button
				out.println("<form action='JCWSMain'>");
				out.println("<input type='submit' value='Cleanup Expired Sessions'/>");
				out.println("<input type='hidden' name='action' value='viewsessions'/>");
				out.println("<input type='hidden' name='function' value='cleanup'/>");
				out.println("</form>");
				
				out.println("</body></html>");
			}
			else if(function.equals("cleanup"))
			{
				SessionManager.getInstance().removeExpired();
				response.sendRedirect("JCWSMain?action=viewsessions");
			}
		}
		else
			writeErrorResponse(response, "The specified action was unknown and could not be processed.");
		
		out.close();
	}

	private void writeErrorResponse(HttpServletResponse response, String errorText) throws IOException
	{
		PrintWriter out = response.getWriter();
		
		out.println("<html>");
		out.println("<head>");
		out.println("<title> JCAT Model Server - Error</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<p>");
		out.println("<p><h2>An error has occured processing your JCAT Web Service request!</h2>");
		out.println("<p>"+ errorText);
		
		out.println("</body></html>");
		
		out.close();
		
		if(errorText == fatalErrorText);
			fatalErrorText = null;
	}

	private String getHeader(HttpSession session)
	{
		StringBuffer header = new StringBuffer();
		
		header.append("<table width='100%' border='0' cellspacing='0' cellpadding='0'>");
		header.append("<tr>");
		header.append("<td align='left' valign='top'><b> JCAT Web Services "+version+" </b></td>");
		header.append("<td align='right' valign='top'> <img src='logo.jpg'> </td>");
		header.append("</tr>");
//		if(session != null)
//		{
//			header.append("<tr>");
//			header.append("<td>");
//			header.append("session: " 
//					+ "<br> ID: "+session.getId()
//					+ "<br> Created: "+session.getCreationTime()
//					+ "<br> Max Inactive: "+session.getMaxInactiveInterval()
//					+ "");
//			Enumeration attributeNames = session.getAttributeNames();
//			while(attributeNames.hasMoreElements())
//				header.append("<br> "+attributeNames.nextElement());
//			header.append("<p>");
//			header.append("</td>");
//			header.append("</tr>");
//		}
		header.append("</table>");
		header.append("<p>");
		
		return header.toString();
	}
	
	
	private void scheduleItem(HashMap<Integer, Float> schedule, Guid itemg, AbstractPlan plan)
	{
		PlanItem item = plan.getItem(itemg);
		item.setSchedule(new TreeMap()); // clear it first
		for(int time : schedule.keySet())
		{
			item.scheduleEvent(time, schedule.get(time));
		}
	}
	
	private void elicitProbability(AbstractPlan plan, Guid itemID, List<Guid> mechIDs, float prob, int prot)
	{
		Event event = (Event)plan.getItem(itemID);
		
		if(event != null)
		{
			ArrayList<Guid> signalIDs = new ArrayList<Guid>();
			for(Guid mID : mechIDs)
				signalIDs.add(((Mechanism)plan.getItem(mID)).getSignalGuid());
			plan.getLibrary().addElicitedValue(event.getProcessGuid(), prot, signalIDs, prob, "");
		}
	}

	/**
	 * Helper method to prepare the xml for response
	 */
	private String prepareTriggerEvents(Collection<Event> list)
	{
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Events");
		Element i;
		for(Event e : list)
		{
			if(e.getCauses().size() == 0 && e.getInhibitors().size() == 0)
			{
				i = root.addElement("item");
				i.addAttribute("guid", e.getGuid().getValue());
				i.addAttribute("name", e.getName());
			}
		}
		return doc.asXML();
	}

	private String prepareEvents(Collection<Event> list)
	{
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Events");
		Element i;
		for(Event e : list)
		{

			i = root.addElement("item");
			i.addAttribute("guid", e.getGuid().getValue());
			i.addAttribute("name", e.getName());

		}
		return doc.asXML();
	}
	
	private String prepareMechanisms(Collection<Mechanism> list)
	{
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Mechanisms");
		Element i;
		for(Mechanism e : list)
		{

			i = root.addElement("item");
			i.addAttribute("guid", e.getGuid().getValue());
			i.addAttribute("name", e.getName());

		}
		return doc.asXML();
	}

	/**
	 * Helper method used to prepare timeline data for a given item
	 * Use 0 for predicted prob or 1 for inferred as type
	 */
	private String prepareTimelineData(AbstractPlan plan, Guid iguid, int type)
	{
		Document doc = DocumentHelper.createDocument();
		Element el = doc.addElement("timeline");
		PlanItem item = plan.getItem(iguid);
		plan.loadProbabilites(new Object[] { item });
		el.addAttribute("name", item.getName());
		el.addAttribute("guid", iguid.getValue());

		try{
			double[] probs = (type == 0) ? plan.getPredictedProbs(item.getGuid()) : plan.getInferredProbs(item.getGuid());
			for(int j = 0; j < probs.length; j++)
			{
				Element t = el.addElement("time");
				t.addAttribute("slice", j + "");
				if(probs.length > 0)
					t.addAttribute("prob", probs[j] + " ");
				else
					t.addAttribute("prob", 0.0f + " ");
			}
		}catch(BayesNetException exc)
		{
			exc.printStackTrace();
		}

		return doc.asXML();
	}

	/** Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//TODO:  check out getRequestedSessionId, perhaps to transfer
		if(SessionManager.getInstance().getController(request.getSession()) == null) // new/unknown client
		{
			Control newControl = SessionManager.getInstance().createController(request.getSession());
			for(Document planDoc : planDocuments)
			{
				try{
					
					Guid newID = newControl.openPlan(planDoc);
					newControl.getPlan(newID).setPlanName(planDoc.getName());
					
				}catch(DuplicateNameException exc)
				{
					System.err.println("DuplicateNameException openening document  "+planDoc.getName()+"  for session  "+request.getSession().getId()+"\n "+exc.getMessage());
					exc.printStackTrace();
				}catch(DocumentException exc)
				{
					System.err.println("DocumentException openening document  "+planDoc.getName()+"  for session  "+request.getSession().getId()+"\n "+exc.getMessage());
					exc.printStackTrace();
				}
			}
		}
		
		processRequest(request, response);
	}

	/** Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if(SessionManager.getInstance().getController(request.getSession()) == null) // new/unknown client
		{
			Control newControl = SessionManager.getInstance().createController(request.getSession());
			for(Document planDoc : planDocuments)
			{
				try{
					
					Guid newID = newControl.openPlan(planDoc);
					newControl.getPlan(newID).setPlanName(planDoc.getName());
					
				}catch(DuplicateNameException exc)
				{
					System.err.println("DuplicateNameException openening document  "+planDoc.getName()+"  for session  "+request.getSession().getId()+"\n "+exc.getMessage());
					exc.printStackTrace();
				}catch(DocumentException exc)
				{
					System.err.println("DocumentException openening document  "+planDoc.getName()+"  for session  "+request.getSession().getId()+"\n "+exc.getMessage());
					exc.printStackTrace();
				}
			}
		}
		
		processRequest(request, response);
	}

	/** Returns a short description of the servlet.
	 */
	public String getServletInfo()
	{
		return "JCAT Web Services "+version;
	}

	public void init()
	{
		planDocuments = new ArrayList<Document>();
		
		String plansPath = getServletContext().getInitParameter("models");
		String timeout = getInitParameter("session-timeout");
		String planImgs = getServletContext().getRealPath("model_imgs");
		
		File plansDir = new File(plansPath);
		File planImgsDir = new File(planImgs);
		
		if(timeout != null && !timeout.trim().equals(""))
		{
			try{
				SessionManager.getInstance().setTimeout(Integer.parseInt(timeout));
			}catch(NumberFormatException exc){
				System.err.println("JCWSMain:init - Invalid session-timeout specified:  "+timeout);
			}
		}

		if(plansDir.exists() && plansDir.canRead())
		{
			File[] planFiles = plansDir.listFiles();
			for(int x = 0; x < planFiles.length; x++)
			{
				try{
					
					if(planFiles[x].getName().toLowerCase().endsWith(".jcat"))
					{
						// to support multiple clients/sessions:
						//	- for efficiency load plans from files into a dom4j doc if possible (def dont wana read plans from drive on each new client connection)
						//	- then open the document in the clients controller at first request (creating AbstractPlan and such)
						//	  or just open all documents in each controller when created, but def not here
						//	- copy the plan images here still, only need to do that once
						
						Document planDoc = Control.getPlanDocument(planFiles[x]);
						planDoc.setName(planFiles[x].getName());
						
						planDocuments.add(planDoc);
//						Guid pID = Control.getInstance().openPlan(planFiles[x]);
						
						// TODO:  perhaps if one doesn't exist somewhere in web root (has to be reletive to web dir for html)
						// then look for one in plan location and copy to web root/plan_imgs dir, if none exist then perhaps generate one
						String[] tokens = planFiles[x].getName().split("[.]");
						String imageName = tokens[0] + ".jpeg"; //what about jpg and gif
						
						String webPath = planImgs + System.getProperty("file.separator") + imageName;
						if(!(new File(webPath).exists()) && planImgsDir.exists() && planImgsDir.canWrite())
						{
							File srcImg = new File(planFiles[x].getParent() + System.getProperty("file.separator") + imageName);
							if(srcImg.exists())
							{
								System.out.println("-------- copied image from model dir:  "+imageName);
								copyFile(srcImg, planImgs);
							}
						}
					}
					
				}catch(Exception e)
				{
					e.printStackTrace(System.err);
				}
			}
		}
		else
			fatalErrorText = "There was an error initializing JCWS.  Check your server setup to ensure the specified models path exists and has proper permissions.";
		
		
	}
	
	
	@Override
	public void destroy()
	{
		SessionManager.getInstance().destroy();
		
		super.destroy();
	}

	
	private void copyFile(File thisFile, String destDir) throws IOException
	{
		File from = thisFile;
		File to = new File(destDir + System.getProperty("file.separator") + thisFile.getName());
		
		FileInputStream reader = null;
		FileOutputStream writer = null;
		
		try{
			if(!to.getParentFile().exists())
				to.getParentFile().mkdirs();
			to.createNewFile();
			
			reader = new FileInputStream(from);
			writer = new FileOutputStream(to);
			byte[] readBytes = new byte[1024];
			int bytesRead = 0;
			int totalRead = 0;
			
			while((bytesRead = reader.read(readBytes)) > 0)
				writer.write(readBytes, 0, bytesRead);
			
//			if(from.length() != to.length())
//				System.err.println("\t WARNING:  Src and Dest not equal length: "+to.getName());
			
		}catch(IOException exc){
			throw exc;
		}
		finally
		{
			try{
				if(reader != null)
					reader.close();
				if(writer != null)
				{
					writer.flush();
					writer.close();
				}
			}catch(IOException exc){   }
		}
	}
}
