package mil.af.rl.jcat.integration.gui;

import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

public class AgentMonitor extends JDialog {

    private static final long serialVersionUID = 1L;
    private SourceTable table;//reuse the source table
    private JScrollPane spane;


    public AgentMonitor() throws HeadlessException {
        super();
        setModal(true);
        setSize(500,300);
        setTitle("Agent Monitor");
        init();
        
    }
    
    private void init()
    {
        spane = new JScrollPane();
        table = new SourceTable(new AgentTableModel());
        spane.setViewportView(table);
        this.getContentPane().add(spane);
    }
    
    

}
