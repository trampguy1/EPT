/*
 * Created on Jun 21, 2005
 *
 */
package mil.af.rl.jcat.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;

/**
 * @author mcnamacr
 *
 *
 */
public class IbcConnector implements Runnable
{
    private static boolean kill = false;
    private Driver driver = null;
    private Connection con = null;
    private HashMap plans = new HashMap();
    private LinkedList sampledCOAs = new LinkedList();
    private final String app = "JCAT";
    private final String user = System.getProperty("user.name");
    private HashMap evidence = new HashMap();
    
    /**
     * 
     */
    public IbcConnector()
    {
        super();
        loadMySQLConnector();
    }

    /**
     * 	Server: 66.92.161.216

		Database: ibc

		Username: ibc

		Password: ibcIXiiIII

     */
    private void loadMySQLConnector()
    {
        try
        {
            //logger.info("Creating MySQL connection");
            driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            //logger.info("Registering Driver for MySQL");
            DriverManager.registerDriver(driver);
            //logger.info("Connecting");
            File login = new File("resources/config/coweb.login");
            if(login.exists())
            {
                BufferedReader in = new BufferedReader(new FileReader(login));
                String ip = in.readLine();
                ip = ip.substring(4);
                String user = in.readLine();
                user = user.substring(6);
                String pass = in.readLine();
                pass = pass.substring(6);
                con = DriverManager.getConnection("jdbc:mysql://"+ip+"/ibc",
                        user, pass);
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
            //logger.error("Connector Failed: " + e.getMessage());
        }         
    }
    
    public void publishPlan(Guid planid)
    {
        Statement stmt = null;
        String query = "";
        AbstractPlan pl = Control.getInstance().getPlan(planid);
        String doc = Control.getInstance().getPlanAsXML(planid);
        
        String insert = "INSERT INTO element VALUES ('" + app +"' ,'" + 
        user +"' ,'" + planid + "' ," + "'Iraq Insurgency Model'" +" ,'"+
        doc + "' ," + "'user'" + " ," + "' '" +" ,"+"UNIX_TIMESTAMP())"; 
        
        String update = "UPDATE element SET time = UNIX_TIMESTAMP(),  " +
        "element.value = '" + doc +"' " + "WHERE instance = '" + planid + 
        "' AND variable = 'Iraq Insurgency Model' AND application = 'JCAT'" +
        " AND userIdentity = '"+ user +"'";
       
        if(isEntryPublished("JCAT", planid.toString(), "Iraq Insurgency Model"))
            query = update;
        else
            query = insert;
       
        try
        {
            stmt = con.createStatement();
            stmt.execute(query);
            this.plans.put(planid, pl);
            new Thread(this).start();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        
    }
    
    private boolean isEntryPublished(String app, String id, String var)
    {
        String query = "SELECT application FROM ibc.element WHERE application = '" + app +
                       "' AND instance = '" + id + "' AND variable = '" +var +"' " +
                       "AND userIdentity = '" + user +"'";
        Statement smt;
        ResultSet rs = null;
        try
        {
            smt = con.createStatement();
            smt.execute(query);
            rs = smt.getResultSet();
            if(rs.next())
            {
                return true;
            }
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }        
        return false;
    }
   
    /**
     * Removes scheduling while preserving prexisting schedules
     * @param doc
     * @param plan
     */
    private void clearPostedCOA(Document doc, AbstractPlan plan)
    {        
        List tasks = doc.selectNodes("//Task");
        Iterator i = tasks.iterator();
        while(i.hasNext())
        {
            Element e = (Element)i.next();
            PlanItem item = plan.getItem(new Guid(e.attributeValue("GUID")));
            List sch = doc.selectNodes("//Schedule");
            Iterator t = sch.iterator();
            while(t.hasNext())
            {
                Element el = (Element)t.next();
                item.removeScheduledTime(new Integer(el.attributeValue("TIME")));
            }
            
        }  
    }

    private void parseXMLSchedule(Document doc, AbstractPlan plan) throws RemoteException 
    {
        
        List tasks = doc.selectNodes("//Task");
        Iterator i = tasks.iterator();
        while(i.hasNext())
        {
            Element e = (Element)i.next();
            PlanItem item = plan.getItem(new Guid(e.attributeValue("GUID")));
            List sch = doc.selectNodes("//Schedule");
            Iterator t = sch.iterator();
            while(t.hasNext())
            {
                Element el = (Element)t.next();
                item.scheduleEvent(Integer.parseInt(el.attributeValue("TIME")), .33f);
            }
            
        }
    }

    public void postTimeLines(Guid planid, String variable)
    {
        variable += " Timelines";
        Statement stmt = null;
        String query  = "";
        Document doc = DocumentHelper.createDocument();
        Element el = doc.addElement("Timelines");
        AbstractPlan plan = Control.getInstance().getPlan(planid);
        Object [] items =  plan.getItems().values().toArray();
        plan.loadProbabilites(items);
        /*for(int i = 0; i < items.length; i++)
        {
            PlanItem item = (PlanItem)items[i];
            Element e = el.addElement("TimeLine");
            e.addAttribute("Name", item.getName() + " - " +item.getLabel());
            for(int j = 0; j < item.getPriorProbs().length; j++)
            {
                Element t = e.addElement("Point");
                t.addAttribute("Time", j + "");
                t.addAttribute("Prob", item.getPredictedProbs()[j] + " ");
            }
            //e.setText(times);
            System.out.println(e.asXML());
        }*/
        String insert = "INSERT INTO element VALUES ('" + app +"' ,'" +
        user +"' ,'" + planid + "' ," + "'" + variable +"' ,'"+
        doc.asXML() + "' ," + "'user'" + " ," + "' '" +" ,"
        +"UNIX_TIMESTAMP())"; 
        
        String update = "UPDATE element SET time = UNIX_TIMESTAMP(),  " +
        "element.value = '" + doc.asXML() +"' " + "WHERE instance = '" + planid + 
        "' AND variable = '"+ variable +"' AND application = 'JCAT'"+
        " AND userIdentity = '"+ user +"'";
        
        if(isEntryPublished("JCAT", planid.toString(), variable))
            query = update;
        else
            query = insert;
        
        try
        {
            stmt = con.createStatement();
            stmt.execute(query);
        }catch(SQLException e)
        {
            
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        int iter = 0;
        while(plans.size() > 0 || kill == true)
        {
        	gatherEvidence();
            queryCoWeb();
            iter++;
            if(iter > 15)
                IbcConnector.kill = true;
            try
            {
               
                System.out.println("Web Queried");
                Thread.sleep(10000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

	private void gatherEvidence() 
	{
		Statement smt = null;
		ResultSet rs = null;
		String reflexQuery = "SELECT variable, instance, value FROM ibc.element WHERE " +
		"element.application = 'REFLEXINSURGENCYMODEL'";
		try
		{
			smt = con.createStatement();
			smt.execute(reflexQuery);
			rs = smt.getResultSet();
			while(rs.next())
			{
				String name = rs.getString("variable");
				int key = rs.getInt("instance");
				if(!evidence.containsKey(name))
					evidence.put(name, new HashMap());
				HashMap ev = (HashMap)evidence.get(name);
				//ev.put(new Integer(key), new Evidence(rs.getFloat("value")/10));				
			}
		}
		catch(SQLException e)
		{
			
		}
	}
	
	 public void queryCoWeb()
	    {
	        Statement smt = null;
	        ResultSet rs = null;
	        String capesQuery = "SELECT variable, instance, value FROM ibc.element WHERE " +
	        		"element.application = 'CAPES'";
	        try
	        {
	            smt = con.createStatement();
	            smt.execute(capesQuery);
	            rs = smt.getResultSet();
	            while (rs.next())
	            {   
	                Guid id = new Guid(rs.getString("instance"));
	                String variable = rs.getString("variable");
	                if(plans.containsKey(id) && !sampledCOAs.contains(variable))
	                {
	                    try
	                    {
	                        AbstractPlan plan = (AbstractPlan)plans.get(id);
	                        Document doc = DocumentHelper.parseText(rs.getString("value")); 
	                        parseXMLSchedule(doc, plan);
	                        addEvidence(plan);
	                        plan.buildBayesNet(31);
	                        try
	                        {
	                            Thread.sleep(25000);
	                        } catch (InterruptedException e)
	                        {
	                            e.printStackTrace();
	                        }                        
	                        postTimeLines(plan.getId(), variable);
	                        clearPostedCOA(doc, plan);
	                        sampledCOAs.add(variable);
	                    } catch (DocumentException e)
	                    {
	                        e.printStackTrace();
	                    } catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }
	                }     
	            }  
	        
	        } catch (SQLException e)
	        {
	            e.printStackTrace();
	        }
	        
	    }

	private void addEvidence(AbstractPlan plan) 
    {
		Object [] keys = evidence.keySet().toArray();
        Collection events = plan.getAllEvents();
        for(int i = 0; i < keys.length; i++)
        {
            String name = (String)keys[i];            
            Iterator it = events.iterator();
            while(it.hasNext())
            {
                Event ev = (Event) it.next();
                if(ev.getName().equals(name) || ev.getLabel().equals(name))
                {
                    ev.setEvidence((HashMap)evidence.get(name));
                    
                }
            }
        }
		
	}
    
    
}
