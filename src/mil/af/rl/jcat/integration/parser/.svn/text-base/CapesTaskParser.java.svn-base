package mil.af.rl.jcat.integration.parser;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mil.af.rl.jcat.integration.MTask;
import mil.af.rl.jcat.integration.Task;
import mil.af.rl.jcat.plan.PlanItem;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public final class CapesTaskParser extends InputParser {

    public CapesTaskParser(int seconds, int parsertype, ArrayList<JWBShape> sh,
            JWBController m, String query,Object[] row) {
        super(seconds, parsertype, sh, m, query,row);
        
    }

    @Override
    protected boolean parseResult(ResultSet result) throws SQLException {
        Document doc = null;
        List telements;
        MTask task;
        if(result != null)
        {
            result.next();
            try{
                long newtime = Long.parseLong(result.getString("time"));
                if(newtime != super.timestamp)
                {
                	System.out.println("attempting to parse capes result set...");
                    timestamp = newtime;
                    
                    doc = DocumentHelper.parseText(result.getString("value"));
                    telements = doc.selectNodes("//Task");
                    for(Object node : telements)
                    {
                        Element taske = (Element)node;
                        task = new MTask(taske.attributeValue("GUID"),taske.attributeValue("NAME"),Task.SCHEDULE);
                        
                        Element prox = (Element)taske.selectSingleNode("./Proximity");
                        if(prox != null && prox.getText().trim().equals("Vicinity"))
                            task.setVacinity(true);
                        List times = taske.selectNodes("./Schedule");
                        for(Object n : times)
                        {
                        	System.out.println("capes has new tasks...");
                        	if(task.getVacinity())
                        		task.addTime(Integer.parseInt(((Element)n).attributeValue("TIME")),0.9f);
                        	else
                        		task.addTime(Integer.parseInt(((Element)n).attributeValue("TIME")),0.1f);
                        }
                        
                        List subjects = taske.selectNodes("./Subject");
                        for(Object s : subjects)
                        {
                            task.addEffect(((Element)s).getText());
                        }
                        tasks.put(task.getGuid().getValue(), task);
                        
                    }
                    return true;
                }
            }catch(DocumentException de)
            {
                de.printStackTrace(System.err);
            }
        }
        return false;
    }

    @Override
    protected void updatePlanItems() {
    	PlanItem item;
        //MTask t;
        
        for(JWBShape shape : shapes)
        {
        	System.out.println("Updating plan items for capes...");
            item = (PlanItem)shape.getAttachment();
            for(Task t : tasks.values())
            {
            	//if(item.getName().contains(t.getName().trim()))
            	//{
            		boolean scheduled = false;
                    for(Integer timing : t.getSchedule().keySet())
                    {
                        item.scheduleEvent(timing,t.getSchedule().get(timing));
                        scheduled = true;
                    }
//                    if(scheduled){
//                        // first remove items
//                        shape.removeMarkup('S');
//                        shape.addMarkup('S');
//                    }
            	//}
            }
            newshapes.add(shape);
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
