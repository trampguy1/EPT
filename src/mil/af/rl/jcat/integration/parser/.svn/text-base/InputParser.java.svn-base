package mil.af.rl.jcat.integration.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.integration.ConnectionManager;
import mil.af.rl.jcat.integration.Task;
import mil.af.rl.jcat.plan.AbstractPlan;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public abstract class InputParser implements Agent {
    
    protected HashMap<Integer,Float> schedule;
    protected int time;
    protected int type;
    protected boolean alive = true;
    protected ArrayList<JWBShape> shapes;
    protected ArrayList<JWBShape> newshapes;
    protected String queryname;
    protected long timestamp = 0L; // to do is compare timestamps to check for new info
    protected HashMap<String,Task> tasks;
    protected JWBController control;
    protected Object[] tablerow;

    public InputParser(int seconds, int parsertype,ArrayList<JWBShape> sh, JWBController m, String query,Object[] row) {
       
        schedule = new HashMap<Integer,Float>();
        time = seconds * 1000;
        type = parsertype;
        shapes = sh;
        queryname = query;
        control = m;
        tasks = new HashMap<String,Task>();
        newshapes = new ArrayList<JWBShape>();
        tablerow = row;
        Control.getInstance().addAgent(this);
    }
    
    public int getType()
    {
        return Agent.INPUT;
    }
    public void detach()
    {
        alive = false;
    }
    public int getInterval()
    {
        return time;
    }
    
    public void setExecuteInterval(int sec) {
        time = sec * 1000;
    }
    public int getParserType() {
        return type;
    }
    
    public boolean isAlive()
    {
        return alive;
    }
    
    public void run() {
        Connection con = ConnectionManager.INSTANCE().getConnection("coweb");
        PreparedStatement stm = null;
        boolean valid = false;
        try{
            stm = prepareQuery(con,ConnectionManager.INSTANCE().getQuery(queryname));
        }catch(SQLException s){s.printStackTrace(System.err);}
        ResultSet rs;
        do{
            
            try{
                rs = stm.executeQuery();
                valid = parseResult(rs);
                if(valid)
                    updatePlanItems();
            }catch(SQLException s)
            {
                s.printStackTrace(System.err);
            }
            if(valid)
                rebuild();
            try{
                Thread.sleep(time);
                
           }catch(InterruptedException interrupt)
           {
              //ignore
           }
            
        }while(alive);
       
    }
    public void rebuild()
    {
    	AbstractPlan plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(control.getUID()));
    	try{
    	plan.buildBayesNet(90);
    	}catch(Exception e)
    	{
    		e.printStackTrace(System.err);
    	}
    }
    public String toString()
    {
        return queryname;
    }
    protected abstract PreparedStatement prepareQuery(Connection con,String update)throws SQLException;
    protected abstract boolean parseResult(ResultSet result)throws SQLException;
    protected abstract void updatePlanItems();
    
}
