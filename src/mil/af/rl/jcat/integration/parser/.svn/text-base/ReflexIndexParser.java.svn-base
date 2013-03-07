package mil.af.rl.jcat.integration.parser;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import mil.af.rl.jcat.bayesnet.Evidence;
import mil.af.rl.jcat.integration.IATask;
import mil.af.rl.jcat.integration.Task;
import mil.af.rl.jcat.plan.PlanItem;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class ReflexIndexParser extends InputParser {

	public ReflexIndexParser(int seconds, int parsertype,
			ArrayList<JWBShape> sh, JWBController m, String query,Object[] row) {
		super(seconds, parsertype, sh, m, query,row);
	}

	@Override
	protected boolean parseResult(ResultSet result) throws SQLException {

        IATask task;
        if(result != null)
        {
            result.next();
           
                long newtime = Long.parseLong(result.getString("time"));
                float index = Float.parseFloat(result.getString("value"));
                if(newtime != super.timestamp)
                {
                	Random rand = new Random();
                    timestamp = newtime;
                    task = new IATask("Reflex Index",Task.EVIDENCE);
                    task.addTime(rand.nextInt(90+1),index/10);
                    //task.addTime(40,index/10);
                    tasks.put(task.getName(),task);
                    
                    return true;
                }
            
        }
        return false;
	}

	@Override
	protected void updatePlanItems() {
		PlanItem item;
        HashMap<Integer,Evidence> evidence = new HashMap<Integer,Evidence>();
        for(JWBShape shape : shapes)
        {
            item = (PlanItem)shape.getAttachment();
            if(item.getItemType() == PlanItem.EVENT)
            {
              
                HashMap schedule = tasks.get("Reflex Index").getSchedule();
                // hack - set persistance for a week
                //item.setPersistence(30);
                for(Object i : schedule.keySet())
                {
                	// for now sub it for scheduling instead of evidence
                    double value = (double)((Float)schedule.get(i)).floatValue();
                    evidence.put((Integer)i,new Evidence(value));
                    item.setEvidence(evidence);
                    // hack - set persistance for a week
                    //item.scheduleEvent((Integer)i,((Float)schedule.get(i)).floatValue());
                    
                }
                //item.setEvidence(evidence);
//                shape.removeMarkup('E');
//                shape.addMarkup('E');
                //shape.removeMarkup('S');
                //shape.addMarkup('S');// just in case
                newshapes.add(shape);
            	
            }
                
            
        }
        try{
            control.putShapes(newshapes);
        }catch(RemoteException re)
        {
            
        }

	}

    @Override
    protected PreparedStatement prepareQuery(Connection con, String update) throws SQLException {
        PreparedStatement ps = con.prepareStatement(update);
        //ps.setString(1,prepareTimelineData());
        ps.setString(1,super.tablerow[3].toString()); // set app name
        ps.setString(2,super.tablerow[0].toString()); // set variable name
        
        return ps;
    }

}
