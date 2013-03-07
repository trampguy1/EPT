package mil.af.rl.jcat.integration.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.integration.ConnectionManager;
import mil.af.rl.jcat.integration.Task;

public abstract class OutputParser implements Agent{
    
    protected HashMap<Integer,Float> schedule;
    protected int time;
    protected Object[] tablerow;
    protected boolean alive = true;
    protected ArrayList<JWBShape> shapes;
    protected ArrayList<JWBShape> newshapes;
    protected String updatename;
    protected long timestamp = 0L; // to do is compare timestamps to check for new info
    protected HashMap<String,Task> tasks;
    protected JWBController control;

    public OutputParser(int seconds, Object[] row,ArrayList<JWBShape> sh, JWBController m, String update) {
        schedule = new HashMap<Integer,Float>();
        time = seconds * 1000;
        tablerow = row;
        shapes = sh;
        updatename = update;
        control = m;
        tasks = new HashMap<String,Task>();
        newshapes = new ArrayList<JWBShape>();
        Control.getInstance().addAgent(this);
    }
    public int getType()
    {
        return Agent.OUPUT;
    }
    public boolean isAlive()
    {
        return alive;
    }
    
    public int getInterval()
    {
        return time;
    }
    public void setExecuteInterval(int interval)
    {
        time = interval * 1000;
    }
    public void run() {
        
        String updatetext;
        Connection con = ConnectionManager.INSTANCE().getConnection("coweb");
        PreparedStatement stm = null;
        
        do{
            try{
                stm = prepareUpdate(con,ConnectionManager.INSTANCE().getQuery(updatename));
                System.out.println(stm.toString());
                int res = stm.executeUpdate();
                
                if(PreparedStatement.EXECUTE_FAILED == res)
                {
                    System.out.println("Execution failed...");
                }
                    
            }catch(SQLException s)
            {
                s.printStackTrace(System.err);
            }
            
            try{
                Thread.sleep(time);
                
           }catch(InterruptedException interrupt)
           {
              //ignore
           }
        }while(alive);
       
    }
    public void detach()
    {
        alive = false;
    }
    // used for monitoring functions
    public String toString()
    {
        return updatename;
    }
    protected abstract PreparedStatement prepareUpdate(Connection con,String update)throws SQLException;
    protected abstract void updatePlanItems();

}
