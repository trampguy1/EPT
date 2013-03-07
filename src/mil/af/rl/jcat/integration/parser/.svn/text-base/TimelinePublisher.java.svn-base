package mil.af.rl.jcat.integration.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class TimelinePublisher extends OutputParser {

    public TimelinePublisher(int seconds, Object[] row, ArrayList<JWBShape> sh,
            JWBController m, String update) {
        super(seconds, row, sh, m, update);
    }

    @Override
    protected PreparedStatement prepareUpdate(Connection con,String update) throws SQLException {
        PreparedStatement ps = con.prepareStatement(update);
        ps.setString(1,prepareTimelineData());
        ps.setString(2,super.tablerow[3].toString());
        ps.setString(3,super.tablerow[0].toString());
        
        return ps;
        
        
    }

    @Override
    protected void updatePlanItems() {
       

    }
    
    private String prepareTimelineData()
    {
        AbstractPlan plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(control.getUID()));
        ArrayList<PlanItem> items = new ArrayList<PlanItem>();
        for(JWBShape shape : shapes)
        {
            items.add((PlanItem)shape.getAttachment());
        }
        Document doc = DocumentHelper.createDocument();
        Element el = doc.addElement("Timelines");
        plan.loadProbabilites(items.toArray());
        for(PlanItem item : items)
        {
            
            Element e = el.addElement("TimeLine");
            e.addAttribute("Name", item.getName() + " - " +item.getLabel());
            // since loop below is commented and nobody cares right now, i shall comment this line as well to fix compile error
            //double [] probs = plan.getInferredProbs(item.getGuid());
            /*for(int j = 0; j < item.getPriorProbs().length; j++)
            {
                Element t = e.addElement("Point");
                t.addAttribute("Time", j + "");
                if(probs.length > 0)
                    t.addAttribute("Prob", probs[j] + " ");
                else
                    t.addAttribute("Prob", 0.0f + " ");
            }*/
        }
        return doc.asXML();
    }

}
