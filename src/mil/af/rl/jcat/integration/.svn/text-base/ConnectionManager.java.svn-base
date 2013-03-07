package mil.af.rl.jcat.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



/**
 * ConnectionManager.java
 * @author verenice
 * @company Primate Technologies / C3I Associates
 * 2005
 */
public class ConnectionManager {
    
    private static ConnectionManager manager = null;
    private static HashMap<String,Connection> connections;
    private static ArrayList<String> apps;
    private static HashMap<String,String> queries;
    private static HashMap<String,String> parsers;
    /**
     * Method returns an instance of the connection manager
     * @return ConnectionManager manager
     */
    public static ConnectionManager INSTANCE()
    {
        if(manager == null)
        {
            manager = new ConnectionManager();
        }
        return manager;
    }
    private ConnectionManager()
    {
        connections = new HashMap<String,Connection>();
        apps = new ArrayList<String>();
        queries = new HashMap<String,String>();
        parsers = new HashMap<String,String>();
        init();
    }
    
    /**
     * Initializes all the connections.
     */
    private void init()
    {
        SAXReader sar = new SAXReader();
        Document doc = null;
        try{
            doc = sar.read(this.getClass().getClassLoader().getResourceAsStream("ibc.xml"));
            //System.out.println(doc.asXML());
            
        }catch(DocumentException ex)
        {ex.printStackTrace(System.err);
        }
         
        
        
        List conlist = doc.selectNodes("//connection");
        List applications = doc.selectNodes("//app");
        List qs = doc.selectNodes("//query");
        try{
            for(Object node : conlist)
            {
                Element ce = (Element)node;
                
                connections.put(ce.attributeValue("name"),createConnection(ce.attributeValue("driver"),ce.attributeValue("url"),
                        ce.attributeValue("user"),ce.attributeValue("pswd")));
               
            }
            for(Object node : applications)
            {
                apps.add(((Element)node).attributeValue("name"));
            }
            for(Object node : qs)
            {
                Element e = (Element)node;
                queries.put(e.attributeValue("name"),e.attributeValue("text"));
                parsers.put(e.attributeValue("name"),e.attributeValue("parser"));
            }
        }catch(Exception ex)
        {
            ex.printStackTrace(System.err);
        }
        
        
        
    }
    /**
     * Helper method to initialize connections
     * @param driver
     * @param url
     * @param user
     * @param pswd
     * @return
     */
    @SuppressWarnings({"unused"})
    private Connection createConnection(String driver, String url, String user, String pswd)throws Exception
    {
        Connection con = null;
        DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
        con = DriverManager.getConnection(url,user, pswd);
        return con;
    }
    /**
     * Method returns a connection with a given name
     * @param name
     * @return
     */
    public Connection getConnection(String name)
    {
        return connections.get(name);
    }
    /**
     * Method returns the name of the parser that is mapped to this query
     * @param name
     * @return
     */
    public String getParser(String name)
    {
        return parsers.get(name);
    }
    
    
    /**
     * Method returns all available connection names
     * @return
     */
    public Set<String> getConNames()
    {
        return connections.keySet();
    }
    /**
     * Method closes a connection specified by a name.
     * @param name
     * @throws SQLException
     */
    public void killConnection(String name) throws SQLException
    {
        connections.get(name).close();
    }
    
    public ArrayList<String> getAppNames()
    {
        return apps;
    }
    /**
     * Method returns queries defined in the ibc config file.
     * @return
     */
    public Collection<String> getQueryNames()
    {
        return queries.keySet();
    }
    /**
     * method returns a query with a given name
     * @param name
     * @return
     */
    public String getQuery(String name)
    {
        return queries.get(name);
    }
    /**
     * Method closes all active connections
     * @throws SQLException
     */
    public void killAll() throws SQLException
    {
        for(Connection con : connections.values())
        {
            con.close();
        }
    }
    
    
    // test
    public static void main(String[] args)throws SQLException
    {
        System.out.println("Initializing cons..");
        ConnectionManager.INSTANCE();
        DatabaseMetaData metadata = ConnectionManager.INSTANCE().getConnection("coweb").getMetaData();
        ResultSet rs = metadata.getSchemas();
        Statement st = ConnectionManager.INSTANCE().getConnection("coweb").createStatement();
        String query = "SELECT * FROM ibc.element WHERE application = 'JCAT' OR  application = 'CAPES'";
        ResultSet result = st.executeQuery(query);
        
        for(int x=0;result.next();x++)
        {
            System.out.println(result.getString("application")+ " variable name: "+result.getString("variable"));
        }
       
       
        
        try{
            ConnectionManager.INSTANCE().killAll();
        }catch(SQLException s)
        {
            s.printStackTrace(System.err);
        }
        
    }

}
