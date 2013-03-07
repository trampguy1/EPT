package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JTable;
import mil.af.rl.jcat.gui.table.model.BayesThreadModel;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * <p>Title: PlanTrackerDialog.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: C3I Associates</p>
 * @author Edward Verenich
 * @version 1.0
 */
public class PlanTrackerDialog extends JDialog 
{

	private static final long serialVersionUID = 1L;
	private JPanel panel1 = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private static Logger logger = Logger.getLogger(PlanTrackerDialog.class);

    public PlanTrackerDialog(Frame owner, boolean modal)
    {
        super(owner, "Model Tracker", modal);
        setSize(300,400);
        try{
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }catch(Exception exception){
        	logger.error("Constructor - error initializing tracker dialog:  "+exception.getMessage());
        }
    }


    private void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        javax.swing.JScrollPane spane = new javax.swing.JScrollPane();
        spane.getViewport().add(new JTable(new BayesThreadModel()));
        panel1.add(spane,BorderLayout.CENTER);
        getContentPane().add(panel1);
    }
}
