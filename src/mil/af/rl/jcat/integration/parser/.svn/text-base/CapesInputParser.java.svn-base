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

public class CapesInputParser extends InputParser {
    
    
    

    public CapesInputParser(int seconds, int parsertype,ArrayList<JWBShape> sh, JWBController m, String query,Object[] row) {
        super(seconds, parsertype,sh,m,query,row);
    }

    protected boolean parseResult(ResultSet result) throws SQLException {
        
        Document doc = null;
        List telements;
        Task task;
        if(result != null)
        {
            result.next();
            try{
                long newtime = Long.parseLong(result.getString("time"));
                if(newtime != super.timestamp)
                {
                    timestamp = newtime;
                    
                    doc = DocumentHelper.parseText(result.getString("value"));
                    telements = doc.selectNodes("//Task");
                    for(Object node : telements)
                    {
                        Element taske = (Element)node;
                        task = new Task(taske.attributeValue("GUID"),taske.attributeValue("NAME"),Task.SCHEDULE);
                        List times = taske.selectNodes("./Schedule");
                        for(Object n : times)
                        {
                            task.addTime(Integer.parseInt(((Element)n).attributeValue("TIME")),0.9f);
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
    protected void updatePlanItems()
    {
        PlanItem item;
        Task t;
        for(JWBShape shape : shapes)
        {
            item = (PlanItem)shape.getAttachment();
            if(tasks.containsKey(item.getGuid().getValue()))
            {
                t = tasks.get(item.getGuid().getValue());
                boolean scheduled = false;
                for(Integer timing : t.getSchedule().keySet())
                {
                    item.scheduleEvent(timing,t.getSchedule().get(timing));
                    scheduled = true;
                }
//                if(scheduled){
//                    // first remove items
//                    shape.removeMarkup('S');
//                    shape.addMarkup('S');
//                }
            }
            newshapes.add(shape);
        }
        try{
            control.putShapes(newshapes);
        }catch(RemoteException re)
        {
            
        }
    }


    public HashMap getSchedule() {
        
        return schedule;
    }

    public List getEvidence() {
       
        return null;
    }

    public int getParserType() {
        return type;
    }

    @Override
    protected PreparedStatement prepareQuery(Connection con, String update) throws SQLException {
        return null;
    }

   

}
