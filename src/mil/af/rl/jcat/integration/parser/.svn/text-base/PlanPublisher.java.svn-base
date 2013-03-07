package mil.af.rl.jcat.integration.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.plan.AbstractPlan;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class PlanPublisher extends OutputParser {

    public PlanPublisher(int seconds, Object[] row, ArrayList<JWBShape> sh,
            JWBController m, String update) {
        super(seconds, row, sh, m, update);
    }

    @Override
    protected PreparedStatement prepareUpdate(Connection con, String update)
            throws SQLException {
        PreparedStatement ps = con.prepareStatement(update);
        AbstractPlan plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(control.getUID()));
        ps.setString(1,Control.getInstance().getPlanAsXML(plan.getId()));
        //SerialBlob blob = new SerialBlob(Control.getInstance().getPlanAsXML(plan.getId()).getBytes());
        //ps.setBlob(1,blob);
        ps.setString(2,tablerow[3].toString());
        ps.setString(3,tablerow[0].toString());
        return ps;
    }
    
    @Override
    protected void updatePlanItems() {

    }

}
