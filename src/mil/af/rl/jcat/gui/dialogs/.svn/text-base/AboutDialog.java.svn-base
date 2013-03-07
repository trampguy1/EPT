package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;

/**
 * <p>Title: AboutDialog.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: C3I Associates</p>
 *
 * @author Edward Verenich
 * @version 1.0
 */
public class AboutDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private JPanel tpane;
	private static Logger logger = Logger.getLogger(AboutDialog.class);

    public AboutDialog(Frame owner, boolean modal)
    {
        super(owner, "About", modal);
        setResizable(false);
        try{
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
            setLocationRelativeTo(owner);
            setVisible(true);
        }catch(Exception exc){
        	logger.warn("Constructor - error initializing dialog:  ", exc);
        }
    }

    private AboutDialog()
    {
        this(new Frame(), false);
    }

    private void jbInit() throws Exception
    {
        JPanel infopanel = new JPanel();
        infopanel.setLayout(new GridLayout(1,2));
        String[] team = new String[]{"JCAT TEAM","","Dygert, Michael","Lemmer, John PhD","Martin, Zachary","McNamara, Craig","Verenich, Edward"};

        String[] tools = new String[]{"VERSION INFO", "", "JCAT Version " + Control.fileVer, "", "", "JRE "+System.getProperty("java.version")};
        infopanel.add(new JList(tools));
        infopanel.add(new JList(team));
        
//        System.out.println("ENV ----------------------------------");
//        Map<String, String> env = System.getenv();
//        for(String n : env.keySet())
//        	System.out.println(n + " : " + env.get(n));
//        System.out.println("Properties ----------------------------------");
//        Properties properties = System.getProperties();
//        for(Object p : properties.keySet())
//        	System.out.println(p.toString() + " : " + properties.get(p).toString());

        tpane = new JPanel(new BorderLayout());
        tpane.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("about.png"))), BorderLayout.NORTH);
        tpane.add(infopanel, BorderLayout.SOUTH);
        getContentPane().add(tpane);
    }

    public static void main(String[] args)
    {
	    
        AboutDialog dialog = new AboutDialog();
        
    }
}
