package mil.af.rl.jcat.integration.parser;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mil.af.rl.jcat.integration.Task;
import mil.af.rl.jcat.plan.PlanItem;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class TimelineParser extends InputParser {

	public TimelineParser(int seconds, int parsertype, ArrayList<JWBShape> sh,
			JWBController m, String query, Object[] row) {
		super(seconds, parsertype, sh, m, query, row);
	}

	@Override
	protected PreparedStatement prepareQuery(Connection con, String update)
			throws SQLException {
		PreparedStatement ps = con.prepareStatement(update);
        //ps.setString(1,prepareTimelineData());
        ps.setString(1,super.tablerow[3].toString()); // set app name
        ps.setString(2,super.tablerow[0].toString()); // set variable name
        ps.setString(3,super.tablerow[2].toString()); // set instance
        
        return ps;
	}

	@Override
	protected boolean parseResult(ResultSet result) throws SQLException {
		Document doc = null;
        List telements;
        Task task;
        //long newtime = Long.parseLong(result.getString("time"));
        boolean success = false;
        if(result != null)
        {
            
            try{
            	    result.next();
                    long newtime = Long.parseLong(result.getString("time"));
                    if(timestamp >= newtime)
                    {
                    	System.out.println("OLD DATA IN TIMELINE...");
                    	return false;
                    }
                    
                    doc = DocumentHelper.parseText(result.getString("value"));
                    telements = doc.selectNodes("//TimeLine");
                    for(Object node : telements)
                    {
                        Element taske = (Element)node;
                        task = new Task(taske.attributeValue("Name"),Task.SCHEDULE);
                        List timeset = taske.selectNodes("./Point");
                        int time = 0;
                        float prob = 0.0f;
                        for(Object t : timeset)
                        {
                        	time = Integer.parseInt(((Element)t).attributeValue("Time"));
                        	prob = Float.parseFloat(((Element)t).attributeValue("Prob"));
                        	task.addTime(time,prob);
                        }
                        tasks.put(task.getName(), task);
                    }
                    
                    // now put them into 
                    success = true;
                    timestamp = newtime;
                
            }catch(DocumentException de)
            {
                de.printStackTrace(System.err);
            }
        
            
            
        }
        return success;
	}

	@Override
	protected void updatePlanItems() {
		PlanItem item;
		StringBuffer fullname = new StringBuffer();
		char[] del = new char[]{' ','-',' '};
        for(JWBShape shape : shapes)
        {
            item = (PlanItem)shape.getAttachment();
            fullname.append(item.getName().toCharArray());
            fullname.append(del);
            fullname.append(item.getLabel().toCharArray());
            if(item.getItemType() == PlanItem.EVENT)
            {
            	System.out.println(tasks);
                System.out.println(fullname);
                HashMap<Integer,Float> schedule = tasks.get(fullname.toString()).getSchedule();
                // hack - set persistance for a week
                //item.setPersistence(7);
                for(Integer i : schedule.keySet())
                {
                    item.scheduleEvent(i,schedule.get(i));   
                }
                //shape.removeMarkup('T');
                //shape.addMarkup('T');
//                shape.removeMarkup('S');
//                shape.addMarkup('S');// just in case
                newshapes.add(shape);
            	
            }
                
            
        }
        try{
            control.putShapes(newshapes);
        }catch(RemoteException re)
        {
            
        }

	}

}
