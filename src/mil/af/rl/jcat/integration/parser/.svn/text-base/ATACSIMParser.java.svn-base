package mil.af.rl.jcat.integration.parser;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mil.af.rl.jcat.integration.IATask;
import mil.af.rl.jcat.integration.Task;
import mil.af.rl.jcat.plan.PlanItem;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class ATACSIMParser extends InputParser {

    public ATACSIMParser(int seconds, int parsertype, ArrayList<JWBShape> sh,
            JWBController m, String query,Object[] row) {
        super(seconds, parsertype, sh, m, query,row);
    }

    

    public HashMap getSchedule() {
        return super.schedule;
    }

    public List getEvidence() {
        return null;
    }


    protected boolean parseResult(ResultSet result) throws SQLException {
        Document doc = null;
        List telements;
        IATask task;
        //long newtime = Long.parseLong(result.getString("time"));
        boolean success = false;
        if(result != null)
        {
            System.out.println("Parsing ATACSIM data...");
            //result.next();
            //int x = 0;
            while(result.next())
            {
                
            try{
                    //long newtime = Long.parseLong(result.getString("time"));
                    
                    doc = DocumentHelper.parseText(result.getString("value"));
                   
                    telements = doc.selectNodes("//Attack");
                    if(telements.size() < 1)
                    {
                        continue;
                    }
                    for(Object node : telements)
                    {
                        Element taske = (Element)node;
                        if(tasks.containsKey(taske.attributeValue("type")))
                        {
                            task = (IATask)tasks.get(taske.attributeValue("type"));
                            
                        }else{
                            task = new IATask(taske.attributeValue("type"),Task.EVIDENCE);
                            
                        }
                        
                        System.out.println("updating a task: "+task.getName());
                        task.addTime(Integer.parseInt(taske.attributeValue("time")),.3f);
                        tasks.put(task.getName(), task);
                       
                        
                    }
                    // now put them into 
                    success = true;
                
                
            }catch(DocumentException de)
            {
                de.printStackTrace(System.err);
            }
        }//end while
            
            //timestamp = newtime;
        }
        return success;
    }

    // in here were are not matching guids to plan items, but for each event we'll go through
    // their effects and match their names, such as suicide bombing or small arms fire
    protected void updatePlanItems() {
        PlanItem item;
        //AbstractPlan plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(control.getUID()));
        //PlanItem effect;
        //HashMap<Integer,Evidence> evidence = new HashMap<Integer,Evidence>();
        for(JWBShape shape : shapes)
        {
            item = (PlanItem)shape.getAttachment();
            if(item.getItemType() == PlanItem.EVENT)
            {
            	// remove later
            	item.setPersistence(1);
                //List<Guid> effects = ((Event)item).getEffects();
                // now get them from abplan and match them against tasks
                //for(Guid g : effects)
                //{
                    
                    //effect = plan.getItem(g);
                    
                    for(String key : tasks.keySet())
                    {
                    	
                    	
                        //if(effect.getName().trim().equalsIgnoreCase(key.trim()))
                        //{
                        	// FOR NOW, WE WILL SIMULATE INSURGENCY, TRUSTING THE
                        	// SIMULATING INFO SOURCE
                            HashMap schedule = tasks.get(key).getSchedule();
                            for(Object i : schedule.keySet())
                            {
                                //double value = (double)((Float)schedule.get(i)).floatValue();
                                //evidence.put((Integer)i,new Evidence(value));
                                item.scheduleEvent((Integer)i,((Float)schedule.get(i)).floatValue());
                            }
                            //evidence = tasks.get(key).getSchedule();
                            //item.setEvidence(evidence);
//                            shape.removeMarkup('S');
//                            shape.addMarkup('S');// just in case                           
                            newshapes.add(shape);
                        //}
                    }
                    
                    
                    
                //}
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
        ps.setString(3,super.tablerow[2].toString()); // set instance
        
        return ps;
    }
   

}
